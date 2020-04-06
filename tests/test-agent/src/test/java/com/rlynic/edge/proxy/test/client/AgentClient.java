/*
  This file created at 2020/4/6.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.test.client;

import com.rlynic.edge.proxy.api.AgentApi;
import com.rlynic.edge.proxy.test.base.AbstractAgentCase;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * <code>{@link AgentClient}</code>
 *
 * @author crisis
 */
@FeignClient(value="agent", url = AbstractAgentCase.LOCAL_URL)
public interface AgentClient extends AgentApi {
}