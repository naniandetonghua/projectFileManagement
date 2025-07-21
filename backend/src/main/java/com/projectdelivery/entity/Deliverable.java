package com.projectdelivery.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_deliverable")
public class Deliverable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Long projectId;

    private String type;

    private String status;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String uploader;

    private LocalDateTime uploadTime;

    private LocalDateTime updateTime;
} 