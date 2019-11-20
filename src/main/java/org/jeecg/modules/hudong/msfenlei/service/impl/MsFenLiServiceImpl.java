package org.jeecg.modules.hudong.msfenlei.service.impl;

import org.jeecg.modules.hudong.child.mapper.ChildMapper;
import org.jeecg.modules.hudong.child.service.IChildService;
import org.jeecg.modules.hudong.kc.mapper.KcMapper;
import org.jeecg.modules.hudong.msfenlei.entity.MsFenLi;
import org.jeecg.modules.hudong.msfenlei.mapper.MsFenLiMapper;
import org.jeecg.modules.hudong.msfenlei.service.IMsFenLiService;
import org.jeecg.modules.hudong.xk.mapper.XueKeMapper;
import org.jeecg.modules.hudong.xthf.mapper.XthfMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 模式分类
 * @author： jeecg-boot
 * @date：   2019-06-21
 * @version： V1.0
 */
@Service
@Transactional
public class MsFenLiServiceImpl extends ServiceImpl<MsFenLiMapper, MsFenLi> implements IMsFenLiService {

    @Autowired
    private MsFenLiMapper msFenLiMapper;
    @Autowired
    private ChildMapper childMapper;
    @Autowired
    private XueKeMapper xueKeMapper;
    @Autowired
    private KcMapper kcMapper;
    @Autowired
    private XthfMapper xthfMapper;

    @Override
    public int updateFenLiById(MsFenLi msFenLi) {

        //需要修改孩子
        //修改学科
        //课程
        //系统回复OK

        childMapper.updateFenLiById(msFenLi.getId(),msFenLi.getFlName());
        xueKeMapper.updateFenLiById(msFenLi.getId(),msFenLi.getFlName());
        kcMapper.updateFenLiById(msFenLi.getId(),msFenLi.getFlName());
        xthfMapper.updateFenLiById(msFenLi.getId(),msFenLi.getFlName());
        return msFenLiMapper.updateById(msFenLi);
    }
}
