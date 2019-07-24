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

    @Select("select hmx.ID AS hid,hmx.MQ_CONTENT as content,hc.id as cid from hd_mq_xiangqing hmx," +
            "hd_child hc where hmx.ms_id = hc.MS_ID and " +
            "(hc.PT_ID=#{token} or hc.id = #{token}) and " +
            " timestampdiff(minute,CONCAT(#{format},MQ_TIME),now()) > 1 and hmx.ID not in(select XX_MQ_ID from hd_xx_xuexi where " +
            "XX_MQ_ID is not null and to_days(create_time) = to_days(now()) and (XX_PARENT_ID=#{token} or XX_CHILD_ID=#{token}))" +
            " order by mq_time ")
    List<Map<String,String>> selectXtMsg(@Param("format") String format, @Param("token") String token);
}
