package org.jeecg.modules.hudong.mqx.service;

import org.jeecg.modules.hudong.mqx.entity.MqXQing;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 模式详情
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface IMqXQingService extends IService<MqXQing> {

    List<MqXQing> getListByChildAndKc(String day, String kmName, String flId);
}
