package org.jeecg.modules.hudong.mqx.entity;

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
 * @Description: 模式详情
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Data
@TableName("hd_mq_xiangqing")
public class MqXQing implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**详情ID*/
	@Excel(name = "详情ID", width = 32)
	@TableId(type = IdType.UUID)
	private java.lang.String id;
	/**描述ID*/
	@Excel(name = "描述ID", width = 15)
	private java.lang.String msId;
	/**科目*/
	@Excel(name = "科目", width = 15)
	private java.lang.String mqKemu;
	/**详情时间*/
	@Excel(name = "详情时间", width = 15)
	private java.lang.String mqTime;
	/**详情内容*/
	@Excel(name = "详情内容", width = 15)
	private java.lang.String mqContent;
	/**消息周几提示*/
	@Excel(name = "消息周几提示", width = 15)
	private java.lang.String week;
	/**
	 * 提醒类型 (0.静音. 1.响铃 2.震动 3.响铃加震动)
	 * */
	@Excel(name = "提醒类型", width = 15)
	private java.lang.String txType;
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
}
