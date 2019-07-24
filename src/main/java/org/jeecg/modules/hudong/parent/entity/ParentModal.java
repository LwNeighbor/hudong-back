package org.jeecg.modules.hudong.parent.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ParentModal implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 对应parent中的id字段,前端数据树中的key*/
    private String key;

    /** 对应parent中的id字段,前端数据树中的value*/
    private String value;

    /** 对应pt_name字段,前端数据树中的title*/
    private String title;

    private String id;
    private String ptName;
    private String ptPhone;
    private String ptAvater;
    private String ptPassword;
    private String ptFrozen;

    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;



    /**
     * 将Parent对象转换成ParentModal对象
     * @param parent
     */
    public ParentModal(Parent parent) {
        this.key = parent.getId();
        this.value = parent.getId();
        this.title = parent.getPtName();
        this.id = parent.getId();
        this.ptName = parent.getPtName();
        this.ptPhone = parent.getPtPhone();
        this.ptAvater = parent.getPtAvater();
        this.ptPassword = parent.getPtPassword();
        this.ptFrozen = parent.getPtFrozen();
        this.createBy = parent.getCreateBy();
        this.createTime = parent.getCreateTime();
        this.updateBy = parent.getUpdateBy();
        this.updateTime = parent.getUpdateTime();
    }

}
