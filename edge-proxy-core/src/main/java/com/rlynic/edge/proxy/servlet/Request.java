/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.servlet;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

/**
 * <code>{@link Request}</code>
 *
 * 接收到的请求对象
 *
 * @author crisis
 */
@Data
public class Request {

	/**
	 * 请求id
	 */
	private String requestId;
	/**
	 * 请求uri
	 */
	private String uri;
	/**
	 * 请求method
	 */
	private String httpMethod;
	/**
	 * 请求的headers
	 */
	private Map<String, Object> headers;
	/**
	 * 请求体
	 */
	private Object body;

	/**
	 * 请求时间
	 */
	private long requestTime = System.currentTimeMillis();

	public Request() {
		super();
	}

	public Request(String requestId) {
		super();
		this.requestId = requestId;
	}

	public Request(String uri, Map<String, Object> headers, String httpMethod) {
		super();
		this.requestId = UUID.randomUUID().toString();
		this.uri = uri;
		this.headers = headers;
		this.httpMethod = httpMethod;
	}

	public Request(String uri, Map<String, Object> headers, String httpMethod, Object body) {
		super();
		this.requestId = UUID.randomUUID().toString();
		this.uri = uri;
		this.headers = headers;
		this.body = body;
		this.httpMethod = httpMethod;
	}

	@Override
	 public boolean equals(Object otherObject) {
		if(otherObject instanceof Request) {
			Request r = (Request)otherObject;
			if(getRequestId().equals(r.getRequestId()))
				return true;
		}
		return false;
	 }

	 @Override
	 public int hashCode() {
		 return getRequestId().hashCode();
	 }

}
