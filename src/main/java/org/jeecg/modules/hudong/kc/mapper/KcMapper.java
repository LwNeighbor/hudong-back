package org.jeecg.modules.hudong.kc.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.kc.entity.Kc;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 课程表管理
 * @author： jeecg-boot
 * @date：   2019-07-31
 * @version： V1.0
 */
public interface KcMapper extends BaseMapper<Kc> {


    List<Kc> getClassByChild(@Param("childid") String childid, @Param("week") String week, @Param("date") String date);

    //查询开始时间大于当前时间的全部课程并排序,得到下一节课
    List<Kc> listOrderByNumberAsc(@Param("childid") String childid, @Param("week") String week, @Param("date") String date);

    @Update("update hd_kc set fl_name=#{flName} where fl_id=#{id}")
    void updateFenLiById(String id, String flName);


    @Update("update hd_kc set name=#{cdName}, phone=#{cdPhone} where ch_id=#{id}")
    void upChildByChild(String id, String cdName, String cdPhone);
}
