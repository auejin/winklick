//기존 faceService

package me.blog.haj990108.winklick;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.NativeCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import me.blog.haj990108.winklick.R;


public class FaceService extends Service implements CvCameraViewListener2, OnInitListener{
	//======== 인식1B (눈동자로 터치환경) ========///
	
		private LayoutInflater li;
		private RelativeLayout mPopupView;//항상 보이게할 뷰(nativecam_view.xml의 레이아웃)
		private CameraBridgeViewBase mOpenCvCameraView;//mPopupView 안의 카메라뷰 (NativeCameraView1)
		// 변수종류 CameraBridgeViewBase,NativeCameraView 둘다 됨
		private WindowManager.LayoutParams mParams; //뷰의 위치 및 크기
		private WindowManager mWindowManager;
		
		

		
		private static final String TAG = "OCVSample::Activity";
	    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
	    public static final int JAVA_DETECTOR = 0;
	    
	    private ArrayList<Integer> hei_t = new ArrayList<Integer>();//마이닝되니 데이터 저장//위//rList
	    private ArrayList<Integer> hei_m = new ArrayList<Integer>();//마이닝되니 데이터 저장//아래//rList
	    private ArrayList<Integer> hei_b = new ArrayList<Integer>();//마이닝되니 데이터 저장//아래
	    
	    private ArrayList<Integer> thr_t = new ArrayList<Integer>();//마이닝되니 데이터 저장//위
	    private ArrayList<Integer> thr_m = new ArrayList<Integer>();//마이닝되니 데이터 저장//중간
	    private ArrayList<Integer> thr_b = new ArrayList<Integer>();//마이닝되니 데이터 저장//아래
	    
	    private ArrayList<Integer> maxH_list = new ArrayList<Integer>();//윙크인식용
	    private int maxH = -1;
	    
	    private static int learn_framesMAX = 30;//이 기간 이상 고정시 확정
	    private static int a_hei_t, a_hei_m, a_hei_b, avg_thr_t, avg_thr_m, avg_thr_b, maxH_t, maxH_avg, maxH_b;// avg_bri_b
	    private int learn_course = 0;//0:없음, 1:가운데, 2:위 얻음->작동 시작
	    //int method = 0;
	    
	    private Mat mRgba;
	    private Mat mGray;
	    // matrix for zooming
	    //private Mat mZoomWindow;
	    //private Mat mZoomWindow2;
	 
	    private File mCascadeFile;
	    private CascadeClassifier mJavaDetector;
	    
	    private CascadeClassifier mJavaDetectorTest;
		private float mRelativeEyeSize = 0.3f;
		private int mAbsoluteEyeSize = 0;
		public int eyeMissingFrame = 0;
	    
	    // TODO : 설정.
	    // 설정창이랑 연결할려면 static 지우자
	    private boolean isFaceVisible = true;//아직은 false로 하면 Assertion Failed뜬다
	    private static int cameraWidth = 352;//1280*720
	    private static int cameraHeight = 288;//352*288
	    private static boolean isLineVisible = true;
	   
	    //클릭 인식용, 커서 이동용 눈을 따로 설정하게끔 하자.
	    
	            	    
	    private String[] mDetectorName;
	    private float mRelativeFaceSize = 0.5f;//카메라 세로*이거 가 얼굴 최소 세로길이
	    private int mAbsoluteFaceSize = 0;
	    private static boolean isTablet;
	    
	    private Toast toast = null; 
	    double xCenter = -1;
	    double yCenter = -1;
	    
	    private int isLeft = 0, isUp = 0;
	    private boolean isWink = false;//outOfArea가 마우스 속도에 영향을 주지 않게 하기 위해 따로 bool 만들자
	    private GlobalSettings global;//globalSettings 로 전송될 변수
	    
	    
	    static {
	        if (!OpenCVLoader.initDebug()) {
	        	Log.e("TAG", "OPENCV initialization error");
	        }else{
	        	Log.i("TAG", "OPENCV initialization success");
	        }
	    }//openCV내장
	    
		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
		
		private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	        @Override
	        public void onManagerConnected(int status) {
	            switch (status) {
	            case LoaderCallbackInterface.SUCCESS: {
	                Log.i(TAG, "OpenCV loaded successfully");
	 
	 
	                try {
	                	//==얼굴전면==//
	                    InputStream is = getResources().openRawResource(
	                            R.raw.lbpcascade_frontalface);
	                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
	                    mCascadeFile = new File(cascadeDir,
	                            "lbpcascade_frontalface.xml");
	                    FileOutputStream os = new FileOutputStream(mCascadeFile);
	 
	                    byte[] buffer = new byte[4096];
	                    int bytesRead;
	                    while ((bytesRead = is.read(buffer)) != -1) {
	                        os.write(buffer, 0, bytesRead);
	                    }
	                    is.close();
	                    os.close();
	                    //-- end --//
	                    
	                    //==눈영역==//
						InputStream ist = getResources().openRawResource(R.raw.haarcascade_eye);
						File cascadeDirT = getDir("cascadeER",
								Context.MODE_PRIVATE);
						File cascadeFileT = new File(cascadeDirT,
								"haarcascade_eye.xml");
						FileOutputStream ost = new FileOutputStream(cascadeFileT);
						byte[] bufferT = new byte[4096];
						int bytesReadT;
						while ((bytesReadT = ist.read(bufferT)) != -1) {
							ost.write(bufferT, 0, bytesReadT);
						}
						ist.close();
						ost.close();
						//-- end --//
	 
	                    mJavaDetector = new CascadeClassifier(
	                            mCascadeFile.getAbsolutePath());
	                    if (mJavaDetector.empty()) {
	                        Log.e(TAG, "Failed to load cascade classifier");
	                        mJavaDetector = null;
	                    } else
	                        Log.i(TAG, "Loaded cascade classifier from "
	                                + mCascadeFile.getAbsolutePath());
	                    
	                    mJavaDetectorTest = new CascadeClassifier(
								cascadeFileT.getAbsolutePath());
						if (mJavaDetectorTest.empty()) {
							Log.e(TAG, "Failed to load cascade classifier");
							mJavaDetectorTest = null;
						} else
							Log.i(TAG, "Loaded cascade classifier from "
									+ mCascadeFile.getAbsolutePath());
	                    
	                    
	                    cascadeDir.delete();
	                    cascadeDirT.delete();
	 
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
	                }
	                mOpenCvCameraView.setCameraIndex(1);
	                mOpenCvCameraView.setMaxFrameSize(cameraWidth, cameraHeight);// TODO : 최대 크기 지정
	                mOpenCvCameraView.enableFpsMeter();
	                mOpenCvCameraView.enableView();//뷰 활성화
	 
	            }
	                break;
	            default: {
	                super.onManagerConnected(status);
	            }
	                break;
	            }
	        }
	    };
	    
	    public FaceService() {
	        mDetectorName = new String[2];
	        mDetectorName[JAVA_DETECTOR] = "Java";
	 
	        Log.i(TAG, "Instantiated new " + this.getClass());
	    }
		
		@Override
		public void onCreate() {
			super.onCreate();
			Log.i("TAG", "Face Service Started");
			
			li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			
			//myTTS = new TextToSpeech(this, this);
			myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

				@Override
				public void onInit(int status) {
				    if (status == TextToSpeech.SUCCESS) {
				        int result = myTTS.setLanguage(global.local);
				        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				            Log.e("TAG", "This Language is not supported");
				            Intent installIntent = new Intent();
				            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				            startActivity(installIntent);
				        }
				    } else {
				        Log.e("TAG", "Initilization Failed!");
				    }
				}
		    });
	        myTTS.setSpeechRate( (float)1.4);
			mParams = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,//
		            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
					PixelFormat.TRANSLUCENT);
			//(int w, int h, int _type, int _flags, int _format)
			
			mParams.width = cameraWidth;
	        mParams.height = cameraHeight;
			
			mParams.gravity = Gravity.LEFT | Gravity.TOP; //팝업 화면을 좌상에 위치시키자
	        //mParams.gravity = Gravity.CENTER_VERTICAL | Gravity.TOP; 
	        
			mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
			
			mPopupView = (RelativeLayout) li.inflate(R.layout.nativecam_view, null);
			
			
			
			mOpenCvCameraView = (NativeCameraView) mPopupView.findViewById(R.id.NativeCameraView1);
			mOpenCvCameraView.setCameraIndex(1);
	        mOpenCvCameraView.setCvCameraViewListener(this);
	        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
	        
	        
	        mWindowManager.addView(mPopupView, mParams); //SYSTEM_ALERT_WINDOW permission 필요
	        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
	        global = (GlobalSettings) getApplicationContext();//super.onCreate()뒤에 넣자
	        
	        //TODO global.set등은 처음엔 초기화를 시켜주자
	        
	        global.setMove(true, isLeft);
            global.setMove(false, isUp);
            global.setIsWink(isWink);
            global.setLearn_course(learn_course);
            
            //Log.e("TAG", "global.local = "+global.local);
            
            isTablet = global.isTablet;
            
            mFpsMeter.init();
		}
		
		@Override
		public void onDestroy() {
			Log.i("TAG", "Face Service Ended");
			
			if (mWindowManager != null) { //서비즈 종료시 꼭 뷰를 제거하자.
				if (mPopupView != null) {
					mWindowManager.removeView(mPopupView);
				}
			}
			myTTS.shutdown();
			super.onDestroy();
		}
		public void onCameraViewStarted(int width, int height) {
	        mGray = new Mat();
	        mRgba = new Mat();
	    }
	 
	    public void onCameraViewStopped() {
	        mGray.release();
	        mRgba.release();
	        //mZoomWindow.release();
	        //mZoomWindow2.release();
	    }
	    
	    public synchronized void resizeCamera(int width, int height){
	    	//cameraWidth, cameraHeight
	    	//352, 288
	    	
	    	mOpenCvCameraView.getHolder().setFixedSize(width, height);//이거 안쓰면 저화질
        	mParams.width = height;
	        mParams.height = cameraHeight;//params지우든 안지우든 assertionfailed뜸
	        Log.i("TAG", "resizeCamera = ("+width+", "+height+")");
	    }

	    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
	    	
	    	//System.gc();//asdf
	    	//타블렛 : 15->13
	    	//스마트폰 : 14->13 별 차이 없어....
	    	//놀랍게도 화면에 뜬 opencv의 속도는 타블렛과 비슷했다. 그런데 왜 렉이 심한걸까. 액션서비스나 screen receiver 때문인가?
	    	
	    	// TODO : 설정. 적용
	    	isClickEyeLeft = global.isClickEyeLeft;
	        isCursorEyeLeft = global.isCursorEyeLeft;
	        isFaceVisible = global.isFaceVisible;
	    	
	        mRgba = inputFrame.rgba();
	        mGray = inputFrame.gray();//(352,288)
	        
	        Core.flip(mRgba, mRgba, 1);
	        Core.flip(mGray, mGray, 1);//(352,288)
	        
	        if(!isTablet){//스마트폰에 맞게 화면회전
	        	mRgba = mRgba.t();
	            mGray = mGray.t();//(288,352)
	            Core.flip(mRgba, mRgba, 1);
	            Core.flip(mGray, mGray, 1);//(288,352)
	        }
	        
	 
	        if (mAbsoluteFaceSize == 0) {
	            int height = mGray.rows();
	            if (Math.round(height * mRelativeFaceSize) > 0) {
	                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
	            }
	        }
	         
	        /*if (mZoomWindow == null || mZoomWindow2 == null)
	            CreateAuxiliaryMats();*/
	        
	        
	        
	 
	        MatOfRect faces = new MatOfRect();
	 
	            if (mJavaDetector != null)
	                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2,
	                        2,
	                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize),
	                        new Size());
	 
	        Rect[] facesArray = faces.toArray();
	        if(facesArray.length > 0){
	        	if (isLineVisible) Core.rectangle(mRgba, facesArray[0].tl(), facesArray[0].br(), FACE_RECT_COLOR, 3);
	            
	            xCenter = (facesArray[0].x + facesArray[0].width + facesArray[0].x) / 2;
	            yCenter = (facesArray[0].y + facesArray[0].y + facesArray[0].height) / 2;
	            
	            if (isLineVisible){
		            Point center = new Point(xCenter, yCenter);//얼굴 중점 그리고 중점에 미니원(뽀대용)을 그린다
		            Core.circle(mRgba, center, 10, new Scalar(255, 0, 0, 255), 3);
		            Core.putText(mRgba, "[" + center.x + "," + center.y + "]",
		                    new Point(center.x + 20, center.y + 20),
		                    Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255, 255, 255,
		                            255));//중점 옆에 위치좌표를 보여주는 텍스트 표시
	            }
	            
	 
	           Rect r = facesArray[0];
	           
	           //TODO : 여기서부터 변경함 
				Rect eyearea_left = returnEyeArea(r, true);
				Rect eyearea_right = returnEyeArea(r, false);
				
				Core.rectangle(mRgba, eyearea_left.tl(), eyearea_left.br(),
						new Scalar(255, 0, 0, 255), 2);
				Core.rectangle(mRgba, eyearea_right.tl(), eyearea_right.br(),
						new Scalar(255, 0, 0, 255), 2);
				
				traceUpdateEyesarray(r, true);//핵심 코드
				
				if(isWink){
					Log.e("WINK", "-----wink----");
				}else{
					Log.e("WINK", "-------------");
				}
				if(isLeft == 1){
					Log.d("가로", "왼");
				}else if(isLeft == -1){
					Log.d("가로", "오른");
				}else{
					Log.d("가로", "--");
				}
				if(isUp == 1){
					Log.i("세로", "위");
				}else if(isUp == -1){
					Log.i("세로", "아래");
				}else{
					Log.i("세로", "--");
				}
				//윙크판별
				
				
				if(eyeLMissingFrame > 2 || eyeRMissingFrame > 2) Log.e("ERROR", "눈인식 안됨. 오차율 증가하니 자리이동!!");//인식이 안됨. 경고만 하자
				
	           global.setMove(true, isLeft);
	           global.setMove(false, isUp);
	           global.setIsWink(isWink);
	           global.setLearn_course(learn_course);
	           global.isFaceExist = true;
	           faceRec_f++;
	        }else{
	        	faceMissingFrame ++;
				if(faceMissingFrame > 2) alertNotFound(mGray, prev_facearea);//얼굴 미발견시
	        	global.isFaceExist = false;
	        }
	        
	        
	        
	        if(!isFaceVisible){
	        	Imgproc.resize(mRgba, mRgba, new Size(1,1));//리턴값을 투명하게 하는 대신 1,1사이즈로 해서 안보이게함
	        }else{
	        	Imgproc.resize(mRgba, mRgba, new Size(352,288));//TODO : 아직도 isFaceVisible = false하면 Assertion Failed 뜸
	        }
	        
	        //=== 실험 ====//
			//t_pfrm_sppedx10.add(object);
	        if(faceRec_f>0)faceRec_t++;
			mFpsMeter.measure();
			
	        return mRgba;
	    }
	    
	    
	    //TODO : faceService에 없던거
	    public Rect prev_facearea = new Rect(1,1,3,3); //얼굴 미발견시 추적
		public Rect prev_detected_eyearea_left = new Rect(1,1,2,2);public Rect prev_detected_eyearea_right = new Rect(1,1,2,2);//prev보단 prev_detected가 더 커야됨. 안크면 submat시 오류.
		public Rect prev_eye_a_area = new Rect(1,1,1,1);//머신러닝으로 측정된 눈 이미지에서 검출된 진짜 "눈" 영역. 회색 사각테두리.
		public int eyeLMissingFrame = 0, eyeRMissingFrame = 0, faceMissingFrame = 0;
		private boolean isMainEyeWink = false;
		private Rect eye_a_area; private Mat eye_a, eye_b, eye_temp;
		
		public void traceUpdateEyesarray(Rect face, boolean isMainEye){//checkWink
			final boolean isTargetEyeLeft = (isMainEye == isClickEyeLeft);
			final Rect eyearea = returnEyeArea(face, isTargetEyeLeft);//얼굴 영역에서 일정 비율 자름
			boolean isWinkTmp = false;//현재 타깃 눈의 눈이 감겨있는가?
			Rect prev_detected_eyearea = new Rect();//이걸 없애고 LEFT와 RIGHT 둘다 검사하도록 해야한다.
			
			
			
			if(isTargetEyeLeft){
				prev_detected_eyearea = prev_detected_eyearea_left;
			}else{
				prev_detected_eyearea = prev_detected_eyearea_right;
			}
			//Log.d("WINK", "isTargetEyeLeft = "+isTargetEyeLeft);
			
			Rect[] eyesArray = detectedEyesOnEyeArea(eyearea);
			//TODO : haar cascade기반 검색 : detectedEyesOnEyeArea
			
			//|| (eyesArray[0].width < eyesArray[0].height * 2 && prev_detected_eyearea == new Rect(1,1,2,2)
			//
			if(eyesArray.length == 0 || eyesArray[0].width < eyearea.width / 2
					|| (eyesArray[0].width < prev_detected_eyearea.width * 2 && eyesArray[0].height < prev_detected_eyearea.height * 2) ){//크기 너무 작거나 정사각형인건 일단 배제
				if(isTargetEyeLeft){
					eyeLMissingFrame ++;
				}else{
					eyeRMissingFrame ++;
				}
				
				Rect tempEyeArea = new Rect(1,1,1,1);//추적이 안된경우, 이전 영역을 바탕으로 추정함 (빨간색)
				tempEyeArea.x = prev_detected_eyearea.x * face.width / prev_facearea.width;
				tempEyeArea.y = prev_detected_eyearea.y * face.height / prev_facearea.height;
				tempEyeArea.width = prev_detected_eyearea.width * face.width / prev_facearea.width;
				tempEyeArea.height = prev_detected_eyearea.height * face.height / prev_facearea.height;
				
				//여기서 ratio가 face를 기준으로 했는데, 지금 eye측정 유무의 주기랑 face유무 주기랑 달라서 범위 초과로 assertion failed 오류났던 적이 있다.
				//근데 이 코드는 얼굴을 찾았을 때 face인수가 있을 때 발생하는거라서....//그러니까 prev가 face로 덮어지면서 같아지니까 오류가 발생하지!!!!!
				
				Core.rectangle(mRgba
						,new Point(eyearea.tl().x + tempEyeArea.tl().x, eyearea.tl().y + tempEyeArea.tl().y)
						,new Point(eyearea.tl().x + tempEyeArea.br().x, eyearea.tl().y + tempEyeArea.br().y)
						,new Scalar(255, 0, 0, 255), 3);//빨간색
				
				Mat m = mGray.submat(eyearea);
				if(m.height() < tempEyeArea.y + tempEyeArea.height || m.width() < tempEyeArea.x + tempEyeArea.width){
					isWinkTmp = false;//윙클릭 처음 작동시의 오류. 그냥 무시.
				}else{
					eye_temp = m.submat(tempEyeArea);//.clone() 아직까진 따로 잘리게 나올 필요는 없어서 clone 안함
					isWinkTmp = getPupilBlink(eye_temp, isMainEye);
				}
				//Imgproc.resize(setPupilWink_mat(m.submat(tempEyeArea)), mZoomWindow2,mZoomWindow2.size());//setPupilWink_mat
				
			}else{
				if(isTargetEyeLeft){
					eyeLMissingFrame = 0;
				}else{
					eyeRMissingFrame = 0;
				}
				if(isTargetEyeLeft){
					prev_detected_eyearea_left = eyesArray[0];
				}else{
					prev_detected_eyearea_right = eyesArray[0];
				}
				//이전꺼 업뎃
				Core.rectangle(mRgba
						,new Point(eyearea.tl().x + eyesArray[0].tl().x, eyearea.tl().y + eyesArray[0].tl().y)
						,new Point(eyearea.tl().x + eyesArray[0].br().x, eyearea.tl().y + eyesArray[0].br().y)
						,FACE_RECT_COLOR, 3);
				Mat m = mGray.submat(eyearea);
				eye_temp = m.submat(eyesArray[0]);//.clone()하면 원래 mGray에서 잘리게 나옴. 지우면 히스토그램부터 블러처리까지 된걸 자른 mat이 리턴된다.
				isWinkTmp = getPupilBlink(eye_temp, isMainEye);//Imgproc.resize(setPupilWink_mat(m.submat(eyesArray[0])), mZoomWindow2,mZoomWindow2.size());//표시하는거//setPupilWink_mat
				
			}
			
			
			if(isMainEye){//첫째 실행
				isMainEyeWink = isWinkTmp;
				if(isClickEyeLeft == isCursorEyeLeft){//WNK = MOV
					if(isWinkTmp){
						isLeft = isUp = 0; //Log.d("가로", "-"); Log.d("세로", "-");//메인눈 감으면 이동이 정지됨 -> 눈 감으면 무조건 정지
						traceUpdateEyesarray(face, false);//WNK = MOV서 윙크여부 파악 위해
					}else{
						isWink = false;
						getPupilMove(isTargetEyeLeft);//LL || RR일때 윙크 안하면 동공추적
					}
				}else{
					traceUpdateEyesarray(face, false);//WNK != MOV서 윙크 먼저 실행됨
				}
			}else{//둘째 실행 (머신러닝 1회만 하는 경우시 안될수도 있음)
				if(isMainEyeWink != isWinkTmp){
					isWink = true;
					//if((isClickEyeLeft != isCursorEyeLeft)&& isWinkTmp ) isWink = false, getPurpilMove();
					//이렇게 하면 WNK != MOV서 고의 오류도 MOVE 가능케 하지만, 세로이동시 h의 너비가 줄어드므로 악영향. 그냥 고의오류로 윙크로 인식시키자
				}else{
					isWink = false;
					if((isClickEyeLeft != isCursorEyeLeft) && !isWinkTmp  && !isMainEyeWink ) getPupilMove(isTargetEyeLeft);
				}
			}
			
			
			prev_facearea = face; faceMissingFrame = 0; //TODO : checkWinkORMove이후에 실행시켜야 한다. prev_의 업데이트 주기 때문임. assertion failed뜨면 여기부터 고치렴
			//return isWinkTmp;
		}
		
		//=== 실험용 ===//
		
		private FpsMeter mFpsMeter = new FpsMeter(); private ArrayList<Double> t_pfrm_spped = new ArrayList<Double>();
		private double type_speed=0, typo_ratio=0; private static String typeStr;//private ArrayList<Integer> t_type_speed = new ArrayList<Integer>();
		
		private int faceRec_f=0, faceRec_t=0;//private ArrayList<Integer> f_rec_ratio = new ArrayList<Integer>();
		//--- end ---//
		
		public class FpsMeter {
		    int step;
		    int framesCouner;
		    double freq;
		    long prevFrameTime;
		    //String strfps;
		    DecimalFormat twoPlaces = new DecimalFormat("0.00");
		    
		    
		    
		    private void traceFinalResult(){
		    	Log.i("TEST", "avg_fps = "+avg(t_pfrm_spped));
		    	Log.i("TEST", "face_rec_ratio = "+ ( faceRec_t==0 ? 0:Double.toString((double)faceRec_f/(double)faceRec_t) )   );
		    	Log.i("TEST", "type_speed = "+type_speed);//완전히 입력하면 종료
		    	Log.i("TEST", "typo_ratio = "+typo_ratio);//길게 눌러서 다 지우는거 금지!
		    }
		    
		    private void f_rec_ratio() {
		    	if(faceRec_f != 0) Log.i("FaceRec", Double.toString((double)faceRec_f/(double)faceRec_t));
		    }
		    
		    double typingTime=0;
	    	String s; int totalWriteNum;
	    	Timer timeCheck = new Timer();
	    	boolean testEnd = false;
	    	
		    private void type_typo_test (){
		    	if(testEnd) return;
		    	
		    	if(global.isTestQwerty){
		    		if(((TestQwertyActivity)TestQwertyActivity.mContext) == null) return;
		    		s = ((TestQwertyActivity)TestQwertyActivity.mContext).textViewStr;//화면에 보이는 문자 = textViewStr
		    		totalWriteNum = ((TestQwertyActivity)TestQwertyActivity.mContext).totalWriteNum;
		    	}else{
		    		if(((TestActivity)TestActivity.mContext) == null) return;
		    		s = ((TestActivity)TestActivity.mContext).dialNum;//화면에 보이는 숫자 = dialNum
		    		totalWriteNum = ((TestActivity)TestActivity.mContext).totalWriteNum; //총 입력 횟수
		    	}
		    	if(s.length()==1 && typingTime == 0){
		    		timeCheck.schedule(new TimerTask(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							typingTime += 0.1;
						}
			    		
			    	}, 0, 100);
		    	}
		    	
	    		if(s.equals(typeStr)) {
	    			timeCheck.cancel();
	    			type_speed = (s.length()-1)/(typingTime);
	    			//Log.d("TEST", s.length()+" / "+totalWriteNum);  //  11/15
	    			typo_ratio = (((double)totalWriteNum-(double)s.length())/((double)totalWriteNum - 1)); //이게 왜 0으로 나오지?
	    			traceFinalResult();
	    			testEnd = true;
	    		}
	    		//Log.d("TEST", "totalWriteNum = "+totalWriteNum);
		    }
		    
		    private double avg(ArrayList <Double> marks) {
		        if (marks == null || marks.isEmpty()) {
		            return 0;
		        }

		        double sum = 0;
		        for (Double mark : marks) {
		            sum += mark;
		        }

		        return sum / marks.size();
		    }
		    
		    public void init() {
		        step = 5;//step프레임마다 한번 실행
		        framesCouner = 0;
		        freq = Core.getTickFrequency();
		        prevFrameTime = Core.getTickCount();
		        //strfps = "";
		        if(global.isTestQwerty){
		        	typeStr = "GOODMORNING";
		        }else{
		        	typeStr = "01023033138";
		        }
		    }

		    public void measure() {
		        framesCouner++;
		        if (framesCouner % step == 0) {
		            long time = Core.getTickCount();
		            double fps = step * freq / (time - prevFrameTime);
		            fps = Math.round(fps);
		            prevFrameTime = time;
		            Log.i("FPS", "int fps = "+fps);
		            t_pfrm_spped.add(fps);
		            f_rec_ratio();
		        }
		        type_typo_test ();
		    }

		}
		
		private Rect[] detectedEyesOnEyeArea(Rect eyeArea) {
			////Imgproc.equalizeHist(mGray, mGray);
			Mat testMat = mGray.submat(eyeArea);
			Imgproc.equalizeHist(testMat, testMat);//원본은 이거 주석 안됨
			
			MatOfRect eyeTest = new MatOfRect();
			if (mAbsoluteEyeSize == 0) {
				int height = testMat.rows();
				if (Math.round(height * mRelativeEyeSize) > 0) {
					mAbsoluteEyeSize = Math.round(height * mRelativeEyeSize);
				}
			}
			if (mJavaDetectorTest != null)
				mJavaDetectorTest.detectMultiScale(testMat, eyeTest, 1.1, 2,
						2, // objdetect.CV_HAAR_SCALE_IMAGE
						new Size(mAbsoluteEyeSize, mAbsoluteEyeSize),
						new Size());
			return eyeTest.toArray();
		}
		
		//2세대 영역잡기. 너무 구리다.
		/*private Rect[] detectedEyesOnEyeArea_old(Rect eyeArea) {
			Mat C = mGray.submat(eyeArea);
			Imgproc.equalizeHist(C, C);
	    	C.convertTo(C, -1, 30);
	    	Imgproc.erode(C, C, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));//살붙이기
	    	Imgproc.threshold(C, C, 65, 255, Imgproc.THRESH_BINARY);//Imgproc.threshold(C, C, 50, 255, Imgproc.THRESH_BINARY);
	    	//값을 크게하면 어둡게 됨
	    	
	    	//Log.i("TAG", "CTYPE = "+C.type());//0
	    	//Log.i("TAG", "colorwhite = "+colorArr[0]);//백색uchar 255.0
	    	
	    	//=== 머리카락 제거 ===//
	    	//색채우기 알고리즘  http://ko.wikipedia.org/wiki/플러드_필 (알고리즘 특성상 mask는 가로세로 크기가 원본c보다 +2씩 커야함)
	    	Floodfill : for(int x = 1; x < C.width() + 1; x++){
            	double colorArr[] = C.get(1,x);//배열길이 = 1
	    		if (colorArr != null && colorArr[0] < 10){
	    			Imgproc.floodFill(C, Mat.zeros(C.rows() + 2, C.cols() + 2, CvType.CV_8U), new Point(x,1), new Scalar(255,255,255));
	    			break Floodfill;
	    		}
            	
	        }
	    	final Rect[] r = {getAccEyeArea(C)};
			return r;
		}
		*/
		
		private Rect returnEyeArea(Rect face, boolean isLeft) {
			
			if(!isLeft){
				face = new Rect(face.x + face.width / 16
						+ (face.width - 2 * face.width / 16) / 2,
						(int) (face.y + (face.height / 4.5)),
						(face.width - 2 * face.width / 16) / 2, (int) (face.height / 3.0));
			}else{
				face = new Rect(face.x + face.width / 16,
						(int) (face.y + (face.height / 4.5)),
						(face.width - 2 * face.width / 16) / 2, (int) (face.height / 3.0));
			}
			
			
			return face;
		}
	    
		private Rect getAccEyeArea(Mat eyeA){
			Rect accEyeArea = new Rect();//eyeA에서 알아낸 눈영역
			for(int x = 1; x < eyeA.width(); x++){
				l : for(int y = 1; y < eyeA.height(); y++){
					double colorArr[] = eyeA.get(y,x);
					if (colorArr != null && colorArr[0] < 10){
						if(accEyeArea.x == 0){
							accEyeArea.x = x;
						}else{
							accEyeArea.width = x - accEyeArea.x;
						}
						break l;
					}
				}
			}
			for(int y = 1; y < eyeA.height(); y++){
				l : for(int x = 1; x < eyeA.width(); x++){
					double colorArr[] = eyeA.get(y,x);
					if (colorArr != null && colorArr[0] < 10){
						if(accEyeArea.y == 0){
							accEyeArea.y = y;
						}else{
							accEyeArea.height = y - accEyeArea.y;
						}
						break l;
					}
				}
			}
			return accEyeArea;
		}
		
		private  boolean isClickEyeLeft = true;//클릭인식 기준 눈이 왼쪽인가
	    private  boolean isCursorEyeLeft = true;//커서이동 기준 눈이 왼쪽인가
	    
	    //TODO : 여기서 바꾸지 말고 직접 앱의 settings 메뉴에서 바꿔야 한다
	    //TODO : 학교에서 인식 안되는 이유 : 설정서 오른, 오른 시 명암차가 너무 커서 눈꼬리가 동공으로 인식되서 아무리 왼쪽 봐도 가운데로 인식되고 오른쪽만 잘됨
		
		public void getPupilMove(boolean isTargetEyeLeft){
			if(eye_b == null || eye_b.width() < 2 || eye_b.height() < 2 ) return;
			
	        //세로이동 시 머신러닝 할 것. (상, 중, 하를 볼 때의 ~~를 머신러닝)
	        //만약 여기서 accEyeArea.y를 사용하지 않는다면, 윙크시에도 세로인식 되게 한다.
			
			final int thr = (int)getThreshRatio1000(eye_a.submat(eye_a_area), eye_b.submat(eye_a_area), 1);//이건 eqHist랑 관계x
			
			/*if(eye_temp.width() > 10){//원본으로부터 잘린 eye_temp의 유일한 변형지점
				Imgproc.dilate(eye_temp, eye_temp, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));//살붙이기
				Imgproc.equalizeHist(eye_temp, eye_temp);
			}*/
			
	        //final int a_bri = (int)getColorAvg(eye_temp.submat(eye_a_area), 1);//클수록 위에본거//getColorAvg는 threshhold된 eye_a가 아닌 흑백이미지서 해야 함
	        //final int a_bri = (int)getColorAvgWithThresh(eye_temp.submat(eye_a_area), eye_a.submat(eye_a_area), 1);
	        
	        final Rect dg = getAccEyeArea(eye_b.submat(eye_a_area));//dg와 eye_a_area는 모두 인풋 eye_a(eye_b) 내에서 추적된 영역
	        dg.x += eye_a_area.x; dg.y += eye_a_area.y;//화면표시 때문에 어쩔 수 없다
	        //String k = "";for(int i=0; i<dg.width*10/dg.height; i+= 1) k += "=";Log.d("세로", "밝기비율 = "+ k + thr);
            
            
	        int y1 = 0;//Point E1 = new Point(0,0);
	        int y2 = 0;//Point E2 = new Point(0,0);
			E1Loop : for(int x = 0; x < eye_a.width(); x++){
				for(int y = eye_a.height(); y > 0; y--){
					double colorArr[] = eye_a.get(y,x);
					if (colorArr != null && colorArr[0] < 10){
	            		y1 = y;//E1 = new Point(x,y);
	            		Core.circle(eye_temp, new Point(x, y), 3, new Scalar(100, 100, 100, 255), 1);
	            		break E1Loop;
	            	}
	            }
	        }//눈 왼쪽
			E2Loop : for(int x = eye_a.width(); x > 0; x--){
	            for(int y = eye_a.height(); y > 0; y--){
	            	double colorArr[] = eye_a.get(y,x);
	            	if (colorArr != null && colorArr[0] < 10){
	            		y2 = y;//E2 = new Point(x,y);
	            		Core.circle(eye_temp, new Point(x, y), 3, new Scalar(100, 100, 100, 255), 1);
	            		break E2Loop;
	            	}
	            }
	        }//눈 오른쪽
	        
	        
	        final int hei = (dg.y - (y1 + y2)/2)*20;//20곱한건 dst와 영향력 비슷하게 하기 위함
	        
	        boolean learnEyeArea = true;
            if(learn_course <= 4){
            	final int x = dg.x + dg.width/2;
            	if(x < eye_a_area.width/3  + eye_a_area.tl().x){
            		isLeft = 1;//왼쪽
        		}else if(x > eye_a_area.width*2/3  + eye_a_area.tl().x){
        			isLeft = -1;//오른쪽
        		}else{
        			/*learnEyeArea = false;
        			//이건 가로세로 인식이 너무 예민해. 그리고 세로가 묻힘.
        			//화면 밖으로 나가는 경우가 있어서 assertion failed가 뜬다.
        			if(dg.width > eye_a_area.width/2){//눈에 비해 동공 큼
    					if(x > prev_eye_a_area.width/2 + prev_eye_a_area.x){
    						isLeft = 1;
    					}else{
    						isLeft = -1;
    					}
    				}else{//눈에 비해 동공 작음
    					if(x < eye_a_area.width/2  + eye_a_area.tl().x){
                    		isLeft = 1;//왼쪽
                		}else{
                			isLeft = -1;//오른쪽
                		}
    				}*/
        			
        			/*if(eye_a_area.width > eye_a_area.height*1.5){
        				isLeft = 0;//가운데
        			}else{//세로 사각형//자꾸 왔다갔다 함
        				if(dg.width > eye_a_area.width/2){//눈에 비해 동공 큼
        					learnEyeArea = false;
        					if(x > prev_eye_a_area.width/2 + prev_eye_a_area.x){
        						isLeft = -1;
        					}else{
        						isLeft = 1;
        					}
        				}else{//눈에 비해 동공 작음
        					if(x < eye_a_area.width/2  + eye_a_area.tl().x){
                        		isLeft = 1;//왼쪽
                    		}else{
                    			isLeft = -1;//오른쪽
                    		}
        				}
        			}*/
        			
        			if(eye_a_area.width > eye_a_area.height*1.5){
        				isLeft = 0;//가운데
        			}else{//세로 사각형//자꾸 왔다갔다 함
        				learnEyeArea = false;
        				if(dg.width > eye_a_area.width/2){//눈에 비해 동공 큼
        					if(x > prev_eye_a_area.width/2 + prev_eye_a_area.x){
        						isLeft = -1;
        					}else{
        						isLeft = 1;
        					}
        				}else{//눈에 비해 동공 작음
        					if(x < eye_a_area.width/2  + eye_a_area.tl().x){
                        		isLeft = 1;//왼쪽
                    		}else{
                    			isLeft = -1;//오른쪽
                    		}
        				}
        			}
        		}
            }
            if(learnEyeArea) {
            	prev_eye_a_area = eye_a_area;
            	//Log.i("TEST", "-------------");
            }else{
            	eye_a_area = prev_eye_a_area.clone();
            	//Log.i("TEST", "영역 정사각형");
            }
            
	        if(learn_course < 4){
	        	if(learn_course == 0){//가운데
	        		sayText(6);
	        		if(hei != 0)hei_m.add(hei);
		        	if(thr != 0)thr_m.add(thr);
		        	if(hei_m.size() > learn_framesMAX && thr_m.size() > learn_framesMAX){
		        		a_hei_m = mode(hei_m); avg_thr_m = mode(thr_m);
		        		hei_m.clear(); thr_m.clear();
		        		learn_course ++;
		        	}
		        }else if(learn_course == 1){//윙크
		        	//getPupilBlink에서 실행함
		        }else if(learn_course == 2){//위
		        	sayText(7);
		        	if(hei != 0)hei_t.add(hei);
		        	if(thr != 0)thr_t.add(thr);
		        	if(hei_t.size() > learn_framesMAX && thr_t.size() > learn_framesMAX){
		        		a_hei_t =  mode(hei_t); avg_thr_t = mode(thr_t);
		        		hei_t.clear(); thr_t.clear();
		        		learn_course ++;
		        	}
		        }else if(learn_course == 3){//아래
		        	sayText(8);
		        	if(hei != 0)hei_b.add(hei);
		        	if(thr != 0)thr_b.add(thr);
		        	if(hei_b.size() > learn_framesMAX && thr_b.size() > learn_framesMAX){
		        		a_hei_b = mode(hei_b);avg_thr_b = mode(thr_b);
		        		hei_b.clear(); thr_b.clear();
		        		learn_course ++;sayText(69);
		        		if(a_hei_t == a_hei_m) a_hei_t-=5; if(a_hei_b == a_hei_m) a_hei_b+=5;//hei끼리 비교후 같은게 있으면 보정하기
		        	}
		        }
	        }else if(isLeft == 0){// if(isLeft == 0) 가로 세로 중 하나만 됨
		        /*if(a_bri > avg_bri_t){//머2때의 코드 원본
		        	isUp = 1; //Log.d("세로", "위");
		        }else{
		            eye_b.convertTo(eye_b, -1, 1, 100);
		            if(!isMainEyeWink && dg.width > dg.height * 3){//잘 작동됨
		            	isUp = -1; //Log.d("세로", "아래");
		            }else{
		            	isUp = 0; //Log.d("세로", "-");
		            }
		        }*/
	        	//Log.d("세로", "t, m, m, b"+ avg_bri_t + ", " + avg_bri_m + " ; " + avg_thr_m + ", " + avg_thr_b);
	        	
	        	Log.d("세로", "Height ; "+ hei + " : " + a_hei_t + ", " + a_hei_m + ", " + a_hei_b);
	        	Log.d("세로", "Thresh ; "+ thr + " : " + avg_thr_t + ", "  + avg_thr_m + ", " + avg_thr_b);
	        	//t, m, b -> 86, 77, 82  
	        	
	        	final int dstT = (int)(Math.pow(a_hei_t - hei, 2) + Math.pow(avg_thr_t - thr, 2));//3NN알고리즘. 효과 없음
	        	final int dstM = (int)(Math.pow(a_hei_m - hei, 2) + Math.pow(avg_thr_m - thr, 2));
	        	final int dstB = (int)(Math.pow(a_hei_b - hei, 2) + Math.pow(avg_thr_b - thr, 2));
	        	final int min = Math.min(dstT, Math.min(dstM, dstB));
	        	
	        	/*if(min == dstM){
	        		isUp = 0;
	        	}else if(min == dstB){
	        		isUp = -1;
	        	}else if(min == dstT){
	        		isUp = 1;
	        	}*/
	        	if(min == dstM){
	        		isUp = 0;
	        	}else if(dstT == dstB){
	        		isUp = 0;
	        	}else if(min == dstB){
	        		isUp = -1;
	        	}else if(min == dstT){
	        		isUp = 1;
	        	}
	        
	        }
	        
	        
	        //TODO : 가운데 쳐다보게 머신러닝된 값중 최대값인 a_bri_midmax와 a_bri값을 비교해 위 쳐다봄 -> 아니면 dg값으로 아래 쳐다봄 검사. 
	        
	        //eye_temp = eye_a;//스레시홀드된 눈 영역 리턴
	        
			Core.rectangle(eye_temp, eye_a_area.tl(), eye_a_area.br(),new Scalar(100, 100, 100, 255), 1);//추정된 눈영역 리턴
			Core.rectangle(eye_temp, dg.tl(), dg.br(),new Scalar(200, 200, 200, 255), 1);//추정된 동공영역 리턴
			if(eye_temp.height() > 2 && eye_temp.width() > 2 && eye_temp.type() == 0) Imgproc.cvtColor(eye_temp, eye_temp, Imgproc.COLOR_GRAY2RGBA);//표시를 위해 흑백을 rgba로 변환
			//이거 안쓰면 아에 창이 안보임
			Imgproc.resize(eye_temp, mRgba,mRgba.size());//eye_temp //미니 창으로 표시
		}
		
		private void updateEyeAB(Mat eye) {
			eye.convertTo(eye, -1, 1, 10);
			Imgproc.equalizeHist(eye, eye);//detected area 불러옴
			Imgproc.erode(eye, eye, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));//살붙이기
			Imgproc.GaussianBlur(eye, eye, new Size(5,5),5);
			Mat eyeA = new Mat(); Imgproc.threshold(eye, eyeA, 40, 255, Imgproc.THRESH_BINARY);//눈영역
			
			for(int y=0; y<5; y++){
				Floodfill : for(int x = 1; x < eyeA.width(); x++){
		        	double colorArr[] = eyeA.get(y,x);
		    		if (colorArr != null && colorArr[0] < 10){
		    			Imgproc.floodFill(eyeA, Mat.zeros(eyeA.rows() + 2, eyeA.cols() + 2, CvType.CV_8U), new Point(x,y), new Scalar(255,255,255));
		    			break Floodfill;
		    		}
		        }
			}
			//eye_a, eye_b는 바이너리된 영역, eye_a_area는 눈의 크기이다. 이게 끝임. 흑백 이미지는 eye_temp에 저장됨. 그건 이거랑 관계x
			eye_a_area = getAccEyeArea(eyeA);//eyeA에서 눈 영역 알아냄
			eye_a = eyeA;
			eye_b = new Mat();Imgproc.threshold(eye, eye_b, 7, 255, Imgproc.THRESH_BINARY);//B에 동공표시
		}
		private boolean getPupilBlink(Mat eye, boolean isMainEye) {
			updateEyeAB(eye);
			//Rect accEyeArea = eye_a_area;
			//Mat eyeB = eye_b;
			
			/*eye.convertTo(eye, -1, 1, 10);
			Imgproc.equalizeHist(eye, eye);//detected area 불러옴
			Imgproc.erode(eye, eye, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));//살붙이기
			Imgproc.GaussianBlur(eye, eye, new Size(5,5),5);
			Mat eye_b = new Mat(); Imgproc.threshold(eye, eye_b, 60, 255, Imgproc.THRESH_BINARY);//눈영역*/			
			boolean isEmpty = false;
			boolean isBlink = false;
			if(eye_a_area.width > eye_a_area.height * 2){//윙크검사
				isEmpty = true;
				L : for(int x = (int)eye_a_area.tl().x; x < (int)eye_a_area.br().x + 1; x++){
		        	for(int y = (int)eye_a_area.tl().y; y < (int)eye_a_area.br().y + 1; y++){
		            	double colorArr[] = eye_b.get(y,x);
		            	if (colorArr != null && colorArr[0] < 10){
		            		isEmpty = false;
		            		break L;
		            	}
		            }
		        }
				if(isEmpty) isBlink = true;
			}else{//정사각형//세로인식시에는 아예 배제해야하는 부분
				Floodfill : for(int x = 1; x < eye_b.width(); x++){
					for(int y = 1; y < eye_b.height()/3; y++){//정사각형 위쪽 1/3지점을 floodfill//눈썹제거
						double colorArr[] = eye_b.get(y,x);
						if (colorArr != null && colorArr[0] < 10){
							Imgproc.floodFill(eye_b, Mat.zeros(eye_b.rows() + 2, eye_b.cols() + 2, CvType.CV_8U), new Point(x,y), new Scalar(255,255,255));
							//Log.e("WINK", "-------플러드 필 진행함-------");
			    			//break Floodfill;
		            	}
		            }
		        }
			}
			maxH = 0;
			for(int x = 1; x < eye_b.width(); x++){
				int maxHtemp = 0;
				for(int y = 1; y < eye_b.height(); y++){
					double colorArr[] = eye_b.get(y,x);
					if (colorArr != null && colorArr[0] < 10) maxHtemp++;
					if (maxHtemp > maxH) maxH = maxHtemp;
				}
			}
			//Log.d("WINK", "maxH = "+maxH);
			
			if(learn_course == 1){//윙크 머신러닝
				
	        	if(global.isClickEyeLeft){
	        		sayText(9);
	        	}else{
	        		sayText(10);
	        	}
	        	Log.e("WINK", "윙크 머신러닝 중");
	        	if(maxH != -1)maxH_list.add(maxH);
	        	if(maxH_list.size() > learn_framesMAX){
	        		maxH_avg = mode(maxH_list);
	        		maxH_b = Collections.min(maxH_list);
	        		maxH_t = Collections.max(maxH_list);
	        		//Log.i("TEST", "maxH_list = "+maxH_list);
	        		maxH_list.clear();
	        		learn_course ++;
	        	}
	        	
	        }else if(learn_course == 4){//maxH_t는 당연히 일반눈이 나온다
	        	if(isMainEye){
					if(maxH <= maxH_avg) isBlink = true;
				}else{
					if(maxH < maxH_avg) isBlink = true;//maxH_avg==0시 기준 감으면 무조건 윙크
				}
	        	
				Log.i("WINK", "maxH = "+maxH+", maxH_t = "+maxH_t+", maxH_avg = "+maxH_avg+", maxH_b = "+maxH_b);
	        }
			
			
			/*
			 * //final double r = eye_a_area.height / 12;
			 * if(isMainEye){
				if(maxH <= 4 * r) isBlink = true;//3~4로 하면 윙크는 인식이 잘 되는데, 아래 보는거도 윙크로 인식
			}else{
				if(maxH <= 2 * r) isBlink = true;//메인눈이 감기면, 다른 눈이 뜰 수 있는 최대 높이는 감소. -> 윙크인식을 둔화시켜야
			}
			Log.i("WINK", "maxH = "+maxH+", maxH_b = "+maxH_b+", maxH_t = "+maxH_t);
			//Log.i("WINK", "isMainEye = "+isMainEye+", maxH = "+maxH+", isEmpty = "+isEmpty);*/
			
			
			//Log.e("MOVE", "eye_a_area = ("+eye_a_area.width+", "+eye_a_area.height+")");
			/*if(!isBlink){//윙크검사 통과시 진행
				Log.e("WINK", "---------");
			}else{
				Log.i("WINK", "---wink--");
			}*/
			
			//Core.rectangle(eye_b, eye_a_area.tl(), eye_a_area.br(),new Scalar(100, 100, 100, 255), 1);//추정된 눈영역 리턴
			//Imgproc.cvtColor(eye_b, eye, Imgproc.COLOR_GRAY2RGBA);
			return isBlink;
		}
		
		
	    private TextToSpeech myTTS;
	    private int prevErr = 0;//0:에러 없음, 1:눈 범위 밖, 2:얼굴 못찾음
	    public void sayText(int currentErr) {
	    	if(prevErr != currentErr){
	    		String err = "!";
	    		switch(currentErr){
	    		case 1:
	    			err = getString(R.string.st_fout);
	    			break;
	    		case 2:
	    			err = getString(R.string.st_close);
	    			break;
	    		case 3:
	    			err = getString(R.string.st_far);
	    			break;
	    		case 4:
	    			err = getString(R.string.st_bright);
	    			break;
	    		case 5:
	    			err = getString(R.string.st_dark);
	    			break;
	    		case 6:
	    			err = getString(R.string.st_learn_mid);
	    			break;
	    		case 7:
	    			err = getString(R.string.st_learn_up);
	    			break;
	    		case 8:
	    			err = getString(R.string.st_learn_down);
	    			break;
	    		case 9:
	    			err = getString(R.string.st_learn_lWink);
	    			break;
	    		case 10:
	    			err = getString(R.string.st_learn_rWink);
	    			break;
	    		case 69:
	    			err = getString(R.string.st_learn_end);
	    			break;
	    		}
	    		myTTS.speak(err, TextToSpeech.QUEUE_FLUSH, null);
	    		prevErr = currentErr;
	    	}else{
	    		//계속 사태가 계속되는경우 계속 말한다. 너무 시끄러워서 주석
	    		/*switch(currentTTSType){
	    		case 1:
	    			myTTS.speak(tts1, TextToSpeech.QUEUE_ADD, null);
	    			break;
	    		case 2:
	    			myTTS.speak(tts2, TextToSpeech.QUEUE_ADD, null);
	    			break;
	    		}*/
	    	}
	    }
	    
	    public void alertNotFound(Mat gray, Rect prev_face_area){//mat.type=0
			//Mat face = mGray.submat(prev_face_area);
			double gray_p = 0;//face_p = 0, 
			//face_p = getColorAvg(face, 5);
			gray_p = getColorAvg(gray, 5);
			
			
			if(gray_p > 60){
				final int outer_pixel_range = 30;
				if(prev_face_area.tl().x < outer_pixel_range
						|| prev_face_area.tl().y < outer_pixel_range
						|| prev_face_area.br().x > gray.width() - outer_pixel_range
						|| prev_face_area.br().y > gray.height() - outer_pixel_range){
					Log.e("ERROR", "err1 = 얼굴이 화면 밖으로 나갔단다.");
					sayText(1);
				}else if(prev_face_area.height > 0.7*gray.height()){//테두리에 거의 닿았을 때
					Log.e("ERROR", "err2 = 얼굴이 너무 가까이에 있어.");
					sayText(2);
				}else if(prev_face_area.height < 0.55*gray.height()){//최소 측정기준이 높이의 0.5배이므로 0.5보다는 커야한다
					Log.e("ERROR", "err3 = 얼굴이 너무 멀리 있어.");
					sayText(3);
				}else{
					Log.e("ERROR", "err4 = 너무 밝아! 빛이 너의 앞에 오도록 하렴.");
					sayText(4);
				}
				//Log.d("ERROR", "gray_p - face_p = "+ (double)(gray_p - face_p));
				/*if(face_p > 100){
					Log.e("ERROR", "err3 = 화면에 얼굴이 전부 나오도록 하렴.");//화면에 얼굴이 안나옴//이전 얼굴영역의 물체에 따라 랜덤함
				}else{
					Log.e("ERROR", "err1 = 카메라 위로 더 올려서, 광원이 너의 앞에 오도록 하렴.");//렌즈플레어//밝거나 오히려 대비로 더 어둡거나
				}*/
			}else{
				Log.e("ERROR", "err5 = 너무 어두워");//광원부족
				sayText(5);
			}
			Log.d("ERROR", "prev_face_area.height = "+prev_face_area.height +", 0.5*gray.height() = " + 0.5*gray.height());
			//Log.d("TAG", "tl = "+prev_face_area.tl()+", br = "+prev_face_area.br());
			//Log.d("TAG", "face_p = "+face_p);
			//Log.d("TAG", "gray_p = "+gray_p);
			//Log.d("TAG", "white_gray_p = "+white_gray_p);
		}
	    
	    private double getColorAvg(Mat mat, int roughRate) {
			double p = 0;
			//final int roughRate = 5;//샘플링 할 픽셀 간격
			int size = mat.width()*mat.height()/(roughRate*roughRate);
			for(int x = 1; x < mat.width()/roughRate; x++){
				for(int y = 1; y < mat.height()/roughRate; y++){
					double colorArr[] = mat.get(y*roughRate,x*roughRate);
					p += colorArr[0];
				}
			} p /= size;
			return p;
		}
	    
	    private double getThreshRatio1000(Mat eye_a, Mat eye_b, int roughRate) {
			double p = 0;
			if(eye_a.width() != eye_b.width() || eye_a.height() != eye_b.height()) return 0;
			//int size = mat.width()*mat.height()/(roughRate*roughRate);
			int a = 0, b = 0;
			for(int x = 1; x < eye_a.width()/roughRate; x++){
				for(int y = 1; y < eye_a.height()/roughRate; y++){
					double eye_aArr[] = eye_a.get(y*roughRate,x*roughRate);
					double eye_bArr[] = eye_b.get(y*roughRate,x*roughRate);
					if(eye_aArr[0] < 10) a++;
					if(eye_bArr[0] < 10) b++;
					
				}
			}
			return a==0 ? 0 : 1000*b/a;
		}
	    
	    private double getColorAvgWithThresh(Mat mat, Mat thresh, int roughRate) {
			double p = 0;
			if(mat.width() != thresh.width() || mat.height() != thresh.height()) return 0;
			//int size = mat.width()*mat.height()/(roughRate*roughRate);
			int size = 0;
			for(int x = 1; x < mat.width()/roughRate; x++){
				for(int y = 1; y < mat.height()/roughRate; y++){
					double colorArr[] = mat.get(y*roughRate,x*roughRate);
					double threshArr[] = thresh.get(y*roughRate,x*roughRate);
					if(threshArr[0] > 200){
						size ++;
						p += colorArr[0];
					}
					
				}
			} p /= size;
			return p;
		}
	    
	    @Override
		public void onInit(int status) {
			// Auto-generated method stub
		}
	    
	    /*private void toastShowUI(final String message){
	    	new Handler(Looper.getMainLooper()).post(new Runnable() { // new Handler and Runnable
	            @Override
	            public void run() {
	                toastShow(message);
	            }
	    	});
	    }
	    
	    private void toastShow(String message) {
	    	if (toast == null) {
	    		toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
	    	}else{
	    		toast.setText(message);
	    	}
	    	toast.setGravity(Gravity.CENTER, 0, 0);
	    	toast.show();
	    }
	    	 
	    //toastShowUI("말할 메시지"); */
	    
	    private int mode(ArrayList<Integer> arr){
	    	// arr에서 최빈값을 반환
	    	ArrayList<Integer> freqArr = new ArrayList<Integer>();
	    	for (int k = 0; k < arr.size(); k++){
        		freqArr.add(Collections.frequency(arr, arr.get(k)));
            }
	    	
	    	//arr.indexOf(object)
	    	//Collections.frequency(arr, 2);
	    	return arr.get(freqArr.indexOf(Collections.max(freqArr)));
	    }
	    
	    /* 여기서 얼굴, 동공 인식관련 변수를 GlobalSettings에 보내고
	     * 그곳에서 최종적으로 STATUS라는 global String을 통해
	     * ActionService와 UnlockService가 판단후 각각의 액션을 최한다
	     * */
}
