/**
 * This file created at 2018年7月15日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.channel.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import rest.proxy.channel.properties.TaskProperties;
import rest.proxy.servlet.Request;
import rest.proxy.servlet.Response;

/**
 * <code>{@link ScheduleTaskExecutor}</code>
 *
 * 定时轮询任务执行者
 *
 * @author crisis
 */
public class ScheduleTaskExecutor implements TaskExecutor, InitializingBean {
	private final static Logger log = LoggerFactory.getLogger(ScheduleTaskExecutor.class);
	
	private final static Map<String, Future<Response>> futureMaps = new ConcurrentHashMap<>();
	private static volatile AtomicBoolean isStartedCompletedTask = new AtomicBoolean(false);
	
	private TaskProvider taskProvider;
	private ThreadPoolTaskExecutor restTaskThreadPoolTaskExecutor;
	private ThreadPoolTaskExecutor restTaskCompletedThreadPoolTaskExecutor;
	private RestTemplate restTemplate;
	private TaskProperties taskProperties;
	/**
	 * @param taskProvider
	 * @param restTaskThreadPoolTaskExecutor
	 */
	public ScheduleTaskExecutor(TaskProvider taskProvider, 
			ThreadPoolTaskExecutor restTaskThreadPoolTaskExecutor, 
			ThreadPoolTaskExecutor restTaskCompletedThreadPoolTaskExecutor,
			RestTemplate restTemplate,
			TaskProperties taskProperties) {
		super();
		this.taskProvider = taskProvider;
		this.restTaskThreadPoolTaskExecutor = restTaskThreadPoolTaskExecutor;
		this.restTaskCompletedThreadPoolTaskExecutor = restTaskCompletedThreadPoolTaskExecutor;
		this.restTemplate = restTemplate;
		this.taskProperties = taskProperties;
	}

	@Override
	public void execute() {
		List<Request> requests = taskProvider.obtain();

		if(CollectionUtils.isNotEmpty(requests)) {
			requests.forEach(r -> futureMaps.put(r.getRequestId(), restTaskThreadPoolTaskExecutor.submit(new RequestTask(r, restTemplate, taskProperties))));
		}
	}
	
	@Scheduled(fixedDelay=100)
	public void start() {
		execute();
	}

	@Override
	public void afterPropertiesSet() {
		if(isStartedCompletedTask.compareAndSet(false, true)) {
			restTaskCompletedThreadPoolTaskExecutor.submit((Runnable) () -> {
				Map<String, Response> responseMaps = new HashMap<>();
				while(true) {
					try {
						responseMaps.clear();
						futureMaps.forEach((k, v) -> {
							if(v.isDone())
								try {
									responseMaps.put(k, v.get());
								} catch (InterruptedException | ExecutionException e) {
									log.error("Unknown abnormal", e);
								}
						});
						if(responseMaps.isEmpty())
							continue;

						responseMaps.forEach((k, v) -> futureMaps.remove(k));

						taskProvider.completeTasks(new ArrayList<>(responseMaps.values()));
					}catch(Exception e) {
						log.error("Failed to complete rest task", e);
					}
				}
			});
		}
	}
	
}
