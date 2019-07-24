package org.jeecg.modules.hudong.child.service.impl;

import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.child.mapper.ChildMapper;
import org.jeecg.modules.hudong.child.service.IChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @Description: 儿童管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Service
public class ChildServiceImpl extends ServiceImpl<ChildMapper, Child> implements IChildService {

    @Autowired
    private ChildMapper childMapper;

    @Override
    public List<Map<String,String>> selectMsChild(String xxOpion, int week) {
        return childMapper.selectMsChild(xxOpion,week);
    }


}
