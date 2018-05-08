package io.spring.up.jhipster.config;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jcache.JCacheGaugeSet;
import com.codahale.metrics.jvm.*;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import com.zaxxer.hikari.HikariDataSource;
import io.github.jhipster.config.JHipsterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableMetrics(proxyTargetClass = true)
public class MetricsConfiguration extends MetricsConfigurerAdapter {

    private static final String PROP_METRIC_REG_JVM_MEMORY = "jvm.memory";
    private static final String PROP_METRIC_REG_JVM_GARBAGE = "jvm.garbage";
    private static final String PROP_METRIC_REG_JVM_THREADS = "jvm.threads";
    private static final String PROP_METRIC_REG_JVM_FILES = "jvm.files";
    private static final String PROP_METRIC_REG_JVM_BUFFERS = "jvm.buffers";
    private static final String PROP_METRIC_REG_JVM_ATTRIBUTE_SET = "jvm.attributes";

    private static final String PROP_METRIC_REG_JCACHE_STATISTICS = "jcache.statistics";

    private final Logger log = LoggerFactory.getLogger(MetricsConfiguration.class);

    private final MetricRegistry metricRegistry = new MetricRegistry();

    private final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

    private final JHipsterProperties jHipsterProperties;

    private HikariDataSource hikariDataSource;

    public MetricsConfiguration(final JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    @Autowired(required = false)
    public void setHikariDataSource(final HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    @Bean
    public MetricRegistry getMetricRegistry() {
        return this.metricRegistry;
    }

    @Override
    @Bean
    public HealthCheckRegistry getHealthCheckRegistry() {
        return this.healthCheckRegistry;
    }

    @PostConstruct
    public void init() {
        this.log.debug("[ UP Matrix ] Registering JVM gauges");
        this.metricRegistry.register(PROP_METRIC_REG_JVM_MEMORY, new MemoryUsageGaugeSet());
        this.metricRegistry.register(PROP_METRIC_REG_JVM_GARBAGE, new GarbageCollectorMetricSet());
        this.metricRegistry.register(PROP_METRIC_REG_JVM_THREADS, new ThreadStatesGaugeSet());
        this.metricRegistry.register(PROP_METRIC_REG_JVM_FILES, new FileDescriptorRatioGauge());
        this.metricRegistry.register(PROP_METRIC_REG_JVM_BUFFERS, new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        this.metricRegistry.register(PROP_METRIC_REG_JVM_ATTRIBUTE_SET, new JvmAttributeGaugeSet());
        this.metricRegistry.register(PROP_METRIC_REG_JCACHE_STATISTICS, new JCacheGaugeSet());
        if (this.hikariDataSource != null) {
            this.log.debug("[ UP Matrix ] Monitoring the datasource");
            // remove the factory created by HikariDataSourceMetricsPostProcessor until JHipster migrate to Micrometer
            this.hikariDataSource.setMetricsTrackerFactory(null);
            this.hikariDataSource.setMetricRegistry(this.metricRegistry);
        }
        if (this.jHipsterProperties.getMetrics().getJmx().isEnabled()) {
            this.log.debug("[ UP Matrix ] Initializing Metrics JMX reporting");
            final JmxReporter jmxReporter = JmxReporter.forRegistry(this.metricRegistry).build();
            jmxReporter.start();
        }
        if (this.jHipsterProperties.getMetrics().getLogs().isEnabled()) {
            this.log.info("[ UP Matrix ] Initializing Metrics Log reporting");
            final Marker metricsMarker = MarkerFactory.getMarker("metrics");
            final Slf4jReporter reporter = Slf4jReporter.forRegistry(this.metricRegistry)
                    .outputTo(LoggerFactory.getLogger("metrics"))
                    .markWith(metricsMarker)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build();
            reporter.start(this.jHipsterProperties.getMetrics().getLogs().getReportFrequency(), TimeUnit.SECONDS);
        }
    }
}
