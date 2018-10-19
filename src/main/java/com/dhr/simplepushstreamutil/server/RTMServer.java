package com.dhr.simplepushstreamutil.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class RTMServer extends Thread {
    private Process process = null;
    private String[] command = {"cmd"};

    public RTMServer() {
        this.setName("RTMServer");
    }

    public void start_server() {
        // 添加程序关闭监听线程
        Runtime.getRuntime().addShutdownHook(this);
    }

    @Override
    public void run() {
        boolean isWindows = System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
        if (isWindows) {
            try {
                process = Runtime.getRuntime().exec(command);
                if (process != null) {
                    try {
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())), true);
                        out.println("taskkill /F /IM ffmpeg.exe");
                        out.println("exit");//这个命令必须执行，否则in流不结束。
                        process.waitFor();
                        out.close();
                        process.destroy();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.run();
    }
}