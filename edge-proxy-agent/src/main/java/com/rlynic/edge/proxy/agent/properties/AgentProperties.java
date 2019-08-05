/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.agent.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <code>{@link AgentProperties}</code>
 *
 * @author crisis
 */
@Data
@ConfigurationProperties("rest.agent")
public class AgentProperties {

	private int port;
	private int maxChannels;
	private long timeout;

	private int channelRespondThreads;

}
