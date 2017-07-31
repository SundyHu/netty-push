package com.molbase.dts.server.netty;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyServerBootstrap {
	static Logger logger = Logger.getLogger(NettyServerBootstrap.class);
	private EventLoopGroup boss;
    private EventLoopGroup worker;
    private ServerBootstrap bootstrap;
    private final int port;

    private static NettyServerBootstrap nettyServerBootstrap;
    private NettyServerBootstrap(int port){
    	this.port = port;
    }
    
    public static NettyServerBootstrap instance(int port){
    	if(null==nettyServerBootstrap){
    		nettyServerBootstrap=new NettyServerBootstrap(port);
    	}
    	return nettyServerBootstrap;
    }
    
    public static NettyServerBootstrap getInstance(){
    	return nettyServerBootstrap;
    }
    
    public void close() {
        try {
			boss.shutdownGracefully().await();
			worker.shutdownGracefully().await();
			boss=null;
			worker=null;
		} catch (InterruptedException e) {
			logger.info("Stopped Tcp Server: " + port+"---"+e);
		}
    }
    
    public void start() throws InterruptedException {
        boss=new NioEventLoopGroup();
        worker=new NioEventLoopGroup();
        // 配置服务器的NIO线程租
        bootstrap=new ServerBootstrap();
        bootstrap.group(boss,worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 128);//设置128个最大连接
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                //添加对象解码器 负责对序列化POJO对象进行解码 设置对象序列化最大长度为1M 防止内存溢出
                //设置线程安全的WeakReferenceMap对类加载器进行缓存 支持多线程并发访问  防止内存溢出 
//                p.addLast(new ObjectDecoder(1024*1024,ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                //添加对象编码器 在服务器对外发送消息的时候自动将实现序列化的POJO对象编码
                p.addLast(new ObjectEncoder());
                p.addLast(new NettyServerHandler());
            }
        });
        // 绑定端口，同步等待成功
        ChannelFuture f= bootstrap.bind(port).sync();//线程同步阻塞等待服务器绑定到指定端口,
        f.channel().closeFuture().sync();//成功绑定到端口之后,给channel增加一个 管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程
    }
    
    public static void main(String []args) throws InterruptedException {
        //启动服务器
    	new NettyServerBootstrap(9999).start();
    	//测试并发请求
//    	Thread.sleep(6000);
//    	AskImp ask=new AskImp();
//    	ask.ask();
    }
}
