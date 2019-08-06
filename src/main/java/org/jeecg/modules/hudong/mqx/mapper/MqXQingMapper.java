package org.jeecg.modules.hudong.mqx.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.hudong.mqx.entity.MqXQing;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 模式详情
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface MqXQingMapper extends BaseMapper<MqXQing> {

    List<MqXQing> getListByChildAndKc(@Param("day") String day, @Param("kmName") String kmName, @Param("flId") String flId);
}
