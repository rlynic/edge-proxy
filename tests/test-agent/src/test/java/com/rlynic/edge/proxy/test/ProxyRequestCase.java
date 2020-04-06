/*
  This file created at 2020/4/6.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.test;

import com.rlynic.edge.proxy.agent.broker.ProxyTaskContainer;
import com.rlynic.edge.proxy.servlet.Request;
import com.rlynic.edge.proxy.test.base.AbstractAgentCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * <code>{@link ProxyRequestCase}</code>
 *
 * @author crisis
 */
public class ProxyRequestCase extends AbstractAgentCase {

    @Autowired
    private ProxyTaskContainer proxyTaskContainer;

    @Test
    public void testRequest() {
        String requestId = UUID.randomUUID().toString();
        startRequest(requestId);

        List<Request> requests = proxyTaskContainer.takeTasks(1);
        Assert.assertTrue("not found request", !CollectionUtils.isEmpty(requests));

        String receivedRequestId = (String)requests.get(0).getHeaders().get(REQUEST_HEADER_KEY);
        Assert.assertEquals("not match request", requestId, receivedRequestId);
    }

    @Test
    public void testRequestGatewayTimeout() throws InterruptedException {
        String requestId = UUID.randomUUID().toString();

        CountDownLatch latch = new CountDownLatch(1);
        FutureTask<ResponseEntity<String>> future = startRequest(requestId, latch);

        latch.await();

        int statusCode = HttpStatus.OK.value();
        try {
            ResponseEntity<String> response = future.get();
            statusCode = response.getStatusCodeValue();
        } catch (ExecutionException e) {
            if(HttpStatusCodeException.class.isAssignableFrom(e.getCause().getClass())){
                statusCode = ((HttpStatusCodeException)e.getCause()).getStatusCode().value();
            }
        }

        Assert.assertEquals("not match status code", HttpStatus.GATEWAY_TIMEOUT.value(), statusCode);
    }

}