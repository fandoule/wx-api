package com.github.niefy.modules.wx.config.multiApp;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.enums.WxMpApiUrl;

import java.util.Map;

/**
 * @author Tango
 * @since 2020/6/4
 */
@Getter
@Setter
@Slf4j
public class WxMpStorageServiceImpl extends WxMpServiceImpl {
    //appId -> appUsername
    protected Map<String, String> usernameStorage;
    //appUsername -> appId
    protected Map<String, String> appIdStorage;
    public final static String GLOBAL_TOKEN = "wechat";


    public String getAppId(String appUsername){
        return appIdStorage.get(appUsername);
    }

    public String getAppUsername(String appId){
        return usernameStorage.get(appId);

    }


    public boolean checkSignature(String timestamp, String nonce, String signature) {
        try {
            return SHA1.gen(GLOBAL_TOKEN, timestamp, nonce).equals(signature);
        } catch (Exception e) {
            log.error("Checking signature failed, and the reason is :" + e.getMessage());
            return false;
        }
    }

}
