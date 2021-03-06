package org.jeecg.modules.hudong.child.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.hudong.child.entity.Child;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.hudong.msfenlei.entity.MsFenLi;
import org.jeecg.modules.hudong.parent.entity.Parent;

/**
 * @Description: 儿童管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface ChildMapper extends BaseMapper<Child> {


    @Select("select hc.id as id,hc.pt_id as ptId, hc.cd_name as cdName, hc.cd_birthday as cdBirthday," +
            "hc.cd_sex as cdSex, hc.cd_phone as cdPhone, hc.cd_password as cdPassword, (select count(*) from hd_xx_xuexi where XX_CHILD_ID = hc.id and XX_READ='N' and create_time like #{date}) as count," +
            "(select count(*) from hd_xx_liaotian where LT_CH_ID=hc.id and LT_READ='N' and create_time like #{date}) as lcount " +
            "from hd_child hc where hc.pt_id=#{userid} group by hc.ID ")
    List<Map<String, String>> selectMsgAndChild(String userid,String date);


    @Update("update hd_child set fl_name=#{flName} where fl_id = #{flId}")
    boolean updateFenLiById(String flId, String flName);
}
