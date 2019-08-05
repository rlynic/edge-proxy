/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.agent.channel;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.Executor;

import com.rlynic.edge.proxy.servlet.Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * <code>{@link NettyChannel}</code>
 *
 * @author crisis
 */
public class NettyChannel implements Channel {

	private io.netty.channel.Channel channel;
	private String requestId;
	private long requestTime = System.currentTimeMillis();
	private Executor channelWriteAndFlushExecutor;

	/**
	 * @param channel
	 */
	public NettyChannel(io.netty.channel.Channel channel, String requestId) {
		super();
		this.channel = channel;
		this.requestId = requestId;
	}

	@Override
	public String getRequestId() {
		return requestId;
	}

	@Override
	public long getRequestTime() {
		return requestTime;
	}

	@Override
	public void setExecutor(Executor channelWriteAndFlushExecutor) {
		this.channelWriteAndFlushExecutor = channelWriteAndFlushExecutor;
	}

	@Override
	public void writeAndFlush(Response response) {
		if(!channel.isActive())
			return;

		if(null != channelWriteAndFlushExecutor) {
			channelWriteAndFlushExecutor.execute(() -> nettyWriteAndFlush(response));
		}else
			nettyWriteAndFlush(response);

		ChannelEventTrigger.fireChannelRemoved(this);
	}

	private void nettyWriteAndFlush(Response response) {

		HttpHeaders headers = convertHeaders(response.getHeaders());

		ByteBuf byteBuf = Unpooled.buffer(0);
		if(null != response.getBody())
			byteBuf = Unpooled.wrappedBuffer(Base64.getDecoder().decode((String)response.getBody()));

		channel.writeAndFlush(
				new DefaultFullHttpResponse(
						HttpVersion.HTTP_1_1,
						HttpResponseStatus.valueOf(response.getHttpStatus()),
						byteBuf, headers, headers));
		channel.close();
	}

	private HttpHeaders convertHeaders(Map<String, Object> headerMaps) {
		HttpHeaders headers = new DefaultHttpHeaders();
		if(null != headerMaps && !headerMaps.isEmpty()) {
			headerMaps.forEach((k, v) -> headers.add(k, v));
		}
		return headers;
	}

}
