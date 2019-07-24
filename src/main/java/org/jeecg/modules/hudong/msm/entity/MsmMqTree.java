package org.jeecg.modules.hudong.msm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

public class MsmMqTree implements Serializable {

    /**分类id*/
    private java.lang.String flId;
    /**描述ID*/
    private java.lang.String id;
    /**描述名称*/
    private java.lang.String msName;
    /**描述归属编号*/
    private java.lang.String msBNo;
    /**创建人*/
    private java.lang.String createBy;
    /**创建时间*/
    private java.util.Date createTime;
    /**更新人*/
    private java.lang.String updateBy;
    /**更新时间*/
    private java.util.Date updateTime;
    /**详情ID*/
    private java.lang.String xqid;
    /**描述ID*/
    private java.lang.String msId;
    /**科目*/
    private java.lang.String mqKemu;
    /**详情内容*/
    private java.lang.String mqContent;


}
