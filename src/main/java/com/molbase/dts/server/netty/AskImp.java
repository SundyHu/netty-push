package com.molbase.dts.server.netty;

import io.netty.channel.socket.SocketChannel;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.msgs.Params;
import com.msgs.ReqMsg;
import com.util.SerUtil;
import com.util.ZipUtils;

public class AskImp implements Ask{

	static Logger logger = Logger.getLogger(AskImp.class);
	public AskImp() {

	}

	@Override
	public String req(String clientid, String jsonStr) {
	    Map<String, SocketChannel> map=NettyChannelMap.getMap();
	    for (String key : map.keySet()) {
            logger.info("key："+key+"value: "+map.get(key));
        }
		SocketChannel channel=(SocketChannel)NettyChannelMap.get(clientid);
	      if(channel!=null){
	          logger.info(channel.id());
	          ReqMsg askMsg=new ReqMsg();
	          Params params=new Params();
	          params.setJsonStr(jsonStr);
	          String uuid=UUID.randomUUID().toString();
	          askMsg.setReqId(uuid);
	          askMsg.setParams(params);
	          askMsg.setClientId(uuid);
	          askMsg.setToclientId(clientid);
	          LinkedBlockingQueue<ReqMsg> queue = new LinkedBlockingQueue<>(1);
	          NettyChannelMap.putQueue(askMsg.getReqId(), queue);
	          channel.writeAndFlush(askMsg);
	          //同步等待客户端返回消息
	          ReqMsg replyMsg;
			try {
				 replyMsg=queue.poll(30, TimeUnit.SECONDS);//poll方法非阻塞的    设置超时否则抛出异常null
//				 replyMsg = queue.take();
				 NettyChannelMap.removeQueue(askMsg.getReqId());
//		         System.out.println(replyMsg.getToclientId()+"---"+askMsg.getReqId()+"---"+replyMsg.getParams().getJsonStr());
				 if(null!=replyMsg){
					 return ZipUtils.gunzip(replyMsg.getParams().getJsonStr());
				 }
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
				logger.error("queue poll 消息时超时--------"+e);
			}finally{
				NettyChannelMap.removeQueue(askMsg.getReqId());
			}
	      }
	      return SerUtil.serializeArray(new LinkedList<Map<String,Object>>());
	}
	
	public void ask() {
        final Ask ask=new AskImp();
        
        ExecutorService executorService=new ThreadPoolExecutor(20, 20, 1000, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100), new ThreadPoolExecutor.CallerRunsPolicy());
        
        for (int i = 0; i < 100; i++) {
        	 executorService.execute(new Runnable() {
                 @Override
                 public void run() {
                     try {
                     	 ask.req("001", "{'req':'服务器端  向 001 客户端 主动请求数据'}");
                     }catch(Exception e){
                     	e.printStackTrace();
                     } finally {
                     	
                     }
                 }
             });
		}
        
    }
}
