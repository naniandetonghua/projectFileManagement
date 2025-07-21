package com.projectdelivery.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class DeliverableDTO {
    
    private Long id;
    
    @NotBlank(message = "交付物名称不能为空")
    private String name;
    
    private String description;
    
    @NotNull(message = "所属项目不能为空")
    private Long projectId;
    
    private String projectName;
    
    @NotBlank(message = "交付物类型不能为空")
    private String type;
    
    @NotBlank(message = "状态不能为空")
    private String status;
    
    private String filePath;
    
    private String fileName;
    
    private Long fileSize;
    
    private String uploader;
    
    private LocalDateTime uploadTime;
    
    private LocalDateTime updateTime;
} 