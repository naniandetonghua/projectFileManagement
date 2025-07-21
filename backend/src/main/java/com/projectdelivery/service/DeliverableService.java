package com.projectdelivery.service;

import com.projectdelivery.dto.DeliverableDTO;
import com.projectdelivery.entity.Deliverable;
import com.projectdelivery.entity.Project;
import com.projectdelivery.repository.DeliverableRepository;
import com.projectdelivery.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class DeliverableService {

    private static final Logger logger = LoggerFactory.getLogger(DeliverableService.class);

    @Autowired
    private DeliverableRepository deliverableRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    /**
     * 分页查询交付物
     */
    public Page<DeliverableDTO> findDeliverables(String name, Long projectId, String type, String status, Pageable pageable) {
        logger.debug("开始查询交付物 - 名称: {}, 项目ID: {}, 类型: {}, 状态: {}, 页码: {}, 大小: {}", 
                    name, projectId, type, status, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Deliverable> deliverablePage;
        
        if (name != null && !name.trim().isEmpty() && projectId != null && type != null && !type.trim().isEmpty() && status != null && !status.trim().isEmpty()) {
            logger.debug("使用名称、项目ID、类型和状态查询");
            deliverablePage = deliverableRepository.findByNameContainingAndProjectIdAndTypeAndStatus(name.trim(), projectId, type, status, pageable);
        } else if (name != null && !name.trim().isEmpty() && projectId != null && type != null && !type.trim().isEmpty()) {
            logger.debug("使用名称、项目ID和类型查询");
            deliverablePage = deliverableRepository.findByNameContainingAndProjectIdAndType(name.trim(), projectId, type, pageable);
        } else if (name != null && !name.trim().isEmpty() && projectId != null && status != null && !status.trim().isEmpty()) {
            logger.debug("使用名称、项目ID和状态查询");
            deliverablePage = deliverableRepository.findByNameContainingAndProjectIdAndStatus(name.trim(), projectId, status, pageable);
        } else if (name != null && !name.trim().isEmpty() && projectId != null) {
            logger.debug("使用名称和项目ID查询");
            deliverablePage = deliverableRepository.findByNameContainingAndProjectId(name.trim(), projectId, pageable);
        } else if (name != null && !name.trim().isEmpty() && type != null && !type.trim().isEmpty()) {
            logger.debug("使用名称和类型查询");
            deliverablePage = deliverableRepository.findByNameContainingAndType(name.trim(), type, pageable);
        } else if (name != null && !name.trim().isEmpty() && status != null && !status.trim().isEmpty()) {
            logger.debug("使用名称和状态查询");
            deliverablePage = deliverableRepository.findByNameContainingAndStatus(name.trim(), status, pageable);
        } else if (name != null && !name.trim().isEmpty()) {
            logger.debug("使用名称查询");
            deliverablePage = deliverableRepository.findByNameContaining(name.trim(), pageable);
        } else if (projectId != null && type != null && !type.trim().isEmpty()) {
            logger.debug("使用项目ID和类型查询");
            deliverablePage = deliverableRepository.findByProjectIdAndType(projectId, type, pageable);
        } else if (projectId != null && status != null && !status.trim().isEmpty()) {
            logger.debug("使用项目ID和状态查询");
            deliverablePage = deliverableRepository.findByProjectIdAndStatus(projectId, status, pageable);
        } else if (projectId != null) {
            logger.debug("使用项目ID查询");
            deliverablePage = deliverableRepository.findByProjectId(projectId, pageable);
        } else if (type != null && !type.trim().isEmpty() && status != null && !status.trim().isEmpty()) {
            logger.debug("使用类型和状态查询");
            deliverablePage = deliverableRepository.findByTypeAndStatus(type, status, pageable);
        } else if (type != null && !type.trim().isEmpty()) {
            logger.debug("使用类型查询");
            deliverablePage = deliverableRepository.findByType(type, pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            logger.debug("使用状态查询");
            deliverablePage = deliverableRepository.findByStatus(status, pageable);
        } else {
            logger.debug("查询所有交付物");
            deliverablePage = deliverableRepository.findAll(pageable);
        }
        
        logger.info("交付物查询完成 - 总数: {}, 当前页: {}, 每页大小: {}", 
                   deliverablePage.getTotalElements(), deliverablePage.getNumber(), deliverablePage.getSize());
        
        return deliverablePage.map(this::convertToDTO);
    }

    /**
     * 根据ID查找交付物
     */
    public DeliverableDTO findById(Long id) {
        logger.debug("开始根据ID查询交付物 - ID: {}", id);
        
        Deliverable deliverable = deliverableRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("交付物不存在 - ID: {}", id);
                    return new EntityNotFoundException("交付物不存在，ID: " + id);
                });
        
        logger.info("交付物查询成功 - ID: {}, 名称: {}", id, deliverable.getName());
        return convertToDTO(deliverable);
    }

    /**
     * 创建交付物
     */
    public DeliverableDTO createDeliverable(DeliverableDTO deliverableDTO) {
        logger.info("开始创建交付物 - 名称: {}, 项目ID: {}, 类型: {}", 
                   deliverableDTO.getName(), deliverableDTO.getProjectId(), deliverableDTO.getType());
        
        // 验证项目是否存在
        Project project = projectRepository.findById(deliverableDTO.getProjectId())
                .orElseThrow(() -> {
                    logger.error("项目不存在，无法创建交付物 - 项目ID: {}", deliverableDTO.getProjectId());
                    return new EntityNotFoundException("项目不存在，ID: " + deliverableDTO.getProjectId());
                });
        
        logger.debug("项目验证通过 - 项目名称: {}", project.getName());
        
        Deliverable deliverable = convertToEntity(deliverableDTO);
        deliverable.setProjectId(deliverableDTO.getProjectId());
        deliverable.setUploadTime(LocalDateTime.now());
        deliverable.setUpdateTime(LocalDateTime.now());
        
        Deliverable savedDeliverable = deliverableRepository.save(deliverable);
        logger.info("交付物创建成功 - ID: {}, 名称: {}, 项目: {}", 
                   savedDeliverable.getId(), savedDeliverable.getName(), project.getName());
        
        return convertToDTO(savedDeliverable);
    }

    /**
     * 更新交付物
     */
    public DeliverableDTO updateDeliverable(DeliverableDTO deliverableDTO) {
        logger.info("开始更新交付物 - ID: {}, 名称: {}", deliverableDTO.getId(), deliverableDTO.getName());
        
        Deliverable existingDeliverable = deliverableRepository.findById(deliverableDTO.getId())
                .orElseThrow(() -> {
                    logger.error("交付物不存在，无法更新 - ID: {}", deliverableDTO.getId());
                    return new EntityNotFoundException("交付物不存在，ID: " + deliverableDTO.getId());
                });
        
        // 更新字段
        existingDeliverable.setName(deliverableDTO.getName());
        existingDeliverable.setDescription(deliverableDTO.getDescription());
        existingDeliverable.setType(deliverableDTO.getType());
        existingDeliverable.setStatus(deliverableDTO.getStatus());
        existingDeliverable.setUpdateTime(LocalDateTime.now());
        
        Deliverable updatedDeliverable = deliverableRepository.save(existingDeliverable);
        logger.info("交付物更新成功 - ID: {}, 名称: {}, 状态: {}", 
                   updatedDeliverable.getId(), updatedDeliverable.getName(), updatedDeliverable.getStatus());
        
        return convertToDTO(updatedDeliverable);
    }

    /**
     * 删除交付物
     */
    public void deleteDeliverable(Long id) {
        logger.info("开始删除交付物 - ID: {}", id);
        
        Deliverable deliverable = deliverableRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("交付物不存在，无法删除 - ID: {}", id);
                    return new EntityNotFoundException("交付物不存在，ID: " + id);
                });
        
        // 删除文件
        if (deliverable.getFilePath() != null && !deliverable.getFilePath().isEmpty()) {
            try {
                Path filePath = Paths.get(uploadDir).resolve(deliverable.getFilePath());
                Files.deleteIfExists(filePath);
                logger.debug("文件删除成功 - 路径: {}", filePath);
            } catch (IOException e) {
                logger.warn("文件删除失败 - 路径: {}, 错误: {}", deliverable.getFilePath(), e.getMessage());
            }
        }
        
        deliverableRepository.deleteById(id);
        logger.info("交付物删除成功 - ID: {}, 名称: {}", id, deliverable.getName());
    }

    /**
     * 文件上传
     */
    public String uploadFile(MultipartFile file) throws IOException {
        logger.info("开始文件上传 - 文件名: {}, 大小: {} bytes", 
                   file.getOriginalFilename(), file.getSize());
        
        if (file.isEmpty()) {
            logger.error("文件上传失败 - 文件为空");
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            logger.debug("创建上传目录 - 路径: {}", uploadPath);
        }
        
        // 生成唯一文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + fileExtension;
        
        // 保存文件
        Path targetPath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        logger.info("文件上传成功 - 原始文件名: {}, 保存文件名: {}, 路径: {}", 
                   originalFilename, filename, targetPath);
        
        return filename;
    }

    /**
     * 文件下载
     */
    public Resource downloadFile(Long id) throws MalformedURLException {
        logger.debug("开始文件下载 - 交付物ID: {}", id);
        
        Deliverable deliverable = deliverableRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("交付物不存在，无法下载文件 - ID: {}", id);
                    return new EntityNotFoundException("交付物不存在，ID: " + id);
                });
        
        if (deliverable.getFilePath() == null || deliverable.getFilePath().isEmpty()) {
            logger.error("文件路径为空，无法下载 - 交付物ID: {}", id);
            throw new EntityNotFoundException("文件不存在");
        }
        
        Path filePath = Paths.get(uploadDir).resolve(deliverable.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists() && resource.isReadable()) {
            logger.info("文件下载成功 - 交付物ID: {}, 文件名: {}, 路径: {}", 
                       id, deliverable.getName(), filePath);
            return resource;
        } else {
            logger.error("文件不存在或无法读取 - 交付物ID: {}, 路径: {}", id, filePath);
            throw new EntityNotFoundException("文件不存在或无法读取");
        }
    }

    /**
     * 获取交付物统计信息
     */
    public Map<String, Object> getDeliverableStats() {
        logger.debug("开始获取交付物统计信息");
        
        Map<String, Object> stats = new HashMap<>();
        
        long totalDeliverables = deliverableRepository.count();
        long draftDeliverables = deliverableRepository.countByStatus("DRAFT");
        long reviewDeliverables = deliverableRepository.countByStatus("REVIEW");
        long approvedDeliverables = deliverableRepository.countByStatus("APPROVED");
        long rejectedDeliverables = deliverableRepository.countByStatus("REJECTED");
        
        stats.put("totalDeliverables", totalDeliverables);
        stats.put("draftDeliverables", draftDeliverables);
        stats.put("reviewDeliverables", reviewDeliverables);
        stats.put("approvedDeliverables", approvedDeliverables);
        stats.put("rejectedDeliverables", rejectedDeliverables);
        
        logger.info("交付物统计信息获取完成 - 总数: {}, 草稿: {}, 审核中: {}, 已批准: {}, 已拒绝: {}", 
                   totalDeliverables, draftDeliverables, reviewDeliverables, approvedDeliverables, rejectedDeliverables);
        
        return stats;
    }

    /**
     * 转换为DTO
     */
    private DeliverableDTO convertToDTO(Deliverable deliverable) {
        DeliverableDTO dto = new DeliverableDTO();
        BeanUtils.copyProperties(deliverable, dto);
        // 如果需要项目信息，可以通过projectId查询
        if (deliverable.getProjectId() != null) {
            try {
                Project project = projectRepository.findById(deliverable.getProjectId()).orElse(null);
                if (project != null) {
                    dto.setProjectName(project.getName());
                }
            } catch (Exception e) {
                logger.warn("获取项目信息失败 - 项目ID: {}, 错误: {}", deliverable.getProjectId(), e.getMessage());
            }
        }
        return dto;
    }

    /**
     * 转换为实体
     */
    private Deliverable convertToEntity(DeliverableDTO dto) {
        Deliverable deliverable = new Deliverable();
        BeanUtils.copyProperties(dto, deliverable);
        return deliverable;
    }
} 