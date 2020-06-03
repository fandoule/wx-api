package com.github.niefy.modules.wx.controller;

import com.github.niefy.common.utils.R;
import com.github.niefy.modules.wx.config.WxMpProperties;
import com.github.niefy.modules.wx.entity.WxUser;
import com.github.niefy.modules.wx.form.WxUserTaggingForm;
import com.github.niefy.modules.wx.service.WxUserService;
import com.github.niefy.modules.wx.service.WxUserTagsService;
import com.github.niefy.modules.wx.service.impl.WxMpPropertiesServiceImpl;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wxMpProperties")
@RequiredArgsConstructor
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpPropertiesController {

    private final WxMpProperties properties;

    @GetMapping("/getList")
    public R getList() {
        final List<WxMpProperties.MpConfig> configs = this.properties.getConfigs();
        List<WxMpProperties.MpConfig> collect = configs.stream()
                .map(mpConfig -> {
                    WxMpProperties.MpConfig result = new WxMpProperties.MpConfig();
                    result.setAppId(mpConfig.getAppId());
                    result.setAppName(mpConfig.getAppName());
                    return result;
                })
                .collect(Collectors.toList());
        return R.ok().put(collect);
    }


}
