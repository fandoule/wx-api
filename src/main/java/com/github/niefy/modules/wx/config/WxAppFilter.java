package com.github.niefy.modules.wx.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 取appid
 * @author Tango
 * @since 2020/6/2
 */
@Slf4j
public class WxAppFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("::::::::::::::::init 微信appId过滤 ");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String wechatAppId = httpRequest.getHeader("appId");
        log.info("::::::::::::::::appid -> {}", wechatAppId);
        log.info("::::::::::::::::uri -> {}, params -> {}",httpRequest.getRequestURI(), JSON.toJSONString(httpRequest.getParameterMap()));
        if(StringUtils.isNotBlank(wechatAppId)){
            WxMpConfigStorageHolder.set(wechatAppId);
        }else{
            WxMpConfigStorageHolder.remove();
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
