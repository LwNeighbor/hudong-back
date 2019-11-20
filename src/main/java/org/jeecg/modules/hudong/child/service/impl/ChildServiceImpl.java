package org.jeecg.modules.hudong.child.service.impl;

import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.child.mapper.ChildMapper;
import org.jeecg.modules.hudong.child.service.IChildService;
import org.jeecg.modules.hudong.kc.mapper.KcMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: 儿童管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Service
@Transactional
public class ChildServiceImpl extends ServiceImpl<ChildMapper, Child> implements IChildService {

    @Autowired
    private ChildMapper childMapper;
    @Autowired
    private KcMapper kcMapper;

    @Override
    public List<Map<String, String>> selectMsgAndChild(String userid,String date) {
        return childMapper.selectMsgAndChild(userid,date);
    }

    @Override
    public void updateNameAndKcById(Child child) {
        childMapper.updateById(child);
        kcMapper.upChildByChild(child.getId(),child.getCdName(),child.getCdPhone());
    }
}
