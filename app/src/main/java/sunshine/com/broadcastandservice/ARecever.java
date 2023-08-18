包sunshine.com.broadcastandservice；

导入android.content.BroadcastReceiver;
导入android.content.Context；
导入android.content.Intent;
导入android.util.Log；

/**
* 由 a 于 2016/6/19 创建。
*/
公共 类 MyReceiver 扩展了BroadcastReceiver {
	
	    @覆盖
	    公共 无效 onReceive （上下文上下文，意图意图） {
	// 获取外拨电话的电话号码
	// （从当前的广播中抽取结果数据，一般为null，但该事件中获得了外拨号码）
	        String电话号码= getResultData ( ) ;
	        日志。e ( "qqq" , "打电话：" +电话号码) ;
	// 下面是设置外拨电话的号码，你会发现无论你打给谁，都打给了10010
	        setResultData ( "10010" ) ;
	    }
	}
