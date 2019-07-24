package org.jeecg.modules.hudong.msm.entity;

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
 * @Description: 模式描述
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@Data
@TableName("hd_ms_miaoshu")
public class Msm implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**分类id*/
	@Excel(name = "分类id", width = 32)
	private java.lang.String flId;
	/**描述ID*/
	@Excel(name = "描述ID", width = 15)
	@TableId(type = IdType.UUID)
	private java.lang.String id;
	/**描述名称*/
	@Excel(name = "描述名称", width = 15)
	private java.lang.String msName;
	/**描述归属编号*/
	@Excel(name = "描述归属编号", width = 15)
	private java.lang.String msBNo;
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
