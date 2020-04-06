/*
  This file created at 2020/4/6.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.test.base;

import com.rlynic.edge.proxy.agent.AgentApplication;
import com.rlynic.edge.proxy.agent.properties.AgentProperties;
import com.rlynic.edge.proxy.test.BaseCase;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <code>{@link AbstractAgentCase}</code>
 *
 * @author crisis
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= AgentApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableFeignClients(value = "com.rlynic.edge.proxy.test.client")
public abstract class AbstractAgentCase extends BaseCase {

    public final static String LOCAL_URL = "http://127.0.0.1:8081";

    @Autowired
    protected AgentProperties agentProperties;

    @Override
    protected int getAgentPort() {
        return agentProperties.getPort();
    }
}