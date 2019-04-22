/**
 * This file created at 2018年7月14日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.channel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <code>{@link ChannelApplication}</code>
 *
 * @author crisis
 */
@EnableFeignClients
@EnableScheduling
@SpringBootApplication
public class ChannelApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChannelApplication.class, args);
	}
	
}
