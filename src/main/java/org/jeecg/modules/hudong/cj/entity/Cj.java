package org.jeecg.modules.hudong.cj.entity;

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
 * @Description: 成绩管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Data
@TableName("hd_cj")
public class Cj implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**成绩ID*/
	@Excel(name = "成绩ID", width = 32)
	@TableId(type = IdType.UUID)
	private java.lang.String id;
	/**孩子ID*/
	@Excel(name = "孩子ID", width = 15)
	private java.lang.String cjCdId;
	/**学科ID*/
	@Excel(name = "学科ID", width = 15)
	private java.lang.String cjXkId;
	/**父母ID*/
	@Excel(name = "父母ID", width = 15)
	private java.lang.String cjParentId;
	/**学科名称*/
	@Excel(name = "学科名称", width = 15)
	private java.lang.String xkName;
	/**学科成绩*/
	@Excel(name = "学科成绩", width = 15)
	private java.lang.String cjNumber;
	/**成绩时间*/
	@Excel(name = "成绩时间", width = 15)
	private java.lang.String cjTime;
	/**孩子姓名*/
	@Excel(name = "孩子姓名", width = 15)
	private java.lang.String cjCdName;
	/**孩子手机号*/
	@Excel(name = "孩子手机号", width = 15)
	private java.lang.String cjPhone;
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
