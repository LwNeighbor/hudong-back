package org.jeecg.modules.hudong.feedback.service.impl;

import org.jeecg.modules.hudong.feedback.entity.Feedback;
import org.jeecg.modules.hudong.feedback.mapper.FeedbackMapper;
import org.jeecg.modules.hudong.feedback.service.IFeedbackService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户反馈
 * @author： jeecg-boot
 * @date：   2019-06-28
 * @version： V1.0
 */
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements IFeedbackService {

}
