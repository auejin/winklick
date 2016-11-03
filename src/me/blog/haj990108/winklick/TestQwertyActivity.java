package me.blog.haj990108.winklick;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TestQwertyActivity extends Activity implements OnClickListener{
	
	private TextView textView;
	public String textViewStr = "";
	public int totalWriteNum=0;
	private Boolean isShift = true;
	private static String[] str = {
		"1","2","3","4","5","6","7","8","9","0",
		"q","w","e","r","t","y","u","i","o","p",
		"a","s","d","f","g","h","j","k","l",
		"z","x","c","v","b","n","m",
		"Q","W","E","R","T","Y","U","I","O","P",
		"A","S","D","F","G","H","J","K","L",
		"Z","X","C","V","B","N","M", "Shift", "Space", "Back"};
	//private Button[] buttons = new Button[38];
	public static Context mContext;
	
	private Button buttonShift;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qwerty_test);
        mContext = this;
        textView = (TextView)findViewById(R.id.textView);
        
        //현재 testActivity는 좌상점이 원점이다. 반면, actionService는 중점이 원점이다.
        //효과xdialTextView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);//좌표계가 좌상점을 중심으로..
        //효과x//mParams.gravity = Gravity.CENTER | Gravity.CENTER; //좌표계가 좌상점을 중심으로..
        
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
        findViewById(R.id.button0).setOnClickListener(this);
        
        findViewById(R.id.buttonQ).setOnClickListener(this);
        findViewById(R.id.buttonW).setOnClickListener(this);
        findViewById(R.id.buttonE).setOnClickListener(this);
        findViewById(R.id.buttonR).setOnClickListener(this);
        findViewById(R.id.buttonT).setOnClickListener(this);
        findViewById(R.id.buttonY).setOnClickListener(this);
        findViewById(R.id.buttonU).setOnClickListener(this);
        findViewById(R.id.buttonI).setOnClickListener(this);
        findViewById(R.id.buttonO).setOnClickListener(this);
        findViewById(R.id.buttonP).setOnClickListener(this);//10

        findViewById(R.id.buttonA).setOnClickListener(this);
        findViewById(R.id.buttonS).setOnClickListener(this);
        findViewById(R.id.buttonD).setOnClickListener(this);
        findViewById(R.id.buttonF).setOnClickListener(this);
        findViewById(R.id.buttonG).setOnClickListener(this);
        findViewById(R.id.buttonH).setOnClickListener(this);
        findViewById(R.id.buttonJ).setOnClickListener(this);
        findViewById(R.id.buttonK).setOnClickListener(this);
        findViewById(R.id.buttonL).setOnClickListener(this);//9

        findViewById(R.id.buttonZ).setOnClickListener(this);
        findViewById(R.id.buttonX).setOnClickListener(this);
        findViewById(R.id.buttonC).setOnClickListener(this);
        findViewById(R.id.buttonV).setOnClickListener(this);
        findViewById(R.id.buttonB).setOnClickListener(this);
        findViewById(R.id.buttonN).setOnClickListener(this);
        findViewById(R.id.buttonM).setOnClickListener(this);//7
        
        buttonShift = (Button) findViewById(R.id.buttonShift);
        buttonShift.setOnClickListener(this);
        findViewById(R.id.buttonSpace).setOnClickListener(this);
        findViewById(R.id.buttonBack).setOnClickListener(this);//3
        
        findViewById(R.id.buttonBack).setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
            	textViewStr = "";
            	textView.setText("");
                return true;
            }
        });
	}

	private Button findBtnByName(String name) {
		Button b = null;
		int id = getResources().getIdentifier("button"+name, "id", getPackageName());
		b = (Button) findViewById(id);
		if(b == null) Log.e("TAG", "NULL @ findBtnByName");
		return b;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		for(int i=0; i<str.length; i++){
			if(getResources().getResourceEntryName(v.getId()).equals("button" + str[i]))
			{
				Log.d("TAG", "push : "+str[i]);
				if(i == str.length - 3){//쉬프트키
					isShift = !isShift;
					if(isShift){
						for(int j=10 + 26; j<str.length - 3; j ++) {//text만 바뀌지 id는 안바뀜
							findBtnByName(str[j]).setText(str[j]);
						}
						buttonShift.setText("↑o");
					}else{
						for(int j=10 + 26; j<str.length - 3; j ++) {
							int k = j;
							findBtnByName(str[j]).setText(str[k - 26]);
						}
						buttonShift.setText("↑x");
					}
				}else if(i == str.length - 2){//스페이스바
					writeText(" ");
				}else if(i == str.length - 1){//지우기
					if(textViewStr.length() > 0)
						textViewStr = textViewStr.substring(0, textViewStr.length() - 1);
					textView.setText(textViewStr);
				}else{
					int k = i;
					if(!isShift && i > 35) k -= 26;//알파벳 26개
					writeText(str[k]);//보이는거 그대로 입력
				}
				
			}
		}
	}
	
	/*@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if(event.getAction()== MotionEvent.ACTION_DOWN) {
			Toast.makeText(getApplicationContext(),
					"ACTION_DOWN"
					, Toast.LENGTH_SHORT).show();
		}
		return true;
	}*///터치이벤트 과정에서 얘가 맨 마지막에 호출되므로, 액티비티가 아닌 뷰나 버튼에 터치가 되면 걔내들이 return되버려서 제일 마지막인 onToucEvent가 false가 됨.//http://stackoverflow.com/questions/11001282/
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
	   int action=event.getAction();
	    switch(action) {
	      case MotionEvent.ACTION_DOWN:
	          //code
	    	  /*Toast.makeText(getApplicationContext(),
						"ACTION_DOWN"
						, Toast.LENGTH_SHORT).show();*/
	          break;
	      default:
	          break;
	    }
	    //Log.d("TAG", "event = "+event);
	    return super.dispatchTouchEvent(event);
	}
	
	@SuppressLint("NewApi") private void writeText(String str) {
		textViewStr += str; totalWriteNum++;
		textView.setText(textViewStr);
	}
	
	
}
