package me.blog.haj990108.winklick;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity{
	
	private GlobalSettings global;
	
	private CheckBoxPreference faceVisible;
	private CheckBoxPreference isTestQwerty;
	private EditTextPreference cursorSpeed;
	private EditTextPreference updatePeriodSec;
	private ListPreference cursorEyeLeft;
	private ListPreference clickEyeLeft;
	private ListPreference local;
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        global = (GlobalSettings) getApplicationContext();
        Log.i("TAG", "Settings Activity Started");
        
        addPreferencesFromResource(R.xml.settings);
        
        faceVisible = (CheckBoxPreference) findPreference("isFaceVisible");
        cursorSpeed = (EditTextPreference) findPreference("cursorSpeed");
        updatePeriodSec = (EditTextPreference) findPreference("updatePeriodSec");
        isTestQwerty = (CheckBoxPreference) findPreference("isTestQwerty");
        cursorEyeLeft = (ListPreference) findPreference("isCursorEyeLeft");
        clickEyeLeft = (ListPreference) findPreference("isClickEyeLeft");
        local = (ListPreference) findPreference("local");
        
        setValueIndexFromGlobal();//기존 global 변수를 설정창으로 불러온다
    }
	
	private void setValueIndexFromGlobal(){
		// TODO : 설정.
		//글로벌로부터 기존 값을 받아온다. 글로벌에 적용은 MainActivity.java의 onResume()에서~
		
		faceVisible.setChecked(global.isFaceVisible);
		cursorSpeed.setText(Integer.toString(global.cursorSpeed) );
		updatePeriodSec.setText(Double.toString(Math.round(global.updatePeriodSec * 1000)/1000.0) );
		isTestQwerty.setChecked(global.isTestQwerty);
		
		cursorEyeLeft.setValueIndex( global.isCursorEyeLeft ? 0 : 1 );
		clickEyeLeft.setValueIndex( global.isClickEyeLeft ? 0 : 1 );//삼항연산자 == 조건 ? true일때반환 : false일때반환
		if(global.local.equals(Locale.KOREA)){
			local.setValueIndex(0);
		}else if(global.local.equals(Locale.US)){
			local.setValueIndex(1);
		}
	}
}
