/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.agent.broker;

import java.util.List;

import rest.proxy.agent.channel.Channel;
import rest.proxy.servlet.Request;
import rest.proxy.servlet.Response;

/**
 * <code>{@link RestBroker}</code>
 *
 * rest
 *
 * @author crisis
 */
public interface RestBroker {
	
	/**
	 * 接收rest请求
	 * @param request
	 * @param channel
	 */
	void receive(Request request, Channel channel);
	

	/**
	 * 响应rest请求
	 * @param response
	 */
	void respond(Response response);
	
	/**
	 * 响应多个连接
	 * @param responses
	 */
	default void batchRespond(List<Response> responses) {
		responses.forEach(response -> {
			respond(response);
		});
	}
}
