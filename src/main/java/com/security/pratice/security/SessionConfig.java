package com.security.pratice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@Configuration
@EnableJdbcHttpSession(
		tableName = "SPRING_SESSION",
		cleanupCron = "0 * * * * *",
		maxInactiveIntervalInSeconds = 1800
		)
public class SessionConfig {}
