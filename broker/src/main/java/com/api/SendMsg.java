package com.api;

import io.netty.channel.ChannelFuture;

public interface SendMsg {

	void login(ChannelFuture channelFuture);
	
	void reply(ChannelFuture channelFuture);
}
