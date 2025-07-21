package com.projectdelivery.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.projectdelivery.entity.DeliverableType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeliverableTypeRepository extends BaseMapper<DeliverableType> {
    
    @Select("SELECT * FROM sys_deliverable_type WHERE applicable_project_type = #{projectType} OR applicable_project_type = 3 ORDER BY sort_order")
    List<DeliverableType> findByApplicableProjectType(Integer projectType);
} 