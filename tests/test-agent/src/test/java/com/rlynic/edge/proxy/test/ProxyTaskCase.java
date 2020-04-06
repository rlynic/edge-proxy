/*
  This file created at 2020/4/6.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.test;

import com.google.common.collect.Lists;
import com.rlynic.edge.proxy.servlet.Request;
import com.rlynic.edge.proxy.servlet.Response;
import com.rlynic.edge.proxy.test.base.AbstractAgentCase;
import com.rlynic.edge.proxy.test.client.AgentClient;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * <code>{@link ProxyTaskCase}</code>
 *
 * @author crisis
 */
public class ProxyTaskCase extends AbstractAgentCase {

    @Autowired
    private AgentClient agentClient;

    @Test
    public void testObtainRequestTasks(){
        String requestId = UUID.randomUUID().toString();
        startRequest(requestId);

        List<Request> tasks = agentClient.getTasks();
        Assert.assertTrue("not found request task", !CollectionUtils.isEmpty(tasks));

        String receivedRequestId = (String)tasks.get(0).getHeaders().get(REQUEST_HEADER_KEY);
        Assert.assertEquals("not match request", requestId, receivedRequestId);
    }

    @Test
    public void testCompleteRequestTask() throws InterruptedException {
        String requestId = UUID.randomUUID().toString();
        FutureTask<ResponseEntity<String>> future = startRequest(requestId);

        List<Request> tasks = agentClient.getTasks();
        Assert.assertTrue("not found request task", !CollectionUtils.isEmpty(tasks));

        String body = completeRequestTask(tasks.get(0));

        try {
            ResponseEntity<String> response = future.get();
            Assert.assertEquals("not match body", body, response.getBody());
        } catch (ExecutionException e) {
            Assert.assertTrue("not match response", false);
        }
    }

    private String completeRequestTask(Request request){
        String body = UUID.randomUUID().toString();

        Response resp = new Response(request.getRequestId(), HttpStatus.OK.value());
        resp.setBody(Base64.getEncoder().encodeToString(body.getBytes()));
        agentClient.complete(Lists.newArrayList(resp));

        return body;
    }
}