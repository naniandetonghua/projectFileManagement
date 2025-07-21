package com.projectdelivery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_deliverable_type")
public class DeliverableType {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String typeName;

    private String typeCode;

    private Integer sortOrder;

    private Integer isRequired;

    private Integer applicableProjectType;

    private String description;

    private LocalDateTime createTime;
} 