package com.phd3.onesecond;

import java.io.File;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayer extends Activity {

	private String path = "";
	public String link = "";
	static boolean b = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		link = "";
		setContentView(R.layout.videoplayer);
		setButtons();

		Intent i = getIntent();
		path = i.getStringExtra("filepath");

	}

	@Override
	public void onResume() {
		super.onResume();
		showVideo();
	}

	private void showVideo() {
		final VideoView mVideoView = (VideoView) findViewById(R.id.play);
		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.setVolume(0, 0);
				mp.setLooping(true);

			}
		});

		mVideoView.setVideoPath(path);
		MediaController m = new MediaController(this);
		mVideoView.setMediaController(m);

		mVideoView.start();
	}

	private void setButtons() {
		ImageButton share = (ImageButton) findViewById(R.id.share);
		ImageButton redo = (ImageButton) findViewById(R.id.redo);
		ImageButton upload = (ImageButton) findViewById(R.id.upload);

		share.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (link.equals("")) {
					SendTask s = new SendTask();
					s.execute(path);

					try {
						link = s.get();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}

				Intent sharingIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"Just a second");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, link);
				startActivity(Intent.createChooser(sharingIntent, "Share via"));

			}
		});

		redo.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(),
						AndroidVideoCapture.class);
				startActivityForResult(myIntent, 0);
				File f = new File(path);
				f.delete();

			}
		});

		upload.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (link.equals("")){
					
					SendTask s = new SendTask();
					s.execute(path);
					try {
						link = s.get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				Toast t = new Toast(getApplicationContext());
				t.makeText(getApplicationContext(),
						"Your blink has been posted!", Toast.LENGTH_SHORT)
						.show();

			}
		});

	}
}
