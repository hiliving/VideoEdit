package dev.yong.com.videoedit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import dev.yong.com.videoedit.movieEdit.FFmpegUtils;
import dev.yong.com.videoedit.movieEdit.TranscodeActivity;


public class MainActivity extends AppCompatActivity {

    private Button chose;
    private Button chosef;
    private String workLog=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chose = (Button) findViewById(R.id.test);
        chosef = (Button) findViewById(R.id.chosef);
        requestPermission();
        workLog = getApplicationContext().getFilesDir() + "/";
        chose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TranscodeActivity.class));
            }
        });
        chosef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commandStr = getResources().getString(R.string.commandText);
                FFmpegUtils.getInstance().renderMovie(commandStr,workLog,MainActivity.this);
            }
        });
    }

    private void requestPermission() {
        int checkCode = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int checkRead = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);
        //如果拒绝
        if (checkCode== PackageManager.PERMISSION_DENIED||checkRead==PackageManager.PERMISSION_DENIED){
            //申请权限
            if (checkCode==PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            }
            if (checkRead==PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
            }
        }else if (checkCode==PackageManager.PERMISSION_GRANTED){

        }
    }
}
