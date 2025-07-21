import request from '@/utils/request'

// 获取交付物列表
export function getDeliverableList(params) {
  return request({
    url: '/api/deliverables',
    method: 'get',
    params
  })
}

// 获取交付物详情
export function getDeliverableById(id) {
  return request({
    url: `/api/deliverables/${id}`,
    method: 'get'
  })
}

// 创建交付物
export function createDeliverable(data) {
  return request({
    url: '/api/deliverables',
    method: 'post',
    data
  })
}

// 更新交付物
export function updateDeliverable(data) {
  return request({
    url: `/api/deliverables/${data.id}`,
    method: 'put',
    data
  })
}

// 删除交付物
export function deleteDeliverable(id) {
  return request({
    url: `/api/deliverables/${id}`,
    method: 'delete'
  })
}

// 文件上传
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/api/deliverables/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 文件下载
export function downloadFile(id) {
  return request({
    url: `/api/deliverables/${id}/download`,
    method: 'get',
    responseType: 'blob'
  })
}

// 获取交付物统计
export function getDeliverableStats() {
  return request({
    url: '/api/deliverables/stats',
    method: 'get'
  })
} 