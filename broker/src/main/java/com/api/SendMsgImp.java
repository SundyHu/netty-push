package com.api;

import org.apache.log4j.Logger;

import com.msgs.Constants;
import com.msgs.LoginMsg;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;

public class SendMsgImp implements SendMsg{

	static Logger logger = Logger.getLogger(SendMsgImp.class);
	
	public SendMsgImp() {

	}

	@Override
	public void login(ChannelFuture channelFuture) {
		//客户端服务端验证成功
		SocketChannel socketChannel=(SocketChannel)channelFuture.channel();
        LoginMsg loginMsg=new LoginMsg();
        loginMsg.setPassword("pwd");
        loginMsg.setUserName("molbase");
        if(null!=socketChannel){
        	 socketChannel.writeAndFlush(loginMsg);
        	 logger.info("---------客户端:"+Constants.getClientId()+"连接成功------------");
        }
	}

	@Override
	public void reply(ChannelFuture channelFuture) {
		
	}

}
