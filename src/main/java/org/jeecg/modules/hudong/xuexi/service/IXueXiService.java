package org.jeecg.modules.hudong.xuexi.service;

import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 学习
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface IXueXiService extends IService<XueXi> {

    String selectMaxCreateTime(String id);

    List<Map<String, String>> selectGroupByType(String id, String time);

    List<Map<String, String>> selectCountByMonth(String id, String begin, String end);

    void updateCread(String childid);

    void updateParentRead(String childid);
}
