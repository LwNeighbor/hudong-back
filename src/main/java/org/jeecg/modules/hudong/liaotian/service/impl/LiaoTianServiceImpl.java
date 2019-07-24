package org.jeecg.modules.hudong.liaotian.service.impl;

import org.jeecg.modules.hudong.liaotian.entity.LiaoTian;
import org.jeecg.modules.hudong.liaotian.mapper.LiaoTianMapper;
import org.jeecg.modules.hudong.liaotian.service.ILiaoTianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @Description: 聊天
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Service
public class LiaoTianServiceImpl extends ServiceImpl<LiaoTianMapper, LiaoTian> implements ILiaoTianService {

    @Autowired
    private LiaoTianMapper liaoTianMapper;
    @Override
    public List<Map<String, String>> selectLtList(String user, String childId, String time) {
        return liaoTianMapper.selectLtList(user,childId,time);
    }

    @Override
    public void updateCread(String cid) {
        liaoTianMapper.updateCread(cid);
    }

    @Override
    public void updateParentRead(String childId) {
        liaoTianMapper.updateParentRead(childId);
    }
}
