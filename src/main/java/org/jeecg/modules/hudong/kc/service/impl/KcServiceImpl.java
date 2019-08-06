package org.jeecg.modules.hudong.kc.service.impl;

import org.jeecg.modules.hudong.kc.entity.Kc;
import org.jeecg.modules.hudong.kc.mapper.KcMapper;
import org.jeecg.modules.hudong.kc.service.IKcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @Description: 课程表管理
 * @author： jeecg-boot
 * @date：   2019-07-31
 * @version： V1.0
 */
@Service
public class KcServiceImpl extends ServiceImpl<KcMapper, Kc> implements IKcService {

    @Autowired
    private KcMapper kcMapper;
    @Override
    public Kc getClassByChild(String childid,String week, String date) {
        return kcMapper.getClassByChild(childid,week,date);
    }

    @Override
    public List<Kc> listOrderByNumberAsc(String childid, String week, String date) {
        return kcMapper.listOrderByNumberAsc(childid,week,date);
    }
}
