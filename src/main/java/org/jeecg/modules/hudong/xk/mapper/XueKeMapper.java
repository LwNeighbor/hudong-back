package org.jeecg.modules.hudong.xk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.hudong.xk.entity.XueKe;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 学科设置
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface XueKeMapper extends BaseMapper<XueKe> {

    @Update("update hd_xk set fl_name=#{flName} where fl_id=#{id}")
    void updateFenLiById(String id, String flName);
}
