/*
  This file created at 2020/4/6.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.test;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 * <code>{@link BaseCase}</code>
 *
 * @author crisis
 */
public abstract class BaseCase {

    protected final static String REQUEST_HEADER_KEY = "x-request-id";

    private RestTemplate restTemplate = new RestTemplate();

    protected String getAgentPoint(){
        return "http://127.0.0.1:" + getAgentPort();
    }

    protected FutureTask<ResponseEntity<String>> startRequest(String requestId){
        return startRequest(requestId, null);
    }

    protected FutureTask<ResponseEntity<String>> startRequest(String requestId, CountDownLatch latch){
        return startRequest(requestId, null, latch);
    }

    protected FutureTask<ResponseEntity<String>> startRequest(String requestId, final String path, CountDownLatch latch){

        // waiting agent proxy listener complete
        sleep(500);
        FutureTask<ResponseEntity<String>> task = new FutureTask<>(() -> {
            try {
                String fPath = path;
                if(StringUtils.isEmpty(fPath)){
                    fPath = "/test";
                }

                String url = getAgentPoint() + fPath;
                HttpHeaders headers = new HttpHeaders();
                headers.add(REQUEST_HEADER_KEY, requestId);
                HttpEntity httpEntity = new HttpEntity(headers);
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

                return response;
            }finally {
                if(null != latch){
                    latch.countDown();
                }
            }
        });
        new Thread(task, "thread-request").start();
        //waiting the request reach the agent proxy
        sleep(500);

        return task;
    }

    protected abstract int getAgentPort();

    private void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // ignore
        }
    }

}