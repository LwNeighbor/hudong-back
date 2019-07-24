package org.jeecg.modules.hudong.parent.entity;

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
 * @Description: 父母管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Data
@TableName("hd_parent")
public class Parent implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**家长ID*/
	@Excel(name = "家长ID", width = 32)
	@TableId(type = IdType.UUID)
	private java.lang.String id;
	/**家长姓名*/
	@Excel(name = "家长姓名", width = 15)
	private java.lang.String ptName;
	/**家长手机号*/
	@Excel(name = "家长手机号", width = 15)
	private java.lang.String ptPhone;
	/**家长头像*/
	@Excel(name = "家长头像", width = 15)
	private java.lang.String ptAvater;
	/**登录密码*/
	@Excel(name = "登录密码", width = 15)
	private java.lang.String ptPassword;
	/**冻结状态 Y.冻结,N.未冻结,默认N*/
	@Excel(name = "冻结状态 1.正常 2.冻结 ,默认1", width = 15)
	@Dict(dicCode = "user_status")
	private java.lang.String ptFrozen;
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
