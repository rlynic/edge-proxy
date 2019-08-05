/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.servlet;

import lombok.Data;

import java.util.Map;

/**
 * <code>{@link Response}</code>
 *
 * 接收到的请求对象
 *
 * @author crisis
 */
@Data
public class Response {

	/**
	 * 请求id
	 */
	private String requestId;
	/**
	 * 请求的headers
	 */
	private Map<String, Object> headers;
	/**
	 * 请求体
	 */
	private Object body;
	/**
	 * http状态妈
	 */
	private int httpStatus;

	public Response() {
		super();
	}

	public Response(String requestId, int httpStatus) {
		super();
		this.requestId = requestId;
		this.httpStatus = httpStatus;
	}

	public Response(String requestId, Map<String, Object> headers, int httpStatus, Object body) {
		super();
		this.requestId = requestId;
		this.headers = headers;
		this.body = body;
		this.httpStatus = httpStatus;
	}

}
