package me.blog.haj990108.winklick;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Locale;

import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {
	
	private GlobalSettings global;
	public static Button serviceBtn;
	public static Context mContext;
	//private static boolean isTestQwerty = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        global = (GlobalSettings) getApplicationContext();
        global.isTablet = isTablet(global);
        global.local = getResources().getConfiguration().locale;
        
        
        if(global.isTablet){
        	setContentView(R.layout.activity_main_tablet);
        }else{
        	setContentView(R.layout.activity_main);
        }
        
        mContext = this;
        	
        Log.d("TAG", "global.cursorSpeed = "+global.cursorSpeed);
        Log.d("TAG", "global.updatePeriodSec = "+global.updatePeriodSec);
        
        serviceBtn = (Button)findViewById(R.id.serviceBtn);
        serviceBtn.setOnClickListener(this);
        findViewById(R.id.settingsBtn).setOnClickListener(this);
        findViewById(R.id.testBtn).setOnClickListener(this);
        
        if(isServiceRunning("me.blog.haj990108.winklick.ActionService")){
        	serviceBtn.setText(getString(R.string.end));
        }else{
        	serviceBtn.setText(getString(R.string.run));
        }//갑작스런 종료에도 버튼 텍스트 정상유지
        
        //이걸 넣으면 잠금 화면 전에 이게 나온다
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }
    @Override
    public void onClick(View v) {
        int view = v.getId();
        
        switch(view){
        case R.id.serviceBtn :
        	
        	/*if(!global.isTablet){
        		//스마트폰이면 경고창을 띄우는거 -> 버그 고쳤으므로 지움
        		AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
            	alert_confirm.setMessage("Winklick은 안드로이드 기반 타블렛PC(갤노트 10.1)에 최적화 되어있습니다.\n이 기기는 스마트폰이므로 얼굴 인식이 잘 되지 않을 수 있습니다.\n실행하시겠습니까?").setCancelable(false).setPositiveButton("예",
            	new DialogInterface.OnClickListener() {
            	    @Override
            	    public void onClick(DialogInterface dialog, int which) {
            	        // 'YES'
            	    	startWinklick();
            	    }
            	}).setNegativeButton("아니오",
            	new DialogInterface.OnClickListener() {
            	    @Override
            	    public void onClick(DialogInterface dialog, int which) {
            	        // 'No'
            	    }
            	});
            	AlertDialog alert = alert_confirm.create();
            	alert.show();
        	}else{
        		startWinklick();
        	}*/
        	if(isServiceRunning("me.blog.haj990108.winklick.ActionService")){
        		stopService(new Intent(this, ScreenService.class));
            	stopService(new Intent(this, FaceService.class));
            	stopService(new Intent(this, ActionService.class));
            	
            	Log.i("TAG", "All Service Ended");//기존 잠금화면은 놔두기로 했다. 잠금화면 위에 머신러닝이 뜨므로....
            	serviceBtn.setText(getString(R.string.run));
        	}else{
        		startActivity(new Intent(this, BeforeStartActivity.class));
        		//startWinklick();serviceBtn.setText("End Winklick");//이 줄은 BeforeStartActivity가 대신 해준다.
        	}
        	/*if(!global.isWinklickStarted){
        	global.isWinklickStarted = !global.isWinklickStarted;*/
        	break;
        	
        case R.id.settingsBtn :
        	startActivity(new Intent(this, SettingsActivity.class));
        	/*이렇게 액션서비스를 바로 실행시키면,
        	 * 서비스 종료시 ScreenReceiver.actionService가 아니고 그냥 actionService로 제거해야된다*/
        	
        	break;
        	
        case R.id.testBtn :
        	if(global.isTestQwerty){
        		startActivity(new Intent(this, TestQwertyActivity.class));
        	}else{
        		startActivity(new Intent(this, TestActivity.class));
        	}
        	
        	//여기서 테스트 액티비티 바꾸려면 MainActivity.java/startActivity()랑 ActionService.java/dispatchTouchToTestActivity()를 수정해야...
        	
        	/*ActivityManager context = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
			Toast.makeText(getApplicationContext(),
					"Total Memory = "+(Runtime.getRuntime().totalMemory() / (1024 * 1024)) + "MB" +//6422528 => 6MB
					"\nFree Memory = "+(Runtime.getRuntime().freeMemory() / (1024 * 1024)) + "MB" +//1469736 => 1MB
					"\nMax Memory = "+(Runtime.getRuntime().maxMemory() / (1024 * 1024)) + "MB" +//설정에 따라 getMemoryClass 또는 getLargeMemoryClass 이다.
					"\nAllocated Memory = "+((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)) + "MB"//4953056 => 5.9MB
					//BYTE 단위
					, Toast.LENGTH_LONG).show();
        	
        	Toast.makeText(getApplicationContext(),
        			"Normal Memory = "+context.getMemoryClass()+//96MB
        			"\nLarge Memory = "+context.getLargeMemoryClass()//384MB
        			, Toast.LENGTH_LONG).show();*/
        	
        	/*이렇게 액션서비스를 바로 실행시키면,
        	 * 서비스 종료시 ScreenReceiver.actionService가 아니고 그냥 actionService로 제거해야된다*/
        	
        	break;
        }
            
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if(isServiceRunning("me.blog.haj990108.winklick.ActionService")){
        	serviceBtn.setText(getString(R.string.end));
        }else{
        	serviceBtn.setText(getString(R.string.run));
        }
        // TODO : 설정.
        //SettingsActivity 종료시 메인으로 오면서 다시 resume되므로 결과를 global에 반영하자
        
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        
        //설정창의 결과를 불러오는 parse시에는 꼭!! 기본값을 global로 해야 나중에 global만 수정해서 기본값을 바꾸고 버그가 없다.
        int prevint = 0;//백업용
        try {
        	prevint = global.cursorSpeed;
        	global.cursorSpeed = Integer.parseInt(pref.getString("cursorSpeed", String.valueOf(global.cursorSpeed)));
        	
        	if(global.cursorSpeed <= 0){
        		global.cursorSpeed = prevint;
        		Toast.makeText(getApplicationContext(),
            			getString(R.string.cursor_v_low)
            			, Toast.LENGTH_SHORT).show();
        	}
        } catch (NumberFormatException e) {
        	global.cursorSpeed = prevint;
        	Toast.makeText(getApplicationContext(),
        			getString(R.string.cursor_v_int)
        			, Toast.LENGTH_SHORT).show();
        }
        float prevf = 0;//백업용
        try {
        	prevf = global.updatePeriodSec;
        	global.updatePeriodSec = Float.parseFloat(pref.getString("updatePeriodSec", String.valueOf(global.updatePeriodSec)));
        	
        	if(Math.round(global.updatePeriodSec * 1000)/1000.0 <= 0){
        		global.updatePeriodSec = prevf;
        		Toast.makeText(getApplicationContext(),
        				getString(R.string.prdsec_low)
            			, Toast.LENGTH_SHORT).show();
        	}
        } catch (NumberFormatException e) {
        	global.updatePeriodSec = prevf;
        	Toast.makeText(getApplicationContext(),
        			getString(R.string.prdsec_double)//엔터 잘못 입력하는거는 float에선 자동으로 처리해 준다
        			, Toast.LENGTH_SHORT).show();
        }
        
        
        global.isFaceVisible = pref.getBoolean("isFaceVisible", global.isFaceVisible);
        global.isClickEyeLeft = Boolean.valueOf(pref.getString("isClickEyeLeft", String.valueOf(global.isClickEyeLeft)));
        global.isCursorEyeLeft = Boolean.valueOf(pref.getString("isCursorEyeLeft", String.valueOf(global.isCursorEyeLeft)));//list에서 pref.getString 사용. 작동됨
        global.isTestQwerty = pref.getBoolean("isTestQwerty", global.isTestQwerty);
        
        String langChanged = pref.getString("local", "-");
        if(langChanged.equals("ko")){
        	if(global.local.equals(Locale.US)){
        		Locale lang = Locale.KOREA;
        		Configuration config = new Configuration();
        		config.locale = lang;
        		getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        		global.local = lang;
        		((Activity)mContext).finish();
        		startActivity(new Intent(this, MainActivity.class));
        		//Log.i("TAG", "lang = "+lang);
    		}
        }else if(langChanged.equals("en")){
        	Log.i("TAG", "영어로 바꿈");
        	if(global.local.equals(Locale.KOREA)){
        		Locale lang = Locale.US;
        		Configuration config = new Configuration();
        		config.locale = lang;
        		getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        		global.local = lang;
        		((Activity)mContext).finish();
        		startActivity(new Intent(this, MainActivity.class));
        		//Log.i("TAG", "lang = "+lang);
    		}
        }else if(langChanged == "-"){
        	Log.e("TAG", "응 적용 안돼");
        }
        //Log.d("TAG", "langChanged = "+langChanged);
        //Log.d("TAG", "global.local = "+global.local);
        //Log.d("TAG", "global.local.equals(Locale.KOREA) = "+global.local.equals(Locale.KOREA));
        //Log.d("TAG", "getLocale = "+global.local.toString());
        //Log.i("TAG", "test = "+global.isClickEyeLeft);
    }
    
    public void startWinklick(){
    	
    	
    	startService(new Intent(this, ScreenService.class));    //화면 잠금여부 확인하는 서비스 시작
    	
    	startService(new Intent(this, FaceService.class));	//얼굴 및 동공인식관련 서비스 시작//token null is not for an application
    	//이게 1byteArray의 데이터를 뿔리는게 아니다. 이거 주석처리 해도 렉 걸리더라
    	
    	startService(new Intent(this, ActionService.class));
    	
    	if(global.isTestQwerty){
    		startActivity(new Intent(this, TestQwertyActivity.class));
    	}else{
    		startActivity(new Intent(this, TestActivity.class));
    	}
    	
    	/*if(ScreenReceiver.actionService == null) {
    		ScreenReceiver.actionService = new Intent(getApplicationContext(), ActionService.class);
    		ScreenReceiver.actionService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		startService(ScreenReceiver.actionService);//머신러닝과 커서를 담당하는 액션서비스 시작
    	}*///이거 때문에 잘 안되는거 같다. 메모리 비교 결과 별로 차이가 안나는걸 보아하니 메모리 문제인듯 하다.
    	
    	//System.gc();
    	//GC 켜진다. 이유 알아오자
    	
    	
    	/*Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS 
		| Intent.FLAG_ACTIVITY_FORWARD_RESULT
				    | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
				    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(intent);//홈키 누른것처럼
*/    	
    }
    static boolean isTablet (Context context) { 
        // TODO: This hacky stuff goes away when we allow users to target devices 
        int xlargeBit = 4; // Configuration.SCREENLAYOUT_SIZE_XLARGE;  // upgrade to HC SDK to get this 
        Configuration config = context.getResources().getConfiguration(); 
        return (config.screenLayout & xlargeBit) == xlargeBit; 
    }
    
    public boolean isServiceRunning(String serviceName) {
    	/*serviceName : 매니패스트에서 설정한 서비스의 이름.
    	 * ex ) <service android:name="com.biig.tistory.service.BiigService" > 에서 String serviceName = "com.biig.tistory.service.BiigService";*/
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
}
    
    
    
}
