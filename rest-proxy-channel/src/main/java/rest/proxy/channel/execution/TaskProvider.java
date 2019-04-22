/**
 * This file created at 2018年7月15日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.channel.execution;

import java.util.List;

import rest.proxy.servlet.Request;
import rest.proxy.servlet.Response;

/**
 * <code>{@link TaskProvider}</code>
 *
 * 请求任务提供者
 *
 * @author crisis
 */
public interface TaskProvider {
	
	/**
	 * 获取任务
	 * @return
	 */
	List<Request> obtain();
	
	
	/**
	 * 完成任务
	 * @param responses
	 */
	void completeTasks(List<Response> responses);
	
}
