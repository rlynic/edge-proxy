/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.agent.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import rest.proxy.agent.broker.ProxyTaskContainer;
import rest.proxy.agent.broker.RestBroker;
import rest.proxy.api.AgentApi;
import rest.proxy.servlet.Request;
import rest.proxy.servlet.Response;

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
