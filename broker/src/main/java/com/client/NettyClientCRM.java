package com.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.msgs.Constants;
import com.util.Config;
import com.util.PropertiesUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClientCRM {
	static Logger logger = Logger.getLogger(NettyClientCRM.class);
	private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private final int remotePort;
    private final String remoteHost;
    private ChannelFuture channelFuture;
//    private static final EventExecutorGroup group = new DefaultEventExecutorGroup(20);//根据通道相应的事件对通道进行处理，而这个处理也有可能是多个线程的多线程处理
    
    public NettyClientCRM(int port, String host){
        this.remotePort = port;
        this.remoteHost = host;
    }
    
    public ChannelFuture getChannelFuture() {
		return channelFuture;
	}

	public void close() {
        workerGroup.shutdownGracefully();
        logger.info("Stopped Tcp Client: " + getServerInfo());
    }
    
    public void start() throws Exception {
        workerGroup=new NioEventLoopGroup();
    	// 配置客户端NIO线程组
        bootstrap=new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.group(workerGroup);
        bootstrap.remoteAddress(remoteHost,remotePort);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
            	//ReadTimeoutHandler读取数据超时处理  WriteTimeoutHandler写数据超时处理  IdleStateHandler状态空闲处理   通过以上三种方式，可以轻松实现SOCKET长连接的心跳包机制。
            	//要注意的是这里所说的超时是指逻辑上的超时，而非TCP连接超时
                socketChannel.pipeline().addLast(new IdleStateHandler(10,10,0));
                //添加POJO对象解码器 禁止缓存类加载器
//                socketChannel.pipeline().addLast(new ObjectDecoder(1024,ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                //设置发送消息编码器
                socketChannel.pipeline().addLast(new ObjectEncoder());
                socketChannel.pipeline().addLast(new NettyClientHandlerCRM(NettyClientCRM.this));
            }
        });
    }
    
    public void doConnect(){
    	try {
    		// 发起异步连接操作
	    	 channelFuture = bootstrap.connect(new InetSocketAddress(remoteHost, remotePort)).sync();
	    	 channelFuture.channel().closeFuture().sync();
		  }catch(Exception e){
			  e.printStackTrace();
			  logger.error("连接异常---------", e);
		  }  finally {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						doConnect();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		 }
    		
//			channelFuture.addListener(new ChannelFutureListener() {
//	            public void operationComplete(ChannelFuture f) throws Exception {
//	                if (f.isSuccess()) {
//	                    logger.info("connect server success---------"+ getServerInfo());
//	                } else {
//	                    logger.error("connect server failed---------"+ getServerInfo());
//	                    f.channel().eventLoop().schedule(new Runnable() {
//							@Override
//							public void run() {
//								try {
//									doConnect();
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//							}
//						}, 3, TimeUnit.SECONDS);
//	                }
//	            }
//	        });
    }

    public static void init() throws IOException{
		InputStream is = NettyClientCRM.class.getResourceAsStream("/DBConfig.properties");
		Properties properties = new Properties();
		properties.load(is);
		is.close();
		Config.apps.putAll(PropertiesUtil.toMap(properties));
	}
    
    private String getServerInfo() {
        return String.format("RemoteHost=%s RemotePort=%d",remoteHost,remotePort);
    }
    
    public static void main(String[]args) throws Exception{
    	//初始化配置参数的文件加载
    	try {
			init();
			logger.info("初始化配置参数的文件加载成功");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("初始化配置参数的文件加载失败:"+e.getMessage());
		}
    	Constants.setClientId(Config.apps.get("CRMclientID"));
        NettyClientCRM clientCRM=new NettyClientCRM(Integer.parseInt(Config.apps.get("port")), Config.apps.get("host"));
    	try {
			clientCRM.start();
			clientCRM.doConnect();
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error("客户端启动连接绑定服务端失败:"+e.getMessage());
		}
//    	ExecutorService executor = Executors.newFixedThreadPool(6);
//    	for (int i = 0; i < 1; i++) {
//    		executor.submit(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						clientCRM.doConnect();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
//		}
    }
	
}
