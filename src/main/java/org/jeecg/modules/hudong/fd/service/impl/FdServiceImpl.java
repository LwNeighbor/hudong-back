package org.jeecg.modules.hudong.fd.service.impl;

import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.child.mapper.ChildMapper;
import org.jeecg.modules.hudong.fd.entity.Fd;
import org.jeecg.modules.hudong.fd.mapper.FdMapper;
import org.jeecg.modules.hudong.fd.service.IFdService;
import org.jeecg.modules.hudong.msfenlei.entity.MsFenLi;
import org.jeecg.modules.hudong.msfenlei.mapper.MsFenLiMapper;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.parent.mapper.ParentMapper;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;
import org.jeecg.modules.hudong.xuexi.mapper.XueXiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;


/**
 * @Description: 反馈管理
 * @author： jeecg-boot
 * @date： 2019-06-20
 * @version： V1.0
 */
@Service
public class FdServiceImpl extends ServiceImpl<FdMapper, Fd> implements IFdService {

    @Autowired
    private FdMapper fdMapper;
    @Autowired
    private XueXiMapper xueXiMapper;
    @Autowired
    private ParentMapper parentMapper;
    @Autowired
    private ChildMapper childMapper;
    @Autowired
    private MsFenLiMapper msFenLiMapper;


    @Override
    @Transactional
    public void saveFdAndUpdateXuexi(XueXi xueXi, XueXi xueXi1, Parent user, String chid) throws Exception {


        Child child = childMapper.selectById(chid);

        MsFenLi msFenLi = msFenLiMapper.selectById(child.getFlId());

        Fd fd = new Fd();
        fd.setFdPtId(user.getId()); //家长id
        fd.setPtName(user.getPtName()); //家长名字
        fd.setPtPhone(user.getPtPhone());
        fd.setXtId(xueXi.getId());
        fd.setXtMs(msFenLi.getFlName());           //系统消息属于那个分类与模式
        fd.setXxOpion(xueXi.getXxOpion());
        fd.setXtContent(xueXi.getXxContent());
        fd.setChId(chid);

        if (xueXi1 != null) {
            String xxVtype = xueXi1.getXxVtype();
            String avater = "";
            if (xxVtype.equalsIgnoreCase("JZ")) {
                avater = parentMapper.selectById(xueXi1.getXxParentId()).getPtAvater();
            } else if (xxVtype.equalsIgnoreCase("HZ")) {
                avater = childMapper.selectById(xueXi1.getXxChildId()).getChildAvater();
            }
            fd.setDxAvater(avater);
            fd.setDxContent(xueXi1.getXxContent());
            fd.setDxTime(xueXi1.getYyTime());
            fd.setDxVtype(xueXi1.getXxVtype());
            fd.setDxYtype(xueXi1.getXxYtype());
        }
        fdMapper.insert(fd);
        xueXi.setFeedback("Y");
        xueXiMapper.updateById(xueXi);

    }
}
