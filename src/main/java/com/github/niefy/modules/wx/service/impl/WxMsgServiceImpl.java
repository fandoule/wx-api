package com.github.niefy.modules.wx.service.impl;

import com.github.niefy.modules.wx.config.multiApp.WxMpStorageServiceImpl;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.Query;

import com.github.niefy.modules.wx.dao.WxMsgMapper;
import com.github.niefy.modules.wx.entity.WxMsg;
import com.github.niefy.modules.wx.service.WxMsgService;

import javax.annotation.Resource;


@Service("wxMsgService")
public class WxMsgServiceImpl extends ServiceImpl<WxMsgMapper, WxMsg> implements WxMsgService {
    //多app appId -> appUsername
    @Resource
    private WxMpStorageServiceImpl wxMpService;

    public String getAppId(String appUsername){
        return wxMpService.getAppId(appUsername);
    }


    /**
     * 未保存的队列
     */
    private static final ConcurrentLinkedQueue<WxMsg> logsQueue = new ConcurrentLinkedQueue<>();

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        String msgTypes = (String)params.get("msgTypes");
        String startTime = (String)params.get("startTime");
        String openid = (String)params.get("openid");
        //多app 取原始ID参数
        String appId = WxMpConfigStorageHolder.get();
        String appUsername = wxMpService.getAppUsername(appId);

        IPage<WxMsg> page = this.page(
                new Query<WxMsg>().getPage(params),
                new QueryWrapper<WxMsg>()
                        .in(StringUtils.isNotEmpty(msgTypes),"msg_type", Arrays.asList(msgTypes.split(",")))
                        .and(i-> i.eq("app_username", appUsername).or(e-> e.eq("app_id", appId)))
                        .eq(StringUtils.isNotEmpty(appUsername), "app_username", appUsername)
                        .eq(StringUtils.isNotEmpty(openid),"openid",openid)
                        .ge(StringUtils.isNotEmpty(startTime),"create_time",startTime)
        );

        return new PageUtils(page);
    }

    /**
     * 添加访问log到队列中，队列数据会定时批量插入到数据库
     * @param log
     */
    @Override
    public void addWxMsg(WxMsg log) {
        logsQueue.offer(log);
    }

    /**
     * 定时将日志插入到数据库
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    synchronized void batchAddLog(){
        List<WxMsg> logs = new ArrayList<>();
        while (!logsQueue.isEmpty()){
            logs.add(logsQueue.poll());
        }
        if(!logs.isEmpty()){
            this.saveBatch(logs);
        }
    }

}