package org.jeecg.modules.hudong.liaotian.service;

import org.jeecg.modules.hudong.liaotian.entity.LiaoTian;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 聊天
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface ILiaoTianService extends IService<LiaoTian> {

    //查询指定时间以前的记录
    List<Map<String, String>> selectLtList(String user, String childId, String time);

    void updateCread(String cid);

    void updateParentRead(String childId);
}
