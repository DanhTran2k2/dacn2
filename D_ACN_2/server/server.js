var express = require('express');
var app = express();
var crypto = require("crypto");
var bodyParser = require("body-parser");
var con = require('./sql/sql');
var uuid = require('uuid');
var client = require('./mqtt/mqtt');
const MOMENT= require( 'moment' );
let datetime = MOMENT().format( 'YYYY-MM-DD' );
console.log(datetime)

//client.setMaxListeners(100);

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
var getRandomString = function(length){
        return crypto.randomBytes(Math.ceil(length/2))
        .toString('hex')
        .slice(0,length);
}
var sha512 = function(password, salt){
    var hash = crypto.createHmac('sha512',salt);//user sha512
    hash.update(password);
    var value = hash.digest('hex');
    return{
        salt: salt,
        passwordHash: value
    }

};
function saltHashPassword(userPassword){
    var salt = getRandomString(16);
    var passwordData = sha512(userPassword,salt);
    return passwordData;
}
function checkHashPassword(userPassword,salt){
    var passwordData = sha512(userPassword,salt);

    return passwordData;
}

//MQTT
client.on('message', function(topic, message){
    if(topic == "danhtran98/vku/b203"){
        uuid_sv = message.toString();
        con.query('SELECT * FROM `sinhvien` WHERE uuid=?',[uuid_sv],function(err, result, fields){         
            if(result && result.length){
                var id = result[0].ID
                console.log(id);
            }
        });
        
    }
})
app.post('/register', (req,res, next)=>{
    var post_data = req.body;
    var uid = uuid.v4();
    var plaint_password = post_data.password;
    var hash_data = saltHashPassword(plaint_password);
    var password = hash_data.passwordHash;
    var salt = hash_data.salt;
    var name = post_data.name;
    var email = post_data.email;

    con.query('SELECT * FROM sinhvien where username =?',[email],function(err, result, fields){
        con.on('error', function(err){
            console.log('[Myssql error]', err);
        }); 

        if(result && result.length){
            res.json(result);
            res.json("User already exitss");
      }
        else {
            con.query("INSERT INTO `sinhvien`(`name`,`username`, `encrypted_password`, `salt`, `created_at`, `updated_at`)\
             VALUES (?,?,?,?,NOW(),NOW())", [name, email,password,salt],
             function(err, result,fields){
                con.on('error', function(err){
                    console.log('[Myssql error]', err);
                    res.json("Register error: ",err);
                }); 
                res.json("Register ok");
            })
    }
    });
   
});
app.post('/status', (req,res, next)=>{
    var post_data = req.body;
    var idsv = post_data.idsv;
    var idhp = post_data.idhp;
    con.query("SELECT  * FROM `diemdanh` WHERE `idsv`= ? AND `idhp` =? AND `ngay`=?", [idsv, idhp,datetime], 
    function(err, result, fields){
        con.on('error', function(err){
            console.log('[Myssql error]', err);
        });      
        if(result && result.length){
            //res.json(result);
            const statusob={
                status :"Đã điểm danh"
            }
            res.status(200).send(JSON.stringify(statusob));
           // res.json("đã điểm danh");
           console.log("okeeee");
        }          
        else{ 
            const statusob={
                status :"Chưa điểm danh"
            }
            res.status(200).send(JSON.stringify(statusob));
        }
    })
});
app.post('/sinhvien', (req, res, next)=>{
    var arr_idsv = new Array();
    var post_data = req.body;
    var idsv = post_data.idsv;
    var idhp = post_data.idhp;
    var ngay = post_data.ngay;
    
    arr_idsv.push(idsv);
    console.log(arr_idsv);

    con.query("SELECT  * FROM `diemdanh` WHERE `idsv`= ? AND `idhp` =? AND `ngay`=?", [idsv, idhp,datetime], 
    function(err, result, fields){
        con.on('error', function(err){
            console.log('[Myssql error]', err);
        });      
        if(result && result.length){
            //res.json(result);
            const statusob={
                status :"Đã điểm danh"
            }
            res.status(200).send(JSON.stringify(statusob));
           // res.json("đã điểm danh");
           console.log("okeeee");
        }          
        else{
            con.query("INSERT INTO `diemdanh` (`idsv`, `idhp`, `ngay`) VALUES (?,?,?)",[idsv,idhp, datetime], 
            function(err, result,fields){
                con.on('error', function(err){
                    console.log('[Myssql error]', err);
                }); 
                const statusob={
                    status :"Đã điểm danh"
                }
                res.status(200).send(JSON.stringify(statusob));
            });
        }
    })
});
app.post("/login", (req,res,next)=>{
    var post_data = req.body;
    var iddevice = post_data.id_device;
    var user_password = post_data.password;
    var email = post_data.email;
    con.query('SELECT * FROM sinhvien where email =?',[email],function(err, result, fields){
        con.on('error', function(err){
            console.log('[Myssql error]', err);
        }); 
        if(result && result.length)
        {
            var salt = result[0].salt;
            var id_device = result[0].id_device;           
            var encrypted_password = result[0].encrypted_password;
            var hashed_password = checkHashPassword(user_password,salt).passwordHash;
            if(encrypted_password == hashed_password && id_device == iddevice){
                //console.log(hashed_password);
                var idsv = result[0].ID;
                console.log(idsv);
                con.query ('SELECT `phong`.`tenphong`, `phong`.`ID_P`, `hocphan`.`tenhp`, svhocphan.`tiet`, svhocphan.`idhp`, svhocphan.`tuan` FROM `svhocphan`\
                INNER JOIN `hocphan` ON `svhocphan`.`idhp`=`hocphan`.`id` \
				INNER JOIN `phong` ON `svhocphan`.`Id_p` =`phong`.`ID_P`\
                WHERE idsv= ?',[idsv],
                    function(err, result, fields){
                        const objToSend = {
                        idsv : idsv,
                        idhp :result[0].idhp,
                        uuid :result[0].ID_P,
                        tenhp: result[0].tenhp,
                        phong: result[0].tenphong,
                        tiet: result[0].tiet,
                        tuan: result[0].tuan
                    } 
                    res.status(200).send(JSON.stringify(objToSend));
                    console.log(objToSend);
                    }
                )
               
                }
            else
            // (JSON.stringify("Wrong password")
            res.status(404).send(JSON.stringify("Error"));
           
        }
        else {
        
            res.json("User not exitss");
         }
    
    });
 
});

app.listen(8080, () => {
  console.log('listening on *:8080');
});