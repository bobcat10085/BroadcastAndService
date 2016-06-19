package sunshine.com.broadcastandservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * 在Activity绑定一个Service
 * 在Service里注册一个BroadcastReceiver监听手机开关屏幕
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_bind_service;
    private Button bt_regist_screen_broadcast;
    private Button bt_send_orderbroadcast;
    private IRegister iBinder;
    private ServiceConnection conn;
    private Button bt_unregist_screen_broadcast;
    private Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initView();
        setListener();
    }

    private void init() {
        service = new Intent(MainActivity.this, MyService.class);
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iBinder = (IRegister) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    private void setListener() {
        bt_bind_service.setOnClickListener(this);
        bt_regist_screen_broadcast.setOnClickListener(this);
        bt_unregist_screen_broadcast.setOnClickListener(this);
        bt_send_orderbroadcast.setOnClickListener(this);
    }

    private void initView() {

        bt_bind_service = (Button) findViewById(R.id.bt_bind_service);
        bt_regist_screen_broadcast = (Button) findViewById(R.id.bt_regist_screen_broadcast);
        bt_unregist_screen_broadcast = (Button) findViewById(R.id.bt_unregist_screen_broadcast);
        bt_send_orderbroadcast = (Button) findViewById(R.id.bt_send_orderbroadcast);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_bind_service:
                bindService(service, conn, BIND_AUTO_CREATE);
                break;

            case R.id.bt_regist_screen_broadcast:
                if (iBinder == null) {
                    Toast.makeText(MainActivity.this, "请先开启服务", Toast.LENGTH_SHORT).show();
                    break;
                }
                iBinder.register();
                break;

            case R.id.bt_unregist_screen_broadcast:
                if (iBinder == null) {
                    Toast.makeText(MainActivity.this, "请先开启服务", Toast.LENGTH_SHORT).show();
                    break;
                }
                iBinder.unRegister();
                break;
            case R.id.bt_send_orderbroadcast:
                Intent intent = new Intent();
                intent.setAction("com.test.hello");
                intent.putExtra("data", "发送有序广播");
                sendOrderedBroadcast(intent, "com.test.permission", new DRecever(), null, 1, "1000", null);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("qqq", "Activity--->解绑服务");
//      记得解绑服务，否则leaked
        unbindService(conn);
    }
}
