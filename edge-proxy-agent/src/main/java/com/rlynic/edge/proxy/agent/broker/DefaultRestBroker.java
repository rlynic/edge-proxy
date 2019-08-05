/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.agent.broker;

import java.util.concurrent.Executor;

import com.rlynic.edge.proxy.agent.channel.Channel;
import com.rlynic.edge.proxy.agent.channel.ChannelCachePool;
import com.rlynic.edge.proxy.servlet.Request;
import com.rlynic.edge.proxy.servlet.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>{@link DefaultRestBroker}</code>
 *
 * @author crisis
 */
public class DefaultRestBroker implements RestBroker{
	private final static Logger log = LoggerFactory.getLogger(DefaultRestBroker.class);

	private ChannelCachePool channelPool;
	private Executor channelWriteAndFlushExecutor;
	private ProxyTaskContainer proxyTaskContainer;

	/**
	 * @param channelPool
	 */
	public DefaultRestBroker(ChannelCachePool channelPool, Executor channelWriteAndFlushExecutor, ProxyTaskContainer proxyTaskContainer) {
		super();
		this.channelPool = channelPool;
		this.channelWriteAndFlushExecutor = channelWriteAndFlushExecutor;
		this.proxyTaskContainer = proxyTaskContainer;
	}

	@Override
	public void receive(Request request, Channel channel) {
		channel.setExecutor(channelWriteAndFlushExecutor);

		if(log.isInfoEnabled())
        	log.info("Receive a request , request id: '{}', uri : '{}'", request.getRequestId(), request.getUri());

		proxyTaskContainer.add(request);
		channelPool.set(request.getRequestId(), channel);
	}

	@Override
	public void respond(Response response) {
		if(null == response)
			return;

		Channel channel = channelPool.get(response.getRequestId());

		if(log.isInfoEnabled())
        	log.info("Respond a request, request id : '{}', http status : '{}', using {}ms", response.getRequestId(), response.getHttpStatus(), System.currentTimeMillis()-channel.getRequestTime());

		if(null == channel)
			return;

		channel.writeAndFlush(response);
	}

}
