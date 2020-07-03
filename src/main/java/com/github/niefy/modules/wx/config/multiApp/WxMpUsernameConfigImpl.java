package com.github.niefy.modules.wx.config.multiApp;

import lombok.Data;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

/**
 * @author Tango
 * @since 2020/6/4
 */
@Data
public class WxMpUsernameConfigImpl extends WxMpDefaultConfigImpl {
    protected volatile String appUsername;

}
