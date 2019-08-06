package org.jeecg.modules.hudong.child.entity;

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
 * @Description: 儿童管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Data
@TableName("hd_child")
public class Child implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**学生ID*/
	@Excel(name = "学生ID", width = 32)
	@TableId(type = IdType.UUID)
	private java.lang.String id;
	/**家长ID*/
	@Excel(name = "家长ID", width = 15)
	private java.lang.String ptId;
	/**模版描述ID* */
	@Excel(name = "模版描述ID", width = 15)
	private java.lang.String flId;
	/**模版描述内容,包括分类和描述* */
	@Excel(name = "模版描述msName", width = 15)
	private java.lang.String flName;
	/**孩子姓名*/
	@Excel(name = "孩子姓名", width = 15)
	private java.lang.String cdName;
	/**孩子生日*/
	@Excel(name = "孩子生日", width = 15)
	private java.lang.String cdBirthday;
	/**孩子性别 0/男,1/女*/
	@Excel(name = "孩子性别 1/男,2/女", width = 15)
	@Dict(dicCode = "sex")
	private java.lang.String cdSex;
	/**孩子手机号*/
	@Excel(name = "孩子手机号", width = 15)
	private java.lang.String cdPhone;
	/**孩子头像*/
	@Excel(name = "孩子头像", width = 15)
	private java.lang.String childAvater;
	/**登录密码*/
	@Excel(name = "登录密码", width = 15)
	private java.lang.String cdPassword;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
	private java.lang.String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	private java.util.Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
	private java.lang.String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
}
