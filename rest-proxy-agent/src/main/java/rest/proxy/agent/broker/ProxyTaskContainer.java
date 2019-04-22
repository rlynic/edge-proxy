/**
 * This file created at 2018年7月15日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.agent.broker;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import rest.proxy.agent.channel.Channel;
import rest.proxy.agent.channel.ChannelListener;
import rest.proxy.servlet.Request;

/**
 * <code>{@link ProxyTaskContainer}</code>
 *
 * 代理任务队列容器
 *
 * @author crisis
 */
public class ProxyTaskContainer implements ChannelListener{
	
	private static ConcurrentLinkedQueue<Request> taskStoreQueue = new ConcurrentLinkedQueue<>();
	
	public void add(Request request) {
		taskStoreQueue.add(request);
	}
	
	public List<Request> takeTasks(int taskNum) {
		List<Request> tasks = new LinkedList<>();
		
		Request r;
		int i = 0;
		while(taskNum > i) {
			r = taskStoreQueue.poll();
			if(null != r){
				tasks.add(r);
				++i;
			}else
				break;
		}
		
		return tasks;
	}

	@Override
	public void channelRemoved(Channel channel) {
		Request r = new Request(channel.getRequestId());
		if(taskStoreQueue.contains(r))
			taskStoreQueue.remove(new Request(channel.getRequestId()));
	}
	
}
