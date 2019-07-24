package org.jeecg.modules.hudong.msm.service;

import org.jeecg.modules.hudong.mqx.entity.MqXQing;
import org.jeecg.modules.hudong.msm.entity.Msm;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 模式描述
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface IMsmService extends IService<Msm> {

    //根据模式id,查找分类与模式拼接成字符串
    Map<String,String> findFlMsName(String msId);


}
