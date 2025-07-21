<template>
  <div class="dashboard">
    <div class="page-header">
      <h2>仪表盘</h2>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon project-icon">
                <i class="el-icon-s-management"></i>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ projectStats.totalProjects || 0 }}</div>
                <div class="stats-label">总项目数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon deliverable-icon">
                <i class="el-icon-document"></i>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ deliverableStats.totalDeliverables || 0 }}</div>
                <div class="stats-label">总交付物数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon progress-icon">
                <i class="el-icon-s-data"></i>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ projectStats.inProgressProjects || 0 }}</div>
                <div class="stats-label">进行中项目</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon completed-icon">
                <i class="el-icon-circle-check"></i>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ projectStats.completedProjects || 0 }}</div>
                <div class="stats-label">已完成项目</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="chart-card">
            <div slot="header" class="card-header">
              <span>项目状态分布</span>
            </div>
            <div class="chart-container">
              <div ref="projectChart" class="chart"></div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <div slot="header" class="card-header">
              <span>交付物状态分布</span>
            </div>
            <div class="chart-container">
              <div ref="deliverableChart" class="chart"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最近活动 -->
    <div class="recent-activities">
      <el-card>
        <div slot="header" class="card-header">
          <span>最近活动</span>
        </div>
        <div class="activity-list">
          <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
            <div class="activity-icon">
              <i :class="activity.icon"></i>
            </div>
            <div class="activity-content">
              <div class="activity-title">{{ activity.title }}</div>
              <div class="activity-time">{{ formatRelativeTime(activity.time) }}</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import { formatRelativeTime } from '@/utils/date'

export default {
  name: 'Dashboard',
  data() {
    return {
      projectChart: null,
      deliverableChart: null,
      recentActivities: [
        {
          id: 1,
          title: '新项目"电商平台开发"已创建',
          time: new Date(Date.now() - 1000 * 60 * 30),
          icon: 'el-icon-s-management'
        },
        {
          id: 2,
          title: '交付物"需求文档"已上传',
          time: new Date(Date.now() - 1000 * 60 * 60 * 2),
          icon: 'el-icon-document'
        },
        {
          id: 3,
          title: '项目"移动端APP"状态更新为已完成',
          time: new Date(Date.now() - 1000 * 60 * 60 * 4),
          icon: 'el-icon-circle-check'
        }
      ]
    }
  },
  computed: {
    ...mapGetters('project', ['stats']),
    ...mapGetters('deliverable', ['stats']),
    
    projectStats() {
      return this.$store.state.project.stats
    },
    
    deliverableStats() {
      return this.$store.state.deliverable.stats
    }
  },
  mounted() {
    this.loadStats()
    this.initCharts()
  },
  methods: {
    ...mapActions('project', ['getProjectStats']),
    ...mapActions('deliverable', ['getDeliverableStats']),
    
    formatRelativeTime,
    
    // 加载统计数据
    async loadStats() {
      try {
        await Promise.all([
          this.getProjectStats(),
          this.getDeliverableStats()
        ])
        this.updateCharts()
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },
    
    // 初始化图表
    initCharts() {
      this.$nextTick(() => {
        this.initProjectChart()
        this.initDeliverableChart()
      })
    },
    
    // 初始化项目状态图表
    initProjectChart() {
      const chartDom = this.$refs.projectChart
      if (!chartDom) return
      
      this.projectChart = this.$echarts.init(chartDom)
      this.updateProjectChart()
    },
    
    // 初始化交付物状态图表
    initDeliverableChart() {
      const chartDom = this.$refs.deliverableChart
      if (!chartDom) return
      
      this.deliverableChart = this.$echarts.init(chartDom)
      this.updateDeliverableChart()
    },
    
    // 更新项目图表
    updateProjectChart() {
      if (!this.projectChart) return
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '项目状态',
            type: 'pie',
            radius: '50%',
            data: [
              { value: this.projectStats.inProgressProjects || 0, name: '进行中' },
              { value: this.projectStats.completedProjects || 0, name: '已完成' },
              { value: this.projectStats.pausedProjects || 0, name: '已暂停' },
              { value: this.projectStats.cancelledProjects || 0, name: '已取消' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      
      this.projectChart.setOption(option)
    },
    
    // 更新交付物图表
    updateDeliverableChart() {
      if (!this.deliverableChart) return
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '交付物状态',
            type: 'pie',
            radius: '50%',
            data: [
              { value: this.deliverableStats.draftDeliverables || 0, name: '草稿' },
              { value: this.deliverableStats.reviewDeliverables || 0, name: '审核中' },
              { value: this.deliverableStats.approvedDeliverables || 0, name: '已通过' },
              { value: this.deliverableStats.rejectedDeliverables || 0, name: '已拒绝' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      
      this.deliverableChart.setOption(option)
    },
    
    // 更新图表
    updateCharts() {
      this.updateProjectChart()
      this.updateDeliverableChart()
    }
  },
  
  beforeDestroy() {
    if (this.projectChart) {
      this.projectChart.dispose()
    }
    if (this.deliverableChart) {
      this.deliverableChart.dispose()
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
  
  .page-header {
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      color: #303133;
    }
  }
  
  .stats-cards {
    margin-bottom: 20px;
    
    .stats-card {
      .stats-content {
        display: flex;
        align-items: center;
        
        .stats-icon {
          width: 60px;
          height: 60px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 15px;
          
          i {
            font-size: 24px;
            color: #fff;
          }
          
          &.project-icon {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          }
          
          &.deliverable-icon {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          }
          
          &.progress-icon {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          }
          
          &.completed-icon {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
          }
        }
        
        .stats-info {
          .stats-number {
            font-size: 24px;
            font-weight: bold;
            color: #303133;
            margin-bottom: 5px;
          }
          
          .stats-label {
            color: #909399;
            font-size: 14px;
          }
        }
      }
    }
  }
  
  .charts-section {
    margin-bottom: 20px;
    
    .chart-card {
      .chart-container {
        height: 300px;
        
        .chart {
          width: 100%;
          height: 100%;
        }
      }
    }
  }
  
  .recent-activities {
    .activity-list {
      .activity-item {
        display: flex;
        align-items: center;
        padding: 15px 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        .activity-icon {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          background: #f5f7fa;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 15px;
          
          i {
            color: #409eff;
            font-size: 16px;
          }
        }
        
        .activity-content {
          flex: 1;
          
          .activity-title {
            color: #303133;
            margin-bottom: 5px;
          }
          
          .activity-time {
            color: #909399;
            font-size: 12px;
          }
        }
      }
    }
  }
}
</style> 