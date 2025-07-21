<template>
  <div class="app-container">
    <!-- 头部 -->
    <el-header class="app-header">
      <div class="header-left">
        <h2 class="logo">项目交付物管理系统</h2>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            <el-avatar size="small" icon="UserFilled" />
            <span class="username">管理员</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人信息</el-dropdown-item>
              <el-dropdown-item>修改密码</el-dropdown-item>
              <el-dropdown-item divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <div class="main-container">
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="app-sidebar">
        <el-menu
          :default-active="$route.path"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="sidebar-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><DataBoard /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>
          
          <el-menu-item index="/systems">
            <el-icon><Monitor /></el-icon>
            <template #title>系统管理</template>
          </el-menu-item>
          
          <el-menu-item index="/projects">
            <el-icon><Folder /></el-icon>
            <template #title>项目管理</template>
          </el-menu-item>
          
          <el-menu-item index="/deliverables">
            <el-icon><Document /></el-icon>
            <template #title>交付物管理</template>
          </el-menu-item>
          
          <el-menu-item index="/upload">
            <el-icon><Upload /></el-icon>
            <template #title>文件上传</template>
          </el-menu-item>
        </el-menu>
        
        <!-- 折叠按钮 -->
        <div class="sidebar-toggle" @click="toggleSidebar">
          <el-icon>
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
        </div>
      </el-aside>

      <!-- 内容区域 -->
      <el-main class="content-container">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'Layout',
  setup() {
    const isCollapse = ref(false)

    const toggleSidebar = () => {
      isCollapse.value = !isCollapse.value
    }

    return {
      isCollapse,
      toggleSidebar
    }
  }
}
</script>

<style lang="scss" scoped>
.app-header {
  background: #fff;
  border-bottom: 1px solid $border-color-light;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $spacing-lg;
  box-shadow: $box-shadow-light;

  .header-left {
    .logo {
      color: $primary-color;
      font-size: 20px;
      font-weight: 600;
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: $spacing-sm;
      border-radius: $border-radius-base;
      transition: background-color 0.3s;

      &:hover {
        background-color: $background-color-base;
      }

      .username {
        margin: 0 $spacing-sm;
        color: $text-regular;
      }
    }
  }
}

.app-sidebar {
  background: #fff;
  border-right: 1px solid $border-color-light;
  position: relative;
  transition: width 0.3s;

  .sidebar-menu {
    border-right: none;
    height: calc(100vh - 60px);
  }

  .sidebar-toggle {
    position: absolute;
    bottom: $spacing-md;
    left: 50%;
    transform: translateX(-50%);
    width: 32px;
    height: 32px;
    background: $primary-color;
    color: #fff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: darken($primary-color, 10%);
    }
  }
}

.content-container {
  background: $background-color-base;
  padding: $spacing-lg;
  overflow-y: auto;
}
</style> 