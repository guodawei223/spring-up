package io.spring.up.jhipster.config;

import infinispan.autoconfigure.embedded.InfinispanCacheConfigurer;
import infinispan.autoconfigure.embedded.InfinispanEmbeddedCacheManagerAutoConfiguration;
import infinispan.autoconfigure.embedded.InfinispanGlobalConfigurer;
import io.github.jhipster.config.JHipsterProperties;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionType;
import org.infinispan.jcache.embedded.JCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.infinispan.transaction.TransactionMode;
import org.jgroups.Channel;
import org.jgroups.JChannel;
import org.jgroups.PhysicalAddress;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.stack.IpAddress;
import org.jgroups.stack.ProtocolStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = {MetricsConfiguration.class})
@AutoConfigureBefore(value = {WebConfigurer.class, DatabaseConfiguration.class})
@Import(InfinispanEmbeddedCacheManagerAutoConfiguration.class)
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    // Initialize the cache in a non Spring-managed bean
    private static EmbeddedCacheManager cacheManager;

    private DiscoveryClient discoveryClient;

    private Registration registration;

    @Autowired(required = false)
    public void setRegistration(final Registration registration) {
        this.registration = registration;
    }

    @Autowired(required = false)
    public void setDiscoveryClient(final DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public static EmbeddedCacheManager getCacheManager() {
        return cacheManager;
    }

    public static void setCacheManager(final EmbeddedCacheManager cacheManager) {
        CacheConfiguration.cacheManager = cacheManager;
    }

    /**
     * Inject a {@link org.infinispan.configuration.global.GlobalConfiguration GlobalConfiguration} for Infinispan cache.
     * <p>
     * If a service discovery solution is enabled (JHipster Registry or Consul),
     * then the host list will be populated from the service discovery.
     * <p>
     * <p>
     * If the service discovery is not enabled, host discovery will be based on
     * the default transport settings defined in the 'config-file' packaged within
     * the Jar. The 'config-file' can be overridden using the application property
     * <i>jhipster.cache.inifnispan.config-file</i>
     * <p>
     * <p>
     * If no service discovery is defined, you have the choice of 'config-file'
     * based on the underlying platform for hosts discovery. Infinispan
     * supports discovery natively for most of the platforms like Kubernets/OpenShift,
     * AWS, Azure and Google.
     */
    @Bean
    public InfinispanGlobalConfigurer globalConfiguration(final JHipsterProperties jHipsterProperties) {
        this.log.info("[ UP Cache ] Defining Infinispan Global Configuration");
        if (this.registration == null) { // if registry is not defined, use native discovery
            this.log.warn("[ UP Cache ] No discovery service is set up, Infinispan will use default discovery for cluster formation");
            return () -> GlobalConfigurationBuilder
                    .defaultClusteredBuilder().transport().defaultTransport()
                    .addProperty("configurationFile", jHipsterProperties.getCache().getInfinispan().getConfigFile())
                    .clusterName("infinispan-imaAuth-cluster").globalJmxStatistics()
                    .enabled(jHipsterProperties.getCache().getInfinispan().isStatsEnabled())
                    .allowDuplicateDomains(true).build();
        }
        return () -> GlobalConfigurationBuilder
                .defaultClusteredBuilder().transport().transport(new JGroupsTransport(this.getTransportChannel()))
                .clusterName("infinispan-imaAuth-cluster").globalJmxStatistics()
                .enabled(jHipsterProperties.getCache().getInfinispan().isStatsEnabled())
                .allowDuplicateDomains(true).build();
    }

    /**
     * Initialize cache configuration for Hibernate L2 cache and Spring Cache.
     * <p>
     * There are three different modes: local, distributed and replicated, and L2 cache options are pre-configured.
     * <p>
     * <p>
     * It supports both jCache and Spring cache abstractions.
     * <p>
     * Usage:
     * <ol>
     * <li>
     * jCache:
     * <pre class="code">@CacheResult(cacheName = "dist-app-data") </pre>
     * - for creating a distributed cache. In a similar way other cache names and options can be used
     * </li>
     * <li>
     * Spring Cache:
     * <pre class="code">@Cacheable(value = "repl-app-data") </pre>
     * - for creating a replicated cache. In a similar way other cache names and options can be used
     * </li>
     * <li>
     * Cache manager can also be injected through DI/CDI and data can be manipulated using Infinispan APIs,
     * <pre class="code">
     *          &#064;Autowired (or) &#064;Inject
     *          private EmbeddedCacheManager cacheManager;
     *
     *          void cacheSample(){
     *              cacheManager.getCache("dist-app-data").put("hi", "there");
     *          }
     *          </pre>
     * </li>
     * </ol>
     */
    @Bean
    public InfinispanCacheConfigurer cacheConfigurer(final JHipsterProperties jHipsterProperties) {
        this.log.info("[ UP Cache ] Defining {} configuration", "app-data for local, replicated and distributed modes");
        final JHipsterProperties.Cache.Infinispan cacheInfo = jHipsterProperties.getCache().getInfinispan();

        return manager -> {
            // initialize application cache
            manager.defineConfiguration("local-app-data", new ConfigurationBuilder().clustering().cacheMode(CacheMode.LOCAL)
                    .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                    .eviction().type(EvictionType.COUNT).size(cacheInfo.getLocal().getMaxEntries()).expiration()
                    .lifespan(cacheInfo.getLocal().getTimeToLiveSeconds(), TimeUnit.MINUTES).build());
            manager.defineConfiguration("dist-app-data", new ConfigurationBuilder()
                    .clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(cacheInfo.getDistributed().getInstanceCount())
                    .jmxStatistics().enabled(cacheInfo.isStatsEnabled()).eviction()
                    .type(EvictionType.COUNT).size(cacheInfo.getDistributed().getMaxEntries()).expiration().lifespan(cacheInfo.getDistributed()
                            .getTimeToLiveSeconds(), TimeUnit.MINUTES).build());
            manager.defineConfiguration("repl-app-data", new ConfigurationBuilder().clustering().cacheMode(CacheMode.REPL_SYNC)
                    .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                    .eviction().type(EvictionType.COUNT).size(cacheInfo.getReplicated()
                            .getMaxEntries()).expiration().lifespan(cacheInfo.getReplicated().getTimeToLiveSeconds(), TimeUnit.MINUTES).build());

            // initialize Hibernate L2 cache
            manager.defineConfiguration("entity", new ConfigurationBuilder().clustering().cacheMode(CacheMode.INVALIDATION_SYNC)
                    .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                    .locking().concurrencyLevel(1000).lockAcquisitionTimeout(15000).build());
            manager.defineConfiguration("replicated-entity", new ConfigurationBuilder().clustering().cacheMode(CacheMode.REPL_SYNC)
                    .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                    .locking().concurrencyLevel(1000).lockAcquisitionTimeout(15000).build());
            manager.defineConfiguration("local-query", new ConfigurationBuilder().clustering().cacheMode(CacheMode.LOCAL)
                    .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                    .locking().concurrencyLevel(1000).lockAcquisitionTimeout(15000).build());
            manager.defineConfiguration("replicated-query", new ConfigurationBuilder().clustering().cacheMode(CacheMode.REPL_ASYNC)
                    .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                    .locking().concurrencyLevel(1000).lockAcquisitionTimeout(15000).build());
            manager.defineConfiguration("timestamps", new ConfigurationBuilder().clustering().cacheMode(CacheMode.REPL_ASYNC)
                    .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                    .locking().concurrencyLevel(1000).lockAcquisitionTimeout(15000).build());
            manager.defineConfiguration("pending-puts", new ConfigurationBuilder().clustering().cacheMode(CacheMode.LOCAL)
                    .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                    .simpleCache(true).transaction().transactionMode(TransactionMode.NON_TRANSACTIONAL).expiration().maxIdle(60000).build());

            setCacheManager(manager);
        };
    }

    /**
     * <p>
     * Instance of {@link JCacheManager} with cache being managed by the underlying Infinispan layer. This helps to record stats
     * info if enabled and the same is accessible through MBX:javax.cache,type=CacheStatistics.
     * <p>
     * <p>
     * jCache stats are at instance level. If you need stats at clustering level, then it needs to be retrieved from MBX:org.infinispan
     */
    @Bean
    public JCacheManager getJCacheManager(final EmbeddedCacheManager cacheManager, final JHipsterProperties jHipsterProperties) {
        return new InfinispanJCacheManager(Caching.getCachingProvider().getDefaultURI(), cacheManager,
                Caching.getCachingProvider(), jHipsterProperties);
    }

    class InfinispanJCacheManager extends JCacheManager {

        public InfinispanJCacheManager(final URI uri, final EmbeddedCacheManager cacheManager, final CachingProvider provider,
                                       final JHipsterProperties jHipsterProperties) {
            super(uri, cacheManager, provider);
            // register individual caches to make the stats info available.
            // jhipster-needle-infinispan-add-entry
            if (jHipsterProperties.getCache().getInfinispan().isStatsEnabled()) {
                for (final String cacheName : cacheManager.getCacheNames()) {
                    this.enableStatistics(cacheName, true);
                }
            }
        }
    }

    /**
     * TCP channel with the host details populated from the service discovery
     * (JHipster Registry or Consul).
     * <p>
     * MPING multicast is replaced with TCPPING with the host details discovered
     * from registry and sends only unicast messages to the host list.
     */
    private Channel getTransportChannel() {
        final JChannel channel = new JChannel(false);
        final List<PhysicalAddress> initialHosts = new ArrayList<>();
        try {
            for (final ServiceInstance instance : this.discoveryClient.getInstances(this.registration.getServiceId())) {
                final String clusterMember = instance.getHost() + ":7800";
                this.log.debug("[ UP Cache ] Adding Infinispan cluster member " + clusterMember);
                initialHosts.add(new IpAddress(clusterMember));
            }
            final TCP tcp = new TCP();
            tcp.setBindAddress(InetAddress.getLocalHost());
            tcp.setBindPort(7800);
            tcp.setThreadPoolMinThreads(2);
            tcp.setThreadPoolMaxThreads(30);
            tcp.setThreadPoolQueueEnabled(false);
            tcp.setThreadPoolKeepAliveTime(60000);
            tcp.setOOBThreadPoolMinThreads(2);
            tcp.setOOBThreadPoolMaxThreads(200);
            tcp.setOOBThreadPoolKeepAliveTime(60000);
            tcp.setOOBThreadPoolQueueEnabled(false);

            final TCPPING tcpping = new TCPPING();
            initialHosts.add(new IpAddress(InetAddress.getLocalHost(), 7800));
            tcpping.setInitialHosts(initialHosts);
            tcpping.setErgonomics(false);
            tcpping.setPortRange(10);
            tcpping.sendCacheInformation();

            final NAKACK2 nakack = new NAKACK2();
            nakack.setUseMcastXmit(false);
            nakack.setDiscardDeliveredMsgs(false);

            final MERGE3 merge = new MERGE3();
            merge.setMinInterval(10000);
            merge.setMaxInterval(30000);

            final FD_ALL fd = new FD_ALL();
            fd.setTimeout(60000);
            fd.setInterval(15000);
            fd.setTimeoutCheckInterval(5000);

            final ProtocolStack stack = new ProtocolStack();
            // Order shouldn't be changed
            stack
                    .addProtocol(tcp)
                    .addProtocol(tcpping)
                    .addProtocol(merge)
                    .addProtocol(new FD_SOCK())
                    .addProtocol(fd)
                    .addProtocol(new VERIFY_SUSPECT())
                    .addProtocol(nakack)
                    .addProtocol(new UNICAST3())
                    .addProtocol(new STABLE())
                    .addProtocol(new GMS())
                    .addProtocol(new MFC())
                    .addProtocol(new FRAG2());
            channel.setProtocolStack(stack);
            stack.init();
        } catch (final Exception e) {
            throw new BeanInitializationException("[ UP Cache ] Cache (Infinispan protocol stack) configuration failed", e);
        }
        return channel;
    }
}
