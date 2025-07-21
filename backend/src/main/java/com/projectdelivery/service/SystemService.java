package com.projectdelivery.service;

import com.projectdelivery.entity.System;
import com.projectdelivery.repository.SystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SystemService {

    private static final Logger logger = LoggerFactory.getLogger(SystemService.class);

    @Autowired
    private SystemRepository systemRepository;

    /**
     * 获取所有系统
     */
    public List<System> getAllSystems() {
        logger.debug("开始获取所有系统");
        List<System> systems = systemRepository.selectList(null);
        logger.info("系统查询完成 - 总数: {}", systems.size());
        return systems;
    }

    /**
     * 根据ID获取系统
     */
    public System getSystemById(Long id) {
        logger.debug("开始根据ID查询系统 - ID: {}", id);
        System system = systemRepository.selectById(id);
        if (system == null) {
            logger.error("系统不存在 - ID: {}", id);
            throw new EntityNotFoundException("系统不存在，ID: " + id);
        }
        logger.info("系统查询成功 - ID: {}, 名称: {}", id, system.getSystemName());
        return system;
    }

    /**
     * 创建系统
     */
    public System createSystem(System system) {
        logger.info("开始创建系统 - 名称: {}, 编码: {}", system.getSystemName(), system.getSystemCode());
        
        system.setCreateTime(LocalDateTime.now());
        system.setUpdateTime(LocalDateTime.now());
        
        int result = systemRepository.insert(system);
        if (result > 0) {
            logger.info("系统创建成功 - ID: {}, 名称: {}, 编码: {}", 
                       system.getId(), system.getSystemName(), system.getSystemCode());
        return system;
        } else {
            logger.error("系统创建失败 - 名称: {}", system.getSystemName());
            throw new RuntimeException("系统创建失败");
        }
    }

    /**
     * 更新系统
     */
    public System updateSystem(System system) {
        logger.info("开始更新系统 - ID: {}, 名称: {}", system.getId(), system.getSystemName());
        
        System existingSystem = systemRepository.selectById(system.getId());
        if (existingSystem == null) {
            logger.error("系统不存在，无法更新 - ID: {}", system.getId());
            throw new EntityNotFoundException("系统不存在，ID: " + system.getId());
        }
        
        system.setUpdateTime(LocalDateTime.now());
        
        int result = systemRepository.updateById(system);
        if (result > 0) {
            logger.info("系统更新成功 - ID: {}, 名称: {}, 状态: {}", 
                       system.getId(), system.getSystemName(), system.getStatus());
        return system;
        } else {
            logger.error("系统更新失败 - ID: {}", system.getId());
            throw new RuntimeException("系统更新失败");
        }
    }

    /**
     * 删除系统
     */
    public void deleteSystem(Long id) {
        logger.info("开始删除系统 - ID: {}", id);
        
        System system = systemRepository.selectById(id);
        if (system == null) {
            logger.error("系统不存在，无法删除 - ID: {}", id);
            throw new EntityNotFoundException("系统不存在，ID: " + id);
        }
        
        int result = systemRepository.deleteById(id);
        if (result > 0) {
            logger.info("系统删除成功 - ID: {}, 名称: {}", id, system.getSystemName());
        } else {
            logger.error("系统删除失败 - ID: {}", id);
            throw new RuntimeException("系统删除失败");
        }
    }

    /**
     * 根据状态获取系统列表
     */
    public List<System> getSystemsByStatus(Integer status) {
        logger.debug("开始根据状态查询系统 - 状态: {}", status);
        
        // 使用MyBatis Plus的QueryWrapper来查询
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<System> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("status", status);
        List<System> systems = systemRepository.selectList(queryWrapper);
        
        logger.info("系统查询完成 - 状态: {}, 数量: {}", status, systems.size());
        return systems;
    }
} 