/**
 * This file created at 2018年7月15日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.agent.channel;

import java.util.List;

import org.springframework.util.CollectionUtils;

/**
 * <code>{@link ChannelEventTrigger}</code>
 *
 * 连接事件触发
 *
 * @author crisis
 */
public class ChannelEventTrigger {

	private static  List<ChannelListener> channelListeners;

	public static void fireChannelRemoved(Channel channel) {
		if(!CollectionUtils.isEmpty(channelListeners)) {
			channelListeners.forEach(c -> {
				c.channelRemoved(channel);
			});
		}
	}

	/**
	 * @param channelListeners the channelListeners to set
	 */
	public static void setChannelListeners(List<ChannelListener> channelListeners) {
		ChannelEventTrigger.channelListeners = channelListeners;
	}

}
