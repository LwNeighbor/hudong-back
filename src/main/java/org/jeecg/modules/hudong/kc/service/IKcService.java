package org.jeecg.modules.hudong.kc.service;

import org.jeecg.modules.hudong.kc.entity.Kc;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 课程表管理
 * @author： jeecg-boot
 * @date：   2019-07-31
 * @version： V1.0
 */
public interface IKcService extends IService<Kc> {

    List<Kc> getClassByChild(String childid,String week, String date);

    List<Kc> listOrderByNumberAsc(String id, String valueOf, String s);

}
