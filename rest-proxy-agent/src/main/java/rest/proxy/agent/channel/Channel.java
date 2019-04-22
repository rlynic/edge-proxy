/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.agent.channel;

import java.util.concurrent.Executor;

import rest.proxy.servlet.Response;

/**
 * <code>{@link Channel}</code>
 *
 * 连接对象
 *
 * @author crisis
 */
public interface Channel {
	
	/**
	 * 获取绑定的request id
	 * @return
	 */
	String getRequestId();

	/**
	 * 返回数据并flush
	 */
	void writeAndFlush(Response response);
	
	/**
	 * 获取请求时间
	 */
	long getRequestTime();
	
	/**
	 * flush线程池
	 * @param channelWriteAndFlushExecutor
	 */
	void setExecutor(Executor channelWriteAndFlushExecutor);
}
