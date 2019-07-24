package org.jeecg.modules.hudong.feedback.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 用户反馈
 * @author： jeecg-boot
 * @date：   2019-06-28
 * @version： V1.0
 */
@Data
@TableName("hd_feedback")
public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**ID*/
	@TableId(type = IdType.UUID)
	private String id;
	/**内容*/
	@Excel(name = "内容", width = 15)
	private Object content;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
	private String ptId;
	/**用户姓名*/
	@Excel(name = "用户姓名", width = 15)
	private String ptName;
	/**用户手机号*/
	@Excel(name = "用户手机号", width = 15)
	private String ptPhone;
	/**是否处理*/
	@Excel(name = "是否处理", width = 15)
	@Dict(dicCode = "y_n")
	private String deal;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
	private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
	private String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
}
