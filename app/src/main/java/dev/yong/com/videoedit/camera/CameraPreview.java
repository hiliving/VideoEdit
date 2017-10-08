package dev.yong.com.videoedit.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Helloworld on 2017/10/5.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context context;
    public CameraPreview(Context context,Camera camera) {
        super(context);
        this.mCamera = camera;
        this.context = context;
        mHolder= getHolder();
        mHolder.addCallback(this);
        mHolder.setSizeFromLayout();
        //
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
//            mCamera.setDisplayOrientation(0);
            followScreenOrientation(context,mCamera);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void followScreenOrientation(Context context, Camera camera){
        final int orientation = context.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(180);
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(90);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface()==null){
            return;
        }
        //停止预览，在改变状态前
       try {
           mCamera.stopPreview();
       }catch (Exception e){
          e.printStackTrace();
       }

       //状态更改后重新预览
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
