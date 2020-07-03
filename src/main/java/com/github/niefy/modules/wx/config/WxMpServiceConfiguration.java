package com.github.niefy.modules.wx.config;

import com.github.niefy.modules.wx.config.multiApp.WxMpStorageServiceImpl;
import com.github.niefy.modules.wx.config.multiApp.WxMpUsernameConfigImpl;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.common.util.http.apache.DefaultApacheHttpClientBuilder;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceHttpClientImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceOkHttpImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpServiceConfiguration {
    private final WxMpProperties properties;

    @Bean(name="wxMpService")
    public WxMpService wxMpService() {
        // 代码里 getConfigs()处报错的同学，请注意仔细阅读项目说明，你的IDE需要引入lombok插件！！！！
        final List<WxMpProperties.MpConfig> configs = this.properties.getConfigs();
        if (configs == null) {
            throw new RuntimeException("大哥，拜托先看下项目首页的说明（readme文件），添加下相关配置，注意别配错了！");
        }
        DefaultApacheHttpClientBuilder httpClientBuilder = DefaultApacheHttpClientBuilder.get();
        httpClientBuilder.setConnectionRequestTimeout(-1);//默认设置情况下多线程并发推送模板消息会有部分线程获取不到连接导致发送失败
        httpClientBuilder.setMaxConnPerHost(50);
        WxMpStorageServiceImpl service = new WxMpStorageServiceImpl();

        Map<String, String> usernameMap = new HashMap<>(),
                appIdMap = new HashMap<>();

        Map<String, WxMpConfigStorage> configMap = configs.stream()
                .map(a -> {
                    WxMpUsernameConfigImpl configStorage = new WxMpUsernameConfigImpl();
                    configStorage.setAppUsername(a.getAppUsername());
                    configStorage.setAppId(a.getAppId());
                    configStorage.setSecret(a.getSecret());
                    configStorage.setToken(a.getToken());
                    configStorage.setAesKey(a.getAesKey());
                    configStorage.setApacheHttpClientBuilder(httpClientBuilder);
                    //多app
                    usernameMap.put(a.getAppId(), a.getAppUsername());
                    appIdMap.put( a.getAppUsername(), a.getAppId());
                    return configStorage;
                })
                .collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, a -> a, (o, n) -> o));

        service.setMultiConfigStorages(configMap);
        service.setUsernameStorage(usernameMap);
        service.setAppIdStorage(appIdMap);
        return service;
    }





}
