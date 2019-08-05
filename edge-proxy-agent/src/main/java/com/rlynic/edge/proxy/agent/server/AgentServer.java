/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.agent.server;

/**
 * <code>{@link AgentServer}</code>
 *
 * 代理服务
 *
 * @author crisis
 */
public interface AgentServer {

	/**
	 * 启动代理服务
	 */
	void start();

	/**
	 * 关闭服务
	 */
	void stop();
}
