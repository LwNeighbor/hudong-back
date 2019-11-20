package org.jeecg.modules.hudong.xtzc.entity;

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
 * @Description: 注册后第几天的固定内容发送
 * @author： jeecg-boot
 * @date：   2019-08-02
 * @version： V1.0
 */
@Data
@TableName("hd_xt_zc")
public class Xtzc implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**学习记录ID*/
	@TableId(type = IdType.UUID)
	private java.lang.String id;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
	private java.lang.String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
	private java.lang.String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
	/**注册后的天数*/
	@Excel(name = "注册后的天数", width = 15)
	private java.lang.String psTime;
	/**消息标题*/
	@Excel(name = "消息标题", width = 15)
	private java.lang.String title;
	/**消息简介*/
	@Excel(name = "消息简介", width = 15)
	private java.lang.String introduce;
	/**发送时间*/
	@Excel(name = "发送时间", width = 15)
	private java.lang.String sendTime;

	private String type;

	private String content;
}
