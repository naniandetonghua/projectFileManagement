package com.projectdelivery.service;

import com.projectdelivery.dto.ProjectDTO;
import com.projectdelivery.entity.Project;
import com.projectdelivery.entity.System;
import com.projectdelivery.repository.ProjectRepository;
import com.projectdelivery.repository.SystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private SystemRepository systemRepository;

    /**
     * 分页查询项目
     */
    public Page<ProjectDTO> findProjects(String name, Integer status, Long systemId, Pageable pageable) {
        logger.debug("开始查询项目 - 名称: {}, 状态: {}, 系统ID: {}, 页码: {}, 大小: {}", 
                    name, status, systemId, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Project> projectPage;
        
        if (name != null && !name.trim().isEmpty() && status != null && systemId != null) {
            logger.debug("使用名称、状态和系统ID查询");
            projectPage = projectRepository.findByNameContainingAndStatusAndSystemId(name.trim(), status, systemId, pageable);
        } else if (name != null && !name.trim().isEmpty() && status != null) {
            logger.debug("使用名称和状态查询");
            projectPage = projectRepository.findByNameContainingAndStatus(name.trim(), status, pageable);
        } else if (name != null && !name.trim().isEmpty() && systemId != null) {
            logger.debug("使用名称和系统ID查询");
            projectPage = projectRepository.findByNameContainingAndSystemId(name.trim(), systemId, pageable);
        } else if (status != null && systemId != null) {
            logger.debug("使用状态和系统ID查询");
            projectPage = projectRepository.findByStatusAndSystemId(status, systemId, pageable);
        } else if (name != null && !name.trim().isEmpty()) {
            logger.debug("使用名称查询");
            projectPage = projectRepository.findByNameContaining(name.trim(), pageable);
        } else if (status != null) {
            logger.debug("使用状态查询");
            projectPage = projectRepository.findByStatus(status, pageable);
        } else if (systemId != null) {
            logger.debug("使用系统ID查询");
            projectPage = projectRepository.findBySystemId(systemId, pageable);
        } else {
            logger.debug("查询所有项目");
            projectPage = projectRepository.findAll(pageable);
        }
        
        logger.info("项目查询完成 - 总数: {}, 当前页: {}, 每页大小: {}", 
                   projectPage.getTotalElements(), projectPage.getNumber(), projectPage.getSize());
        
        return projectPage.map(this::convertToDTO);
    }

    /**
     * 根据ID查找项目
     */
    public ProjectDTO findById(Long id) {
        logger.debug("开始根据ID查询项目 - ID: {}", id);
        
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("项目不存在 - ID: {}", id);
                    return new EntityNotFoundException("项目不存在，ID: " + id);
                });
        
        logger.info("项目查询成功 - ID: {}, 名称: {}", id, project.getName());
        return convertToDTO(project);
    }

    /**
     * 创建项目
     */
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        logger.info("开始创建项目 - 名称: {}, 编码: {}, 系统ID: {}", 
                   projectDTO.getName(), projectDTO.getCode(), projectDTO.getSystemId());
        
        // 验证系统是否存在
        System system = systemRepository.selectById(projectDTO.getSystemId());
        if (system == null) {
            logger.error("系统不存在，无法创建项目 - 系统ID: {}", projectDTO.getSystemId());
            throw new EntityNotFoundException("系统不存在，ID: " + projectDTO.getSystemId());
        }
        
        logger.debug("系统验证通过 - 系统名称: {}", system.getSystemName());
        
        Project project = convertToEntity(projectDTO);
        project.setCreateTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());
        
        Project savedProject = projectRepository.save(project);
        logger.info("项目创建成功 - ID: {}, 名称: {}, 编码: {}", 
                   savedProject.getId(), savedProject.getName(), savedProject.getCode());
        
        return convertToDTO(savedProject);
    }

    /**
     * 更新项目
     */
    public ProjectDTO updateProject(ProjectDTO projectDTO) {
        logger.info("开始更新项目 - ID: {}, 名称: {}", projectDTO.getId(), projectDTO.getName());
        
        Project existingProject = projectRepository.findById(projectDTO.getId())
                .orElseThrow(() -> {
                    logger.error("项目不存在，无法更新 - ID: {}", projectDTO.getId());
                    return new EntityNotFoundException("项目不存在，ID: " + projectDTO.getId());
                });
        
        // 验证系统是否存在
        if (projectDTO.getSystemId() != null) {
            System system = systemRepository.selectById(projectDTO.getSystemId());
            if (system == null) {
                logger.error("系统不存在，无法更新项目 - 系统ID: {}", projectDTO.getSystemId());
                throw new EntityNotFoundException("系统不存在，ID: " + projectDTO.getSystemId());
            }
            logger.debug("系统验证通过 - 系统名称: {}", system.getSystemName());
        }
        
        // 更新字段
        existingProject.setSystemId(projectDTO.getSystemId());
        existingProject.setName(projectDTO.getName());
        existingProject.setCode(projectDTO.getCode());
        existingProject.setType(projectDTO.getType());
        existingProject.setDescription(projectDTO.getDescription());
        existingProject.setManager(projectDTO.getManager());
        existingProject.setStartDate(projectDTO.getStartDate());
        existingProject.setEndDate(projectDTO.getEndDate());
        existingProject.setStatus(projectDTO.getStatus());
        existingProject.setUpdateTime(LocalDateTime.now());
        
        Project updatedProject = projectRepository.save(existingProject);
        logger.info("项目更新成功 - ID: {}, 名称: {}, 状态: {}", 
                   updatedProject.getId(), updatedProject.getName(), updatedProject.getStatus());
        
        return convertToDTO(updatedProject);
    }

    /**
     * 删除项目
     */
    public void deleteProject(Long id) {
        logger.info("开始删除项目 - ID: {}", id);
        
        if (!projectRepository.existsById(id)) {
            logger.error("项目不存在，无法删除 - ID: {}", id);
            throw new EntityNotFoundException("项目不存在，ID: " + id);
        }
        
        projectRepository.deleteById(id);
        logger.info("项目删除成功 - ID: {}", id);
    }

    /**
     * 获取项目统计信息
     */
    public Map<String, Object> getProjectStats() {
        logger.debug("开始获取项目统计信息");
        
        Map<String, Object> stats = new HashMap<>();
        
        long totalProjects = projectRepository.count();
        long inProgressProjects = projectRepository.countByStatus(1);
        long completedProjects = projectRepository.countByStatus(2);
        long pausedProjects = projectRepository.countByStatus(3);
        long cancelledProjects = projectRepository.countByStatus(4);
        
        stats.put("totalProjects", totalProjects);
        stats.put("inProgressProjects", inProgressProjects);
        stats.put("completedProjects", completedProjects);
        stats.put("pausedProjects", pausedProjects);
        stats.put("cancelledProjects", cancelledProjects);
        
        logger.info("项目统计信息获取完成 - 总数: {}, 进行中: {}, 已完成: {}, 暂停: {}, 取消: {}", 
                   totalProjects, inProgressProjects, completedProjects, pausedProjects, cancelledProjects);
        
        return stats;
    }

    /**
     * 转换为DTO
     */
    private ProjectDTO convertToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        BeanUtils.copyProperties(project, dto);
        
        // 设置系统信息
        if (project.getSystem() != null) {
            dto.setSystemName(project.getSystem().getSystemName());
            dto.setSystemCode(project.getSystem().getSystemCode());
        }
        
        return dto;
    }

    /**
     * 转换为实体
     */
    private Project convertToEntity(ProjectDTO dto) {
        Project project = new Project();
        BeanUtils.copyProperties(dto, project);
        return project;
    }
} 