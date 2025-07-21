package com.projectdelivery.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectDTO {
    
    private Long id;
    
    @NotNull(message = "系统ID不能为空")
    private Long systemId;
    
    @NotBlank(message = "项目名称不能为空")
    private String name;
    
    @NotBlank(message = "项目编码不能为空")
    private String code;
    
    @NotNull(message = "项目类型不能为空")
    private Integer type;
    
    private String description;
    
    @NotBlank(message = "项目经理不能为空")
    private String manager;
    
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @NotNull(message = "项目状态不能为空")
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    // 系统信息
    private String systemName;
    private String systemCode;
} 