import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'projects',
        name: 'Projects',
        component: () => import('@/views/Project/ProjectList.vue'),
        meta: { title: '项目管理' }
      },
      {
        path: 'projects/:id',
        name: 'ProjectDetail',
        component: () => import('@/views/Project/ProjectDetail.vue'),
        meta: { title: '项目详情' }
      },
      {
        path: 'deliverables',
        name: 'Deliverables',
        component: () => import('@/views/Deliverable/DeliverableList.vue'),
        meta: { title: '交付物管理' }
      },
      {
        path: 'systems',
        name: 'Systems',
        component: () => import('@/views/System/SystemList.vue'),
        meta: { title: '系统管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router 