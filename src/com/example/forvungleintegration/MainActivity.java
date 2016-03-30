package com.example.forvungleintegration;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import java.io.File;

import com.vungle.publisher.AdConfig;
import com.vungle.publisher.EventListener;
import com.vungle.publisher.Orientation;
import com.vungle.publisher.VunglePub;

public class MainActivity extends Activity implements OnClickListener {

	// get the VunglePub instance
	final VunglePub vunglePub = VunglePub.getInstance();

	// buttons
	private ImageButton buttonPlayAd;
	private ImageButton buttonPlayAdOptions;
	private ImageButton buttonPlayAdIncentivized;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// get your App ID from the app's main page on the Vungle Dashboard after setting up your app
		final String app_id = "56fb92770c73c78f02000027";

		//clean the data files from app.
       // cleanFiles(this);

		// initialize the Publisher SDK
		vunglePub.init(this, app_id);

		vunglePub.setEventListeners(vungleDefaultListener, vungleSecondListener);

		// initialize buttons
		buttonPlayAd = (ImageButton) findViewById(R.id.button_play_ad);
		buttonPlayAdOptions = (ImageButton) findViewById(R.id.button_play_ad_options);
		buttonPlayAdIncentivized = (ImageButton) findViewById(R.id.button_play_ad_incentivized);

		boolean buttonsEnabled = vunglePub.isAdPlayable();
		setButtonState(buttonPlayAd, buttonsEnabled);
		setButtonState(buttonPlayAdIncentivized, buttonsEnabled);
		setButtonState(buttonPlayAdOptions, buttonsEnabled);

		// attach listener to buttons
		buttonPlayAd.setOnClickListener(this);
		buttonPlayAdOptions.setOnClickListener(this);
		buttonPlayAdIncentivized.setOnClickListener(this);
	}

//	public static void cleanFiles(Context context) { 
//		  File sdDir = Environment.getExternalStorageDirectory();//»ñÈ¡¸úÄ¿Â¼ 
//		  sdDir = new File(sdDir.getPath()+File.separator+"xxx"); 
//		  
//		  sdDir = new File(sdDir.getPath() +File.separator+ "files"); 
//		  
//		  deleteFilesByDirectory(sdDir); 
//	} 
//
//	private static void deleteFilesByDirectory(File sdDir) {
//		sdDir.delete();
//	}

	private void setButtonState(ImageButton button, boolean enabled) {
		button.setEnabled(enabled);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			button.setAlpha(enabled ? 1.0f : 0.5f);
		}
	}

	private final EventListener vungleDefaultListener = new EventListener() {
		@Deprecated
		@Override
		public void onVideoView(boolean isCompletedView, int watchedMillis, int videoDurationMillis) {
			// This method is deprecated and will be removed. Please use onAdEnd() instead.
		}

		@Override
		public void onAdStart() {
			// Called before playing an ad.
		}

		@Override
		public void onAdUnavailable(String reason) {
			// Called when VunglePub.playAd() was called but no ad is available to show to the user.
		}

		@Override
		public void onAdPlayableChanged(boolean isAdPlayable) {
			// Called when ad playability changes.
			Log.d("DefaultListener", "This is a default eventlistener.");
			final boolean enabled = isAdPlayable;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// Called when ad playability changes.
					setButtonState(buttonPlayAd, enabled);
					setButtonState(buttonPlayAdIncentivized, enabled);
					setButtonState(buttonPlayAdOptions, enabled);
				}
			});
		}

		@Override
		public void onAdEnd(boolean arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	private final EventListener vungleSecondListener = new EventListener() {
		// Vungle SDK allows for multiple listeners to be attached. This secondary event listener is only
		// going to print some logs for now, but it could be used to Pause music, update a badge icon, etc.
		@Deprecated
		@Override
		public void onVideoView(boolean isCompletedView, int watchedMillis, int videoDurationMillis) {}

		@Override
		public void onAdStart() {}

		@Override
		public void onAdUnavailable(String reason) {}

		@Override
		public void onAdPlayableChanged(boolean isAdPlayable) {
			Log.d("SecondListener", String.format("This is a second event listener! Ad playability has changed, and is now: %s", isAdPlayable));
		}

		@Override
		public void onAdEnd(boolean arg0) {
			// TODO Auto-generated method stub
		}
	};

	public void onClick(View view) {
		// Check if Vungle Ad is available
		if(!vunglePub.isAdPlayable()) {
			return;
		}

		switch ((view.getId())){
			// PLAY AD WITH DEFAULT OPTIONS
			case R.id.button_play_ad:
				PlayAd();
				break;

			// PLAY AD WITH CUSTOM OPTIONS
			case R.id.button_play_ad_options:
				PlayAdOptions();
				break;

			// PLAY INCENTIVIZED AD
			case R.id.button_play_ad_incentivized:
				PlayAdIncentivized();
				break;
		}
	}

	private void PlayAd() {
		vunglePub.playAd();
	}

	private void PlayAdOptions() {
		// create a new AdConfig object
		final AdConfig overrideConfig = new AdConfig();

		// set any configuration options you like.
		overrideConfig.setOrientation(Orientation.matchVideo);
		overrideConfig.setSoundEnabled(false);
		overrideConfig.setBackButtonImmediatelyEnabled(false);
		overrideConfig.setPlacement("StoreFront");
		//overrideConfig.setExtra1("LaunchedFromNotification");

		// the overrideConfig object will only affect this ad play.
		vunglePub.playAd(overrideConfig);
	}

	private void PlayAdIncentivized() {
		// create a new AdConfig object
		final AdConfig overrideConfig = new AdConfig();

		// set incentivized option on
		overrideConfig.setIncentivized(true);
		overrideConfig.setIncentivizedCancelDialogTitle("Careful!");
		overrideConfig.setIncentivizedCancelDialogBodyText("If the video isn't completed you won't get your reward! Are you sure you want to close early?");
		overrideConfig.setIncentivizedCancelDialogCloseButtonText("Close");
		overrideConfig.setIncentivizedCancelDialogKeepWatchingButtonText("Keep Watching");

		// the overrideConfig object will only affect this ad play.
		vunglePub.playAd(overrideConfig);
	}

	@Override
	protected void onPause() {
		super.onPause();
		vunglePub.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		vunglePub.onResume();
	}

	@Override
	protected void onDestroy() {
		// onDestroy(), remove eventlisteners.
		vunglePub.removeEventListeners(vungleDefaultListener, vungleSecondListener);
		super.onDestroy();
	}
}
