package dev.yong.com.videoedit.movieEdit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import com.netcompss.ffmpeg4android.CommandValidationException;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.ffmpeg4android.ProgressCalculator;
import com.netcompss.loader.LoadJNI;

/**
 * 传入命令，交与ffmpeg库处理
 * Created by HY on 2017/9/28.
 */

public class FFmpegUtils {
    private static String [] command = {"ffmpeg", "-y" ,"-i", "/sdcard/videokit/in.mp4","-ss","00:01:23.26","-r","1","-vframes","1","-an","-sn","-vcodec","/sdcard/videokit/out.mp4"};
    private static LoadJNI vk;
    private final int FINISHED_TRANSCODING_MSG = 0;
    private static final int STOP_TRANSCODING_MSG = -1;
    private static String vkLogPath;
    private static String workFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videokit/";
    private  static FFmpegUtils utils;
    private static ProgressDialog progressBar;

    public static void renderMovie(final String[] commandStr, final Context context){
        progressBar = new ProgressDialog(context);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setTitle("videoEdit 正在转码...");
        progressBar.setMessage("点取消按钮结束当前渲染队列");
        progressBar.setMax(100);
        progressBar.setProgress(0);
        if (vk==null){
            vk = new LoadJNI();
        }
        if (utils==null){
            utils = new FFmpegUtils();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    vk.run(commandStr, context.getApplicationContext().getFilesDir()+"/", context.getApplicationContext());
//                    vk.run(command, workLog, context.getApplicationContext());
                } catch (CommandValidationException e) {
                    e.printStackTrace();
                }
                //将日志文件拷贝到视频输出文件夹
                vkLogPath =context.getApplicationContext().getFilesDir() + "/"+ "vk.log";
                GeneralUtils.copyFileToFolder(vkLogPath, workFolderPath);
            }
        }).start();
        progressBar.setCancelable(false);
        progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (progressBar != null) {
                    progressBar.dismiss();
                    // stopping the transcoding native
                    vk.fExit(context.getApplicationContext());
                    Log.d(Prefs.TAG,"任务终止");
                }
            }
        });

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
                            Log.i(Prefs.TAG, "setting progress notification: " + progress );
                        }
                        else if (progress == 100) {
                            Log.i(Prefs.TAG, "==== progress is 100, exiting Progress update thread");
                            pc.initCalcParamsForNextInter();
                            progressBar.setProgress(100);
                            sleep(300);
                            progressBar.dismiss();
                            break;
                        }
                    }

                } catch(Exception e) {
                    Log.e("threadmessage",e.getMessage());
                }
            }
        }.start();
        progressBar.show();
    }
}
