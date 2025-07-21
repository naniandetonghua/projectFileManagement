-- 项目交付物管理系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS project_delivery_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE project_delivery_system;

-- 1. 系统表
CREATE TABLE sys_system (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    system_name VARCHAR(100) NOT NULL COMMENT '系统名称',
    system_code VARCHAR(50) NOT NULL COMMENT '系统编码',
    description TEXT COMMENT '系统描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_system_code (system_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统表';

-- 2. 项目表
CREATE TABLE sys_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    system_id BIGINT NOT NULL COMMENT '所属系统ID',
    project_name VARCHAR(200) NOT NULL COMMENT '项目名称',
    project_code VARCHAR(100) NOT NULL COMMENT '项目编码',
    project_type TINYINT NOT NULL COMMENT '项目类型：1-重大，2-普通',
    description TEXT COMMENT '项目描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-进行中，2-已完成，3-暂停，4-中止',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (system_id) REFERENCES sys_system(id) ON DELETE CASCADE,
    UNIQUE KEY uk_project_code (project_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- 3. 交付物类型表
CREATE TABLE sys_deliverable_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(100) NOT NULL COMMENT '交付物类型名称',
    type_code VARCHAR(50) NOT NULL COMMENT '交付物类型编码',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_required TINYINT DEFAULT 1 COMMENT '是否必填：1-必填，0-非必填',
    applicable_project_type TINYINT COMMENT '适用项目类型：1-重大，2-普通，3-全部',
    description TEXT COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_type_code (type_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交付物类型表';

-- 4. 交付物表
CREATE TABLE sys_deliverable (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL COMMENT '项目ID',
    deliverable_type_id BIGINT NOT NULL COMMENT '交付物类型ID',
    file_name VARCHAR(200) COMMENT '文件名',
    file_path VARCHAR(500) COMMENT '文件路径',
    file_size BIGINT COMMENT '文件大小(字节)',
    upload_time DATETIME COMMENT '上传时间',
    upload_user VARCHAR(50) COMMENT '上传用户',
    status TINYINT DEFAULT 0 COMMENT '状态：0-未上传，1-已上传',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES sys_project(id) ON DELETE CASCADE,
    FOREIGN KEY (deliverable_type_id) REFERENCES sys_deliverable_type(id),
    UNIQUE KEY uk_project_deliverable (project_id, deliverable_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交付物表';

-- 5. 用户表（基础权限管理）
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始化交付物类型数据
INSERT INTO sys_deliverable_type (type_name, type_code, sort_order, is_required, applicable_project_type, description) VALUES
-- 必填交付物（所有项目）
('业务需求文档', 'BRD', 1, 1, 3, '业务需求文档，描述项目的业务目标和需求'),
('项目立项申请表', 'PIA', 2, 1, 3, '项目立项申请表，申请项目立项'),
('技术可行性分析报告', 'TFR', 3, 1, 3, '技术可行性分析报告，分析技术实现的可行性'),
('立项综合评审报告', 'ICR', 4, 1, 3, '立项综合评审报告，综合评审项目立项'),
('技术需求分析文档', 'TRD', 5, 1, 3, '技术需求分析文档，分析技术需求'),
('技术研发需求要点清单', 'TRRL', 6, 1, 3, '技术研发需求要点清单，列出技术研发要点'),
('技术需求评审报告', 'TRRR', 7, 1, 3, '技术需求评审报告，评审技术需求'),
('概要设计说明书', 'HDS', 8, 1, 3, '概要设计说明书，系统概要设计'),
('详细设计说明书', 'DDS', 9, 1, 3, '详细设计说明书，系统详细设计'),
('数据库设计说明书', 'DBS', 10, 1, 3, '数据库设计说明书，数据库设计'),
('系统设计评审报告', 'SDRR', 11, 1, 3, '系统设计评审报告，评审系统设计'),
('代码评审报告', 'CRR', 12, 1, 3, '代码评审报告，代码质量评审'),
('技术测试方案', 'TTP', 13, 1, 3, '技术测试方案，技术测试计划'),
('技术测试方案评审报告', 'TTPRR', 14, 1, 3, '技术测试方案评审报告，评审技术测试方案'),
('业务测试方案', 'BTP', 15, 1, 3, '业务测试方案，业务测试计划'),
('业务测试方案评审报告', 'BTPRR', 16, 1, 3, '业务测试方案评审报告，评审业务测试方案'),
('需求变更申请及确认表', 'RCACT', 17, 1, 3, '需求变更申请及确认表，需求变更管理'),
('项目验收报告', 'PAR', 18, 1, 3, '项目验收报告，项目验收'),
('项目后评价报告', 'PPER', 22, 1, 3, '项目后评价报告，项目后评价'),
('系统后评价报告', 'SPER', 23, 1, 3, '系统后评价报告，系统后评价'),

-- 重大项目额外交付物
('重大项目立项报告', 'MPIR', 24, 1, 1, '重大项目立项报告，重大项目立项'),
('重大项目季度进展报告', 'MPQR', 25, 1, 1, '重大项目季度进展报告，重大项目季度进展'),
('重大项目后评价报告', 'MPPER', 26, 1, 1, '重大项目后评价报告，重大项目后评价'),

-- 非必填交付物
('项目暂停/中止报告', 'PSR', 19, 0, 3, '项目暂停/中止报告，项目暂停或中止'),
('项目重新启动申请', 'PRRA', 20, 0, 3, '项目重新启动申请，项目重新启动'),
('生产系统终止运行申请', 'PSTRA', 21, 0, 3, '生产系统终止运行申请，生产系统终止运行');

-- 初始化示例系统数据
INSERT INTO sys_system (system_name, system_code, description) VALUES
('新核心系统', 'NEW_CORE', '新一代核心业务系统'),
('客户管理系统', 'CMS', '客户关系管理系统'),
('风险管理系统', 'RMS', '风险管理控制系统');

-- 初始化示例项目数据
INSERT INTO sys_project (system_id, project_name, project_code, project_type, description, start_date, end_date) VALUES
(1, '新核心系统2024年1季度项目', 'NEW_CORE_2024_Q1', 1, '新核心系统2024年第一季度开发项目', '2024-01-01', '2024-03-31'),
(1, '新核心系统2024年2季度项目', 'NEW_CORE_2024_Q2', 1, '新核心系统2024年第二季度开发项目', '2024-04-01', '2024-06-30'),
(2, '客户管理系统升级项目', 'CMS_UPGRADE_2024', 2, '客户管理系统功能升级项目', '2024-01-01', '2024-06-30'),
(3, '风险管理系统优化项目', 'RMS_OPTIMIZE_2024', 2, '风险管理系统性能优化项目', '2024-03-01', '2024-08-31');

-- 初始化示例用户数据
INSERT INTO sys_user (username, password, real_name, email, phone) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '系统管理员', 'admin@company.com', '13800138000'),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '张三', 'zhangsan@company.com', '13800138001'),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '李四', 'lisi@company.com', '13800138002');

-- 创建索引
CREATE INDEX idx_project_system ON sys_project(system_id);
CREATE INDEX idx_project_type ON sys_project(project_type);
CREATE INDEX idx_deliverable_project ON sys_deliverable(project_id);
CREATE INDEX idx_deliverable_type ON sys_deliverable(deliverable_type_id);
CREATE INDEX idx_deliverable_status ON sys_deliverable(status);

-- 创建视图：项目交付物状态视图
CREATE VIEW v_project_deliverable_status AS
SELECT 
    p.id as project_id,
    p.project_name,
    p.project_code,
    p.project_type,
    dt.id as deliverable_type_id,
    dt.type_name,
    dt.type_code,
    dt.is_required,
    dt.applicable_project_type,
    CASE 
        WHEN d.id IS NOT NULL THEN 1 
        ELSE 0 
    END as is_uploaded,
    d.file_name,
    d.upload_time,
    d.upload_user
FROM sys_project p
CROSS JOIN sys_deliverable_type dt
LEFT JOIN sys_deliverable d ON p.id = d.project_id AND dt.id = d.deliverable_type_id
WHERE (dt.applicable_project_type = 3) 
   OR (dt.applicable_project_type = 1 AND p.project_type = 1)
   OR (dt.applicable_project_type = 2 AND p.project_type = 2)
ORDER BY p.id, dt.sort_order;

-- 创建存储过程：获取项目交付物统计
DELIMITER //
CREATE PROCEDURE GetProjectDeliverableStats(IN projectId BIGINT)
BEGIN
    SELECT 
        COUNT(*) as total_deliverables,
        SUM(CASE WHEN is_uploaded = 1 THEN 1 ELSE 0 END) as uploaded_count,
        SUM(CASE WHEN is_uploaded = 0 THEN 1 ELSE 0 END) as not_uploaded_count,
        ROUND(SUM(CASE WHEN is_uploaded = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as completion_rate
    FROM v_project_deliverable_status
    WHERE project_id = projectId;
END //
DELIMITER ;

-- 创建触发器：项目删除时清理相关数据
DELIMITER //
CREATE TRIGGER tr_project_delete
BEFORE DELETE ON sys_project
FOR EACH ROW
BEGIN
    -- 删除项目相关的交付物记录
    DELETE FROM sys_deliverable WHERE project_id = OLD.id;
END //
DELIMITER ;

-- 创建触发器：系统删除时清理相关数据
DELIMITER //
CREATE TRIGGER tr_system_delete
BEFORE DELETE ON sys_system
FOR EACH ROW
BEGIN
    -- 删除系统相关的项目记录（级联删除会处理交付物）
    DELETE FROM sys_project WHERE system_id = OLD.id;
END //
DELIMITER ;

-- 提交事务
COMMIT;

-- 显示创建结果
SELECT 'Database and tables created successfully!' as message;
SELECT COUNT(*) as deliverable_types_count FROM sys_deliverable_type;
SELECT COUNT(*) as systems_count FROM sys_system;
SELECT COUNT(*) as projects_count FROM sys_project; 