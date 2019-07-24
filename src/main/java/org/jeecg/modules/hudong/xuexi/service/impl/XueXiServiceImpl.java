package org.jeecg.modules.hudong.xuexi.service.impl;

import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;
import org.jeecg.modules.hudong.xuexi.mapper.XueXiMapper;
import org.jeecg.modules.hudong.xuexi.service.IXueXiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @Description: 学习
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Service
public class XueXiServiceImpl extends ServiceImpl<XueXiMapper, XueXi> implements IXueXiService {

    @Autowired
    private XueXiMapper xueXiMapper;
    @Override
    public String selectMaxCreateTime(String id) {
        return xueXiMapper.selectMaxCreateTime(id);
    }


    @Override
    public List<Map<String, String>> selectGroupByType(String id, String time) {
        return xueXiMapper.selectGroupByType(id,time);
    }

    @Override
    public List<Map<String, String>> selectCountByMonth(String id, String begin, String end) {
        return xueXiMapper.selectCountByMonth(id,begin,end);
    }

    @Override
    public void updateCread(String childid) {
        xueXiMapper.updateCread(childid);
    }

    @Override
    public void updateParentRead(String childid) {
        xueXiMapper.updateParentRead(childid);
    }
}
