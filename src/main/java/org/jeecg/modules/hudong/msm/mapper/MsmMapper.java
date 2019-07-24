package org.jeecg.modules.hudong.msm.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.hudong.mqx.entity.MqXQing;
import org.jeecg.modules.hudong.msm.entity.Msm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 模式描述
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface MsmMapper extends BaseMapper<Msm> {

    @Select("select mf.FL_NAME as flname,mm.MS_NAME as msname from hd_ms_fenlei mf,hd_ms_miaoshu mm " +
            "where mf.ID = mm.FL_ID and mm.ID=#{msId}")
    Map<String,String> findFlMsName(@Param("msId") String msId);

}
