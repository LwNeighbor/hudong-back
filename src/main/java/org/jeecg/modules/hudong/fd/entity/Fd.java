package org.jeecg.modules.hudong.fd.entity;

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
 * @Description: 反馈管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Data
@TableName("hd_fd")
public class Fd implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**反馈ID*/
	@Excel(name = "反馈ID", width = 32)
	@TableId(type = IdType.UUID)
	private java.lang.String id;
	/**家长ID*/
	@Excel(name = "家长ID", width = 15)
	private java.lang.String fdPtId;
	/**家长姓名*/
	@Excel(name = "家长姓名", width = 15)
	private java.lang.String ptName;
	/**家长电话*/
	@Excel(name = "家长电话", width = 15)
	private java.lang.String ptPhone;
	/**孩子ID*/
	@Excel(name = "孩子ID", width = 15)
	private java.lang.String chId;
	/**系统ID*/
	@Excel(name = "系统ID", width = 15)
	private java.lang.String xtId;
	/**消息所属模式 分类与模式 汉字拼接*/
	@Excel(name = "消息所属模式", width = 15)
	private java.lang.String xtMs;
	/**系统内容*/
	@Excel(name = "系统内容", width = 15)
	private java.lang.String xtContent;
	/**消息标志*/
	@Excel(name = "消息标志", width = 15)
	private java.lang.String xxOpion;
	/**是否反馈 	N.未反馈	Y.已反馈*/
	@Excel(name = "是否反馈", width = 15)
	@Dict(dicCode = "y_n")
	private java.lang.String isFd;
	/**反馈内容*/
	@Excel(name = "反馈内容", width = 15)
	private java.lang.String fdContent;
	/**建议平台提醒时间*/
	@Excel(name = "建议平台提醒时间", width = 15)
	private java.lang.String fdTime;
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

	/**对象归属 HZ/JZ*/
	@Excel(name = "对象归属 HZ/JZ", width = 15)
	private java.lang.String dxVtype;
	/**对象头像*/
	@Excel(name = "对象头像", width = 15)
	private java.lang.String dxAvater;
	/**对象消息内容*/
	@Excel(name = "对象消息内容", width = 15)
	private java.lang.String dxContent;
	/**对象消息类型 /IMG/WZ/YY*/
	@Excel(name = "对象消息类型 /IMG/WZ/YY", width = 15)
	private java.lang.String dxYtype;
	/**语音时间*/
	@Excel(name = "语音时间", width = 15)
	private java.lang.String dxTime;

}
