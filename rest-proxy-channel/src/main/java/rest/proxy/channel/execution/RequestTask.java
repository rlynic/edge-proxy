/**
 * This file created at 2018年7月15日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.channel.execution;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import rest.proxy.channel.properties.TaskProperties;
import rest.proxy.servlet.Request;
import rest.proxy.servlet.Response;

/**
 * <code>{@link RequestTask}</code>
 *
 * 请求任务
 *
 * @author crisis
 */
public class RequestTask implements Callable<Response>{
	private final static Logger log = LoggerFactory.getLogger(RequestTask.class);
	private Request request;
	private RestTemplate restTemplate;
	
	private TaskProperties taskProperties;
	
	/**
	 * @param request
	 */
	public RequestTask(Request request, RestTemplate restTemplate, TaskProperties taskProperties) {
		super();
		this.request = request;
		this.restTemplate = restTemplate;
		this.taskProperties = taskProperties;
	}

	@Override
	public Response call() {
		Response response;
		
		String url = getUrl(request.getUri());
		try{
			
			long point = System.currentTimeMillis();
			
			if(log.isInfoEnabled())
				log.info("invoke request, id : '{}', url : '{}'", request.getRequestId(), url);
			
			HttpHeaders requestHeaders = convertHeaders(request.getHeaders());
			
			Object body = request.getBody();
			HttpMethod method = HttpMethod.valueOf(request.getHttpMethod());
			
			HttpEntity<Object> entity = null != body ? new HttpEntity<>(body, requestHeaders) : new HttpEntity<>(requestHeaders);
			ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
					url, 
					method, 
					entity, 
					byte[].class);
			
			Object recbody = null;
			if(null != responseEntity.getBody())
				recbody = Base64.getEncoder().encodeToString(responseEntity.getBody());
			
			response = new Response(request.getRequestId(), extractHeaders(responseEntity.getHeaders()), responseEntity.getStatusCodeValue(), recbody);
			
			if(log.isInfoEnabled())
				log.info("Successfully to invoke request, id : '{}', url : '{}', http status : '{}', using {}ms", request.getRequestId(), url, response.getHttpStatus(), System.currentTimeMillis() - point);
			
		}catch(Exception e){
			log.error(String.format("Failed to invoke request, id : '%s', url : '%s'", request.getRequestId(), url), e);
			
			response = new Response(request.getRequestId(), HttpStatus.GATEWAY_TIMEOUT.value());
		}
		return response;
	}
	
	private HttpHeaders convertHeaders(Map<String, Object> headerMaps) {
		HttpHeaders headers = new HttpHeaders();
		if(MapUtils.isNotEmpty(headerMaps)) {
			headerMaps.forEach((k, v) -> {
				headers.add(k, (String)v);
			});
		}
		return headers;
	}
	
	private Map<String, Object> extractHeaders(HttpHeaders headers){
		Map<String, Object> headerMaps = new HashMap<>();
		if(!headers.isEmpty()) {
			headers.forEach((k, v) -> {
				headerMaps.put(k, StringUtils.join(v.toArray(), ";"));
			});
		}
		return headerMaps;
	}
	
	private String getUrl(String uri) {
		return taskProperties.getTargetUrl() + uri;
	}
	
}
