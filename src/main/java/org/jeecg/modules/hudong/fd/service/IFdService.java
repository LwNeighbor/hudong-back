package org.jeecg.modules.hudong.fd.service;

import org.jeecg.modules.hudong.fd.entity.Fd;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;

import java.util.List;

/**
 * @Description: 反馈管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface IFdService extends IService<Fd> {

    void saveFdAndUpdateXuexi(XueXi xueXi, XueXi xueXi1, Parent user, String chid) throws Exception;
}
