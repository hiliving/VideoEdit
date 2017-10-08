package dev.yong.com.videoedit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netcompss.ffmpeg4android.GeneralUtils;

import java.io.File;

import dev.yong.com.videoedit.movieEdit.FFmpegUtils;

/**
 * Created by Helloworld on 2017/10/4.
 */

public class LocalVideoCompress extends AppCompatActivity {

    private TextView tips;
    private TextView show;
    private EditText editText;
    private Button run;
    private StringBuilder builder;
    private String[] commond;
    private String workLog=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_layout);
        tips = (TextView) findViewById(R.id.tips);
        show = (TextView) findViewById(R.id.show);
        editText = (EditText) findViewById(R.id.et_comand);
        run = (Button) findViewById(R.id.run);
        editText.addTextChangedListener(textw);
        workLog = getApplicationContext().getFilesDir() + "/";
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FFmpegUtils.renderMovie(commond,workLog,LocalVideoCompress.this);
            }
        });
    }

    private Intent getVideoFileIntent(String s) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot",0);
        intent.putExtra("configchange",0);
        Uri uri = Uri.fromFile(new File(s));
        intent.setDataAndType(uri,"video/*");
        return intent;
    }

    TextWatcher textw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {


            String[] split = s.toString().split(" ");
            GeneralUtils.fixComplexCommand(split);
            show.setText(split.toString());
            builder = new StringBuilder();
            builder.append("{");
            for (int i = 0; i < split.length; i++) {
                builder.append("\""+split[i]+"\",");
                if (i==split.length-1){
                    builder.delete(builder.length()-1, builder.length());
                    builder.append("}");
                }

            }
            commond = split;
            show.setText(builder.toString());
            try {
                if ( GeneralUtils.isValidCommand(commond)){
                    show.setTextColor(Color.GREEN);
                    tips.setText("很好，输入格式正确");
                }else {
                    show.setTextColor(getResources().getColor(R.color.colorAccent));
                    tips.setText("格式有误，再仔细看看");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

}
