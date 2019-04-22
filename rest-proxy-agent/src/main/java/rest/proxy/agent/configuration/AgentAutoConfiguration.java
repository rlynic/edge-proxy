/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.agent.configuration;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import rest.proxy.agent.broker.DefaultRestBroker;
import rest.proxy.agent.broker.ProxyTaskContainer;
import rest.proxy.agent.broker.RestBroker;
import rest.proxy.agent.channel.ChannelCachePool;
import rest.proxy.agent.channel.ChannelEventTrigger;
import rest.proxy.agent.channel.ChannelListener;
import rest.proxy.agent.channel.MemoryChannelCachePool;
import rest.proxy.agent.properties.AgentProperties;
import rest.proxy.agent.server.AgentServer;
import rest.proxy.agent.server.NettyAgentServer;

/**
 * <code>{@link AgentAutoConfiguration}</code>
 *
 * @author crisis
 */
@Configuration
@EnableConfigurationProperties(AgentProperties.class)
@Import(StreamConfiguration.class)
public class AgentAutoConfiguration {
	
	@Autowired
	private List<ChannelListener> channelListeners;
	
	@Bean
	public ChannelCachePool channelCachePool(AgentProperties agentProperties) {
		return new MemoryChannelCachePool(agentProperties.getMaxChannels(), agentProperties.getTimeout());
	}
	
	@Bean
	public ProxyTaskContainer proxyTaskContainer() {
		return new ProxyTaskContainer();
	}
	
	@Bean
	public RestBroker restBroker(ChannelCachePool channelCachePool, Executor channelWriteAndFlushExecutor, ProxyTaskContainer proxyTaskContainer) {
		return new DefaultRestBroker(channelCachePool, channelWriteAndFlushExecutor, proxyTaskContainer);
	}
	
	@Bean
	public AgentServer agentServer(AgentProperties agentProperties, RestBroker restBroker) {
		return new NettyAgentServer(agentProperties, restBroker);
	}
	
	@Bean
    public Executor channelWriteAndFlushExecutor(AgentProperties agentProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        executor.setCorePoolSize(agentProperties.getChannelRespondThreads());
        executor.setMaxPoolSize(agentProperties.getChannelRespondThreads());
        executor.setThreadNamePrefix("channel-respond-thread");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        
        return executor;
    }
	
	@Configuration
	class AgentServerRunner implements ApplicationRunner{
		
		@Autowired
		private AgentServer agentServer;

		@Override
		public void run(ApplicationArguments args) {
			ChannelEventTrigger.setChannelListeners(channelListeners);
			agentServer.start();
		}

	}

}
