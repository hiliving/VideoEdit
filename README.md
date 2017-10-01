[![CircleCI](https://img.shields.io/circleci/project/github/RedSparr0w/node-csgo-parser.svg)]()
[![GitHub release](https://img.shields.io/github/release/qubyte/rubidium.svg)]()
[![GitHub release](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-Huangyong-ff69b4.svg)]()

## 安卓端集成可用的执行ffmpeg命令的库

安卓应用纯java代码编辑你需要的功能，不需要自己写C代码和NDK，只需要关注逻辑实现。

最新版本 ,[3.22_02 点击下载](http://pan.baidu.com/s/1hsvh0jy)。
提取密码：30hi
拓展库下载：[libx264](http://pan.baidu.com/s/1eSImjv0)
提取密码：8u1i

### [英文文档](https://github.com/hiliving/VideoEdit/blob/master/README_EN.md)
### 主要功能：
>文件处理
+ 1.视频压缩
    
        -s 控制视频分辨率（如160X320）
        -b 比特率（例如 150K）
        这两个参数直接决定压缩质量，分辨率高，比特率增加，都可以提升画质，但是同时也会增加文件体积。
        
        
        
         //常规的压缩代码
         ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -s 160x120 -r 25 -vcodec mpeg4 -b 150k -ab 48000 -ac 2 -ar 22050 /sdcard/videokit/out.mp4
         //使用h264编码，需要拓展库
         ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -vcodec libx264 -preset ultrafast -crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 320x240 -aspect 4:3 /sdcard/videokit/out3.mp4
   建议使用数组格式的命令，可以避免校验出现的问题。例如：
    
     String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/video kit/in.mp4","-strict","experimental","-s", "160x120","-r","25", "-vcodec", "mpeg4", "-b", "150k", "-ab","48000", "-ac", "2", "-ar", "22050", "/sdcard/video kit/out.mp4"};
   
    
    
+ 2.音频压缩
    
       String commandStr = "ffmpeg -y -i /sdcard/vk2/in.wav -ar 44100 -ac 2 -ab 64k -f mp3 /sdcard/videokit/out.mp3";
   音频裁剪
    
       String commandStr ={"ffmpeg","-y","-i","/storage/emulated/0/vk2/in.mp3","-strict","experimental","-acodec","copy","-ss","00:00:00","-t","00:00:03.000","/storage/emulated/0/videokit/out.mp3"};
+ 3.视频旋转
    
    旋转90度    
    
       ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -vf transpose=1 -s 160x120 -r 30 -aspect 4:3 -ab 48000 -ac 2 -ar 22050 -b 2097k /sdcard/video_output/out.mp4
+ 4.视频画面局部裁剪
    
    
       ffmpeg -y -i /sdcard/videokit/short.mp4 -strict experimental -vf crop=100:100:0:0 -s 320x240 -r 15 -aspect 3:4 -ab 12288 -vcodec mpeg4 -b 2097152 -sample_fmt s16 /sdcard/videokit/out.mp4
+ 5.从视频内提取一帧保存为图片
    
    
       ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -an -r 1/2 -ss 00:00:00.000 -t 00:00:03 /sdcard/videokit/filename%03d.jpg
+ 6.从视频中提取音频文件
    
    
       //示例1
       ffmpeg -y -i /sdcard/videokit/in.avi -strict experimental -acodec copy /sdcard/videokit/out.mp3
       //示例2
       ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -vn -ar 44100 -ac 2 -ab 256k -f mp3 /sdcard/videokit/out.mp3
+ 7.对视频中的音频重新编码
    
    
       ffmpeg -y -i /sdcard/in.mp4 -strict experimental -vcodec copy -acodec libmp3lame -ab 64k -ac 2 -b 1200000 -ar 22050 /sdcard/out.mp4
+ 8.改变视频的分辨率，4:3 or 16:9
    
    
       ffmpeg -y -i /sdcard/in.mp4 -strict experimental -vf transpose=3 -s 320x240 -r 15 -aspect 3:4 -ab 12288 -vcodec mpeg4 -b 2097152 -sample_fmt s16 /sdcard/out.mp4
+ 9.从视频中剪辑一段时间的视频
    
    
       ffmpeg -ss 00:00:01.000 -y -i /sdcard/videokit/in.mp4 -strict experimental -t 00:00:02.000 -s 320x240 -r 15 -vcodec mpeg4 -b 2097152 -ab 48000 -ac 2 -b 2097152 -ar 22050 /sdcard/videokit/out.mp4
+ 10.音频转码
    
    
       ffmpeg -y -i /sdcard/videokit/big.wav /sdcard/videokit/small.mp3
+ 11.视频添加水印
    
    
       //  test with watermark.png 128x128, add it to /sdcard/videokit/
       String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/videokit/in.mp4","-strict","experimental", "-vf", "movie=/sdcard/videokit/watermark.png [watermark]; [in][watermark] overlay=main_w-overlay_w-10:10 [out]","-s", "320x240","-r", "30", "-b", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050", "/sdcard/videokit/out.mp4"};
>流以及更多场景处理
+ 0.从安卓设备推流到PC设备，并播放。
    
    
     // APP中使用这条命令 ( 192.168.1.11 是电脑的IP)
     ffmpeg -i /sdcard/videokit/2.mpg -strict experimental -f mpegts udp://192.168.1.11:8090
     // (192.168.1.14 是安卓设备的 IP)
     ffplay -f mpegts -ast 1 -vst 0 -ar 48000 udp://192.168.1.14:8090 
     
+ 1.从相机预览获取原始格式的视频流。
    
    
         Parameters parameters = camera.getParameters();
         imageFormat = parameters.getPreviewFormat();
         if (imageFormat == ImageFormat.NV21) {
                        Camera.Size previewSize = parameters.getPreviewSize();
                        frameWidth = previewSize.width;
                        frameHeight = previewSize.height;
                        Rect rect = new Rect(0, 0, frameWidth, frameHeight);
                        YuvImage img = new YuvImage(data, ImageFormat.NV21, frameWidth, frameHeight, null);
                        try {
                                        outStream.write(data);
                                        outStream.flush();
                        }
         }
   执行编码命令
    
          
          "ffmpeg -f rawvideo -pix_fmt nv21 -s 640x480 -r 15 -i " + Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/yuv.data rtmp://host/stream.flv"
+ 2.从一台设备接收另一台设备的视频流，并保持为文件。
  【记得添加网络权限】
  
  
    
         //第一台设备：
         ffmpeg -i /sdcard/one3.mp4 -f mpegts udp://192.168.0.107:8090
         //
         //第二台设备：   
         String[] complexCommand = {"ffmpeg","-y" ,"-i", "udp://192.168.0.108:8090","-strict","experimental","-crf", "30","-preset", "ultrafast", "-acodec", "aac", "-ar", "44100", "-ac", "2", "-b:a", "96k", "-vcodec", "libx264", "-r", "25", "-b:v", "500k", "-f", "flv", "/sdcard/videokit/t.flv"};
+ 3.H264编码（需要拓展库）
    
    
         //例子1
         ffmpeg -y -i /sdcard/Video/1.MTS -strict experimental -vcodec libx264 -preset ultrafast -crf 24 /sdcard/videokit/out.mp4
         //例子2
         ffmpeg -y -i /sdcard/videokit/m.mkv -strict experimental -vcodec libx264 -preset ultrafast -crf 24 -sn /sdcard/videokit/m2.mkv
+ 4.添加字幕
    
        
         //例子1
         ffmpeg -y -i /sdcard/videokit/m2.mkv -i /sdcard/videokit/in.srt -strict experimental -vcodec libx264 -preset ultrafast -crf 24 -scodec copy /sdcard/videokit/mo.mkv
         //例子2
         ffmpeg -y -i /sdcard/videokit/m2.mkv -i /sdcard/videokit/in.srt -strict experimental -scodec copy /sdcard/videokit/outm3.mkv
+ 5.将一个mp3音频文件转为m4a格式
    
    
        ffmpeg -i /sdcard/videokit/in.mp3 /sdcard/videokit/out.m4a
+ 6.将一个视频和一个音频文件渲染为一个H.264编码的视频（需要额外的编码库）
    
    
        ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -vcodec libx264 -crf 24 -acodec aac /sdcard/videokit/out.mkv
+ 7.添加复古滤镜
    
    
      commandStr = "ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -vf curves=vintage -s 640x480 -r 30 -aspect 4:3 -ab 48000 -ac 2 -ar 22050 -b 2097k -vcodec mpeg4 /sdcard/videokit/curve.mp4";
+ 8.黑白滤镜
    
    
      commandStr = "ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -vf hue=s=0 -vcodec mpeg4 -b 2097152 -s 320x240 -r 30 /sdcard/videokit/out.mp4";
+ 9.色彩通道滤镜，类似PS中的颜色通道
    
    
      String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/videokit/sample.mp4","-strict", "experimental", "-filter_complex",
      "[0:v]colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131[colorchannelmixed];[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0[color_effect]",
      "-map", "[color_effect]","-map", "0:a", "-vcodec", "mpeg4","-b", "15496k", "-ab", "48000", "-ac", "2", "-ar", "22050","/sdcard/videokit/out.mp4"};
+ 10.添加用PS的曲线制作的滤镜，也就是可以自己制作滤镜
  在PS中，选择图像>调整>曲线，调出曲线窗口，或者直接按快捷键Ctrl+N，调出曲线窗口
  将调整好的效果导出为acv文件，将其路径添加到ffmpeg命令中
    
      
      String[] complexCommand={"ffmpeg","-y","-i","/storage/emulated/0/vk2/in.mp4","-strict","experimental","-vf","curves=psfile=/storage/emulated/0/videokit/sepia.acv","-b","2097k","-vcodec","mpeg4","-ab","48000","-ac","2","-ar","22050","/storage/emulated/0/videokit/out.mp4"}﻿
+ 11.为视频添加淡入和淡出过渡效果
    
    
      String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/videokit/in.m4v","-acodec", "copy", "-vf", "fade=t=in:st=0:d=5, fade=t=out:st=20:d=5", "/sdcard/videokit/out.mp4"};

+ 12.将两个同尺寸的视频文件拼接并输出
    
    
         String[] complexCommand = {"ffmpeg","-y","-i", "/sdcard/videokit/in1.mp4", "-i", "/sdcard/videokit/in2.mp4", "-strict","experimental", "-filter_complex", "[0:0] [0:1] [1:0] [1:1] concat=n=2:v=1:a=1", "/sdcard/videokit/out.mp4"};
        //
        //拼接不同编码，不同尺寸，不同帧率，不同纵横比的视频
        
         String[] complexCommand = {"ffmpeg","-y","-i","/storage/emulated/0/videokit/sample.mp4",
            "-i","/storage/emulated/0/videokit/in.mp4","-strict","experimental",
            "-filter_complex",
            "[0:v]scale=640x480,setsar=1:1[v0];[1:v]scale=640x480,setsar=1:1[v1];[v0][0:a][v1][1:a] concat=n=2:v=1:a=1",
            "-ab","48000","-ac","2","-ar","22050","-s","640x480","-r","30","-vcodec","mpeg4","-b","2097k","/storage/emulated/0/vk2_out/out.mp4"}

+ 13.将一组图片序列渲染为视频文件，图片必须按照规律的格式有序。
  此命令严格要求图片序列尺寸统一，对应输出视频的分辨率
    
       
       commandStr = "ffmpeg -y -r 1/5 -i /sdcard/videokit/pic00%d.jpg /sdcard/videokit/out.mp4";
       //添加音频，png也可以，这里的图片尺寸应该为320x240
       ffmpeg -y -r 1 -i /sdcard/videokit/pic00%d.jpg -i /sdcard/videokit/in.mp3 -strict experimental -ar 44100 -ac 2 -ab 256k -b 2097152 -ar 22050 -vcodec mpeg4 -b 2097152 -s 320x240 /sdcard/videokit/out.mp4
+ 14.Advanced filtering（这个功能从字面我没理解，待亲自验证后再解释）
    
    
        String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/videokit/in.mp4","-strict","experimental", "-vf", "crop=iw/2:ih:0:0,split[tmp],pad=2*iw[left]; [tmp]hflip[right]; [left][right] overlay=W/2", "-vb", "20M", "-r", "23.956", "/sdcard/videokit/out.mp4"};
+ 15.加快视频和音频文件播放速度，也就是时间压缩。
    
    
       String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/videokit/in.mp4","-strict","experimental", "-filter_complex", "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]","-map","[v]","-map","[a]", "-b", "2097k","-r","60", "-vcodec", "mpeg4", "/sdcard/videokit/out.mp4"};
+ 16.将两个视频并排显示
    
    
         String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/Movies/sample.mp4","-i", "/sdcard/Movies/sample2.mp4", "-strict","experimental",
                            "-filter_complex", 
                            "[0:v:0]pad=iw*2:ih[bg];" + 
                            "[bg][1:v:0]overlay=w",
                            "-s", "320x240","-r", "30", "-b", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050",
                            "/sdcard/videokit/out.mp4"};  
+ 17.添加时间水印
    
    
        String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/Movies/sample.mp4","-strict","experimental",
                        "-vf", 
                        "movie=/sdcard/videokit/watermark002.png [watermark];" + 
                        "[in][watermark] overlay=main_w-overlay_w-10:10 [out_overlay];" +
                        "[out_overlay]curves=vintage[out]",  
                        "-s", "320x240","-r", "30", "-b", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050",
                        "/sdcard/videokit/out_water_vinta.mp4"};
+ 18.加水印，音频替换
    
        
      String commandStr = "ffmpeg -y -loop 1 -i /sdcard/videokit/pic001.jpg -i /sdcard/videokit/in.mp3 -strict experimental -s 1270x720 -r 25 -aspect 16:9 -vcodec mpeg4 -vcodec mpeg4 -ab 48000 -ac 2 -b 2097152 -ar 22050 -shortest /sdcard/videokit/out2.mp4";
+ 19.更换视频音轨
    
    
        String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/videokit/sample.mp4","-i", "/sdcard/videokit/in.mp3", "-strict","experimental",
                        "-map", "0:v", "-map", "1:a",
                        "-s", "320x240","-r", "30", "-b", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050","-shortest","/sdcard/videokit/out.mp4"};
+ 20.GIF图片转视频，或者视频转为GIF图片
    
    
        //Compress animated gif:
        fmpeg -f gif -i /sdcard/videokit/pic1.gif -strict experimental -r 10 /sdcard/videokit/pic1_1.gif
        
        //Convert mp4 to animated gif:
        ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -r 20 /sdcard/videokit/out.gif
        
        //Convert animated gif to mp4:
        ffmpeg -y -f gif -i /sdcard/videokit/infile.gif /sdcard/videokit/outfile.mp4


# License


    Copyright 2017 Johnny Shieh Open Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.




