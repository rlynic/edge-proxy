/**
 * This file created at 2018年7月15日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.channel.client;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.rlynic.edge.proxy.api.AgentApi;

/**
 * <code>{@link AgentClient}</code>
 *
 * @author crisis
 */
@FeignClient(name="agent", url = "${rest.channel.url}")
public interface AgentClient extends AgentApi{

}
