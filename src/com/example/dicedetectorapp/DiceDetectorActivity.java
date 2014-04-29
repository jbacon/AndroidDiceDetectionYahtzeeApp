package com.example.dicedetectorapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.*;

import com.example.tools.*;

import android.support.v4.app.Fragment;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Build;

public class DiceDetectorActivity extends Activity implements CvCameraViewListener2, View.OnClickListener, View.OnTouchListener {

	public static final String TAG	= "com.example.dicedetctorapp.MainActivity";
	
	//Instances Variables for UI
	private PreviewView 		mOpenCvCameraView;
	private List<Camera.Size> 	previewResolutionsList;
    private View 				controlsView;
    private View				cameraPreviewView;
    private ViewGroup 			detectionViewGroup;
    private ImageView[]			DiceObject = new ImageView[5];
    private Button				acceptDiceDetection;
    private Button				retryDiceDetection;
    private AlertDialog.Builder dialogBuilderPreview;
	private ArrayAdapter<String> previewAdapter;
	private AlertDialog			previewResolutionSelector;
	
	//Instance Variables for UiHider
	private SystemUiHider 			mSystemUiHider;
	private static final boolean 	AUTO_HIDE = false;
	private static final int 		AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final boolean 	TOGGLE_ON_CLICK = true;
	private static final int 		HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	
	
	//Instance Variables for detectDice()
	private static boolean		filterByColor = false;
	private static boolean 	filterByNoise = true;
	private static boolean		contourEllipseMethod = true;
	private static boolean		houghMethod = false;
	
	//Instance Variables for filterByColor()
	private boolean		filterOutBrightColors = true;
	private static int	thresholdBrightColors = 180;	//Higher value up to 255 will result in brighterColors
	private static int 	thresholdDarkColors = 55;		//Lower will result in darker colors
	private Mat			filterColorIntermediate;
	private Mat			filterNoiseIntermediate;
	
	//Instance Variables for onCameraFrame();
	private boolean 	pauseDetection = false;
	private Mat 		gray;
	private Mat 		rgba;
	private Mat 		element;
	private Size 		kSize;
	private int 		size;
	private Point 		anchorPoint;
	private Mat 		gaussianIntermediate;
	private Mat 		convertIntermediate;
	private Mat 		dilateIntermediate;
	private Mat 		erodeIntermediate;
	private Mat 		thresholdIntermediate;
	private RotatedRect	ellipseRotRect;	//Used for ellipse filtering
	private double 		maxPipThreshold = 1.18;
	private double 		minPipThreshold = .82;
	private double 		maxPipAreaRatioThreshold = .006;
	private double 		maxPipArea;
	private double 		minPipAreaRatioThreshold = .0005;
	private double 		minPipArea;
	private Scalar 		color;
	

	
	//Instance Variables for findingDiceValues()
	private List<DiceObject> accumulatedDiceList = new ArrayList<DiceObject>();
	private ArrayList<Integer> finalDiceValueList = new ArrayList<Integer>();
	
	//Instance Variables for Circle/DiceObject Analysis
	private int frameCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dice_detector);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		controlsView = findViewById(R.id.fullscreen_content_controls);
		cameraPreviewView = findViewById(R.id.cameraPreviewView);
		controlsView.setVisibility(View.VISIBLE);
		detectionViewGroup = (ViewGroup) findViewById(R.id.detectionLayout);
		DiceObject[0] = (ImageView) findViewById(R.id.dice1);
		DiceObject[1] = (ImageView) findViewById(R.id.dice2);
		DiceObject[2] = (ImageView) findViewById(R.id.dice3);
		DiceObject[3] = (ImageView) findViewById(R.id.dice4);
		DiceObject[4] = (ImageView) findViewById(R.id.dice5);
		acceptDiceDetection = (Button) findViewById(R.id.action_accept);
		retryDiceDetection = (Button) findViewById(R.id.action_retry);
		acceptDiceDetection.setOnClickListener(this);
		retryDiceDetection.setOnClickListener(this);
		initOpenCvCameraView();
		initSystemUiHider();
	}
	
	/**
	 * Initializes the openCv camera view for the activity UI
	 */
	private void initOpenCvCameraView() {
		mOpenCvCameraView = (PreviewView) findViewById(R.id.cameraPreviewView);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);

	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	            case LoaderCallbackInterface.SUCCESS:
	                Log.i("TTT", "OpenCV loaded successfully");
	              //Selects a resolution that near the lower 4th of the supported camera preview resolutions list (Lower Resolution)
	        		mOpenCvCameraView.enableView();
	        		break;
	            default:
	                super.onManagerConnected(status);
	                break;
	        }
	    }
	};
	
	/**
	 * Initializes OpenCV library
	 */
	protected void onResume() {
		super.onResume();
	    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mLoaderCallback);
	}
	
	/**
	 * Disables the mOpenCvCameraView
	 */
	protected void onDestory() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	}
	
	/**
	 * Disables the mOpenCvCameraView and unregisters the sensor listener
	 */
	protected void onPause() {
		super.onPause();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_activity_dice_detector, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) {
			case R.id.action_cancel:
				finish();
				break;
			case R.id.action_preview_resolutions:
				previewResolutionSelector.show();
				break;
			case R.id.color_contrast_method:
				break;
			case R.id.filter_out_bright:
				break;
			case R.id.filter_out_dark:
				break;
			case R.id.noise_reduction_method:
				break;
			case R.id.hough_method:
				break;
			case R.id.action_settings:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.cameraPreviewView:
			if (TOGGLE_ON_CLICK) {
				mSystemUiHider.toggle();
			} else {
				mSystemUiHider.show();
			}
			break;
		case R.id.action_accept:
			Intent intent = new Intent(this, YahtzeeActivity.class);
			intent.putIntegerArrayListExtra("DICE_LIST", finalDiceValueList);
			this.startActivity(intent);
			break;
		case R.id.action_retry:
			pauseDetection = false;
			detectionViewGroup.setVisibility(View.GONE);
			break;
		}
	}
	
	/**
	 * Used to delay hiding the system UI after activity UI has been created
	 * To reveal to the user the hidden system UI controls
	 */
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(100);
	}
	/**
	 *  Touch listener to use for in-layout UI controls. Delays hiding the system
	 *	UI to prevent jarring behavior of controls while interacting with activity UI.
	*/
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (AUTO_HIDE) {
			delayedHide(AUTO_HIDE_DELAY_MILLIS);
		}
		return false;
	}
	
	/**
	 * When camera view starts, the Sepia kernel matrix is initialized for use in the onCameraFrame() method
	 * Several matrix' objects are created for use in onCameraFrame();
	 */
	public void onCameraViewStarted(int width, int height) {
		maxPipArea = (width * height) * maxPipAreaRatioThreshold;
		minPipArea = (width * height) * minPipAreaRatioThreshold;
		size = height/200;	//Bigger size means bigger kernal size and more erosion/dilation
		kSize = new Size(size + 1, size + 1);
		anchorPoint = new Point(size,size);
		color = new Scalar(0, 0, 255);	//Blue Color
		previewResolutionsList = mOpenCvCameraView.getPreviewResolutionsList();
		mOpenCvCameraView.setPreviewResolution(previewResolutionsList.get((int)(previewResolutionsList.size() * .5)));
		
        //Now that the mOpenCvCameraView has been initialized I can now create 
  		//get and use the .getParameters method of the view to create alert dialogs...
  		//This needs to be done here, because onCreateOptionsMenu might fire before mOpenCvCamera is created.
  		//Initializes the selector for Preview Resolution button
  	    previewResolutionsList = mOpenCvCameraView.getPreviewResolutionsList();
  	    ListIterator<Camera.Size> previewResolutionItr = previewResolutionsList.listIterator();
  	    previewAdapter = new ArrayAdapter<String>(this, 
  	    		android.R.layout.select_dialog_singlechoice);
  	    while(previewResolutionItr.hasNext()) {
  	    	Camera.Size element = previewResolutionItr.next();
  	    	previewAdapter.add(Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
  	    }
  	    dialogBuilderPreview = new AlertDialog.Builder(this);
  	    dialogBuilderPreview.setCancelable(true);
  	    dialogBuilderPreview.setTitle("Choose Preview Resolution");
  	    dialogBuilderPreview.setAdapter(previewAdapter, new DialogInterface.OnClickListener() {
          	@Override
          	public void onClick(DialogInterface dialog, int which) {
          		mOpenCvCameraView.setPreviewResolution(previewResolutionsList.get(which));
          	}
        });
        previewResolutionSelector = dialogBuilderPreview.create();
	}
	
	/**
	 * Deallocates/releases Matrix' used for the camera view (mZoomWindow,mGrayInnerWindow, mRgba, mIntermediateMat)
	 */
	public void onCameraViewStopped() {
        // Explicitly deallocate Mats

	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		frameCount++;
		gray = inputFrame.gray();
		rgba = inputFrame.rgba();
		if(!pauseDetection) {
			detectDice();
		}
		return rgba;
	}
	
	private void detectDice() {
		if(filterByColor) {
			filterByColor();
		}
		if(filterByNoise) {
			filterByNoise();
		}
		if(houghMethod) {
			houghCircleDetection();
		}
		else if(contourEllipseMethod) {
			contourEllipseDetection();
		}
		if(frameCount%80 == 0) {
			//finalDiceValueList.clear();
			finalDiceValueList.clear();
			//Yahtzee requires 5 DiceObject
			if(accumulatedDiceList.size() >= 5) {
				Collections.sort(accumulatedDiceList);
				//Gets the 5 DiceObject that appear across the most frames
				for(int i = accumulatedDiceList.size() - 1; i > accumulatedDiceList.size() - 6; i--) {
					DiceObject DiceObject = accumulatedDiceList.get(i);
					if(DiceObject.getNumFramesDetected() > 60) {
						finalDiceValueList.add(DiceObject.getNumPips());
					}
					if(finalDiceValueList.size() == 5) {
						pauseDetection = true;
						this.runOnUiThread(new Runnable() {
							public void run() {
								diceDetected();
							}
						});
					}
				}
			}
			accumulatedDiceList.clear();
		}
		
		
		/*
		//Mat rgb = new Mat();
		//Imgproc.cvtColor(rgba, rgb, Imgproc.COLOR_RGBA2RGB);
		//Mat intermediateMedBlur = new Mat();
		
		//Imgproc.medianBlur(rgb, intermediateMedBlur, size+1);
		//dilateIntermediate = new Mat();
		//element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, kSize, anchorPoint);
		//Imgproc.dilate(rgb, dilateIntermediate, element); //DiceObject dissed4appear further away from camera

		//Mat rgb2 = new Mat();
		//Core.inRange(rgb, new Scalar(0, 0, 0), new Scalar(50, 50, 50), rgb2);
		
		
		//Cleanup image for better contour analysis
		//Uses a separate intermediate Mat for each filter, because it would not work with just one 
		//intermediate Mat and caused flickering camera preview
		element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, kSize, anchorPoint);
		//blurIntermediate = new Mat();
		//Imgproc.blur(convertIntermediate, blurIntermediate, new Size(size,size));
		gaussianIntermediate = new Mat();
		Imgproc.GaussianBlur(gray, gaussianIntermediate, new Size(size*2 + 1, size*2+1), 4);
		dilateIntermediate = new Mat();
		Imgproc.dilate(gaussianIntermediate, dilateIntermediate, element); //DiceObject dissed4appear further away from camera
		erodeIntermediate = new Mat();
		Imgproc.erode(dilateIntermediate,erodeIntermediate,element);	//DiceObject seen from further away
		thresholdIntermediate = new Mat();
		Imgproc.threshold(erodeIntermediate, thresholdIntermediate, 0, 255, Imgproc.THRESH_BINARY|Imgproc.THRESH_OTSU);	
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		//Find contours of frame and then finds all the 
		//contours that are "pips" of the DiceObject by using fitEllipse on each contour.
		Imgproc.findContours(thresholdIntermediate.clone(), contours, new Mat(), Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_NONE);
		//Find rotated rectangles and ellipses for each contour
		for(int i = 0; i < contours.size(); i++) {
			MatOfPoint2f contourPoints = new MatOfPoint2f(contours.get(i).toArray());
			if(contourPoints.toArray().length > 5) {
				ellipseRotRect = Imgproc.fitEllipse(contourPoints);
				if(ellipseShapeOfPip(ellipseRotRect)) {
					addToADice(ellipseRotRect);
					if(addToADice(ellipseRotRect) == true) {
						Core.ellipse(rgba, ellipseRotRect,  color, 2);
					}
				}
			}
		}
		/*
		//Iterates through DiceObject List and prints text to screen of the most probable actual
		//face values of the DiceObject (Which are the DiceObject that appear on each frame the
		//most times within 40 frames
		if(accumulatedDiceList.size() > 0){
			for(int i = 0; i < accumulatedDiceList.size(); i++) {
				DiceObject DiceObject = accumulatedDiceList.get(i);
				if(accumulatedDiceList.get(i).getNumFramesDetected() > 15) {
					Core.putText(rgba, "DiceObject: "+DiceObject.getNumPips(), DiceObject.getDiceCenter(), Core.FONT_HERSHEY_PLAIN, (double)2, color, 5);
				}
			}
		}
		if(frameCount%10 == 0) {
			for(int i = 0; i < accumulatedDiceList.size(); i++) {
				if(accumulatedDiceList.get(i).getNumFramesDetected() < 5) {
					accumulatedDiceList.remove(i);
				}
			}
		}
		//Every 40 frames detect the DiceObject 
		if(frameCount%40 == 0) {
			for(int i = 0; i < accumulatedDiceList.size(); i++) {
				if(accumulatedDiceList.get(i).getNumFramesDetected() < 20) {
					accumulatedDiceList.remove(i);
				}
			}
		}
	
		if(frameCount%80 == 0) {
			//finalDiceValueList.clear();
			finalDiceValueList.clear();
			//Yahtzee requires 5 DiceObject
			if(accumulatedDiceList.size() >= 5) {
				Collections.sort(accumulatedDiceList);
				//Gets the 5 DiceObject that appear across the most frames
				for(int i = accumulatedDiceList.size() - 1; i > accumulatedDiceList.size() - 6; i--) {
					DiceObject DiceObject = accumulatedDiceList.get(i);
					if(DiceObject.getNumFramesDetected() > 60) {
						finalDiceValueList.add(DiceObject.getNumPips());
					}
					if(finalDiceValueList.size() == 5) {
						pauseDetection = true;
						this.runOnUiThread(new Runnable() {
							public void run() {
								diceDetected();
							}
						});
					}
				}
			}
			accumulatedDiceList.clear();
		}
		*/
	}
	
	//Applies image processing by filtering colors
	private void filterByColor() {
		Mat rgb = new Mat();
		Imgproc.cvtColor(rgba, rgb, Imgproc.COLOR_RGBA2RGB);
		Mat rgb2 = new Mat();
		if(filterOutBrightColors)
			Core.inRange(rgb, new Scalar(0, 0, 0), new Scalar(thresholdDarkColors, thresholdDarkColors, thresholdDarkColors), rgb2);
		else
			Core.inRange(rgb, new Scalar(thresholdBrightColors, thresholdBrightColors, thresholdBrightColors), new Scalar(255, 255, 255), rgb2);
		Imgproc.cvtColor(rgb2, filterColorIntermediate, Imgproc.COLOR_RGB2RGBA);
	}
	
	//Applies noise reduction techniques to make contours and edge detection
	//Smoother and more accurate in some cases.
	private void filterByNoise() {
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, kSize, anchorPoint);
		Mat gaussianIntermediate = new Mat();
		if(filterByColor){
			Imgproc.GaussianBlur(filterColorIntermediate, gaussianIntermediate, new Size(size*2 + 1, size*2+1), 4);
		}
		else
			Imgproc.GaussianBlur(gray, gaussianIntermediate, new Size(size*2 + 1, size*2+1), 4);
		Mat dilateIntermediate = new Mat();
		Imgproc.dilate(gaussianIntermediate, dilateIntermediate, element); //DiceObject dissed4appear further away from camera
		Imgproc.erode(dilateIntermediate,filterNoiseIntermediate,element);	//DiceObject seen from further away
	}
	
	//Uses Canny Edge detection and the Hough algorithm to detect circle 
	//	objects in the Matrix. 
	//General circle filtering then determines which are Dice pips
	private void houghCircleDetection() {
		
	}
	
	//Finding contours involves forming shapes by joining curves that are formed
	//	by boundaries of continuous points.
	//Finding ellipse from the contours involves detecting curves/shapes that 
	//	contain 5 or more points. It find fits the smallest possible ellipse
	//	to the contour .
	//General ellipse filtering then determines which ellipse are the Dice pips
	private void contourEllipseDetection() {
		Mat thresholdIntermediate = new Mat();
		if(filterByColor) 
			Imgproc.threshold(filterColorIntermediate, thresholdIntermediate, 0, 255, Imgproc.THRESH_BINARY|Imgproc.THRESH_OTSU);
		else if(filterByNoise)
			Imgproc.threshold(filterNoiseIntermediate, thresholdIntermediate, 0, 255, Imgproc.THRESH_BINARY|Imgproc.THRESH_OTSU);			
		else
			Imgproc.threshold(rgba, thresholdIntermediate, 0, 255, Imgproc.THRESH_BINARY|Imgproc.THRESH_OTSU);
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		//Find contours of frame and then finds all the 
		//contours that are "pips" of the DiceObject by using fitEllipse on each contour.
		Imgproc.findContours(thresholdIntermediate.clone(), contours, new Mat(), Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_NONE);
		//Find rotated rectangles and ellipses for each contour
		for(int i = 0; i < contours.size(); i++) {
			MatOfPoint2f contourPoints = new MatOfPoint2f(contours.get(i).toArray());
			if(contourPoints.toArray().length > 5) {
				ellipseRotRect = Imgproc.fitEllipse(contourPoints);
				if(ellipseShapeOfPip(ellipseRotRect)) {
					addToADice(ellipseRotRect);
					if(addToADice(ellipseRotRect) == true) {
						Core.ellipse(rgba, ellipseRotRect,  color, 2);
					}
				}
			}
		}
	}
	
	private void diceDetected() {
				for(int i = 0; i < 5; i++) {
					switch(finalDiceValueList.get(i).intValue()) {
					case 1:
						DiceObject[i].setImageResource(R.drawable.dice1);
						break;
					case 2:
						DiceObject[i].setImageResource(R.drawable.dice2);
						break;
					case 3:
						DiceObject[i].setImageResource(R.drawable.dice3);
						break;
					case 4:
						DiceObject[i].setImageResource(R.drawable.dice4);
						break;
					case 5:
						DiceObject[i].setImageResource(R.drawable.dice5);
						break;
					case 6:
						DiceObject[i].setImageResource(R.drawable.dice6);
						break;
					}
				}
				detectionViewGroup.setVisibility(View.VISIBLE);
	}
	
	//private boolean is ellipseAlreadyDetected
	//Tries to add the pip to a DiceObject..
	//Returns False IF pip is not 
	private boolean addToADice(RotatedRect pipRect){
		//If DiceObject list is empty create new DiceObject
		//Return true
		if(accumulatedDiceList.size() == 0) {
			DiceObject newDice = new DiceObject(pipRect);
			accumulatedDiceList.add(newDice);
			return true;
		}
		//Find nearest DiceObject to pipRect from list
		DiceObject nearestDice = null;
		int nearestDiceIndex = 0;
		double distToNearestDice = -1;
		for(int i = 0; i < accumulatedDiceList.size(); i++) {
			double diceDistToPip = accumulatedDiceList.get(i).getDistFrom(pipRect.center);
			if(distToNearestDice > diceDistToPip || distToNearestDice == -1) {
				distToNearestDice = diceDistToPip;
				nearestDiceIndex = i;
			}
		}
		//If Pip is too far away from DiceObject to be part of it
		//Then make a new Dices
		nearestDice = accumulatedDiceList.get(nearestDiceIndex);
		double diceAvgPipWidth = nearestDice.getAveragePipDiameter();
		if(distToNearestDice > diceAvgPipWidth * 4) {
			DiceObject newDice = new DiceObject(pipRect);
			accumulatedDiceList.add(newDice);
			return true;
		}
		//If Area of pip is too large or too small compared to the size of the DiceObject pips
		//Then...
		//Return false because Pip is an outlier
		Point[] points = new Point[4];
		pipRect.points(points);
		double side1 = Math.sqrt(Math.pow(points[0].x - points[1].x, 2) + Math.pow(points[0].y - points[1].y, 2));
		double side2 = Math.sqrt(Math.pow(points[1].x - points[2].x, 2) + Math.pow(points[1].y - points[2].y, 2));
		double newPipArea = side1 * side2;
		if(newPipArea > nearestDice.getAveragePipArea() * 1.2 || newPipArea < nearestDice.getAveragePipArea() * .8) {	
			//Detected Pip is outlier
			return false;
		}
		//Find closest existing DiceObject pip to the detected pip.
		//If the new detected pip overlaps any of the existing pips,
		//The number of frames for the DiceObject are incremented
		List<Point> dicePips = nearestDice.getPipLocations();
		for(int i = 0; i < dicePips.size(); i++) {
			if(pipRect.center.x < dicePips.get(i).x + nearestDice.getAveragePipDiameter() 
					&& pipRect.center.x > dicePips.get(i).x - nearestDice.getAveragePipDiameter()
					&& pipRect.center.y < dicePips.get(i).y + nearestDice.getAveragePipDiameter()
					&& pipRect.center.y > dicePips.get(i).y - nearestDice.getAveragePipDiameter()) {	//The number .5 extremely impacts detections
				double diffX =  (pipRect.center.x - nearestDice.getPipCenter(i).x);
				double diffY = (pipRect.center.y - nearestDice.getPipCenter(i).y);
				double diffPipArea = newPipArea - nearestDice.getAveragePipArea();
				nearestDice.updateLocationOfDice(diffX, diffY, diffPipArea);
				nearestDice.incermentFrameCount();
				return true;
			}
		}
		if(nearestDice.getNumFramesDetected() < 10) {
			nearestDice.addPip(pipRect);
		}
		return true;
	}
	
	//Returns True IF ellipse is close to a perfect circle like a DiceObject Pip
	//Returns False IF ellipse is an oval and NOT a DiceObject pip
	private boolean ellipseShapeOfPip(RotatedRect rotatedRec) {
		Point[] points = new Point[4];
		rotatedRec.points(points);
		double side1 = Math.sqrt(Math.pow(points[0].x - points[1].x, 2) + Math.pow(points[0].y - points[1].y, 2));
		double side2 = Math.sqrt(Math.pow(points[1].x - points[2].x, 2) + Math.pow(points[1].y - points[2].y, 2));
		double ellipseArea = side1 * side2;
		//Calculate area of RotatedRect and find the side length of a square 
		//with the same area (Ideal length)
		double idealSideLength = Math.sqrt(side1 * side2);	
		double minSideLength = idealSideLength * minPipThreshold;
		double maxSideLength = idealSideLength * maxPipThreshold;
		if(side1 > minSideLength && side1 < maxSideLength
				&& side2 > minSideLength && side2 < maxSideLength
				&& ellipseArea < maxPipArea && ellipseArea > minPipArea) {
			return true;
		}
		return false;
	}
	
	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};
	
	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	/**
	 * Initializes the System UI hiding control for activity.
	 */
	private void initSystemUiHider() {
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, cameraPreviewView, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {	
			// Cached values.
			int mControlsHeight;
			int mShortAnimTime;
			@Override
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
			public void onVisibilityChange(boolean visible) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
					// If the ViewPropertyAnimator API is available
					// (Honeycomb MR2 and later), use it to animate the
					// in-layout UI controls at the bottom of the
					// screen.
					if (mControlsHeight == 0) {
						mControlsHeight = controlsView.getHeight();
					}
					if (mShortAnimTime == 0) {
						mShortAnimTime = getResources()
							.getInteger(android.R.integer.config_shortAnimTime);
					}
					controlsView
							.animate()
							.translationY(visible ? 0 : mControlsHeight)
							.setDuration(mShortAnimTime);
				} else {
					// If the ViewPropertyAnimator APIs aren't
					// available, simply show or hide the in-layout UI
					// controls.
					controlsView.setVisibility(visible ? View.VISIBLE
							: View.GONE);
				}
				if (visible && AUTO_HIDE) {
					// Schedule a hide().
					delayedHide(AUTO_HIDE_DELAY_MILLIS);
				}
			}
		});
		// Set up the user interaction to manually show or hide the system UI.
		cameraPreviewView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
