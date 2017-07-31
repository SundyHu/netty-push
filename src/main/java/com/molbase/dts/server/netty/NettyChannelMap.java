package com.molbase.dts.server.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.socket.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import com.msgs.ReqMsg;

public class NettyChannelMap {
	static Logger logger = Logger.getLogger(NettyChannelMap.class);
	//保存客户端的channel 唯一标识id   以便服务器向指定客户端推送消息
    private static Map<String,SocketChannel> map=new ConcurrentHashMap<String, SocketChannel>();
    
    private static Map<ChannelId,String> map1=new ConcurrentHashMap<ChannelId, String>();
    
    //保存推送到客户端没有得到响应的消息   客户端断开连接的情况
    private static Map<String, LinkedBlockingQueue<ReqMsg>> res=new ConcurrentHashMap<String, LinkedBlockingQueue<ReqMsg>>();

    
    public static void add(String clientId,SocketChannel socketChannel){
        map.put(clientId,socketChannel);
    }
    
    public static void add(ChannelId channelId,String clientid){
        map1.put(channelId,clientid);
    }
    
    public static Channel get(String clientId){
       return map.get(clientId);
    }
    
    public static String get(ChannelId channelId){
    	if(map1.containsKey(channelId)){
    		return map1.get(channelId);
    	}
		return null;
    }
    
    public static void remove(SocketChannel socketChannel){
        for (Map.Entry entry:map.entrySet()){
            if (entry.getValue()==socketChannel){
                map.remove(entry.getKey());
            }
        }
    }
    
    public static void remove(String clientid){
        for (Map.Entry entry:map1.entrySet()){
            if (entry.getValue()==clientid){
                map1.remove(entry.getKey());
            }
        }
    }
    
    public static void putQueue(String reqId,LinkedBlockingQueue<ReqMsg> queue){
    	res.put(reqId, queue);
    }
    
    public static void putMsg(String reqId,ReqMsg msg){
    	LinkedBlockingQueue<ReqMsg> queue= res.get(reqId);
    	if(null == queue){
    		logger.error("临时队列不存在!响应消息未放成功!");
    		throw new RuntimeException("not found req..");
    	}
    	try {
    		queue.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
	public static void removeQueue(String reqId) {
		res.remove(reqId);
	}

    public static Map<String, SocketChannel> getMap() {
        return map;
    }

    public static void setMap(Map<String, SocketChannel> map) {
        NettyChannelMap.map = map;
    }
	
}
