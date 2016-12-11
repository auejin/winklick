package me.blog.haj990108.winklick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class BeforeStartActivity extends Activity implements OnClickListener{
	private ViewPager mPager;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.before_start);
        setLayout();
        

		Log.i("TAG", "alert window (미니화면) 권한 있니?");
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
		           && !Settings.canDrawOverlays(getApplicationContext())) {
			
			Log.d("TAG", "alert window 권한 쇼부 보는중..");
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
		    startActivityForResult(intent, 1);
		    Toast.makeText(getApplicationContext(),
        			getString(R.string.alert_window_permission_denied)
        			, Toast.LENGTH_LONG).show();

		} else {
			Log.i("TAG", "alert window 권한 이미 있어!");
			
			// TODO : start activity
		}
        
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new PagerAdapterClass(getApplicationContext()));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
        case R.id.btn_one:
            setCurrentInflateItem(0);
            break;
        case R.id.btn_two:
            setCurrentInflateItem(1);
            break;
        case R.id.btn_three:
            setCurrentInflateItem(2);
            break;
        }
	}
	
	private void setCurrentInflateItem(int type){
        if(type==0){
            mPager.setCurrentItem(0);
        }else if(type==1){
            mPager.setCurrentItem(1);
        }else{
            mPager.setCurrentItem(2);
        }
    }
     
    private Button btn_one;
    private Button btn_two;
    private Button btn_three;//노트1~3 바로가기 버튼
     
    /**
     * Layout
     */
    private void setLayout(){
        btn_one = (Button) findViewById(R.id.btn_one);
        btn_two = (Button) findViewById(R.id.btn_two);
        btn_three = (Button) findViewById(R.id.btn_three);
         
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        btn_three.setOnClickListener(this);
    }
     
    private View.OnClickListener mPagerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	int view = v.getId();
            
            switch(view){
            case R.id.buttonReadAllNotes :
            	MainActivity.serviceBtn.setText("End Winklick");
            	((MainActivity)MainActivity.mContext).startWinklick();
            	BeforeStartActivity.this.finish();
            	break;
            }
            //String text = ((Button)v).getText().toString();
            //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    };
     
    /**
     * PagerAdapter 
     */
	
	private class PagerAdapterClass extends PagerAdapter{
        
        private LayoutInflater mInflater;
 
        public PagerAdapterClass(Context c){
            super();
            mInflater = LayoutInflater.from(c);
        }
         
        @Override
        public int getCount() {
            return 3;
        }
 
        @Override
        public Object instantiateItem(View pager, int position) {
            View v = null;
            if(position==0){
                v = mInflater.inflate(R.layout.inflate_one, null);
                v.findViewById(R.id.iv_one);
                //v.findViewById(R.id.btn_click).setOnClickListener(mPagerListener);
            }
            else if(position==1){
                v = mInflater.inflate(R.layout.inflate_two, null);
                v.findViewById(R.id.iv_two);
                //v.findViewById(R.id.btn_click_2).setOnClickListener(mPagerListener);
            }else{
                v = mInflater.inflate(R.layout.inflate_three, null);
                v.findViewById(R.id.iv_three); 
                v.findViewById(R.id.buttonReadAllNotes).setOnClickListener(mPagerListener);
            }
             
            ((ViewPager)pager).addView(v, 0);
             
            return v; 
        }
 
        @Override
        public void destroyItem(View pager, int position, Object view) {    
            ((ViewPager)pager).removeView((View)view);
        }
         
        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj; 
        }
 
        @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
        @Override public Parcelable saveState() { return null; }
        @Override public void startUpdate(View arg0) {}
        @Override public void finishUpdate(View arg0) {}
    }
}
