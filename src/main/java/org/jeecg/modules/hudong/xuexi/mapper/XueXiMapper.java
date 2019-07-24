package org.jeecg.modules.hudong.xuexi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 学习
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface XueXiMapper extends BaseMapper<XueXi> {

    String selectMaxCreateTime(@Param("id") String id);



    List<Map<String, String>> selectGroupByType(@Param("id") String id, @Param("time") String time);

    List<Map<String, String>> selectCountByMonth(@Param("id") String id, @Param("begin") String begin, @Param("end") String end);

    @Update("update hd_xx_xuexi set cread = 'Y' where XX_CHILD_ID=#{childid}")
    void updateCread(String childid);

    @Update("update hd_xx_xuexi set XX_READ = 'Y' where XX_CHILD_ID=#{childid} and XX_YTYPE <> 'YY'")
    void updateParentRead(String childid);
}
