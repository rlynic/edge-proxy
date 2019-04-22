/**
 * This file created at 2018年7月15日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.agent.channel;

/**
 * <code>{@link ChannelListener}</code>
 *
 * 连接监控者
 *
 * @author crisis
 */
public interface ChannelListener {

	/**
	 * 连接被移除
	 * @param channel
	 */
	void channelRemoved(Channel channel);
}
