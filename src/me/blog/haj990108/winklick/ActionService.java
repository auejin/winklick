//API 17
package me.blog.haj990108.winklick;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.app.Service;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


public class ActionService extends Service{
	
	//======== 인식1B (FaceService에서 받은 정보로 터치환경) ========//
	
	
	
	private TextView mPopupView;//항상 보이게할 뷰
	private ImageView mCursorView;
	private WindowManager.LayoutParams mParams; //뷰의 위치 및 크기
	private WindowManager mWindowManager;
	private int prevStatus;
	//private Thread checkLearn_course;
	//private Thread cursorThread;
	private TimerTask cursorTimerTask;
    private Timer mTimer;
    private TimerTask checkLearnCourseTimerTask;
	private boolean wasPressed;
	private long mDownTime;
    
	private static int SCREEN_WIDTH;
	private static int SCREEN_HEIGHT;
	private static int UPDATE_PERIOD;
	
	
	private GlobalSettings global;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("TAG", "Action Service Started");
		global = (GlobalSettings) getApplicationContext();
		
		wasPressed = false;
		
		mPopupView = new TextView(this);
		mPopupView.setText("ActionService.java (잠금해제+커서)\n-");
		mPopupView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		mPopupView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		mPopupView.setTextColor(Color.argb(255, 255, 255, 0));
		mPopupView.setBackgroundColor(Color.argb(255, 170, 0, 106));
		mPopupView.setPadding(30, 30, 30, 30);
		//mPopupView.setShadowLayer(1.5f, 2, 2, Color.argb(255, 67, 178, 131));
		//Color.LTGRAY
		//mPopupView.setShadowLayer(1f, 2, 2, Color.BLACK);
		
		mCursorView = new ImageView(this);
		mCursorView.setImageResource(R.xml.cursor);
		mCursorView.setBackgroundColor(Color.TRANSPARENT);
		mCursorView.setImageResource(R.drawable.cursor_norm);
		
		
		//FaceService.java와 params설정을 같게 해야 한다. 일심동체!
		mParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,//w
				WindowManager.LayoutParams.WRAP_CONTENT,//h
				
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,//_type
				
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON/*&
				SCREEN_ON
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED &
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON &
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD &
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON */,//_flags
				PixelFormat.TRANSLUCENT);//_format
		//(int w, int h, int _type, int _flags, int _format)
		
		mParams.gravity = Gravity.CENTER | Gravity.CENTER; //좌표계가 좌상점을 중심으로..
		
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mWindowManager.addView(mPopupView, mParams); //SYSTEM_ALERT_WINDOW permission 필요
		
		
		//=====여기에서 에러의 원인이 있다=====//
		Point size = new Point();
    	mWindowManager.getDefaultDisplay().getSize(size);//API 13 이상
    	SCREEN_WIDTH = size.x;
    	SCREEN_HEIGHT = size.y;
    	
    	UPDATE_PERIOD = Math.round(global.updatePeriodSec * 1000);
    	
    	mTimer = new Timer();
    	
    	cursorTimerTask = new TimerTask() {
            @Override
            public void run() {
            	new Handler(Looper.getMainLooper()).post(new Runnable() { // new Handler and Runnable
    	            @Override
    	            public void run() {
    	            	if(global.getMove(true) != 0 && global.getMove(false) != 0){
    	            		Log.d("MOVE", "x = "+global.getMove(true)+", y = "+global.getMove(false));
    	            	}
    	            	
    	            	moveCursor(global.getMove(true), global.getMove(false));
    	            	
    	            	int[] location = new int[2];//절대좌표계(중점에서 좌상점을 원점으로 바꿈)
    	            	mCursorView.getLocationOnScreen(location);
    	            	moveClick(global.getIsWink(), location[0] + mCursorView.getWidth()/2, location[1] + mCursorView.getHeight()/2);//커서->위치정보->클릭순!!
    	            	
    	            	
    	            	
    		        	mWindowManager.updateViewLayout(mCursorView, mParams);
    		        	//클릭시 down으로 처리하자
    	            }
    	    	});
            }
        };
         
        prevStatus=-1;
        
        checkLearnCourseTimerTask = new TimerTask() {

			@Override
			public void run() {
				switch(global.getLearn_course()){
	            case 0:
	            	if(prevStatus!=0){
	            		changePopupText(getString(R.string.as_mid));
	            		prevStatus=global.getLearn_course();
	            	}
	            	break;
	            case 1:
	            	if(prevStatus!=1){
	            		if(global.isClickEyeLeft){
		            		changePopupText("( > _ I )\n"+getString(R.string.as_lWink));
		            	}else{
		            		changePopupText("( I _ < )\n"+getString(R.string.as_rWink));
		            	}
	            		prevStatus=global.getLearn_course();
	            	}
	            	break;
	            case 2:
	            	if(prevStatus!=2){
	            		changePopupText(getString(R.string.as_up));
	            		prevStatus=global.getLearn_course();
	            	}
	            	break;
	            case 3:
	            	if(prevStatus==2){
	            		changePopupText(getString(R.string.as_down));
	            		prevStatus=global.getLearn_course();
	            	}
	            	break;
	            case 4:
	            	mWindowManager.removeView(mPopupView);
	            	checkLearnCourseTimerTask.cancel();
	            	new Handler(Looper.getMainLooper()).post(new Runnable() { // new Handler and Runnable
	    	            @Override
	    	            public void run() {
	    	            	mWindowManager.addView(mCursorView, mParams);
	    	            	
	    	            	mTimer.schedule(cursorTimerTask, 0, UPDATE_PERIOD);
	    	            	//cursorThread.start();//메인 스레드에서 커서 스레드 시작
	    	            }
	    	    	});//addView는 메인 스레드에서만!
	            	break;
	            }
	        }
        };
        mTimer.schedule(checkLearnCourseTimerTask, 0, UPDATE_PERIOD);
		//=====여기에서 에러의 원인이 있다=====//
		//이게 머신러닝이 끝날 때 interrupt()가 되는데, 끝나기 전에 End Winklick을 해도 오류가 생기므로 mTask, mTimer는 문제의 원인이 아닌 것 같다.
		//그렇다면, 내 생각엔 Thread가 너무 자주 발생해서 렉이 걸리는거 같으니 이걸 Timer Event로 바꾸자
	}
	
	
	
	 private void changePopupText(final String message){
	    	new Handler(Looper.getMainLooper()).post(new Runnable() { // new Handler and Runnable
	            @Override
	            public void run() {
	            	//mPopupView.setText("ActionService.java (잠금해제+커서)\n"+message);
	            	mPopupView.setText(getString(R.string.as_base)+message);
	            }
	    	});
	    }
	
	@Override
	public void onDestroy() {
		Log.i("TAG", "Action Service Ended");
		
		checkLearnCourseTimerTask.cancel();
		cursorTimerTask.cancel();
		mTimer.cancel();
		
		
		if (mWindowManager != null) { //서비즈 종료시 꼭 뷰를 제거하자.
			if (mPopupView.isShown()) mWindowManager.removeView(mPopupView);
			if (mCursorView.isShown()) mWindowManager.removeView(mCursorView);//허니콤 이상 사용가능
			
			
			//Log.d("TAG", "false = "+mPopupView.isShown()+" = "+mCursorView.isShown());//버그 나든 말든 false = false = false
			
			//러닝후 제거 = 커서제거안됨
			//러닝전 제거 = 팝업제거안됨//removeView는 잘됨. isActivated()항상 거짓, isEnableed()항상 참
			
		}
		
		super.onDestroy();
	}
	
	
	//private InstrumentationTestCase uiAutoClass = new InstrumentationTestCase();
	private void moveClick(boolean isClick, int x, int y){
		long eventTime = SystemClock.uptimeMillis();//downTime = SystemClock.uptimeMillis()
		if(isClick){
			mCursorView.setImageResource(R.drawable.cursor_clicked);
			//Log.d("TAG", "TOUCH.x = "+x+", TOUCH.y = "+y);
			if(wasPressed){
				//move
				MotionEvent down_event = MotionEvent.obtain(mDownTime, eventTime,   MotionEvent.ACTION_MOVE,x,y,0);
				dispatchTouchToTestActivity(down_event);
				down_event.recycle();
			}else{
				//down
				mDownTime = eventTime;
				wasPressed = true;
				MotionEvent down_event = MotionEvent.obtain(mDownTime, eventTime,   MotionEvent.ACTION_DOWN,x,y,0);
				dispatchTouchToTestActivity(down_event);
				down_event.recycle();
			}
			
			
			
			// TODO : 터치이벤트 넣는곳
			/*final UiAutomation uiAutomation = uiAutoClass.getInstrumentation().getUiAutomation();
			//final UiAutomation uiAutomation = new Instrumentation().getUiAutomation();
			//NULL_POINTER_EXCEPTION
			
			final long eventTime = SystemClock.uptimeMillis();
	
		    //A typical click event triggered by a user click on the touchscreen creates two MotionEvents,
		    //first one with the action KeyEvent.ACTION_DOWN and the 2nd with the action KeyEvent.ACTION_UP
		    MotionEvent motionDown = MotionEvent.obtain(eventTime, eventTime, KeyEvent.ACTION_DOWN,
		            x,  y, 0); 
		    //We must set the source of the MotionEvent or the click doesn't work.
		    motionDown.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		    uiAutomation.injectInputEvent(motionDown, true);//여기에서 nullPointerException 뜬다.
		    MotionEvent motionUp = MotionEvent.obtain(eventTime, eventTime, KeyEvent.ACTION_UP,
		            x, y, 0);
		    motionUp.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		    uiAutomation.injectInputEvent(motionUp, true);
		    //Recycle our events back to the system pool.
		    motionUp.recycle();
		    motionDown.recycle();
			////uiautomationTouch(x, y);//instrumentTouch(x,y);
			////dispatchTouch(x, y, mCursorView);*/
		}else{
			if(wasPressed){
				//up
				wasPressed = false;
				MotionEvent up_event = MotionEvent.obtain(mDownTime, eventTime,   MotionEvent.ACTION_UP,x,y,0);
				dispatchTouchToTestActivity(up_event);
				up_event.recycle();
			}
			mCursorView.setImageResource(R.drawable.cursor_norm);
		}
	}
	
	private void moveCursor(int x, int y){
		//getMove도 속도임. 윈점이 한가운데임을 잊지말자//오른쪽+, 아래+
		/*mParams.x += x;
		mParams.y += y;*/
		
		if(mParams.x + x >= SCREEN_WIDTH/2){
			mParams.x = SCREEN_WIDTH/2;
		}else if(mParams.x + x <= -SCREEN_WIDTH/2){
			mParams.x = -SCREEN_WIDTH/2;
		}else{
			mParams.x += x;
		}
		if(mParams.y + y >= SCREEN_HEIGHT/2){
			mParams.y = SCREEN_HEIGHT/2;
		}else if(mParams.y + y <= -SCREEN_HEIGHT/2){
			mParams.y = -SCREEN_HEIGHT/2;
		}else{
			mParams.y += y;
		}
	}
	
	private void pressETCKey(final int key){
		//UnlickService.java에서 wakeDevice(), vibrate() 는 잠금해제에만 쓰임. 참고하자
		new Thread(new Runnable() {         
		       public void run() {
		    	   new Instrumentation().sendKeyDownUpSync(key);
		    	   
		    	   //new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT);
		    	   //new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
		    	   //new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
		    	   //new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		    	   //new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);//DPAD이벤트. 키보드 이벤트 비슷.. focuseAble만 찾아서 선택 홈화면에서만 되고 외부앱에서는 이걸 안받는다.
		    	   
		           //new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);//됨
		           //new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_UP);//됨
		           //new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN);//됨
		           //new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_POWER);//안됨
		           
		       }   
		    }).start();
	}
	
	private void pressHomeKey(){
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS 
		| Intent.FLAG_ACTIVITY_FORWARD_RESULT
				    | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
				    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(intent);
	}
	
	
	//Manifest에서 android:minSdkVersion="17" -> android:minSdkVersion="18"
	/*public class UIAutomationTest extends InstrumentationTestCase {
		private Instrumentation instr;
		public UIAutomationTest() {
		    instr = getInstrumentation();
		    Log.d("TAG", "UIAutomationTest instrumentation: " + instr);
		}
		
		@Override
		protected void setUp() throws Exception {super.setUp();

		    instr = getInstrumentation();
		    Log.d("TAG", "setUp instrumentation: " + instr);
		}
		@MediumTest
		public void injectClickEvent(int x, int y) throws Exception {
			final UiAutomation uiAutomation = instr.getUiAutomation();

			final long eventTime = SystemClock.uptimeMillis();

		    //A typical click event triggered by a user click on the touchscreen creates two MotionEvents,
		    //first one with the action KeyEvent.ACTION_DOWN and the 2nd with the action KeyEvent.ACTION_UP
		    MotionEvent motionDown = MotionEvent.obtain(eventTime, eventTime, KeyEvent.ACTION_DOWN,
		            x,  y, 0); 
		    //We must set the source of the MotionEvent or the click doesn't work.
		    motionDown.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		    uiAutomation.injectInputEvent(motionDown, true);
		    MotionEvent motionUp = MotionEvent.obtain(eventTime, eventTime, KeyEvent.ACTION_UP,
		            x, y, 0);
		    motionUp.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		    uiAutomation.injectInputEvent(motionUp, true);
		    //Recycle our events back to the system pool.
		    motionUp.recycle();
		    motionDown.recycle();
		}
	}*/
	
	
	/* //API 18 이상!!! 중요!
	 * private void uiautomationTouch(int x, int y){
		Instrumentation i = new Instrumentation();
				//new Instrumentation();
		final UiAutomation automation = i.getUiAutomation();//err
		//UiAutomation claims to allow you to interact with other applications by leveraging the Accessibility Framework,
		//즉 외부 패키지 = 외부 앱 사용가능!!!!! https://www.youtube.com/watch?v=_SlBHUW0ybM
		
		//A MotionEvent is a type of InputEvent.  
	    //The event time must be the current uptime.
	    final long eventTime = SystemClock.uptimeMillis();

	    //A typical click event triggered by a user click on the touchscreen creates two MotionEvents,
	    //first one with the action KeyEvent.ACTION_DOWN and the 2nd with the action KeyEvent.ACTION_UP
	    MotionEvent motionDown = MotionEvent.obtain(eventTime, eventTime, KeyEvent.ACTION_DOWN,
	            x,  y, 0); 
	    //We must set the source of the MotionEvent or the click doesn't work.
	    motionDown.setSource(InputDevice.SOURCE_TOUCHSCREEN);
	    automation.injectInputEvent(motionDown, true);
	    MotionEvent motionUp = MotionEvent.obtain(eventTime, eventTime, KeyEvent.ACTION_UP,
	            x, y, 0);
	    motionUp.setSource(InputDevice.SOURCE_TOUCHSCREEN);
	    automation.injectInputEvent(motionUp, true);
	    //Recycle our events back to the system pool.
	    motionUp.recycle();
	    motionDown.recycle();
	}*/
	
	private void instrumentTouch(int x, int y){
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		
		final MotionEvent event = MotionEvent.obtain(downTime, eventTime,
		MotionEvent.ACTION_DOWN, x, y, 0);//메인 스레드에선 사용 불가!!! ->스레드 돌리므로 final
		
		final MotionEvent event2 = MotionEvent.obtain(downTime, eventTime,
		MotionEvent.ACTION_UP, x, y, 0);
		
		event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		event2.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		new Thread(new Runnable() {

           private Handler handler;

           @Override
           public void run() {
               Looper.prepare();
               handler = new Handler();
               handler.post(new Runnable() {

                   @Override
                   public void run() {
                		new Instrumentation().sendPointerSync(event);
                       try {
                           Thread.sleep(750);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                		new Instrumentation().sendPointerSync(event2);
                   }
               });
               Looper.loop();
           }
       }).start();
		
		event.recycle();
		event2.recycle();
	}
	
	private void dispatchTouch(int x, int y, View view){
		// Obtain MotionEvent object
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis() + 100;
		// List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
		int metaState = 0;
		MotionEvent motionEvent = MotionEvent.obtain(
		    downTime, 
		    eventTime, 
		    MotionEvent.ACTION_UP, 
		    x, 
		    y, 
		    metaState
		);

		// Dispatch touch event to view
		view.dispatchTouchEvent(motionEvent);
		motionEvent.recycle();
	}
	
	private void dispatchTouchToTestActivity(MotionEvent ev){
		//TODO dispatchTouchToTestActivity
		Activity testActivity;
		if(global.isTestQwerty) {
			testActivity = (Activity) TestQwertyActivity.mContext;
		}else{
			testActivity = (Activity) TestActivity.mContext;
		}
		Log.d("TAG","testActivity = "+testActivity);
		testActivity.dispatchTouchEvent(ev);
	}
	
	private String topActivityPackageName(){
		String rtn;
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			final Set<String> activePackages = new HashSet<String>();
			  final List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
			  for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
			    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
			      activePackages.addAll(Arrays.asList(processInfo.pkgList));
			    }
			  }
			  return activePackages.toArray(new String[activePackages.size()])[0];
		}else{
			
		    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);//안드L에선 작동x
		    //Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
		    ComponentName componentInfo = taskInfo.get(0).topActivity;//액티비티의 클래스 주소
		    return componentInfo.getPackageName();//앱 패키지 주소만 나옴
		}
	}
	

}
