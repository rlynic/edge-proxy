/**
 * This file created at 2018年7月15日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.channel.configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rlynic.edge.proxy.channel.execution.ScheduleTaskExecutor;
import com.rlynic.edge.proxy.channel.execution.TaskExecutor;
import com.rlynic.edge.proxy.channel.properties.TaskProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.rlynic.edge.proxy.channel.execution.RemoteTaskProvider;
import com.rlynic.edge.proxy.channel.execution.TaskProvider;

/**
 * <code>{@link ChannelAutoConfiguration}</code>
 *
 * @author crisis
 */
@Configuration
@EnableConfigurationProperties(TaskProperties.class)
public class ChannelAutoConfiguration {

	@Bean
	public TaskProvider taskProvider() {
		return new RemoteTaskProvider();
	}

	@Bean
	public TaskExecutor taskExecutor(
			TaskProvider taskProvider,
			ThreadPoolTaskExecutor restTaskThreadPoolTaskExecutor,
			ThreadPoolTaskExecutor restTaskCompletedThreadPoolTaskExecutor,
			TaskProperties taskProperties) {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new AbstractHttpMessageConverter<byte[]>(MediaType.ALL) {
			@Override
			public boolean supports(Class<?> clazz) {
				return byte[].class == clazz;
			}

			@Override
			public byte[] readInternal(Class<? extends byte[]> clazz, HttpInputMessage inputMessage) throws IOException {
				long contentLength = inputMessage.getHeaders().getContentLength();
				ByteArrayOutputStream bos =
						new ByteArrayOutputStream(contentLength >= 0 ? (int) contentLength : StreamUtils.BUFFER_SIZE);
				StreamUtils.copy(inputMessage.getBody(), bos);
				return bos.toByteArray();
			}

			@Override
			protected Long getContentLength(byte[] bytes, MediaType contentType) {
				return (long) bytes.length;
			}

			@Override
			protected void writeInternal(byte[] bytes, HttpOutputMessage outputMessage) throws IOException {
				StreamUtils.copy(bytes, outputMessage.getBody());
			}
		});

		RestTemplate restTemplate = new RestTemplate(messageConverters);
		restTemplate.setErrorHandler(new ResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) {
				return false;
			}

			@Override
			public void handleError(ClientHttpResponse response) {
			}
		});
		return new ScheduleTaskExecutor(
				taskProvider,
				restTaskThreadPoolTaskExecutor,
				restTaskCompletedThreadPoolTaskExecutor,
				restTemplate,
				taskProperties);
	}

	@Bean
	public ScheduledExecutorService scheduledExecutorService(){
		ScheduledExecutorService ss= Executors.newScheduledThreadPool(5);
		return ss;
	}

	@Bean
	public TaskScheduler taskScheduler(){
		return new ConcurrentTaskScheduler();
	}

	@Bean
    public ThreadPoolTaskExecutor restTaskThreadPoolTaskExecutor(TaskProperties taskProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(taskProperties.getMaxConcurrency());
        executor.setMaxPoolSize(taskProperties.getMaxConcurrency());

        executor.setThreadNamePrefix("task-executor-thread");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        return executor;
    }


	@Bean
    public ThreadPoolTaskExecutor restTaskCompletedThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);

        executor.setThreadNamePrefix("task-completed-thread");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        return executor;
    }

    @Bean
    public ObjectMapper objectMapper(){
		return new ObjectMapper();
	}

}
