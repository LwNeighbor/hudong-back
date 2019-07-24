package org.jeecg.modules.hudong.qu.service.impl;

import org.jeecg.modules.hudong.qu.entity.Question;
import org.jeecg.modules.hudong.qu.mapper.QuestionMapper;
import org.jeecg.modules.hudong.qu.service.IQuestionService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 常见问题
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {

}
