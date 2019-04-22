/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.api;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import rest.proxy.servlet.Request;
import rest.proxy.servlet.Response;

/**
 * <code>{@link AgentApi}</code>
 *
 * 代理任务交互接口
 *
 * @author crisis
 */
public interface AgentApi {

	/**
	 * 拉取代理任务
	 * @return
	 */
	@RequestMapping(value = "/agent/tasks", method = RequestMethod.GET)
	List<Request> getTasks();
	
	/**
	 * 完成任务
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/agent/tasks/complete", consumes = { "application/json" }, method = RequestMethod.POST)
	boolean complete(@RequestBody List<Response> response);
}
