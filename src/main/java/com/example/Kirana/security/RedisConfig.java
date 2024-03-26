//package com.example.Kirana.security;
//
//import io.github.bucket4j.distributed.proxy.ProxyManager;
//import io.github.bucket4j.grid.jcache.JCacheProxyManager;
//import org.redisson.config.Config;
//import org.redisson.jcache.configuration.RedissonConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.cache.Cache;
//import javax.cache.CacheManager;
//import javax.cache.Caching;
//
//@Configuration
//public class RedisConfig  {
//
//    @Bean
//    public Config config() {
//        Config config = new Config();
//        config.useSingleServer().setAddress("redis://localhost:6379");
//        return config;
//    }
//
//    @Bean
//    public Cache<String, String> cache(Config config) {
//        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
//        Cache<String, String> cache = cacheManager.createCache("cache", RedissonConfiguration.fromConfig(config));
//        return cache;
//    }
//
//    @Bean
//    ProxyManager<String> proxyManager(CacheManager cacheManager) {
//        return new JCacheProxyManager<>(cacheManager.getCache("cache"));
//    }
//}