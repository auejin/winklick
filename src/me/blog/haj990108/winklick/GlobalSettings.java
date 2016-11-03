package me.blog.haj990108.winklick;

import java.util.Locale;

import org.opencv.core.Point;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

public class GlobalSettings extends Application{
	
	//전역변수들을 다룬다.
	//저장관련 http://todayhumor.com/?programmer_8220
	
	//public String STATUS;
	
	public boolean isTestQwerty = false;
	
    private int learn_course = 0;//0:없음, 1:가운데, 2:위 얻음 -> 머신러닝 종료 및 작동 시작
    public boolean isTablet = false;
    public boolean isWinklickStarted = false;
    public boolean isFaceExist = false;
    
    // TODO : 설정.SettingsActivity <- 여기 -> FaceService //global.isClickEyeEvent로 사용하자
    
    public boolean isClickEyeLeft = true;//true		//클릭인식 기준 눈이 왼쪽인가
    public boolean isCursorEyeLeft = true;//false		//커서이동 기준 눈이 왼쪽인가
    
    public boolean isFaceVisible = true;//true		//아직은 false로 하면 Assertion Failed뜬다
    public int cursorSpeed = 6;//6
    public float updatePeriodSec = 0.1f;//0.1f
    
    public Locale local;
    //설치 후 최초 설정값 수정시 여기에서만 수정하면 된다.
    
    
	
	/*사용법
	 * private GlobalSettings global;
	 * global = (GlobalSettings) getApplicationContext();//super.onCreate()뒤에 넣자
	 * String 얻어옴 = global.getSTATUS();	//get
	 * global.setSTATUS( 설정할String );	//get
	 *  */
	
	/*STATUS 종류 : 지금까지 아래의 내용은 비효율적이다.
	 * 이건 각 클래스가 판별할 문제고
	 * 일단은 보기좋게 자료만 정리해서 뿌려주는 역할만 하자.
	 * 
	 * WINKED
	 * 
	 * CURSOR_STOP
	 * CURSOR_MOVE
	 * CURSOR_TOUCH
	 * CURSOR_TOUCHSCROLL
	 * CURSOR_PINCHZOOM
	 * */
	
	@Override
	public void onCreate(){
		super.onCreate();
	}
	
	@Override
	public void onTerminate(){
		super.onTerminate();
	}
	
	
	public int getLearn_course(){
		return learn_course;
	}
	public void setLearn_course(int learn_course){
		this.learn_course = learn_course;
		//Log.i("GLOBAL", "learn_course == "+learn_course);
	}
	
	//faceService에서 받아올 것 전부 잘 됨
	private int isLeft = 0, isUp = 0;
	//private int isLeftNum = 0, isUpNum = 0;//위치의 누적. 0이면 그냥 0으로
	private int isWinkNum = 0;//클릭여부의 누적. 3이상이아야 get을 true로 리턴
	//private boolean isWink = false;
    
    
	public boolean getIsWink(){
		boolean returnB = false;
		returnB = this.isWinkNum > 2?true:false;//1
		return returnB;
	}
	public void setIsWink(boolean isWink){
		if(isWink){
			this.isWinkNum ++;
		}else{
			this.isWinkNum =0;
		}
		//Log.i("GLOBAL", "isWink == "+isWink);
	}
	
	//여기서 아예 커서의 움직임 x,y 속도를 계산해 리턴하자
	public int getMove(boolean isXnotY){
		//isLeft : 좌우인지 위아래 인지 (좌측이면 true)
		//isLeftEye : 왼눈인지 오른눈인지 (왼눈이면 true)
		int returnInt = 0;
		
		if(isXnotY){
			returnInt = isLeft*cursorSpeed;
		}else{
			returnInt = isUp*cursorSpeed;
		}
		
		return returnInt;
	}
	public void setMove(boolean isLeftNotUp, int newInt){
		//getMove에선 isUp, isLeft를 그대로 반환
		/*if(isLeftNotUp){
			isLeft = newInt * -1;//좌표계 특성상 좌측이 음수
		}else{
			isUp = newInt * -1;//좌표계 특성상 상측이 음수
		}*/
		
		if(isLeftNotUp){
			if(newInt == 0){
				isLeft = 0;
			}else{
				isLeft -= newInt;//좌표계 특성상 좌측이 음수
			}//중앙시 속도는 바로 0. 그 이외엔 누적
		}else{
			if(newInt == 0){
				isUp = 0;
			}else{
				isUp -= newInt;//좌표계 특성상 상측이 음수
			}
		}
		
		//Log.i("GLOBAL", "setMove (isLeft, isUp) == ( "+isLeft+", "+isUp+")");
	}
	
	/*public boolean getFaceExist(){
		return faceExist;
	}
	public void setFaceExist(boolean faceExist){
		this.faceExist = faceExist;
		//Log.i("GLOBAL", "faceExist == "+faceExist);
	}*/
	
	

}
