package org.jeecg.modules.hudong.xtzc.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.hudong.xtzc.entity.Xtzc;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 注册后第几天的固定内容发送
 * @author： jeecg-boot
 * @date：   2019-08-02
 * @version： V1.0
 */
public interface XtzcMapper extends BaseMapper<Xtzc> {


    @Select("select * from hd_xt_zc zc where zc.ps_time=(select to_days(now())-to_days(hz.create_time) as tian " +
            "from hd_child hz where hz.id = #{id}) and concat(#{day},zc.send_time) = sysdate()")
    Map<String, String> selectMsgByChildTime(String id,String day);
}
