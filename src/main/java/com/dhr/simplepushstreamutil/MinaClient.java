package com.dhr.simplepushstreamutil;


import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

/**
 * mina客户端
 * @author aniyo
 * blog:http://aniyo.iteye.com
 */
public class MinaClient {

    public static void main(String []args)throws Exception{

        //Create TCP/IP connection
        NioSocketConnector connector = new NioSocketConnector();

        //创建接受数据的过滤器
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();

        //设定这个过滤器将一行一行(/r/n)的读取数据
        chain.addLast("myChin", new ProtocolCodecFilter(new TextLineCodecFactory()));

        //客户端的消息处理器：一个SamplMinaServerHander对象
        connector.setHandler(new MinaClientHandler());

        //set connect timeout
        connector.setConnectTimeout(5000);

        //连接到服务器：
        ConnectFuture cf = connector.connect(new InetSocketAddress("localhost",8080));

//        Wait for the connection attempt to be finished.
        cf.awaitUninterruptibly();

        cf.getSession().getCloseFuture().awaitUninterruptibly();

        connector.dispose();
    }
}