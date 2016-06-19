package sunshine.com.broadcastandservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by a on 2016/6/19.
 */
public class DRecever extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("qqqq","DRecever:"+getResultData());
        Log.e("qqqq","DRecever--->得到在intent里面携带的数据:"+intent.getStringExtra("data"));
    }
}
