package org.jeecg.modules.hudong.xthf.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.hudong.xthf.entity.Xthf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 学生OK回复的系统回复
 * @author： jeecg-boot
 * @date：   2019-08-02
 * @version： V1.0
 */
public interface XthfMapper extends BaseMapper<Xthf> {

    @Update("update hd_xt_hf set grade_name=#{flName} where grade=#{id}")
    void updateFenLiById(String id, String flName);
}
