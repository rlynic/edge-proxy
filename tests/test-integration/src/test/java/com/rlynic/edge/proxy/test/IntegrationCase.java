/*
  This file created at 2020/4/6.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rlynic.edge.proxy.test.base.AbstractIntegrationCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.concurrent.*;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * <code>{@link IntegrationCase}</code>
 *
 * @author crisis
 */
@Import({
        IntegrationCase.MockServerRunner.class
})
public class IntegrationCase extends AbstractIntegrationCase {

    private final static String MOCK_BODY = "{\"id\":\""+ UUID.randomUUID().toString() +"\"\"name\":\"edge.proxy\"}";

    public final static String MOCK_PATH = "/mock/articles";
    public final static int MOCK_PORT = 8082;

    @Test
    public void test() throws InterruptedException, ExecutionException {
        String requestId = UUID.randomUUID().toString();
        CountDownLatch latch = new CountDownLatch(1);
        FutureTask<ResponseEntity<String>> future = startRequest(requestId, MOCK_PATH, latch);

        latch.await();

        ResponseEntity<String> response = future.get();
        Assert.assertEquals("not match body", MOCK_BODY, response.getBody());
    }

    @Configuration
    public static class MockServerRunner implements ApplicationRunner {

        @Override
        public void run(ApplicationArguments args) {
            ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("test-mock-server-%d").setDaemon(true).build();
            ExecutorService executorService = new ThreadPoolExecutor(
                    1,
                    1,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(1024),
                    threadFactory,
                    new ThreadPoolExecutor.AbortPolicy());

            executorService.submit(() -> {
                WireMockServer wireMockServer = new WireMockServer(options().port(MOCK_PORT));
                wireMockServer.start();

                wireMockServer.stubFor(
                        WireMock.get(WireMock.urlPathEqualTo(MOCK_PATH))
                                .willReturn(
                                        WireMock.aResponse()
                                                .withBody(MOCK_BODY)
                                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                                                .withStatus(200)));
            });
        }
    }

}