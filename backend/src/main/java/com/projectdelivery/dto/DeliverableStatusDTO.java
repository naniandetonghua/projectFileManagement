package com.projectdelivery.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliverableStatusDTO {
    private Long deliverableTypeId;
    private String typeName;
    private String typeCode;
    private Boolean required;
    private Integer sortOrder;
    private Boolean uploaded;
    private String fileName;
    private LocalDateTime uploadTime;
    private String uploadUser;
    private Long deliverableId;
} 