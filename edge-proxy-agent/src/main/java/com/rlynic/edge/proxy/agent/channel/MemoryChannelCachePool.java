/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.agent.channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.rlynic.edge.proxy.servlet.Response;
import org.springframework.http.HttpStatus;

/**
 * <code>{@link MemoryChannelCachePool}</code>
 *
 * 基于内存的连接换成池
 *
 * @author crisis
 */
public class MemoryChannelCachePool implements ChannelCachePool{

	public static final int MAX_CHANNELS = 1000;
	public static final Long DEFAULT_TIMEOUT = 60000L;

	protected int maxChannels = MAX_CHANNELS;
	protected Long timeout = DEFAULT_TIMEOUT;

	private Map<String, DelayChannel> channelCache = new ConcurrentHashMap<>();
	protected final DelayQueue<Delay> delayQueue = new DelayQueue<>();

	@PostConstruct
	public void initClearTask() {
		new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				flush();
			}

		}, "channel-clear-thread").start();
	}

	public MemoryChannelCachePool(int maxChannels, Long timeout) {
		super();
		this.maxChannels = maxChannels;
		this.timeout = timeout;
	}

	@Override
	public void set(String id, Channel channel){
		flush();

		Delay delay = new Delay(id, timeout + System.currentTimeMillis());

		channelCache.put(id, new DelayChannel(delay, channel));
		delayQueue.put(delay);
	}

	@Override
	public Channel get(String id){
		DelayChannel c = channelCache.get(id);
		if(null != c) {
			channelCache.remove(id);
			delayQueue.remove(c.getDelay());
			return c.getChannel();
		}
		return null;
	}

	@Override
	public Map<String, Channel> getPool() {
		return null;
	}

	protected void flush(){
		Delay delay = delayQueue.poll();
		while (delay != null) {
			DelayChannel delayChannel = channelCache.remove(delay.getkey());
			if(null != delayChannel)
				delayChannel.getChannel().writeAndFlush(new Response(delayChannel.getChannel().getRequestId(), HttpStatus.GATEWAY_TIMEOUT.value()));

			delay = delayQueue.poll();
		}

	}

	protected void remove() {
		while(channelCache.size() >= maxChannels){
			Delay delay = delayQueue.peek();
			if(delay != null){
				delayQueue.remove(delay);
				channelCache.remove(delay.getkey());
			}
		}
	}

	private static class Delay implements Delayed{

		private final String id;
		private final Long expire;

		public Delay(String id, Long expire){
			this.id = id;
			this.expire = expire;
		}

		@Override
		public int compareTo(Delayed that) {

			if (this == that)
				return 0;

			if(this.getDelay(TimeUnit.MILLISECONDS) > that.getDelay(TimeUnit.MILLISECONDS))
				return -1;
			else
				return 1;

		}

		@Override
		public long getDelay(TimeUnit unit) {
			return expire - System.currentTimeMillis();
		}

		/**
		 * @return the key
		 */
		public String getkey() {
			return id;
		}

	}

	private class DelayChannel{
		private Delay delay;
		private Channel channel;

		/**
		 * @param delay
		 * @param channel
		 */
		public DelayChannel(Delay delay, Channel channel) {
			super();
			this.delay = delay;
			this.channel = channel;
		}

		/**
		 * @return the delay
		 */
		public Delay getDelay() {
			return delay;
		}
		/**
		 * @return the channel
		 */
		public Channel getChannel() {
			return channel;
		}
	}

}
