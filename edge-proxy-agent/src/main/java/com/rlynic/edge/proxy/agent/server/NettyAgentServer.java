/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.agent.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import com.rlynic.edge.proxy.agent.broker.RestBroker;
import com.rlynic.edge.proxy.agent.properties.AgentProperties;

/**
 * <code>{@link NettyAgentServer}</code>
 *
 * netty实现的代理agent
 *
 * @author crisis
 */
public class NettyAgentServer implements AgentServer {
	private final static Logger log = LoggerFactory.getLogger(NettyAgentServer.class);

	private EventLoopGroup bossGroup = new NioEventLoopGroup();
	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	private AgentProperties agentProperties;
	private RestBroker restBroker;

	/**
	 * @param httpProperties
	 */
	public NettyAgentServer(AgentProperties agentProperties, RestBroker restBroker) {
		super();
		this.agentProperties = agentProperties;
		this.restBroker = restBroker;
	}

	@Override
	public void start() {
		try {
			ServerBootstrap b = new ServerBootstrap();

			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							// server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
							ch.pipeline().addLast(new HttpResponseEncoder());
							// server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
							ch.pipeline().addLast(new HttpRequestDecoder());

							ch.pipeline().addLast(new HttpObjectAggregator(65536));
							//rest请求代理处理
							ch.pipeline().addLast(new HttpServletHandler(restBroker));
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = b.bind(agentProperties.getPort()).sync();

			if(log.isInfoEnabled())
				log.info("Agent Server started on port(s): {} (http)", agentProperties.getPort());

			f.channel().closeFuture().sync();

		} catch (Exception e) {
			log.error("failed to start agent server at port {}", agentProperties.getPort(), e);
		} finally {
			 workerGroup.shutdownGracefully();
			 bossGroup.shutdownGracefully();
		}

	}

	@Override
	public void stop() {

	}

}
