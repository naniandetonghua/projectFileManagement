package com.projectdelivery.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.projectdelivery.entity.System;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SystemRepository extends BaseMapper<System> {
} 