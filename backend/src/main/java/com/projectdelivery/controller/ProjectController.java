package com.projectdelivery.controller;

import com.projectdelivery.dto.ApiResponse;
import com.projectdelivery.dto.ProjectDTO;
import com.projectdelivery.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@CrossOrigin(origins = "*")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    @GetMapping("/list")
    public ApiResponse<Page<ProjectDTO>> getProjectList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long systemId) {
        
        logger.info("收到项目列表查询请求 - 页码: {}, 大小: {}, 名称: {}, 状态: {}, 系统ID: {}", 
                   page, size, name, status, systemId);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ProjectDTO> projects = projectService.findProjects(name, status, systemId, pageable);
            
            logger.info("项目列表查询成功 - 总数: {}, 当前页: {}, 每页大小: {}", 
                       projects.getTotalElements(), projects.getNumber(), projects.getSize());
            
        return ApiResponse.success(projects);
        } catch (Exception e) {
            logger.error("项目列表查询失败 - 错误: {}", e.getMessage(), e);
            return ApiResponse.error("项目列表查询失败: " + e.getMessage());
    }
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectDTO> getProjectById(@PathVariable Long id) {
        logger.info("收到项目详情查询请求 - ID: {}", id);
        
        try {
            ProjectDTO project = projectService.findById(id);
            logger.info("项目详情查询成功 - ID: {}, 名称: {}", id, project.getName());
        return ApiResponse.success(project);
        } catch (Exception e) {
            logger.error("项目详情查询失败 - ID: {}, 错误: {}", id, e.getMessage(), e);
            return ApiResponse.error("项目详情查询失败: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ApiResponse<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        logger.info("收到项目创建请求 - 名称: {}, 编码: {}, 系统ID: {}", 
                   projectDTO.getName(), projectDTO.getCode(), projectDTO.getSystemId());
        
        try {
            ProjectDTO createdProject = projectService.createProject(projectDTO);
            logger.info("项目创建成功 - ID: {}, 名称: {}", createdProject.getId(), createdProject.getName());
        return ApiResponse.success("项目创建成功", createdProject);
        } catch (Exception e) {
            logger.error("项目创建失败 - 名称: {}, 错误: {}", projectDTO.getName(), e.getMessage(), e);
            return ApiResponse.error("项目创建失败: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ApiResponse<ProjectDTO> updateProject(@RequestBody ProjectDTO projectDTO) {
        logger.info("收到项目更新请求 - ID: {}, 名称: {}", projectDTO.getId(), projectDTO.getName());
        
        try {
            ProjectDTO updatedProject = projectService.updateProject(projectDTO);
            logger.info("项目更新成功 - ID: {}, 名称: {}", updatedProject.getId(), updatedProject.getName());
        return ApiResponse.success("项目更新成功", updatedProject);
        } catch (Exception e) {
            logger.error("项目更新失败 - ID: {}, 错误: {}", projectDTO.getId(), e.getMessage(), e);
            return ApiResponse.error("项目更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteProject(@PathVariable Long id) {
        logger.info("收到项目删除请求 - ID: {}", id);
        
        try {
        projectService.deleteProject(id);
            logger.info("项目删除成功 - ID: {}", id);
        return ApiResponse.success("项目删除成功", null);
        } catch (Exception e) {
            logger.error("项目删除失败 - ID: {}, 错误: {}", id, e.getMessage(), e);
            return ApiResponse.error("项目删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ApiResponse<Object> getProjectStats() {
        logger.info("收到项目统计信息请求");
        
        try {
            Object stats = projectService.getProjectStats();
            logger.info("项目统计信息获取成功");
            return ApiResponse.success(stats);
        } catch (Exception e) {
            logger.error("项目统计信息获取失败 - 错误: {}", e.getMessage(), e);
            return ApiResponse.error("项目统计信息获取失败: " + e.getMessage());
        }
    }
} 