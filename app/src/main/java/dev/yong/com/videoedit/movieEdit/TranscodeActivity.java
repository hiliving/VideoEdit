package dev.yong.com.videoedit.movieEdit;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.netcompss.ffmpeg4android.CommandValidationException;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.ffmpeg4android.ProgressCalculator;
import com.netcompss.loader.LoadJNI;

import dev.yong.com.videoedit.R;

public class TranscodeActivity extends Activity {
	
	public ProgressDialog progressBar;
	
	String workLog = null;
	String workFolderPath = null;//输入和输出的文件夹，工作目录，这个可以写死。
	String inputFile = null;//输入文件路径和名字
	String vkLogPath = null;
	LoadJNI vk;
	
	NotificationManager mNotifyManager;
	Notification.Builder mBuilder;
	private static int NOTIFICATION_ID = 0;
	private final int STOP_TRANSCODING_MSG = -1;
	private final int FINISHED_TRANSCODING_MSG = 0;
	private boolean commandValidationFailedFlag = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(Prefs.TAG, "onCreate ffmpeg4android TranscodeActivity");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ffmpeg_transcode_layout);

		//TODO 这里需要替换为可选的目录，或者可编辑输入路径，和文件
		workFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videokit/";
		inputFile = workFolderPath + "in.mp4";

		Log.i(Prefs.TAG, getString(R.string.app_name) + " version: " + GeneralUtils.getVersionName(getApplicationContext()) );

		Button invoke =  (Button)findViewById(R.id.invokeButton);
		invoke.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				Log.i(Prefs.TAG, "run clicked.");
				runTranscoding();
			}
		});

		workLog = getApplicationContext().getFilesDir() + "/";
		//Log.i(Prefs.TAG, "workLog: " + workLog);
		vkLogPath = workLog + "vk.log";

		GeneralUtils.copyLicenseFromAssetsToSDIfNeeded(this, workLog);
		GeneralUtils.copyDemoVideoFromAssetsToSDIfNeeded(this, workFolderPath);
		int rc = GeneralUtils.isLicenseValid(getApplicationContext(), workLog);
		Log.i(Prefs.TAG, "License check RC: " + rc);
	}

	
	private void runTranscodingUsingLoader() {
		Log.i(Prefs.TAG, "runTranscodingUsingLoader started...");

 		PowerManager powerManager = (PowerManager)TranscodeActivity.this.getSystemService(Activity.POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
		Log.d(Prefs.TAG, "Acquire wake lock");
		wakeLock.acquire();

 		EditText commandText = (EditText)findViewById(R.id.CommandText);
 		String commandStr = commandText.getText().toString();

 		///////////// Set Command using code (overriding the UI EditText) /////
 		// commandStr = "ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -s 320x240 -r 30 -aspect 4:3 -ab 48000 -ac 2 -ar 22050 -vcodec mpeg4 -b 2097152 /sdcard/videokit/out.mp4";
 		//String[] complexCommand = {"ffmpeg", "-y" ,"-i", "/data/data/com.examples.ffmpeg4android_demo/files/in.mp4","-strict","experimental","-s", "160x120","-r","25", "-vcodec", "mpeg4", "-b", "150k", "-ab","48000", "-ac", "2", "-ar", "22050", "/sdcard/videokit/out.mp4"};
 	 	///////////////////////////////////////////////////////////////////////


 		vk = new LoadJNI();
		try {
			//vk.run(complexCommand, workLog, getApplicationContext());

			// 不校验命令，直接执行，这个方法会忽略语法措辞，可能会渲染失败
			//vk.run(complexCommand, workLog, getApplicationContext(), false);

			vk.run(GeneralUtils.utilConvertToComplex(commandStr), workLog, getApplicationContext());

			Log.i(Prefs.TAG, "vk.run finished.");
			// copying vk.log (internal native log) to the videokit folder
			GeneralUtils.copyFileToFolder(vkLogPath, workFolderPath);
		} catch (CommandValidationException e) {
			Log.e(Prefs.TAG, "vk run exeption.", e);
			commandValidationFailedFlag = true;
		} catch (Throwable e) {
			Log.e(Prefs.TAG, "vk run exeption.", e);
		}
		finally {
			if (wakeLock.isHeld()) {
				wakeLock.release();
				Log.i(Prefs.TAG, "Wake lock released");
			}
			else{
				Log.i(Prefs.TAG, "Wake lock is already released, doing nothing");
			}
		}

		// finished Toast
		String rc = null;
		if (commandValidationFailedFlag) {
			rc = "Command Vaidation Failed";
		}
		else {
			rc = GeneralUtils.getReturnCodeFromLog(vkLogPath);
		}
		final String status = rc;
 		TranscodeActivity.this.runOnUiThread(new Runnable() {
			  public void run() {
				  Toast.makeText(TranscodeActivity.this, status, Toast.LENGTH_LONG).show();
				  if (status.equals("视频渲染失败")) {
					  Toast.makeText(TranscodeActivity.this, "查看: " + vkLogPath + " 获取更多信息.", Toast.LENGTH_LONG).show();
				  }
			  }
			});
	}

	private Handler handler = new Handler() {
        	@Override
        	public void handleMessage(Message msg) {
        		Log.i(Prefs.TAG, "Handler got message");
        		if (progressBar != null) {
        			progressBar.dismiss();
        			
        			// stopping the transcoding native
        			if (msg.what == STOP_TRANSCODING_MSG) {
        				Log.i(Prefs.TAG, "Got cancel message, calling fexit");
        				vk.fExit(getApplicationContext());
        			}
        		}
        }
    };
	
	public void runTranscoding() {
		  progressBar = new ProgressDialog(TranscodeActivity.this);
		  progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		  progressBar.setTitle("videoEdit 正在转码...");
		  progressBar.setMessage("点取消按钮结束当前渲染队列");
		  progressBar.setMax(100);
		  progressBar.setProgress(0);
		  
		  progressBar.setCancelable(false);
		  progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
		      @Override
		      public void onClick(DialogInterface dialog, int which) {
		    	  handler.sendEmptyMessage(STOP_TRANSCODING_MSG);
		      }
		  });
		  
		  progressBar.show();
		  prepareProgressNotification();
	      
	      new Thread() {
	          public void run() {
	        	  Log.d(Prefs.TAG,"Worker started");
	              try {
	            	  runTranscodingUsingLoader();
	                  handler.sendEmptyMessage(FINISHED_TRANSCODING_MSG);

	              } catch(Exception e) {
	                  Log.e("threadmessage",e.getMessage());
	              }
	          }
	      }.start();
	      
	      // Progress update thread
	      new Thread() {
	    	  ProgressCalculator pc = new ProgressCalculator(vkLogPath);
	          public void run() {
	        	  Log.d(Prefs.TAG,"Progress update started");
	        	  int progress = -1;
	        	  try {
	        		  while (true) {
	        			  sleep(300);
	        			  progress = pc.calcProgress();
	        			  if (progress != 0 && progress < 100) {
	        				  progressBar.setProgress(progress);
	        				  mBuilder.setProgress(100, progress, false);
	        				  Log.i(Prefs.TAG, "setting progress notification: " + progress );
	        				  try {
								mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
	        				  } catch (Exception e) {
								Log.i(Prefs.TAG, "Android 2.3 or below? " + e.getMessage());
	        				  }
	        			  }
	        			  else if (progress == 100) {
	        				  Log.i(Prefs.TAG, "==== progress is 100, exiting Progress update thread");
	        				  pc.initCalcParamsForNextInter();
	        				  mBuilder.setContentText("Transcoding complete")
	        		          // Removes the progress bar
	        		          .setProgress(0,0,false);
	        				  try {
									mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
	        				  } catch (Exception e) {
									Log.i(Prefs.TAG, "Android 2.3 or below? " + e.getMessage());
	        				  }
	        				  break;
	        			  }
	        		  }

	        	  } catch(Exception e) {
	        		  Log.e("threadmessage",e.getMessage());
	        	  }
	          }
	      }.start();
	  }

		
	private void prepareProgressNotification() {
		 mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
  	     mBuilder = new Notification.Builder(TranscodeActivity.this);
  	     mBuilder.setContentTitle("FFmpeg4Android")
  	    .setContentText("Transcoding in progress")
  	    .setSmallIcon(R.mipmap.ic_launcher_round);
	}
}
