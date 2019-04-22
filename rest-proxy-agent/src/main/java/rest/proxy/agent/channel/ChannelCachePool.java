/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.agent.channel;

import java.util.Map;

/**
 * <code>{@link ChannelCachePool}</code>
 *
 * 连接缓存池
 *
 * @author crisis
 */
public interface ChannelCachePool {
	
	/**
	 * 添加连接
	 * @param id
	 * @param channel
	 */
	void set(String id, Channel channel);
	
	/**
	 * 获取连接
	 * @param id
	 * @return
	 */
	Channel get(String id);
	
	/**
	 * 获取连接池
	 * @return
	 */
	Map<String, Channel> getPool();
}
