/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.agent.controller;

import java.util.List;

import com.rlynic.edge.proxy.agent.broker.ProxyTaskContainer;
import com.rlynic.edge.proxy.agent.broker.RestBroker;
import com.rlynic.edge.proxy.api.AgentApi;
import com.rlynic.edge.proxy.servlet.Request;
import com.rlynic.edge.proxy.servlet.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <code>{@link AgentApiController}</code>
 *
 *
 * @author crisis
 */
@RestController
public class AgentApiController implements AgentApi {

	@Autowired
	private RestBroker restBroker;
	@Autowired
	private ProxyTaskContainer proxyTaskContainer;

	@Override
	public List<Request> getTasks() {
		return proxyTaskContainer.takeTasks(100);
	}

	@Override
	public boolean complete(@RequestBody List<Response> responses) {

		restBroker.batchRespond(responses);

		return false;
	}

}
