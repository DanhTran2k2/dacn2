package com.example.test_atten;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText editTextemail, editTextpass;
    private TextView trangthai;
    private Retrofit retrofit;
    private  RetrofitInterface retrofitInterface;
    private  String url ="http://192.168.0.3:8080";
    BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothLeAdvertiser advertising;
    private String uuid_ble  = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextemail = (EditText)findViewById(R.id.editTextTextPersonName2);
        editTextpass = (EditText)findViewById(R.id.editTextTextPassword);
        trangthai = (TextView)findViewById(R.id.textstatus1);
        //blue
        if( !BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported() ) {
            Toast.makeText( this, "Multiple advertisement not supported", Toast.LENGTH_SHORT ).show();
        }
        String m_androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        System.out.println(m_androidId);
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String > map = new HashMap<>();
                map.put("email", editTextemail.getText().toString());
                map.put("id_device", m_androidId.toString());
                map.put("password", editTextpass.getText().toString());
                Call<LoginResult> call = retrofitInterface.executeLogin(map);
                call.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                        if (response.code()==200){
                            LoginResult result = response.body();
                            String hocphan = result.getTenhp();
                            String phong = result.getPhong();
                            String tiet = result.getTiet();
                            String uuid = result.getUuid();
                            String idsv = result.getIdsv();
                            String idhp = result.getIdhp();
                            //callstatus(idsv, idhp);
//                             .Builder builder = new AlertDialog.Builder(MainActivity.this);
//                            builder.setTitle(result.getName());
//                            builder.setMessage(result.getUsername());
//                            builder.show();
//                            Intent MainIntent = new Intent(MainActivity.this,MainActivity2.class);
//                            startActivity(MainIntent);
                                Intent i = new Intent(getApplicationContext(), MainActivity2.class);
                                i.putExtra("hocphan", hocphan);
                                i.putExtra("phong", phong);
                                i.putExtra("tiet", tiet);
                                i.putExtra("idhp", idhp);
                                i.putExtra("idsv", idsv);
                                i.putExtra("idphong", uuid);
                                startActivity(i);
                            //Toast.makeText(MainActivity.this, "Login success", Toast.LENGTH_LONG).show();

                        }else if(response.code()==404){
//                            LoginResult result = response.body();
//                            String loi = result.getLoi();
                            System.out.println("loi roi");
                            Toast.makeText(MainActivity.this, "Mật khẩu sai hoặc không phải thiết bị của bạn", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResult> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

    }
//    private void callstatus(String idsv, String idhp){
//        HashMap<String, String > map = new HashMap<>();
//        map.put("idsv", idsv);
//        map.put("idhp", idhp);
//        Call<Sinhvien> call = retrofitInterface.exSinhvienCall(map);
//        call.enqueue(new Callback<Sinhvien>() {
//            @Override
//            public void onResponse(Call<Sinhvien> call, Response<Sinhvien> response) {
//                if (response.code()==200) {
//                    Sinhvien result = response.body();
//                    String status = result.getStatus();
//                    trangthai.setText(status);
//                    System.out.println(status);
//                }
//                else if(response.code()==404){
//                    Toast.makeText(MainActivity.this, "Wrong", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Sinhvien> call, Throwable t) {
//
//            }
//        });
//
//    }
    private void handleSignupDialog() {

    }

    private void handleLoginDialog() {

    }
//    private void advertise() {
//        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
//
//        AdvertiseSettings settings = new AdvertiseSettings.Builder()
//                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
//                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
//                .setConnectable(false)
//                .build();
//
//        ParcelUuid pUuid = new ParcelUuid(UUID.fromString(uuid_ble));
//
//        AdvertiseData data = new AdvertiseData.Builder()
//                .setIncludeDeviceName( true )
//                .addServiceUuid( pUuid )
////                .addServiceData( pUuid, "Data".getBytes(Charset.forName("UTF-8") ) )
//                .build();
//
//        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
//            @Override
//            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
//                super.onStartSuccess(settingsInEffect);
//            }
//
//            @Override
//            public void onStartFailure(int errorCode) {
//                Log.e( "BLE", "Advertising onStartFailure: " + errorCode );
//                super.onStartFailure(errorCode);
//            }
//        };
//
//        advertiser.startAdvertising( settings, data, advertisingCallback );
//    }
}