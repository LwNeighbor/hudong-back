package org.jeecg.modules.hudong.xthf.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 学生OK回复的系统回复
 * @author： jeecg-boot
 * @date：   2019-08-02
 * @version： V1.0
 */
@Data
@TableName("hd_xt_hf")
public class Xthf implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**学习记录ID*/
	@TableId(type = IdType.UUID)
	private java.lang.String id;
	/** 注册后天数 */
	@Excel(name = "注册后天数", width = 15)
	private java.lang.String psTime;
	/** 消息内容 */
	@Excel(name = "消息内容", width = 15)
	private java.lang.String introduce;
	/**反馈内容*/
	@Excel(name = "反馈内容", width = 15)
	private java.lang.Object content;
	/**年级ID*/
	private java.lang.String grade;

	@Excel(name = "年级名称", width = 15)
	private java.lang.String gradeName;
	/**科目*/
	@Excel(name = "科目", width = 15)
	private java.lang.String kemu;
	/**内容类型*/
	@Excel(name = "内容类型", width = 15)
	private String type;

	/**创建人*/
	private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**更新人*/
	private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;



}
