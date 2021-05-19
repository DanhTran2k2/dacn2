package com.example.test_atten;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity {
    TextView hocphan_,  phong_, tiet_ ,trangthai_, text1, text2;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 1 ;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter =null;
    BluetoothLeScanner btScanner;
    private boolean scanning;
    private Handler handler = new Handler();
    String uuidcode ="";
    private String idsv_="";
    private String idhp_="";
    String uuidslass="763f7d88-a74b-11eb-bcbc-0242ac130002";
    private  String url ="http://192.168.0.3:8080";
    private Retrofit retrofit;
    private  RetrofitInterface retrofitInterface;

    ListView peripheralTextView;
    private ArrayAdapter<String> ListAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        hocphan_ =(TextView)findViewById(R.id.textHp);
        phong_ =(TextView)findViewById(R.id.textphong1);
        tiet_ = (TextView)findViewById(R.id.texttiet1);

        trangthai_ = (TextView)findViewById(R.id.textstatus1);
        Intent intent = getIntent();
         String hocphan = intent.getStringExtra("hocphan");
         String phong = intent.getStringExtra("phong");
         String tiet = intent.getStringExtra("tiet");
         String idsv = intent.getStringExtra("idsv");

         String idhp = intent.getStringExtra("idhp");

         String idphong = intent.getStringExtra("idphong");
        hocphan_.setText( hocphan);
        phong_.setText(phong);
        tiet_.setText(tiet);

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        //blue
        //peripheralTextView = (ListView) findViewById(R.id.PeripheralTextView);
//        ListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
//        peripheralTextView.setAdapter((ListAdapter));
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();
        if (btManager != null) {
            btAdapter = btManager.getAdapter();
        }
        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
        }
        scanner();
        //callstatus(idsv,idhp);
        callstatus();

    }

    private ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //BluetoothDevice device = result.getDevice();
//            List<ParcelUuid> servicesUuids = result.getScanRecord().getServiceUuids();
//            String uuid = servicesUuids.get(0).getUuid().toString().toLowerCase();
            ble(result);
            List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
            if (uuids != null) {
                for (ParcelUuid uuid : uuids) {
                    uuidcode= uuid.toString();
                    System.out.println(uuidcode);
//                    ListAdapter.add("Name: "+ result.getDevice().getName()+"\n"+ "rssi"+result.getRssi()+ "\n "+"uuid\n" +uuidcode );
//                    ListAdapter.notifyDataSetChanged();
                }
            }
           //uui return ;
        }

    };
    public void ble (ScanResult result){
        int RSSI = result.getRssi();
//            System.out.println(RSSI);
//            System.out.println(uuidcode);
        if(RSSI > -80){
            if (uuidcode.equals(uuidslass)==true) {
                System.out.println("okee");
                //callstatus();
                Intent intent = getIntent();
                HashMap<String, String > map = new HashMap<>();
                map.put("idsv", intent.getStringExtra("idsv"));
                map.put("idhp", intent.getStringExtra("idhp"));
                Call<Sinhvien> call = retrofitInterface.exSinhvienCall(map);
                call.enqueue(new Callback<Sinhvien>() {
                    @Override
                    public void onResponse(Call<Sinhvien> call, Response<Sinhvien> response) {
                        if (response.code()==200) {
                            Sinhvien result = response.body();
                            String status = result.getStatus();
                            trangthai_.setText(status);
                            System.out.println(status);
                        }
                        else {
                        }
                    }
                    @Override
                    public void onFailure(Call<Sinhvien> call, Throwable t) {
                    }
                });
                //System.out.println("name: \n"+result.getDevice().getName() +"uuid\n"+uuidcode +"rssi:\n"+result.getRssi());
                if (!scanning) {
                    // Stops scanning after a pre-defined scan period.
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scanning = false;
                            btScanner.startScan(callback);
                        }
                    },10000);

                    scanning = true;
                    btScanner.stopScan(callback);
                }
            }
//            else {
//                btScanner.startScan(callback);
//            }
        }
    }
    public void callstatus(){
        Intent intent = getIntent();
        HashMap<String, String > map = new HashMap<>();
        map.put("idsv", intent.getStringExtra("idsv"));
        map.put("idhp", intent.getStringExtra("idhp"));
        Call<Status> call = retrofitInterface.STATUS_CALL(map);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                if (response.code()==200) {
                    Status result = response.body();
                    String status = result.getStatus();
                    trangthai_.setText(status);
                    System.out.println(status);
                }
                else {
                }
            }
            @Override
            public void onFailure(Call<Status> call, Throwable t) {
            }
        });
    }
    public void scanner() {
        System.out.println("start scanning");
        if(btScanner != null) {
            if (!scanning) {
                // Stops scanning after a pre-defined scan period.

                btScanner.startScan(callback);
            } else {
                scanning = false;
                btScanner.stopScan(callback);
            }
        }
    }
    private void scanLeDevice() {
        if(btScanner != null) {
            if (!scanning) {
                // Stops scanning after a pre-defined scan period.
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanning = false;
                        btScanner.stopScan(callback);
                    }
                },1000);

                scanning = true;
                btScanner.startScan(callback);
            } else {
                scanning = false;
                btScanner.stopScan(callback);
            }
        }
    }

}