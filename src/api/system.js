import request from '@/utils/request'

  // 获取系统列表
export const getSystemList = () => {
    return request({
    url: '/system/list',
      method: 'get'
    })
}

  // 获取单个系统
export const getSystem = (id) => {
    return request({
    url: `/system/${id}`,
      method: 'get'
    })
}

  // 创建系统
export const createSystem = (data) => {
    return request({
    url: '/system/create',
      method: 'post',
      data
    })
}

  // 更新系统
export const updateSystem = (data) => {
    return request({
    url: '/system/update',
      method: 'put',
      data
    })
}

  // 删除系统
export const deleteSystem = (id) => {
    return request({
    url: `/system/delete/${id}`,
      method: 'delete'
    })
} 