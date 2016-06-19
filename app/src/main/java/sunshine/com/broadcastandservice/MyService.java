package sunshine.com.broadcastandservice;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by a on 2016/6/19.
 */
public class MyService extends Service {

    private ScreenOnBroadcastRecever screenOnBroadcastRecever;
    private ScreenOffBroadcastRecever screenOffBroadcastRecever;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("qqq", "服务--->onBind");

        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("qqq", "服务--->onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("qqq", "服务--->onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("qqq", "服务--->onDestroy");

        if (screenOnBroadcastRecever != null && screenOffBroadcastRecever != null) {
//            记得注销广播接收者，否则leaked
            unRegisterBroadcastReceiver();
        }
    }

    private class MyBinder extends Binder implements IRegister {

        @Override
        public void register() {
            registerBroadcastReceiver();
        }

        @Override
        public void unRegister() {
            unRegisterBroadcastReceiver();
        }
    }

    private void registerBroadcastReceiver() {

        Log.e("qqq", "服务--->注册广播了");
        screenOnBroadcastRecever = new ScreenOnBroadcastRecever();
        registerReceiver(screenOnBroadcastRecever, new IntentFilter(Intent.ACTION_SCREEN_ON));
        screenOffBroadcastRecever = new ScreenOffBroadcastRecever();
        registerReceiver(screenOffBroadcastRecever, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    private void unRegisterBroadcastReceiver() {
        try {
            Log.e("qqq", "服务--->广播注销了");
            unregisterReceiver(screenOnBroadcastRecever);
            unregisterReceiver(screenOffBroadcastRecever);
        } catch (IllegalArgumentException e) {
            Toast.makeText(MyService.this, "广播已经被注销，不能重复注销", Toast.LENGTH_SHORT).show();

        }

    }
}
