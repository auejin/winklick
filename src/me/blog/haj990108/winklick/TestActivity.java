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

public class TestActivity extends Activity implements OnClickListener{
	
	private TextView dialTextView;
	public String dialNum = "";
	public int totalWriteNum=0;
	public static Context mContext;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mContext = this;
        dialTextView = (TextView)findViewById(R.id.dialTextView);
        
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
        findViewById(R.id.buttonSharp).setOnClickListener(this);
        findViewById(R.id.buttonStar).setOnClickListener(this);
        findViewById(R.id.buttonBack).setOnClickListener(this);
        findViewById(R.id.buttonBack).setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
            	dialNum = "";
            	dialTextView.setText("");
                return true;
            }
        });
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int view = v.getId();
		switch(view){
		case R.id.button1 :
			writeDial("1");
			break;
		case R.id.button2 :
			writeDial("2");
			break;
		case R.id.button3 :
			writeDial("3");
			break;
		case R.id.button4 :
			writeDial("4");
			break;
		case R.id.button5 :
			writeDial("5");
			break;
		case R.id.button6 :
			writeDial("6");
			break;
		case R.id.button7 :
			writeDial("7");
			break;
		case R.id.button8 :
			writeDial("8");
			break;
		case R.id.button9 :
			writeDial("9");
			break;
		case R.id.buttonSharp :
			writeDial("#");
			break;
		case R.id.button0 :
			writeDial("0");
			break;
		case R.id.buttonStar :
			writeDial("*");
			break;
		case R.id.buttonBack :
			if(dialNum.length() > 0)
				dialNum = dialNum.substring(0, dialNum.length() - 1);
			dialTextView.setText(PhoneNumberUtils.formatNumber(dialNum));
			break;
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
	    Log.d("TAG", "event = "+event);
	    return super.dispatchTouchEvent(event);
	}
	
	@SuppressLint("NewApi") private void writeDial(String str) {
		dialNum += str; totalWriteNum++;
		
		if (android.os.Build.VERSION.SDK_INT >= 21) {
			Log.i("TAG", "lollipop");
			dialTextView.setText(PhoneNumberUtils.formatNumber(dialNum, Locale.getDefault().getCountry()));
		}else{
			dialTextView.setText(PhoneNumberUtils.formatNumber(dialNum));
		}
		/*Toast.makeText(getApplicationContext(),
				"Clicked Button"+str
				, Toast.LENGTH_SHORT).show();*/
	}
	
	
}
