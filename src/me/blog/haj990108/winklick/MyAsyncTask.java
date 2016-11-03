package me.blog.haj990108.winklick;

import me.blog.haj990108.winklick.UnlockService.UnlockServiceMethod;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class MyAsyncTask extends AsyncTask<Object, Void, Void> {
	
	private static int NAPTIME = 100;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	/* publishProgress() 메서드를 통해 호출. 진행사항을 표시하는데에 쓰임
	@Override
    protected void onProgressUpdate(Integer... progress)
    {
      mProgress.setProgress(progress[0]);//mProgress는 프로그레스
    }*/
	
	@Override
	protected Void doInBackground(Object... params) {
		
		Integer TYPE = (Integer) params[0];
		
		switch(TYPE){
		case 0:
			UnlockServiceMethod unlockM = (UnlockServiceMethod)params[1];
			while(!this.isCancelled()){
				try
				{
					unlockM.checkFaceStatus();
					Thread.sleep(NAPTIME);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				
			}
			
			break;
		}
		
        return null;
		//return이 되면 자동종료. 리턴전에 종료하고싶으면 myAsyncTask.cancel(true);로 하고 myAsyncTask.isCancelled()로 재확인
	}
	
	//작업완료
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		Log.i("TAG", "myAsyncTask onPostExecute() - ended safely");
		if(result != null){
			Log.d("TAG", "result = " + result);
		}
		
	}
	
	//작업중 강제종료
	@Override
	protected void onCancelled() {
		super.onCancelled();
		Log.i("TAG", "myAsyncTask onCancelled() - cancelled by force");
	}
	
	
	
}
