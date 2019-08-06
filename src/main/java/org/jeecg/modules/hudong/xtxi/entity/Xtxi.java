package org.jeecg.modules.hudong.xtxi.entity;

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
 * @Description: 系统消息管理
 * @author： jeecg-boot
 * @date：   2019-06-29
 * @version： V1.0
 */
@Data
@TableName("hd_xt_xx")
public class Xtxi implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**学习记录ID*/
	@TableId(type = IdType.UUID)
	private String id;
	/**系统推送时间*/
	@Excel(name = "系统推送时间", width = 15)
	private String psTime;
	/**消息标题*/
	@Excel(name = "消息标题", width = 15)
	private String title;
	/**消息简介*/
	@Excel(name = "消息简介", width = 15)
	private String introduce;
	/**消息内容*/
	@Excel(name = "消息内容", width = 15)
	private Object content;
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
