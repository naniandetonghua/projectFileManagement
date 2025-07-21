# 前端Vue项目结构说明

## 项目结构
```
project-delivery-frontend/
├── public/
│   ├── index.html
│   ├── favicon.ico
│   └── static/
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── router/
│   │   └── index.js
│   ├── store/
│   │   ├── index.js
│   │   ├── modules/
│   │   │   ├── system.js
│   │   │   ├── project.js
│   │   │   └── deliverable.js
│   ├── views/
│   │   ├── Layout.vue
│   │   ├── SystemTree.vue
│   │   ├── ProjectList.vue
│   │   ├── DeliverableManager.vue
│   │   └── FileUpload.vue
│   ├── components/
│   │   ├── common/
│   │   │   ├── Header.vue
│   │   │   ├── Sidebar.vue
│   │   │   └── Footer.vue
│   │   ├── system/
│   │   │   ├── SystemTree.vue
│   │   │   └── SystemForm.vue
│   │   ├── project/
│   │   │   ├── ProjectList.vue
│   │   │   ├── ProjectForm.vue
│   │   │   └── ProjectCard.vue
│   │   └── deliverable/
│   │       ├── DeliverableList.vue
│   │       ├── DeliverableItem.vue
│   │       └── FileUpload.vue
│   ├── api/
│   │   ├── index.js
│   │   ├── system.js
│   │   ├── project.js
│   │   └── deliverable.js
│   ├── utils/
│   │   ├── request.js
│   │   ├── auth.js
│   │   ├── file.js
│   │   └── constants.js
│   ├── assets/
│   │   ├── images/
│   │   ├── icons/
│   │   └── styles/
│   │       ├── main.scss
│   │       ├── variables.scss
│   │       └── components.scss
│   └── mixins/
│       └── common.js
├── package.json
├── vue.config.js
├── .eslintrc.js
├── .prettierrc
├── babel.config.js
└── README.md
```

## 主要依赖配置 (package.json)
```json
{
  "name": "project-delivery-frontend",
  "version": "1.0.0",
  "description": "项目交付物管理系统前端",
  "private": true,
  "scripts": {
    "serve": "vue-cli-service serve",
    "build": "vue-cli-service build",
    "lint": "vue-cli-service lint",
    "dev": "vue-cli-service serve --mode development",
    "prod": "vue-cli-service build --mode production"
  },
  "dependencies": {
    "vue": "^3.2.0",
    "vue-router": "^4.0.0",
    "vuex": "^4.0.0",
    "element-plus": "^2.3.0",
    "axios": "^0.27.0",
    "js-cookie": "^3.0.0",
    "nprogress": "^0.2.0",
    "file-saver": "^2.0.5",
    "xlsx": "^0.18.5"
  },
  "devDependencies": {
    "@vue/cli-service": "^5.0.0",
    "@vue/cli-plugin-babel": "^5.0.0",
    "@vue/cli-plugin-eslint": "^5.0.0",
    "@vue/cli-plugin-router": "^5.0.0",
    "@vue/cli-plugin-vuex": "^5.0.0",
    "@vue/compiler-sfc": "^3.2.0",
    "babel-eslint": "^10.1.0",
    "eslint": "^8.0.0",
    "eslint-plugin-vue": "^8.0.0",
    "sass": "^1.32.0",
    "sass-loader": "^12.0.0"
  }
}
```

## 配置文件 (vue.config.js)
```javascript
const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    }
  },
  css: {
    loaderOptions: {
      sass: {
        additionalData: `@import "@/assets/styles/variables.scss";`
      }
    }
  },
  configureWebpack: {
    resolve: {
      alias: {
        '@': require('path').resolve(__dirname, 'src')
      }
    }
  }
})
```

## 主要页面组件

### 1. 主布局组件 (Layout.vue)
```vue
<template>
  <div class="app-container">
    <!-- 左侧系统树 -->
    <div class="sidebar">
      <SystemTree @select-system="handleSystemSelect" @select-project="handleProjectSelect" />
    </div>
    
    <!-- 右侧内容区 -->
    <div class="main-content">
      <!-- 顶部项目类型选择 -->
      <div class="project-type-selector">
        <el-radio-group v-model="projectType" @change="handleProjectTypeChange">
          <el-radio :label="1">重大项目</el-radio>
          <el-radio :label="2">普通项目</el-radio>
        </el-radio-group>
      </div>
      
      <!-- 交付物管理区域 -->
      <DeliverableManager 
        :project="currentProject"
        :project-type="projectType"
        @refresh="handleRefresh"
      />
    </div>
  </div>
</template>
```

### 2. 系统树组件 (SystemTree.vue)
```vue
<template>
  <div class="system-tree">
    <el-tree
      :data="treeData"
      :props="treeProps"
      node-key="id"
      :expand-on-click-node="false"
      @node-click="handleNodeClick"
    >
      <template #default="{ node, data }">
        <span class="custom-tree-node">
          <i :class="getNodeIcon(data)"></i>
          <span>{{ node.label }}</span>
        </span>
      </template>
    </el-tree>
  </div>
</template>
```

### 3. 交付物管理组件 (DeliverableManager.vue)
```vue
<template>
  <div class="deliverable-manager">
    <div class="deliverable-list">
      <div
        v-for="item in deliverableList"
        :key="item.id"
        class="deliverable-item"
        :class="getItemClass(item)"
      >
        <div class="item-header">
          <span class="item-title">{{ item.typeName }}</span>
          <span class="item-status" :class="getStatusClass(item)">
            {{ getStatusText(item) }}
          </span>
        </div>
        
        <div class="item-content">
          <div v-if="item.isUploaded" class="file-info">
            <span class="file-name">{{ item.fileName }}</span>
            <span class="upload-time">{{ formatTime(item.uploadTime) }}</span>
          </div>
          
          <div class="item-actions">
            <el-button
              v-if="!item.isUploaded"
              type="primary"
              size="small"
              @click="handleUpload(item)"
            >
              上传文件
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="handleDownload(item)"
            >
              下载
            </el-button>
            <el-button
              v-if="item.isUploaded"
              type="danger"
              size="small"
              @click="handleDelete(item)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 文件上传对话框 -->
    <FileUpload
      v-model="uploadVisible"
      :deliverable-type="currentDeliverableType"
      :project-id="projectId"
      @success="handleUploadSuccess"
    />
  </div>
</template>
```

## API接口封装

### 1. 请求封装 (utils/request.js)
```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || '/api',
  timeout: 30000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(res)
    }
    return res
  },
  error => {
    if (error.response.status === 401) {
      router.push('/login')
    }
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service
```

### 2. 系统API (api/system.js)
```javascript
import request from '@/utils/request'

export function getSystemList() {
  return request({
    url: '/system/list',
    method: 'get'
  })
}

export function createSystem(data) {
  return request({
    url: '/system/create',
    method: 'post',
    data
  })
}

export function updateSystem(data) {
  return request({
    url: '/system/update',
    method: 'put',
    data
  })
}

export function deleteSystem(id) {
  return request({
    url: `/system/delete/${id}`,
    method: 'delete'
  })
}
```

### 3. 项目API (api/project.js)
```javascript
import request from '@/utils/request'

export function getProjectList(params) {
  return request({
    url: '/project/list',
    method: 'get',
    params
  })
}

export function getProjectById(id) {
  return request({
    url: `/project/${id}`,
    method: 'get'
  })
}

export function createProject(data) {
  return request({
    url: '/project/create',
    method: 'post',
    data
  })
}

export function updateProject(data) {
  return request({
    url: '/project/update',
    method: 'put',
    data
  })
}

export function deleteProject(id) {
  return request({
    url: `/project/delete/${id}`,
    method: 'delete'
  })
}
```

### 4. 交付物API (api/deliverable.js)
```javascript
import request from '@/utils/request'

export function getDeliverableList(projectId) {
  return request({
    url: `/deliverable/list/${projectId}`,
    method: 'get'
  })
}

export function uploadDeliverable(data) {
  return request({
    url: '/deliverable/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function downloadDeliverable(id) {
  return request({
    url: `/deliverable/download/${id}`,
    method: 'get',
    responseType: 'blob'
  })
}

export function deleteDeliverable(id) {
  return request({
    url: `/deliverable/delete/${id}`,
    method: 'delete'
  })
}
```

## 状态管理 (Vuex)

### 1. 主状态管理 (store/index.js)
```javascript
import { createStore } from 'vuex'
import system from './modules/system'
import project from './modules/project'
import deliverable from './modules/deliverable'

export default createStore({
  modules: {
    system,
    project,
    deliverable
  }
})
```

### 2. 系统模块 (store/modules/system.js)
```javascript
import { getSystemList } from '@/api/system'

const state = {
  systemList: [],
  currentSystem: null
}

const mutations = {
  SET_SYSTEM_LIST(state, list) {
    state.systemList = list
  },
  SET_CURRENT_SYSTEM(state, system) {
    state.currentSystem = system
  }
}

const actions = {
  async fetchSystemList({ commit }) {
    try {
      const response = await getSystemList()
      commit('SET_SYSTEM_LIST', response.data)
      return response.data
    } catch (error) {
      console.error('获取系统列表失败:', error)
      throw error
    }
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
```

## 样式设计

### 1. 主样式文件 (assets/styles/main.scss)
```scss
// 全局样式
.app-container {
  display: flex;
  height: 100vh;
  
  .sidebar {
    width: 300px;
    border-right: 1px solid #e6e6e6;
    background: #f5f5f5;
  }
  
  .main-content {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
  }
}

// 交付物列表样式
.deliverable-list {
  .deliverable-item {
    border: 1px solid #e6e6e6;
    border-radius: 8px;
    margin-bottom: 16px;
    padding: 16px;
    transition: all 0.3s;
    
    &.uploaded {
      border-color: #67c23a;
      background: #f0f9ff;
    }
    
    &.not-uploaded {
      border-color: #e6a23c;
      background: #fdf6ec;
    }
    
    &.not-required {
      opacity: 0.6;
    }
  }
}

// 状态样式
.status-uploaded {
  color: #67c23a;
}

.status-not-uploaded {
  color: #e6a23c;
}

.status-not-required {
  color: #909399;
}
```

## 路由配置 (router/index.js)
```javascript
import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/system',
    children: [
      {
        path: '/system',
        name: 'System',
        component: () => import('@/views/SystemTree.vue')
      },
      {
        path: '/project/:id',
        name: 'Project',
        component: () => import('@/views/ProjectList.vue')
      },
      {
        path: '/deliverable/:projectId',
        name: 'Deliverable',
        component: () => import('@/views/DeliverableManager.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
```

## 工具函数

### 1. 文件处理工具 (utils/file.js)
```javascript
import { saveAs } from 'file-saver'

// 文件大小格式化
export function formatFileSize(bytes) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 文件类型验证
export function validateFileType(file, allowedTypes) {
  const extension = file.name.split('.').pop().toLowerCase()
  return allowedTypes.includes(extension)
}

// 文件下载
export function downloadFile(blob, filename) {
  saveAs(blob, filename)
}

// 文件上传进度
export function uploadProgress(progressEvent) {
  return Math.round((progressEvent.loaded * 100) / progressEvent.total)
}
```

## 部署配置

### 1. 环境变量配置
```bash
# .env.development
VUE_APP_BASE_API = '/api'
VUE_APP_TITLE = '项目交付物管理系统'

# .env.production
VUE_APP_BASE_API = 'http://your-domain.com/api'
VUE_APP_TITLE = '项目交付物管理系统'
```

### 2. 构建和部署
```bash
# 开发环境
npm run serve

# 生产环境构建
npm run build

# 部署到Nginx
# 将dist目录内容复制到Nginx的静态文件目录
```

### 3. Nginx配置
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /var/www/project-delivery-frontend;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 性能优化

### 1. 代码分割
- 路由懒加载
- 组件懒加载
- 第三方库按需引入

### 2. 缓存策略
- 浏览器缓存
- API响应缓存
- 静态资源缓存

### 3. 用户体验
- 加载状态提示
- 错误处理
- 响应式设计
- 键盘快捷键支持 