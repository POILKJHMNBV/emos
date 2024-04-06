package com.example.emos.db.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户操作日志表
 * @TableName tb_user_operate_log
 */
@TableName(value ="tb_user_operate_log")
@Data
public class TbUserOperateLog implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 5215779585182480173L;
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 操作用户id
     */
    private Integer userId;

    /**
     * 操作描述
     */
    private String operationDescription;

    /**
     * 请求uri
     */
    private String requestUri;

    /**
     * 请求类型
     */
    private String requestMethod;

    /**
     * 请求ip
     */
    private String requestIp;

    /**
     * 请求参数
     */
    private Object requestParameter;

    /**
     * 响应状态
     */
    private Integer responseStatus;

    /**
     * 耗时 单位：ms
     */
    private Integer costTime;

    /**
     * 错误原因
     */
    private String errorReason;

    /**
     * 创建时间
     */
    private Date createTime;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TbUserOperateLog other = (TbUserOperateLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getOperationDescription() == null ? other.getOperationDescription() == null : this.getOperationDescription().equals(other.getOperationDescription()))
            && (this.getRequestUri() == null ? other.getRequestUri() == null : this.getRequestUri().equals(other.getRequestUri()))
            && (this.getRequestMethod() == null ? other.getRequestMethod() == null : this.getRequestMethod().equals(other.getRequestMethod()))
            && (this.getRequestIp() == null ? other.getRequestIp() == null : this.getRequestIp().equals(other.getRequestIp()))
            && (this.getRequestParameter() == null ? other.getRequestParameter() == null : this.getRequestParameter().equals(other.getRequestParameter()))
            && (this.getResponseStatus() == null ? other.getResponseStatus() == null : this.getResponseStatus().equals(other.getResponseStatus()))
            && (this.getCostTime() == null ? other.getCostTime() == null : this.getCostTime().equals(other.getCostTime()))
            && (this.getErrorReason() == null ? other.getErrorReason() == null : this.getErrorReason().equals(other.getErrorReason()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getOperationDescription() == null) ? 0 : getOperationDescription().hashCode());
        result = prime * result + ((getRequestUri() == null) ? 0 : getRequestUri().hashCode());
        result = prime * result + ((getRequestMethod() == null) ? 0 : getRequestMethod().hashCode());
        result = prime * result + ((getRequestIp() == null) ? 0 : getRequestIp().hashCode());
        result = prime * result + ((getRequestParameter() == null) ? 0 : getRequestParameter().hashCode());
        result = prime * result + ((getResponseStatus() == null) ? 0 : getResponseStatus().hashCode());
        result = prime * result + ((getCostTime() == null) ? 0 : getCostTime().hashCode());
        result = prime * result + ((getErrorReason() == null) ? 0 : getErrorReason().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", operationDescription=").append(operationDescription);
        sb.append(", requestUri=").append(requestUri);
        sb.append(", requestMethod=").append(requestMethod);
        sb.append(", requestIp=").append(requestIp);
        sb.append(", requestParameter=").append(requestParameter);
        sb.append(", responseStatus=").append(responseStatus);
        sb.append(", costTime=").append(costTime);
        sb.append(", errorReason=").append(errorReason);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}