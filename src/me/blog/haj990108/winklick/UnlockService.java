package me.blog.haj990108.winklick;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class UnlockService extends Service{
	
	//======== 인식2 (FaceService에서 받은 정보로 눈동자 암호 인식 후 잠금해제) ========//
	
	private TextView mPopupView; //항상 보이게할 뷰
	private WindowManager.LayoutParams mParams; //뷰의 위치 및 크기
	private WindowManager mWindowManager;
	private GlobalSettings global;
	private boolean faceExist = false;
	private boolean isWink = false;
	private MyAsyncTask myAsyncTask;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("TAG", "Unlock Service Started");
		
		
		
		mPopupView = new TextView(this);
		mPopupView.setText("UnlockService.java\n인식 2 : 눈동자 암호로 잠금해제\n이 뷰는 항상 위에 있다.\nㅁㄴㅇㄹ\nㅁㄴㅇㄹ\nㅁㄴㅇㄹ\nㅁㄴㅇㄹ");
		mPopupView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		mPopupView.setTextColor(Color.BLUE);
		mPopupView.setBackgroundColor(Color.argb(128, 0, 255, 255));
		
		mParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, //포커스를 가지지 않음
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, //타임아웃으로 잠금 안됨
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | //잠금시 잠금화면보다 앞에(brocast 받아야함)
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,// 기존 잠금화면 무시
				PixelFormat.TRANSLUCENT);
		
		mParams.gravity = Gravity.LEFT | Gravity.TOP; //좌표계가 좌상점을 중심으로..
		
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mWindowManager.addView(mPopupView, mParams); //SYSTEM_ALERT_WINDOW permission 필요
		
		
		global = (GlobalSettings) getApplicationContext();//super.onCreate()뒤에 넣자
		
		
		myAsyncTask = new MyAsyncTask();
		UnlockServiceMethod m = new UnlockServiceMethod();
		myAsyncTask.execute(0, m);
		
		/*
		 * 지금 해야되는거 = global.getFaceExist()를 통해 얼굴 발견시 진동울리게...
		 */
		
		//mPopupView.setOnTouchListener(mViewTouchListener);//뷰클릭 리스너
	}
	
	protected class UnlockServiceMethod {
		protected void checkFaceStatus(){
			//얼굴 발견시 진동. 잠금해제 액션을 얼굴 찾는동안 계속 수행하는 것을 방지.
			if(global.isFaceExist){
				if(!faceExist){
					faceExist = true;
					vibrate();
					Log.i("TAG", "found the face! -> vibrate!!");
				}
			}else{
				faceExist = false;
				Log.i("TAG", "found you winked! -> wake the device!!");
			}
			
			if(global.getIsWink()){
				if(!isWink){
					isWink = true;
					wakeDevice();
					Log.i("TAG", "global.getFaceExist = true, faceExist = true");
				}
			}else{
				isWink = false;
				Log.i("TAG", "global.getFaceExist = false, faceExist = false");
			}
		}
	}
	
	
	@Override
	public void onDestroy() {
		faceExist = false;
		Log.i("TAG", "global.getFaceExist = ??, faceExist = false");
		//global.faceExist바꾸는거는 어쩌피 end Winklick눌러서 FaceService 종료되지 않는한 서비스에서 업데이트하므로 무용지물
		myAsyncTask.cancel(true);
		Log.i("TAG", "Unlock Service Ended");
		if (mWindowManager != null) { //서비즈 종료시 꼭 뷰를 제거하자.
			if (mPopupView != null)
				mWindowManager.removeView(mPopupView);
		}
		super.onDestroy();
	}
	
	@SuppressLint("Wakelock") @SuppressWarnings("deprecation")
	protected void wakeDevice(){
		Log.i("TAG", "--waked up the device due to the face rec.");
    	PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
    	WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
    	wakeLock.acquire();
    }
	
	protected void vibrate(){
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    	//vibe.vibrate(5000);//우우우웅~
    	vibe.vibrate(new long[] {50, 90, 50, 90}, -1);//웅웅! long은 멈춤-진동-멈춤-진동....
    	//vibe.cancel();
	}
}
