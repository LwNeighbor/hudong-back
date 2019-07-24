package org.jeecg.modules.hudong.liaotian.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.hudong.liaotian.entity.LiaoTian;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.hudong.parent.entity.Parent;

/**
 * @Description: 聊天
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface LiaoTianMapper extends BaseMapper<LiaoTian> {



    @Select("select * from hd_xx_liaotian where LT_PT_ID=#{userId} and LT_CH_ID=#{childId} and " +
            "TO_DAYS(create_time) = TO_DAYS(#{time}) ")
    List<Map<String, String>> selectLtList(String userId, String childId, String time);

    /**
     * 将孩子消息标记为已读
     * @param cid
     */
    @Update("update hd_xx_liaotian set cread = 'Y' where LT_CH_ID=#{cid}")
    void updateCread(String cid);


    @Update("update hd_xx_liaotian set LT_READ = 'Y' where LT_CH_ID=#{childid} and LT_YTYPE <> 'YY'")
    void updateParentRead(String childId);
}
