import request from '@/utils/request'

// 获取项目列表
export function getProjectList(params) {
  return request({
    url: '/project/list',
    method: 'get',
    params
  })
}

// 获取项目详情
export function getProjectById(id) {
  return request({
    url: `/project/${id}`,
    method: 'get'
  })
}

// 创建项目
export function createProject(data) {
  return request({
    url: '/project/create',
    method: 'post',
    data
  })
}

// 更新项目
export function updateProject(data) {
  return request({
    url: '/project/update',
    method: 'put',
    data
  })
}

// 删除项目
export function deleteProject(id) {
  return request({
    url: `/project/delete/${id}`,
    method: 'delete'
  })
}

// 获取项目统计
export function getProjectStats() {
  return request({
    url: '/project/stats',
    method: 'get'
  })
} 