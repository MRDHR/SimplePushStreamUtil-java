package com.dhr.simplepushstreamutil.runnable;

import java.io.*;

public class FindFfmpegPidRunnable implements Runnable {
    private final OutputStream ostrm_;
    private final InputStream istrm_;
    private FindFfmpegCallBack findFfmpegCallBack;
    private String m3u8Url;

    public FindFfmpegPidRunnable(InputStream istrm, OutputStream ostrm, FindFfmpegCallBack findFfmpegCallBack, String m3u8Url) {
        istrm_ = istrm;
        ostrm_ = ostrm;
        this.findFfmpegCallBack = findFfmpegCallBack;
        this.m3u8Url = m3u8Url;
    }

    @Override
    public void run() {
        try {
            String line = null;
            BufferedReader bufferedReader = new BufferedReader
                    (new InputStreamReader(istrm_, "GBK"));
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.contains(m3u8Url)) {
                    String substring = line.substring(line.lastIndexOf("ffmpeg.exe") + 10);
                    String trim = substring.replaceAll(" ", "").trim();
                    System.out.println(trim);
                    findFfmpegCallBack.find(trim);
                } else if (line.contains("没有可用实例")) {
                    findFfmpegCallBack.find("");
                }
            }
        } catch (Exception e) {
            findFfmpegCallBack.find("");
            System.out.println(e.getMessage());
            throw new RuntimeException("处理命令出现错误：" + e.getMessage());
        }
    }

    public interface FindFfmpegCallBack {
        void find(String pid);
    }
}
