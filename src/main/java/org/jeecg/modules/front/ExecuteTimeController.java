package org.jeecg.modules.front;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.child.service.IChildService;
import org.jeecg.modules.hudong.kc.entity.Kc;
import org.jeecg.modules.hudong.kc.service.IKcService;
import org.jeecg.modules.hudong.mqx.entity.MqXQing;
import org.jeecg.modules.hudong.mqx.service.IMqXQingService;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.parent.service.IParentService;
import org.jeecg.modules.hudong.xtzc.service.IXtzcService;
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
    private IXueXiService xueXiService;
    @Resource
    private IMqXQingService mqXQingService;
    @Resource
    private IXtzcService xtzcService;
    @Resource
    private IKcService kcService;
    @Resource
    private IParentService parentService;

    /**
     * 系统注册消息定时
     */
    //@Scheduled(fixedRate=5000)
    @Scheduled(cron = "0 * * * * ? ")
    private void configureTasks() {

        List<Child> list = childService.list();

        list.stream().forEach(child -> {
            Parent user = parentService.getById(child.getPtId());
            String day = DateUtil.formatDate(new Date());
            String dateTime = DateUtil.format(new Date(),"HH:mm");

            //根据孩子的注册时间,查询相应的消息
            Map<String,String> map = xtzcService.selectMsgByChildTime(child.getId(),day+" ");
            if(map != null){

                XueXi xueXi = new XueXi();
                String way = "";
                if("0".equals(String.valueOf(map.get("type")))){
                    way = "WZ";
                }
                if("1".equals(String.valueOf(map.get("type")))){
                    way = "IMG";
                }

                xueXi.setTxType("0");   //提醒方式
                xueXi.setChPhone(child.getCdPhone());
                xueXi.setChName(child.getCdName());
                xueXi.setPtPhone(user.getPtPhone());
                xueXi.setPtName(user.getPtName());
                xueXi.setXxOpion(dateTime);
                xueXi.setXxContent(String.valueOf(map.get("content")));
                xueXi.setXxYtype(way);
                xueXi.setXxVtype("XT");
                xueXi.setXxChildId(child.getId());
                xueXi.setXxParentId(user.getId());

                xueXiService.save(xueXi);
            }
        });
    }

    /**
     * 学习,模式详情定时
     */
    @Scheduled(cron = "0 * * * * ? ")
    private void configureTasks1() {

        int week = DateUtil.dayOfWeek(new Date());      //当前星期几
        if(week == 1){
            week = 7;   //==1时代表周日,现在要给改成数字
        }else{
            week -= 1;  // 每个数字减一才能代表课表上的周
        }
        String date = DateUtil.formatDate(new Date());
        List<Child> list = childService.list();
        for(Child child : list){
            Parent parent = parentService.getById(child.getPtId());
            //注册天数
            long day = DateUtil.betweenDay(new Date(), child.getCreateTime(), true);

            try{
                //查询这个孩子这个时间点是什么课
                List<Kc> kcList = kcService.getClassByChild(child.getId(), String.valueOf(week), date + " ");
               /* if(kc == null){
                    //按照number排序,查询开始时间大于当前时间的数据,选择第一个就是下一节课
                    List<Kc> listKc = kcService.listOrderByNumberAsc(child.getId(), String.valueOf(week), date + " ");

                    if(listKc.size() > 0){
                        kc = listKc.get(0);
                    }else {
                        continue;
                    }
                }else {

                    List<Kc> kcFirst = kcService.list(new QueryWrapper<Kc>().
                            eq("ch_id", child.getId()).
                            eq("weekday", week).
                            eq("number", kc.getNumber()+1)
                    );

                    if(kcFirst.size() == 0){
                        continue;
                    }
                    kc = kcFirst.get(0);
                }*/
               if(kcList.size() == 0){
                   continue;
               }
                Kc kc = kcList.get(0);
                List<MqXQing> listmq = mqXQingService.getListByChildAndKc(String.valueOf(day),kc.getKmName(),child.getFlId());
                for(MqXQing mqXQing : listmq){
                    String mq_time = mqXQing.getMqTime();
                    String format1 = DateUtil.format(new Date(), "HH:mm");
                    String format = DateUtil.format(DateUtil.offsetMinute(new Date(), Integer.parseInt(mq_time)), "HH:mm");
                    if(format.equals(kc.getEndTime())){
                        //说明就是现在,学习消息查询信息
                        XueXi xueXi = new XueXi();
                        xueXi.setTxType(mqXQing.getTxType());   //提醒方式
                        xueXi.setChPhone(child.getCdPhone());
                        xueXi.setChName(child.getCdName());
                        xueXi.setPtPhone(parent.getPtPhone());
                        xueXi.setPtName(parent.getPtName());
                        xueXi.setXxKemu(kc.getKmName());
                        xueXi.setXxOpion(format1);
                        xueXi.setXxContent(mqXQing.getMqContent());
                        xueXi.setXxYtype("WZ");
                        xueXi.setXxVtype("XT");
                        xueXi.setXxChildId(child.getId());
                        xueXi.setXxParentId(parent.getId());

                        xueXiService.save(xueXi);
                    }
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

        }

    }


}

