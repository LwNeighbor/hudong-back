package org.jeecg.modules.hudong.xuexi.entity;

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
 * @Description: 学习
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Data
@TableName("hd_xx_xuexi")
public class XueXi implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**学习记录ID*/
	@Excel(name = "学习记录ID", width = 32)
	@TableId(type = IdType.UUID)
	private java.lang.String id;
	/**描述详情ID*/
	@Excel(name = "描述详情ID", width = 15)
	private java.lang.String xxMqId;
	/**消息所属父母ID*/
	@Excel(name = "消息所属父母ID", width = 15)
	private java.lang.String xxParentId;
	/**消息所属孩子ID*/
	@Excel(name = "消息所属孩子ID", width = 15)
	private java.lang.String xxChildId;
	/**消息所属类型 XT/系统,HZ/孩子,JZ/家长*/
	@Excel(name = "消息所属类型 XT/系统,HZ/孩子,JZ/家长", width = 15)
	@Dict(dicCode = "v_type")
	private java.lang.String xxVtype;
	/**消息类型 WZ/文字,YY/语音,IMG/图片*/
	@Excel(name = "消息类型 WZ/文字,YY/语音,IMG/图片", width = 15)
	@Dict(dicCode = "y_type")
	private java.lang.String xxYtype;
	/**消息内容*/
	@Excel(name = "消息内容", width = 15)
	private java.lang.String xxContent;
	/**语音时长*/
	@Excel(name = "语音时长", width = 15)
	private java.lang.String yyTime;
	/**科目*/
	@Excel(name = "科目", width = 15)
	private java.lang.String xxKemu;
	/**
	 * 提醒类型 (0.静音. 1.响铃 2.震动 3.响铃加震动)
	 * */
	@Excel(name = "提醒类型", width = 15)
	private java.lang.String txType;
	/**消息读取状态 Y/已读.N/未读,默认为N*/
	@Dict(dicCode = "y_n")
	@Excel(name = "消息读取状态 Y/已读.N/未读,默认为N", width = 15)
	private java.lang.String xxRead;
	/**系统消息反馈状态 Y/已读.N/未读,默认为N*/
	@Dict(dicCode = "y_n")
	@Excel(name = "是否反馈 Y/已读.N/未读,默认为N", width = 15)
	private java.lang.String feedback;
	/**消息时间归类(用于一天聊天中的消息分类时间段区分),每次用户进入时发送消息,保存该标记时间,用于分类显示*/
	@Excel(name = "消息时间归类(用于一天聊天中的消息分类时间段区分),每次用户进入时发送消息,保存该标记时间,用于分类显示", width = 15)
	private java.lang.String xxOpion;
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

	/**家长手机号*/
	private java.lang.String ptPhone;
	/**家长姓名*/
	private java.lang.String ptName;
	/**孩子手机号*/
	private java.lang.String chPhone;
	/**孩子姓名*/
	private java.lang.String chName;
	/**  孩子是否已读 */
	private String cread;
	/** 文件物理位置**/
	private String filepath;
	/** 语音对应的文字 **/
	private String yytext;
}
