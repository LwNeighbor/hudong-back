package org.jeecg.modules.hudong.kc.entity;

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
 * @Description: 课程表管理
 * @author： jeecg-boot
 * @date：   2019-07-31
 * @version： V1.0
 */
@Data
@TableName("hd_kc")
public class Kc implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**课程表ID*/
	@TableId(type = IdType.UUID)
	private String id;
	/**孩子ID*/
	private String chId;
	/**孩子姓名*/
	@Excel(name = "孩子姓名", width = 15)
	private String name;
	/**科目名称*/
	@Excel(name = "科目名称", width = 15)
	private String kmName;
	/**上课时间*/
	@Excel(name = "上课时间", width = 15,format = "yyyy-MM-dd")
	private String startTime;
	/**创建人*/
	private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**更新人*/
	private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
}
