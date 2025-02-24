package com.github.niefy.modules.wx.service.impl;

import com.github.niefy.modules.wx.config.multiApp.WxMpStorageServiceImpl;
import com.github.niefy.modules.wx.service.WxUserService;
import com.github.niefy.modules.wx.service.WxUserTagsService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.tag.WxUserTag;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@CacheConfig(cacheNames = {"wxUserTagsServiceCache"})
@Slf4j
public class WxUserTagsServiceImpl implements WxUserTagsService {
    //多app
    @Resource
    private WxMpStorageServiceImpl wxMpService;
//    @Autowired
//    private WxMpService wxService;
    @Autowired
    private WxUserService wxUserService;
    public static final String CACHE_KEY="'WX_USER_TAGS'";


    //多app
    public static String getLocal(){
        return WxMpConfigStorageHolder.get();
    }

    @Override
//    @Cacheable(key = "#root.target.getLocal() + #root.target.CACHE_KEY")
    public List<WxUserTag> getWxTags() throws WxErrorException {
        log.info("拉取公众号用户标签");
        return wxMpService.getUserTagService().tagGet();
    }

    @Override
//    @Cacheable(key = "#root.target.getLocal() + #root.target.CACHE_KEY")
    public void creatTag(String name) throws WxErrorException {
        wxMpService.getUserTagService().tagCreate(name);
    }

    @Override
//    @Cacheable(key = "#root.target.getLocal() + #root.target.CACHE_KEY")
    public void updateTag(Long tagid, String name) throws WxErrorException {
        wxMpService.getUserTagService().tagUpdate(tagid,name);
    }

    @Override
//    @Cacheable(key = "#root.target.getLocal() + #root.target.CACHE_KEY")
    public void deleteTag(Long tagid) throws WxErrorException {
        wxMpService.getUserTagService().tagDelete(tagid);
    }

    @Override
    public void batchTagging(Long tagid, String[] openidList) throws WxErrorException {
        wxMpService.getUserTagService().batchTagging(tagid,openidList);
        wxUserService.refreshUserInfoAsync(WxMpConfigStorageHolder.get(), openidList);//标签更新后更新对应用户信息
    }

    @Override
    public void batchUnTagging(Long tagid, String[] openidList) throws WxErrorException {
        wxMpService.getUserTagService().batchUntagging(tagid,openidList);
        wxUserService.refreshUserInfoAsync(WxMpConfigStorageHolder.get(), openidList);//标签更新后更新对应用户信息
    }

    @Override
    public void tagging(Long tagid, String openid) throws WxErrorException {
        wxMpService.getUserTagService().batchTagging(tagid,new String[]{openid});
        wxUserService.refreshUserInfo(openid);
    }

    @Override
    public void untagging(Long tagid, String openid) throws WxErrorException {
        wxMpService.getUserTagService().batchUntagging(tagid,new String[]{openid});
        wxUserService.refreshUserInfo(openid);
    }

}
