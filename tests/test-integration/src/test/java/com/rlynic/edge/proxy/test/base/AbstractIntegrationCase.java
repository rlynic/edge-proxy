/*
  This file created at 2020/4/6.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.edge.proxy.test.base;

import com.rlynic.edge.proxy.agent.AgentApplication;
import com.rlynic.edge.proxy.agent.properties.AgentProperties;
import com.rlynic.edge.proxy.channel.ChannelApplication;
import com.rlynic.edge.proxy.test.BaseCase;
import com.rlynic.edge.proxy.test.IntegrationCase;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <code>{@link AbstractIntegrationCase}</code>
 *
 * @author crisis
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= {
        AgentApplication.class,
        ChannelApplication.class
}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
        "rest.task.targetUrl=http://127.0.0.1:" + IntegrationCase.MOCK_PORT
})
public abstract class AbstractIntegrationCase extends BaseCase {



    @Autowired
    protected AgentProperties agentProperties;

    @Override
    protected int getAgentPort() {
        return agentProperties.getPort();
    }
}