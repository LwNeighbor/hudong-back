package org.jeecg.modules.hudong.child.service;

import org.jeecg.modules.hudong.child.entity.Child;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.hudong.parent.entity.Parent;

import java.util.List;
import java.util.Map;

/**
 * @Description: 儿童管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
public interface IChildService extends IService<Child> {

    List<Map<String, String>> selectMsgAndChild(String userid);
}
