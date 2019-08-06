package org.jeecg.modules.hudong.xtzc.service.impl;

import org.jeecg.modules.hudong.xtzc.entity.Xtzc;
import org.jeecg.modules.hudong.xtzc.mapper.XtzcMapper;
import org.jeecg.modules.hudong.xtzc.service.IXtzcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Map;

/**
 * @Description: 注册后第几天的固定内容发送
 * @author： jeecg-boot
 * @date：   2019-08-02
 * @version： V1.0
 */
@Service
public class XtzcServiceImpl extends ServiceImpl<XtzcMapper, Xtzc> implements IXtzcService {

    @Autowired
    private XtzcMapper xtzcMapper;

    @Override
    public Map<String, String> selectMsgByChildTime(String id,String day) {
        return xtzcMapper.selectMsgByChildTime(id,day);
    }
}
