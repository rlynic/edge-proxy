/**
 * This file created at 2018年7月15日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.channel.execution;

import java.util.ArrayList;
import java.util.List;

import com.rlynic.edge.proxy.channel.client.AgentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;

import com.rlynic.edge.proxy.servlet.Request;
import com.rlynic.edge.proxy.servlet.Response;

/**
 * <code>{@link RemoteTaskProvider}</code>
 *
 * 向agent节点获取rest请求任务
 *
 * @author crisis
 */
public class RemoteTaskProvider implements TaskProvider {
	private final static Logger log = LoggerFactory.getLogger(RemoteTaskProvider.class);

	@Autowired
	private AgentClient agentClient;

	@Override
	public List<Request> obtain() {
		try {
			if(log.isDebugEnabled())
				log.debug("begin to obtain rest task");

			List<Request> requests =  agentClient.getTasks();

			if(requests.size() > 0)
				if(log.isInfoEnabled())
					log.info("successfully to obain rest task from agent, task size: {}", requests.size());

			if(log.isDebugEnabled())
				log.debug("task : '{}'", requests.size(), JSON.toJSONString(requests));

			return requests;
		}catch(Exception e) {
			log.error("Failed to obtain rest task from agent, error:'{}'", e.getMessage());
		}
		return new ArrayList<>();
	}

	@Override
	public void completeTasks(List<Response> responses) {
		agentClient.complete(responses);
	}

}
