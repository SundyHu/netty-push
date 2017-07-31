package com.client;

import org.apache.log4j.Logger;

import com.api.SendMsg;
import com.api.SendMsgImp;
import com.db.CRMJdbcUtil;
import com.msgs.BaseMsg;
import com.msgs.Constants;
import com.msgs.MsgType;
import com.msgs.Params;
import com.msgs.PingMsg;
import com.msgs.ReqMsg;
import com.util.ZipUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

public class NettyClientHandlerCRM extends SimpleChannelInboundHandler<BaseMsg> {
	private NettyClientCRM clientCRM;
	private SendMsg sendMsg;
	static Logger logger = Logger.getLogger(NettyClientHandlerCRM.class);
	
	public NettyClientHandlerCRM(NettyClientCRM client) {
		this.clientCRM=client;
		sendMsg=new SendMsgImp();
	}
	
	//客户端空闲时执行的方法    心跳的实现    后期可添加心跳失败计数器   失败一定次数后断开通道重连
    @Override   
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case WRITER_IDLE:
                    PingMsg pingMsg=new PingMsg();
                    ctx.writeAndFlush(pingMsg);
                    logger.info("---------------------发送 ping 请求-----------------");
                    break;
                default:
                    break;
            }
        }
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	super.channelActive(ctx);
    	sendMsg.login(clientCRM.getChannelFuture());
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception {
    	super.exceptionCaught(ctx, cause);
    	logger.error(cause);
    }
    
    //客户端接收到消息后的执行方法
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {
        MsgType msgType=baseMsg.getType();
        switch (msgType){
            case LOGIN:{ 
                //向服务器发起登录     登录验证      后期优化
            	sendMsg.login(clientCRM.getChannelFuture());
                logger.info("客户端收到服务端重新让登陆的消息");
            }break;
            case PING:{
            	//客户端接收到服务端心跳回应
//                System.out.println("receive ping from server----------");
            }break;
            case ASK:{
            	ReqMsg replyMsg=(ReqMsg)baseMsg;
                logger.info("客户端"+Constants.getClientId()+"收到服务端请求消息:"+replyMsg.getParams().getJsonStr()+replyMsg.getClientId()+"###"+replyMsg.getReqId());
                replyMsg.setToclientId(replyMsg.getClientId());
                replyMsg.setClientId(Constants.getClientId());
                Params params=new Params();
                //查询本地数据库资源    返回给服务端
                String sql=replyMsg.getParams().getJsonStr();
                String res=CRMJdbcUtil.excuteQuery(CRMJdbcUtil.getConnection(),sql);
                params.setJsonStr(ZipUtils.gzip(res));
                replyMsg.setParams(params);
                replyMsg.setType(MsgType.REPLY);
//                System.out.println("reqId : " + replyMsg.getReqId());
//                logger.info("客户端"+Constants.getClientId()+"回复给服务端的消息:"+replyMsg.getParams().getJsonStr());
                channelHandlerContext.writeAndFlush(replyMsg);
            }break;
            case REPLY:{

            }break;
            default:break;
        }
        ReferenceCountUtil.release(baseMsg);
//        channelHandlerContext.close();
    }
}
