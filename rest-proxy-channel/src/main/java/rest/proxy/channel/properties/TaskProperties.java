/**
 * This file created at 2018年7月15日.
 *
 * Copyright (c) 2002-2018 crisis, Inc. All rights reserved.
 */
package rest.proxy.channel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <code>{@link TaskProperties}</code>
 *
 * @author crisis
 */
@Data
@ConfigurationProperties("rest.task")
public class TaskProperties {

	private int maxConcurrency;
	private String targetUrl;

}
