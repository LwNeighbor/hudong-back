package org.jeecg.modules.hudong.msm.service.impl;

import org.jeecg.modules.hudong.mqx.entity.MqXQing;
import org.jeecg.modules.hudong.msm.entity.Msm;
import org.jeecg.modules.hudong.msm.mapper.MsmMapper;
import org.jeecg.modules.hudong.msm.service.IMsmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @Description: 模式描述
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Service
public class MsmServiceImpl extends ServiceImpl<MsmMapper, Msm> implements IMsmService {

    @Autowired
    private MsmMapper msmMapper;
    @Override
    public Map<String,String> findFlMsName(String msId) {
        return msmMapper.findFlMsName(msId);
    }


}
