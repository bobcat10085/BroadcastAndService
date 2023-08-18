package sunshine.com.broadcastandservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by a on 2016/6/19.
 */
public class MyReceiver extends BroadcastReceiver {
	
	    @Override
	    public void onReceive(Context context, Intent intent) {
	//        获取外拨电话的电话号码
	//       （从当前的广播中抽取结果数据，一般为null，但这个事件中获得了外拨号码）
	        String phoneNumber = getResultData();
	        Log.e("qqq","打电话:"+phoneNumber);
	//        下面是设置外拨电话的号码，你会发现无论你打给谁，都打给了10010
	        setResultData("10010");
	    }
	}