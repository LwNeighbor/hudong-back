package org.jeecg.modules.hudong.xtzc.service;

import org.jeecg.modules.hudong.xtzc.entity.Xtzc;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Description: 注册后第几天的固定内容发送
 * @author： jeecg-boot
 * @date：   2019-08-02
 * @version： V1.0
 */
public interface IXtzcService extends IService<Xtzc> {

    Map<String, String> selectMsgByChildTime(String id,String day);
}
