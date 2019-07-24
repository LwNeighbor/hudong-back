package org.jeecg.modules.hudong.child.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.hudong.child.entity.Child;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 儿童管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface ChildMapper extends BaseMapper<Child> {

    @Select("select hc.pt_id as ptid,hc.id as cid,mq.mq_time as xxopion,mq.mq_content as content,mq.ID as mqid," +
            "mq.mq_kemu as kemu,mq.tx_type as txType from hd_child hc,hd_mq_xiangqing mq " +
            "where" +
            " hc.ms_id = mq.MS_ID" +
            " and mq.week = #{week} "+
            " and mq.mq_time = #{xxOpion}")
    List<Map<String,String>> selectMsChild(String xxOpion, int week);

}
