package io.spring.up.jhipster.config;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.boolex.OnMarkerEvaluator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.FilterReply;
import io.github.jhipster.config.JHipsterProperties;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.Iterator;

@Configuration
@ConditionalOnConsulEnabled
@RefreshScope
public class LoggingConfiguration {

    private static final String LOGSTASH_APPENDER_NAME = "LOGSTASH";

    private static final String ASYNC_LOGSTASH_APPENDER_NAME = "ASYNC_LOGSTASH";

    private final Logger log = LoggerFactory.getLogger(LoggingConfiguration.class);

    private final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    private final String appName;

    private final String serverPort;

    private final ConsulRegistration consulRegistration;

    private final String version;

    private final JHipsterProperties jHipsterProperties;

    public LoggingConfiguration(@Value("${spring.application.name}") final String appName, @Value("${server.port}") final String serverPort,
                                final ConsulRegistration consulRegistration, @Value("${info.project.version:}") final String version, final JHipsterProperties jHipsterProperties) {
        this.appName = appName;
        this.serverPort = serverPort;
        this.consulRegistration = consulRegistration;
        this.version = version;
        this.jHipsterProperties = jHipsterProperties;
        if (jHipsterProperties.getLogging().getLogstash().isEnabled()) {
            this.addLogstashAppender(this.context);
            this.addContextListener(this.context);
        }
        if (jHipsterProperties.getMetrics().getLogs().isEnabled()) {
            this.setMetricsMarkerLogbackFilter(this.context);
        }
    }

    private void addContextListener(final LoggerContext context) {
        final LogbackLoggerContextListener loggerContextListener = new LogbackLoggerContextListener();
        loggerContextListener.setContext(context);
        context.addListener(loggerContextListener);
    }

    private void addLogstashAppender(final LoggerContext context) {
        this.log.info("Initializing Logstash logging");

        final LogstashTcpSocketAppender logstashAppender = new LogstashTcpSocketAppender();
        logstashAppender.setName(LOGSTASH_APPENDER_NAME);
        logstashAppender.setContext(context);
        String optionalFields = "";
        if (this.consulRegistration != null) {
            optionalFields = "\"instance_id\":\"" + this.consulRegistration.getInstanceId() + "\",";
        }
        final String customFields = "{\"app_name\":\"" + this.appName + "\",\"app_port\":\"" + this.serverPort + "\"," +
                optionalFields + "\"version\":\"" + this.version + "\"}";

        // More documentation is available at: https://github.com/logstash/logstash-logback-encoder
        final LogstashEncoder logstashEncoder = new LogstashEncoder();
        // Set the Logstash appender config from JHipster properties
        logstashEncoder.setCustomFields(customFields);
        // Set the Logstash appender config from JHipster properties
        logstashAppender.addDestinations(new InetSocketAddress(this.jHipsterProperties.getLogging().getLogstash().getHost(), this.jHipsterProperties.getLogging().getLogstash().getPort()));

        final ShortenedThrowableConverter throwableConverter = new ShortenedThrowableConverter();
        throwableConverter.setRootCauseFirst(true);
        logstashEncoder.setThrowableConverter(throwableConverter);
        logstashEncoder.setCustomFields(customFields);

        logstashAppender.setEncoder(logstashEncoder);
        logstashAppender.start();

        // Wrap the appender in an Async appender for performance
        final AsyncAppender asyncLogstashAppender = new AsyncAppender();
        asyncLogstashAppender.setContext(context);
        asyncLogstashAppender.setName(ASYNC_LOGSTASH_APPENDER_NAME);
        asyncLogstashAppender.setQueueSize(this.jHipsterProperties.getLogging().getLogstash().getQueueSize());
        asyncLogstashAppender.addAppender(logstashAppender);
        asyncLogstashAppender.start();

        context.getLogger("ROOT").addAppender(asyncLogstashAppender);
    }

    // Configure a log filter to remove "metrics" logs from all appenders except the "LOGSTASH" appender
    private void setMetricsMarkerLogbackFilter(final LoggerContext context) {
        this.log.info("Filtering metrics logs from all appenders except the {} appender", LOGSTASH_APPENDER_NAME);
        final OnMarkerEvaluator onMarkerMetricsEvaluator = new OnMarkerEvaluator();
        onMarkerMetricsEvaluator.setContext(context);
        onMarkerMetricsEvaluator.addMarker("metrics");
        onMarkerMetricsEvaluator.start();
        final EvaluatorFilter<ILoggingEvent> metricsFilter = new EvaluatorFilter<>();
        metricsFilter.setContext(context);
        metricsFilter.setEvaluator(onMarkerMetricsEvaluator);
        metricsFilter.setOnMatch(FilterReply.DENY);
        metricsFilter.start();

        for (final ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            for (final Iterator<Appender<ILoggingEvent>> it = logger.iteratorForAppenders(); it.hasNext(); ) {
                final Appender<ILoggingEvent> appender = it.next();
                if (!appender.getName().equals(ASYNC_LOGSTASH_APPENDER_NAME)) {
                    this.log.debug("Filter metrics logs from the {} appender", appender.getName());
                    appender.setContext(context);
                    appender.addFilter(metricsFilter);
                    appender.start();
                }
            }
        }
    }

    /**
     * Logback configuration is achieved by configuration file and API.
     * When configuration file change is detected, the configuration is reset.
     * This listener ensures that the programmatic configuration is also re-applied after reset.
     */
    class LogbackLoggerContextListener extends ContextAwareBase implements LoggerContextListener {

        @Override
        public boolean isResetResistant() {
            return true;
        }

        @Override
        public void onStart(final LoggerContext context) {
            LoggingConfiguration.this.addLogstashAppender(context);
        }

        @Override
        public void onReset(final LoggerContext context) {
            LoggingConfiguration.this.addLogstashAppender(context);
        }

        @Override
        public void onStop(final LoggerContext context) {
            // Nothing to do.
        }

        @Override
        public void onLevelChange(final ch.qos.logback.classic.Logger logger, final Level level) {
            // Nothing to do.
        }
    }

}
