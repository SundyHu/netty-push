package com.molbase.dts.server.netty;

import org.apache.log4j.Logger;

import com.molbase.dts.server.service.IndexService;
import com.msgs.BaseMsg;
import com.msgs.LoginMsg;
import com.msgs.MsgType;
import com.msgs.PingMsg;
import com.msgs.ReqMsg;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

public class NettyServerHandler extends SimpleChannelInboundHandler<BaseMsg> {

	static Logger logger = Logger.getLogger(NettyServerHandler.class);
	static IndexService indexService=new IndexService();
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	//客户端非活跃时   客户端死机或者卡死
		super.channelInactive(ctx);
		NettyChannelMap.remove((SocketChannel)ctx.channel());
		ChannelId id=ctx.channel().id();
		logger.info("channel inActive id:"+id);
		String clientid=NettyChannelMap.get(id);
		if(null!=clientid){
			NettyChannelMap.remove(clientid);
			logger.error("客户端"+clientid+"已断开或者无响应-----------");
			indexService.updateBroker(Integer.parseInt(clientid));
		}
    }
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		logger.info("----------连接成功一个客户端----------");
	}
	
//	@Override
//	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		//客户端断开连接或者断网时    服务端删除通道连接记录    
//		NettyChannelMap.remove((SocketChannel)ctx.channel());
//		ChannelId id=ctx.channel().id();
//		String clientid=NettyChannelMap.get(id);
//		if(null!=clientid){
//			NettyChannelMap.remove(clientid);
//			cause.printStackTrace();
//			logger.error("客户端"+clientid+"已断开连接或者断网-----------"+cause);
//			//客户端刷新最后一次在线时间
//			indexService.updateBroker(Integer.parseInt(clientid));
//		}
//		cause.printStackTrace();
//		logger.error(cause);
//		ctx.close();
//	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception {
		super.exceptionCaught(ctx, cause);
		logger.error(cause);
	}
	
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {
        if(MsgType.LOGIN.equals(baseMsg.getType())){
            LoginMsg loginMsg=(LoginMsg)baseMsg;
            if("molbase".equals(loginMsg.getUserName())&&"pwd".equals(loginMsg.getPassword())){
                //登录成功,把channel存到服务端的map中    后期优化   加密登录验证
            	ChannelId channelId=channelHandlerContext.channel().id();
            	logger.info("登录成功 channel id:"+channelId);
                NettyChannelMap.add(loginMsg.getClientId(),(SocketChannel)channelHandlerContext.channel());
                NettyChannelMap.add(channelId, loginMsg.getClientId());
                logger.info("客户端"+loginMsg.getClientId()+" 登录成功"+channelId);
            }
        }
//        else{
//            if(NettyChannelMap.get(baseMsg.getClientId())==null){
//                    //说明未登录，或者连接断了，服务器向客户端发起登录请求，让客户端重新登录
//                    LoginMsg loginMsg=new LoginMsg();
//                    channelHandlerContext.channel().writeAndFlush(loginMsg);
//            }
//        }
        switch (baseMsg.getType()){
            case PING:{
            	//服务器端响应客户端心跳   刷新客户端在线时间
                PingMsg pingMsg=(PingMsg)baseMsg;
                PingMsg replyPing=new PingMsg();
                if(null==NettyChannelMap.get(pingMsg.getClientId())){
                	ChannelId channelId=channelHandlerContext.channel().id();
                	logger.info("登录成功 channel id:"+channelId);
                    NettyChannelMap.add(pingMsg.getClientId(),(SocketChannel)channelHandlerContext.channel());
                    NettyChannelMap.add(channelId, pingMsg.getClientId());
                    logger.info("客户端"+pingMsg.getClientId()+" 连接成功"+channelId);
                }
                NettyChannelMap.get(pingMsg.getClientId()).writeAndFlush(replyPing);
                indexService.updateBroker(Integer.parseInt(pingMsg.getClientId()));
                
            }break;
            case ASK:{
                
            }break;
            case REPLY:{
                //收到客户端的请求   并刷新客户端在线时间
            	ReqMsg replyMsg=(ReqMsg)baseMsg;
            	String id = replyMsg.getReqId();
            	NettyChannelMap.putMsg(id, replyMsg);
            	indexService.updateBroker(Integer.parseInt(replyMsg.getClientId()));
            	
            }break;
            default:break;
        }
        ReferenceCountUtil.release(baseMsg);
//        channelHandlerContext.close();
    }
}
