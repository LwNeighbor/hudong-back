package org.jeecg.modules.front;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.hudong.child.service.IChildService;
import org.jeecg.modules.hudong.msm.entity.Msm;
import org.jeecg.modules.hudong.msm.service.IMsmService;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;
import org.jeecg.modules.hudong.xuexi.service.IXueXiService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class ExecuteTimeController {

    @Resource
    private IChildService childService;
    @Resource
    private IMsmService msmService;
    @Resource
    private IXueXiService xueXiService;

    //3.添加定时任务
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    @Scheduled(cron = "0 * * * * ? ")
    private void configureTasks() {


        int week = DateUtil.dayOfWeek(new Date());

        String xxOpion = DateUtil.format(new Date(),"HH:mm");
        List<Map<String, String>> mlist = childService.selectMsChild(xxOpion,week);

        mlist.stream().forEach(map -> {

            XueXi xue = new XueXi();
            xue.setXxParentId(map.get("ptid"));
            xue.setXxChildId(map.get("cid"));
            xue.setXxYtype("WZ"); //发的是文字
            xue.setXxOpion(map.get("xxopion"));
            xue.setXxVtype("XT");
            xue.setXxContent(map.get("content"));
            xue.setXxMqId(map.get("mqid"));
            xue.setXxKemu(map.get("kemu"));
            xue.setTxType(map.get("txType"));
            xueXiService.save(xue);
        });
    }

}

