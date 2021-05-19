var mysql = require('mysql');

var con = require('./sql/sql');

module.exports = {
    post: (req, res) =>{
        var post_data = req.body;
        var idsv = post_data.idsv;
        var idhp = post_data.idhp;
        con.query("SELECT `idsv`, `idhp`, `ngay` FROM `diemdanh` WHERE `idsv`= ? AND `idhp` = ? AND `ngay` = NOW()", [idsv, idhp], 
        function(err, result, fields){
            con.on('error', function(err){
                console.log('[Myssql error]', err);
            });
            if(result && result.length)
            res.status(200).send(JSON.stringify("Đã điểm danh"));
            else{
                con.query("INSERT INTO `diemdanh` (`idsv`, `idhp`, `ngay`) VALUES (?,?, NOW())",[idsv,idhp], 
                function(err, result,fields){
                    con.on('error', function(err){
                        console.log('[Myssql error]', err);
                       // res.json("Register error: ",err);
                    }); 
                    res.status(200);
                });
            }
        })
    }
}