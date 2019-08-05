package com.rlynic.edge.proxy.agent.server;
/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.rlynic.edge.proxy.servlet.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import com.rlynic.edge.proxy.agent.broker.RestBroker;
import com.rlynic.edge.proxy.agent.channel.NettyChannel;

/**
 * <code>{@link HttpServletHandler}</code>
 *
 * netty http请求处理
 *
 * @author crisis
 */
public class HttpServletHandler extends ChannelInboundHandlerAdapter {
	private RestBroker restBroker;

    /**
	 * @param restBroker
	 */
	public HttpServletHandler(RestBroker restBroker) {
		super();
		this.restBroker = restBroker;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception {
        if (msg instanceof FullHttpRequest) {
        	FullHttpRequest request = (FullHttpRequest) msg;

        	Request req = extractRequest(request);

			restBroker.receive(
					req,
					new NettyChannel(ctx.channel(), req.getRequestId()));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 提取请求
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    protected Request extractRequest(FullHttpRequest request) throws UnsupportedEncodingException {
    	String uri = request.uri();
        HttpHeaders headers = request.headers();

        Request req = new Request(uri, resolveHeaders(headers), request.method().name());

        int length = request.content().readableBytes();
		if(length > 0){
			byte[] content = new byte[length];
			request.content().readBytes(content);

			String contentStr = new String(content, "UTF-8");

			req.setBody(contentStr);
		}

		return req;
    }

    private Map<String, Object> resolveHeaders(HttpHeaders headers){
    	Map<String, Object> headerMap = new HashMap<>();

    	if(null != headers && !headers.isEmpty()) {
    		headers.forEach(h -> {
    			headerMap.put(h.getKey(), h.getValue());
    		});
    	}

    	return headerMap;
    }

}
