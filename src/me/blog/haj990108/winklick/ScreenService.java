package me.blog.haj990108.winklick;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ScreenService extends Service {
	
	private ScreenReceiver mReceiver = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("TAG", "ScreenService Created");
		mReceiver = new ScreenReceiver();
		registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
		
		if(intent != null){
			if(intent.getAction()==null){
				if(mReceiver==null){
					mReceiver = new ScreenReceiver();
					registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
					registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
				}
			}
		}
		return START_REDELIVER_INTENT;
	}
	
	@Override
	public void onDestroy(){		 	
		super.onDestroy();
		Log.i("TAG", "ScreenService Destroyed");
		if(mReceiver != null){
			unregisterReceiver(mReceiver);
		}
	}
}
