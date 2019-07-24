package org.jeecg.modules.hudong.mqx.service.impl;

import org.jeecg.modules.hudong.mqx.entity.MqXQing;
import org.jeecg.modules.hudong.mqx.mapper.MqXQingMapper;
import org.jeecg.modules.hudong.mqx.service.IMqXQingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @Description: 模式详情
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Service
public class MqXQingServiceImpl extends ServiceImpl<MqXQingMapper, MqXQing> implements IMqXQingService {

    @Autowired
    private MqXQingMapper mqXQingMapper;
    //该家长下未读的系统消息
    @Override
    public List<Map<String,String>> selectXtMsg(String format, String token) {
        return mqXQingMapper.selectXtMsg(format,token);
    }
}
