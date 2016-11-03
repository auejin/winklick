package me.blog.haj990108.winklick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver{
	
	
	
	public static Intent unlockService, actionService; //static은 정적변수로 위치를 고정하므로 타 클래스와의 공유가 가능.
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// 브로캐스트를 ScreenService를 통해 받으면 여기서 자동으로 리시버된 액션 실행
		// 다른 것과 다르게, ScreenService로 동적으로 브로캐스트리시버를 등록해야 함
		// IntentFilter를 추가하려면 ScreenService에서 두번 추가하세얀
		// 그래도 이건 매 작동시마다 처리해주니 서비스보다 여기에 액션을 넣는게 개이득
		
		
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {	//스크린이 꺼지면
			Log.i("TAG", "ScreenReceiver-ACTION_SCREEN_OFF");
			
			if(ScreenReceiver.actionService != null){
				context.stopService(actionService);
				actionService = null;
			}//잠금시 제거되고 잠금해제시 작동됨
			
			unlockService = new Intent(context, UnlockService.class);//인식2 실행
			unlockService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//context.startActivity(i);
			context.startService(unlockService);
		}
		if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {	//스크린이 켜지면
			Log.i("TAG", "ScreenReceiver-ACTION_SCREEN_ON");
			
			context.stopService(unlockService);//인식2 종료
			if(ScreenReceiver.actionService == null) {
				actionService = new Intent(context, ActionService.class);//인식1 실행
				actionService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startService(actionService);
        	}
		}	
	}
	
}
