package com.projectdelivery.repository;

import com.projectdelivery.entity.Deliverable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliverableRepository extends JpaRepository<Deliverable, Long> {

    /**
     * 根据名称模糊查询
     */
    Page<Deliverable> findByNameContaining(String name, Pageable pageable);

    /**
     * 根据项目ID查询
     */
    Page<Deliverable> findByProjectId(Long projectId, Pageable pageable);

    /**
     * 根据类型查询
     */
    Page<Deliverable> findByType(String type, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<Deliverable> findByStatus(String status, Pageable pageable);

    /**
     * 根据名称和项目ID查询
     */
    Page<Deliverable> findByNameContainingAndProjectId(String name, Long projectId, Pageable pageable);

    /**
     * 根据名称和类型查询
     */
    Page<Deliverable> findByNameContainingAndType(String name, String type, Pageable pageable);

    /**
     * 根据名称和状态查询
     */
    Page<Deliverable> findByNameContainingAndStatus(String name, String status, Pageable pageable);

    /**
     * 根据项目ID和类型查询
     */
    Page<Deliverable> findByProjectIdAndType(Long projectId, String type, Pageable pageable);

    /**
     * 根据项目ID和状态查询
     */
    Page<Deliverable> findByProjectIdAndStatus(Long projectId, String status, Pageable pageable);

    /**
     * 根据类型和状态查询
     */
    Page<Deliverable> findByTypeAndStatus(String type, String status, Pageable pageable);

    /**
     * 根据名称、项目ID和类型查询
     */
    Page<Deliverable> findByNameContainingAndProjectIdAndType(String name, Long projectId, String type, Pageable pageable);

    /**
     * 根据名称、项目ID和状态查询
     */
    Page<Deliverable> findByNameContainingAndProjectIdAndStatus(String name, Long projectId, String status, Pageable pageable);

    /**
     * 根据名称、项目ID、类型和状态查询
     */
    Page<Deliverable> findByNameContainingAndProjectIdAndTypeAndStatus(String name, Long projectId, String type, String status, Pageable pageable);

    /**
     * 统计各状态的交付物数量
     */
    long countByStatus(String status);

    /**
     * 统计各类型的交付物数量
     */
    long countByType(String type);

    /**
     * 根据项目ID统计交付物数量
     */
    long countByProjectId(Long projectId);
} 