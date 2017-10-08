package dev.yong.com.videoedit;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.security.Policy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dev.yong.com.videoedit.camera.CameraPreview;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


/**
 * Created by Helloworld on 2017/10/4.
 */

public class CaptureActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Button tackpicture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.capture_layout);
        tackpicture = (Button) findViewById(R.id.button_capture);
        initPreview();
    }

    private void initPreview() {
        mCamera = getCameraInstance(0);

        mPreview = new CameraPreview(this,mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        tackpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(shutter,null,mPicture);

            }
        });
    }
    Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            //mCamera.notify();
        }
    };
    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutPutMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream outputStream = new FileOutputStream(pictureFile);
                outputStream.write(data);
                outputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            camera.startPreview();
        }
    };
    private File getOutPutMediaFile(int mediaTypeImage) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"VideoEidt");
        //
        if (!file.exists()){
            if (!file.mkdirs()){
                return null;
            }
        }
        //
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (mediaTypeImage==MEDIA_TYPE_IMAGE){
            mediaFile = new File(file.getPath()+file.separator+"IMG_"+timeStamp+".jpg");
        }else if (mediaTypeImage == MEDIA_TYPE_VIDEO){
            mediaFile = new File(file.getPath()+file.separator+"VID_"+timeStamp+".mp4");
        }else {
            return null;
        }
        return mediaFile;
    }

    /**
     *
     * @param which 0或者1，后置，前置相机
     * @return
     */
    public  Camera getCameraInstance(int which){
        Camera camera = null;
        try {
            camera = Camera.open(which);
            List<Camera.Size> supportedResolutions = camera.getParameters().getSupportedPreviewSizes();
            for (int i = 0; i < supportedResolutions.size(); i++) {
                Log.d("TTTTTTTTTTT",supportedResolutions.get(i).width+"");
                Log.d("TTTTTTTTTTT",supportedResolutions.get(i).height+"");
            }
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            parameters.setPreviewSize(3840,2160);
            followScreenOrientation(CaptureActivity.this,camera,parameters);
            parameters.setJpegQuality(100);
            parameters.setPictureFormat(ImageFormat.JPEG);

            parameters.setPictureSize(3840,2160);
            camera.setParameters(parameters);

        }catch (Exception e){
            //camera 不可用
        }
        return camera;
    }
    public  Camera.CameraInfo getCameraInfo(int cameraId){
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId,cameraInfo);
        return cameraInfo;
    }
    public static void followScreenOrientation(Context context, Camera camera, Camera.Parameters parameters){
        final int orientation = context.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            parameters.setRotation(270);
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            parameters.setRotation(90);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCamera.release();
    }
}
