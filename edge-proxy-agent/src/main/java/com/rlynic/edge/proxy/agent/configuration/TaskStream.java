/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.agent.configuration;

/**
 * <code>{@link TaskStream}</code>
 *
 * @author crisis
 */
public interface TaskStream {

	String TASK_INPUT = "ProxyRestTaskInput";

	String TASK_OUTPUT = "ProxyRestTaskOutput";

//	@Input
//	SubscribableChannel proxyRestTaskInput();

//	@Output("myOutput")
//	MessageChannel proxyRestTaskOutput();
}
