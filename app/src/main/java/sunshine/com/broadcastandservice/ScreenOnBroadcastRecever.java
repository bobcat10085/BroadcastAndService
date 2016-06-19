package sunshine.com.broadcastandservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by a on 2016/6/19.
 */
public class ScreenOnBroadcastRecever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("qqq", "屏幕开");
    }
}
