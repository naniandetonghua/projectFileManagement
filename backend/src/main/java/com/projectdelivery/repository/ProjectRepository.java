package com.projectdelivery.repository;

import com.projectdelivery.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * 根据名称模糊查询
     */
    Page<Project> findByNameContaining(String name, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<Project> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据系统ID查询
     */
    Page<Project> findBySystemId(Long systemId, Pageable pageable);

    /**
     * 根据名称和状态查询
     */
    Page<Project> findByNameContainingAndStatus(String name, Integer status, Pageable pageable);

    /**
     * 根据名称和系统ID查询
     */
    Page<Project> findByNameContainingAndSystemId(String name, Long systemId, Pageable pageable);

    /**
     * 根据状态和系统ID查询
     */
    Page<Project> findByStatusAndSystemId(Integer status, Long systemId, Pageable pageable);

    /**
     * 根据名称、状态和系统ID查询
     */
    Page<Project> findByNameContainingAndStatusAndSystemId(String name, Integer status, Long systemId, Pageable pageable);

    /**
     * 统计各状态的项目数量
     */
    long countByStatus(Integer status);

    /**
     * 根据项目经理查询
     */
    Page<Project> findByManager(String manager, Pageable pageable);

    /**
     * 根据名称和项目经理查询
     */
    Page<Project> findByNameContainingAndManager(String name, String manager, Pageable pageable);

    /**
     * 根据项目类型查询
     */
    Page<Project> findByType(Integer type, Pageable pageable);

    /**
     * 根据系统ID和项目类型查询
     */
    Page<Project> findBySystemIdAndType(Long systemId, Integer type, Pageable pageable);
} 