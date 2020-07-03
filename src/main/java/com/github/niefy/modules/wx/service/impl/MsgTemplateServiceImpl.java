package com.github.niefy.modules.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.Query;
import com.github.niefy.common.validator.Assert;
import com.github.niefy.modules.wx.config.multiApp.WxMpStorageServiceImpl;
import com.github.niefy.modules.wx.dao.MsgTemplateMapper;
import com.github.niefy.modules.wx.entity.MsgTemplate;
import com.github.niefy.modules.wx.service.MsgTemplateService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("msgTemplateService")
public class MsgTemplateServiceImpl extends ServiceImpl<MsgTemplateMapper, MsgTemplate> implements MsgTemplateService {

    //多app
    @Resource
    private WxMpStorageServiceImpl wxMpService;
//
//    @Autowired
//    private WxMpService wxService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String title = (String) params.get("title");
        String name = (String) params.get("name");
        IPage<MsgTemplate> page = this.page(
                new Query<MsgTemplate>().getPage(params),
                new QueryWrapper<MsgTemplate>()
                        .eq("app_id", WxMpConfigStorageHolder.get())
                        .like(!StringUtils.isEmpty(title), "title", title)
                        .like(!StringUtils.isEmpty(name), "name", name)
        );

        return new PageUtils(page);
    }

    @Override
    public MsgTemplate selectByName(String name) {
        Assert.isBlank(name, "模板名称不得为空");
        return this.getOne(new QueryWrapper<MsgTemplate>()
                //多app
                .eq("app_id", WxMpConfigStorageHolder.get())
                .eq("name", name)
                .eq("status", 1)
                .last("LIMIT 1"));
    }

    @Override
    public void syncWxTemplate() throws WxErrorException {
        //多app
        final String appId = WxMpConfigStorageHolder.get();
        List<WxMpTemplate> wxMpTemplateList = wxMpService.getTemplateMsgService().getAllPrivateTemplate();
        List<MsgTemplate> templates = wxMpTemplateList.stream()
                .map(wxMpTemplate -> {
                    MsgTemplate msgTemplate = new MsgTemplate(wxMpTemplate);
                    msgTemplate.setAppId(appId);
                    return msgTemplate;
                })
                .collect(Collectors.toList());
        this.saveBatch(templates);
    }

}
