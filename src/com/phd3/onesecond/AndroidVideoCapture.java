package com.phd3.onesecond;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class AndroidVideoCapture extends Activity {

	private Camera myCamera;
	private MyCameraSurfaceView myCameraSurfaceView;
	private MediaRecorder mediaRecorder;
	private String filename = "";

	ImageButton myButton;
	SurfaceHolder surfaceHolder;
	boolean recording;
	static Semaphore lock = new Semaphore(0);
	TimerTask task;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		task = new TimerTask() {

			@Override
			public void run() {
				lock.release();
			}
		};
		recording = false;

		setContentView(R.layout.activity_main);
		

		// Get Camera for preview
		myCamera = getCameraInstance();
		if (myCamera == null) {
			Toast.makeText(AndroidVideoCapture.this, "Fail to get Camera",
					Toast.LENGTH_LONG).show();
		}
		

		myCameraSurfaceView = new MyCameraSurfaceView(this, myCamera);
		MyFrameLayout myCameraPreview = (MyFrameLayout) findViewById(R.id.videoview);
		myCameraPreview.addView(myCameraSurfaceView);

		LayoutInflater controlInflater = LayoutInflater.from(getBaseContext());
		View viewControl = controlInflater.inflate(R.layout.control, null);
		LayoutParams layoutParamsControl = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.addContentView(viewControl, layoutParamsControl);
		myButton = (ImageButton) findViewById(R.id.mybutton);
		myButton.setAlpha(170);
		myButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Timer t = new Timer();
				v.setClickable(false);
				v.setEnabled(false);
				startRecording();
				t.schedule(task, 1300);
				try {
					lock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				playVideo();
				stopRecording();
				
				
				finish();

			}
		});

	}
	
	


	private void playVideo() {
		Intent i=new Intent(getBaseContext(),VideoPlayer.class);
		i.putExtra("filepath", filename);
        startActivityForResult(i, 0);
		
		
	}

	private void startRecording() {

		// Release Camera before MediaRecorder start
		releaseCamera();

		if (!prepareMediaRecorder()) {
			Toast.makeText(AndroidVideoCapture.this,
					"Fail in prepareMediaRecorder()!\n - Ended -",
					Toast.LENGTH_LONG).show();
			finish();
		}

		mediaRecorder.start();
	}

	private void stopRecording() {
		// stop recording and release camera
		mediaRecorder.stop(); // stop the recording
		releaseMediaRecorder(); // release the MediaRecorder object

		// Exit after saved
	}

	private Camera getCameraInstance() {
		// TODO Auto-generated method stub
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private boolean prepareMediaRecorder() {
		myCamera = getCameraInstance();
		mediaRecorder = new MediaRecorder();

		myCamera.unlock();
		mediaRecorder.setCamera(myCamera);

		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		mediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_480P));
		File f = new File("/sdcard/JustASecond");
		if (!f.exists()){
			f.mkdir();
		}
		filename = "/sdcard/JustASecond/Clip"+System.currentTimeMillis()+".mp4";
		mediaRecorder.setOutputFile(filename);
		mediaRecorder.setMaxDuration(1000); // Set max duration 1 sec.
		mediaRecorder.setMaxFileSize(5000000); // Set max file size 5M
		mediaRecorder.setPreviewDisplay(myCameraSurfaceView.getHolder()
				.getSurface());

		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			releaseMediaRecorder();
			return false;
		}
		return true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaRecorder(); // if you are using MediaRecorder, release it
								// first
		releaseCamera(); // release the camera immediately on pause event
	}

	private void releaseMediaRecorder() {
		if (mediaRecorder != null) {
			mediaRecorder.reset(); // clear recorder configuration
			mediaRecorder.release(); // release the recorder object
			mediaRecorder = null;
			myCamera.lock(); // lock camera for later use
		}
	}

	private void releaseCamera() {
		if (myCamera != null) {
			myCamera.release(); // release the camera for other applications
			myCamera = null;
		}
	}

	public class MyCameraSurfaceView extends SurfaceView implements
			SurfaceHolder.Callback {

		private SurfaceHolder mHolder;
		private Camera mCamera;

		public MyCameraSurfaceView(Context context, Camera camera) {
			super(context);
			mCamera = camera;

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			// deprecated setting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format,
				int weight, int height) {
			// If your preview can change or rotate, take care of those events
			// here.
			// Make sure to stop the preview before resizing or reformatting it.

			if (mHolder.getSurface() == null) {
				// preview surface does not exist
				return;
			}

			// stop preview before making changes
			try {
				mCamera.stopPreview();
			} catch (Exception e) {
				// ignore: tried to stop a non-existent preview
			}

			// make any resize, rotate or reformatting changes here

			// start preview with new settings
			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();

			} catch (Exception e) {
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			// The Surface has been created, now tell the camera where to draw
			// the preview.
			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			} catch (IOException e) {
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub

		}
	}
}