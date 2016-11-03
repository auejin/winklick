package me.blog.haj990108.winklick;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class EyeSettingService extends Service{
	
	//======== 사용안합니다 ========//
	
	private TextView mPopupView;//항상 보이게할 뷰
	private WindowManager.LayoutParams mParams; //뷰의 위치 및 크기
	private WindowManager mWindowManager;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("TAG", "EyeSetting Service Started");
		
		mPopupView = new TextView(this);
		mPopupView.setText("사용안함");
		mPopupView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		mPopupView.setTextColor(Color.BLUE);
		mPopupView.setBackgroundColor(Color.argb(128, 0, 255, 255));
		
		mParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE, //항상 최상위. 터치 이벤트 안받기 가능.
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | //포커스를 가지지 않음
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, //타임아웃으로 잠금 안됨
				PixelFormat.TRANSLUCENT);
		mParams.gravity = Gravity.LEFT | Gravity.TOP; //좌표계가 좌상점을 중심으로..
		
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mWindowManager.addView(mPopupView, mParams); //SYSTEM_ALERT_WINDOW permission 필요
		
		//mPopupView.setOnTouchListener(mViewTouchListener);//뷰클릭 리스너
	}
	
	/*private onTouchListener mViewTouchListener = new onTouchListener() {
    @Override public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:                //사용자 터치 다운이면
                if(MAX_X == -1)
                    setMaxPosition();
                START_X = event.getRawX();                    //터치 시작 점
                START_Y = event.getRawY();                    //터치 시작 점
                PREV_X = mParams.x;                            //뷰의 시작 점
                PREV_Y = mParams.y;                            //뷰의 시작 점
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int)(event.getRawX() - START_X);    //이동한 거리
                int y = (int)(event.getRawY() - START_Y);    //이동한 거리
                
                //터치해서 이동한 만큼 이동 시킨다
                mParams.x = PREV_X + x;
                mParams.y = PREV_Y + y;
                
                optimizePosition();        //뷰의 위치 최적화
                mWindowManager.updateViewLayout(mPopupView, mParams);    //뷰 업데이트
                break;
        }
        
        return true;
    }
};*/
	
	@Override
	public void onDestroy() {
		Log.i("TAG", "EyeSetting Service Ended");
		if (mWindowManager != null) { //서비즈 종료시 꼭 뷰를 제거하자.
			if (mPopupView != null) mWindowManager.removeView(mPopupView);
		}
		super.onDestroy();
	}

}
