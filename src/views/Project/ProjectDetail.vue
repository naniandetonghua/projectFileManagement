<template>
  <div class="project-detail">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/project' }">项目管理</el-breadcrumb-item>
        <el-breadcrumb-item>项目详情</el-breadcrumb-item>
      </el-breadcrumb>
      <div class="header-actions">
        <el-button @click="$router.go(-1)">返回</el-button>
        <el-button type="primary" @click="handleEdit">编辑项目</el-button>
      </div>
    </div>

    <div v-loading="loading" class="detail-content">
      <!-- 项目基本信息 -->
      <el-card class="info-card" shadow="never">
        <div slot="header" class="card-header">
          <span>项目基本信息</span>
        </div>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <label>项目名称：</label>
              <span>{{ projectInfo.name }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <label>项目经理：</label>
              <span>{{ projectInfo.manager }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <label>开始日期：</label>
              <span>{{ formatDate(projectInfo.startDate) }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <label>结束日期：</label>
              <span>{{ formatDate(projectInfo.endDate) }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <label>项目状态：</label>
              <el-tag :type="getStatusType(projectInfo.status)">
                {{ getStatusText(projectInfo.status) }}
              </el-tag>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <label>创建时间：</label>
              <span>{{ formatDateTime(projectInfo.createTime) }}</span>
            </div>
          </el-col>
          <el-col :span="24">
            <div class="info-item">
              <label>项目描述：</label>
              <div class="description">{{ projectInfo.description || '暂无描述' }}</div>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!-- 交付物列表 -->
      <el-card class="deliverable-card" shadow="never">
        <div slot="header" class="card-header">
          <span>交付物列表</span>
          <el-button type="primary" size="small" @click="handleAddDeliverable">
            <i class="el-icon-plus"></i> 新增交付物
          </el-button>
        </div>
        
        <el-table
          v-loading="deliverableLoading"
          :data="deliverableList"
          border
          stripe
          style="width: 100%"
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="交付物名称" min-width="200" />
          <el-table-column prop="type" label="类型" width="120">
            <template slot-scope="scope">
              <el-tag size="small">{{ scope.row.type }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getDeliverableStatusType(scope.row.status)" size="small">
                {{ getDeliverableStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="fileSize" label="文件大小" width="100">
            <template slot-scope="scope">
              {{ formatFileSize(scope.row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column prop="uploadTime" label="上传时间" width="160">
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.uploadTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" @click="handleViewDeliverable(scope.row)">查看</el-button>
              <el-button size="mini" type="primary" @click="handleEditDeliverable(scope.row)">编辑</el-button>
              <el-button size="mini" type="success" @click="handleDownload(scope.row)">下载</el-button>
              <el-button size="mini" type="danger" @click="handleDeleteDeliverable(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            :current-page="pagination.current"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pagination.size"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 编辑项目对话框 -->
    <el-dialog
      title="编辑项目"
      :visible.sync="editDialogVisible"
      width="600px"
    >
      <el-form
        ref="editForm"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入项目描述"
          />
        </el-form-item>
        <el-form-item label="项目经理" prop="manager">
          <el-input v-model="editForm.manager" placeholder="请输入项目经理" />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="editForm.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="editForm.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已暂停" value="PAUSED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="editLoading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import { formatDate, formatDateTime, formatFileSize } from '@/utils/date'

export default {
  name: 'ProjectDetail',
  data() {
    return {
      loading: false,
      deliverableLoading: false,
      editLoading: false,
      editDialogVisible: false,
      projectId: null,
      projectInfo: {},
      deliverableList: [],
      editForm: {
        id: null,
        name: '',
        description: '',
        manager: '',
        startDate: '',
        endDate: '',
        status: ''
      },
      editRules: {
        name: [
          { required: true, message: '请输入项目名称', trigger: 'blur' }
        ],
        manager: [
          { required: true, message: '请输入项目经理', trigger: 'blur' }
        ],
        startDate: [
          { required: true, message: '请选择开始日期', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择项目状态', trigger: 'change' }
        ]
      },
      pagination: {
        current: 1,
        size: 10,
        total: 0
      }
    }
  },
  created() {
    this.projectId = this.$route.params.id
    this.loadProjectDetail()
    this.loadDeliverableList()
  },
  methods: {
    ...mapActions('project', ['getProjectById', 'updateProject']),
    ...mapActions('deliverable', ['getDeliverableList', 'deleteDeliverable']),
    
    formatDate,
    formatDateTime,
    formatFileSize,
    
    // 加载项目详情
    async loadProjectDetail() {
      this.loading = true
      try {
        const response = await this.getProjectById(this.projectId)
        this.projectInfo = response
      } catch (error) {
        this.$message.error('加载项目详情失败')
      } finally {
        this.loading = false
      }
    },
    
    // 加载交付物列表
    async loadDeliverableList() {
      this.deliverableLoading = true
      try {
        const params = {
          page: this.pagination.current - 1,
          size: this.pagination.size,
          projectId: this.projectId
        }
        const response = await this.getDeliverableList(params)
        this.deliverableList = response.content
        this.pagination.total = response.totalElements
      } catch (error) {
        this.$message.error('加载交付物列表失败')
      } finally {
        this.deliverableLoading = false
      }
    },
    
    // 编辑项目
    handleEdit() {
      this.editForm = { ...this.projectInfo }
      this.editDialogVisible = true
    },
    
    // 提交编辑
    handleEditSubmit() {
      this.$refs.editForm.validate(async (valid) => {
        if (valid) {
          this.editLoading = true
          try {
            await this.updateProject(this.editForm)
            this.$message.success('更新成功')
            this.editDialogVisible = false
            this.loadProjectDetail()
          } catch (error) {
            this.$message.error('更新失败')
          } finally {
            this.editLoading = false
          }
        }
      })
    },
    
    // 新增交付物
    handleAddDeliverable() {
      this.$router.push(`/deliverable/add?projectId=${this.projectId}`)
    },
    
    // 查看交付物
    handleViewDeliverable(row) {
      this.$router.push(`/deliverable/detail/${row.id}`)
    },
    
    // 编辑交付物
    handleEditDeliverable(row) {
      this.$router.push(`/deliverable/edit/${row.id}`)
    },
    
    // 下载文件
    handleDownload(row) {
      // 这里应该调用下载API
      window.open(`/api/deliverables/${row.id}/download`, '_blank')
    },
    
    // 删除交付物
    handleDeleteDeliverable(row) {
      this.$confirm('确定要删除该交付物吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await this.deleteDeliverable(row.id)
          this.$message.success('删除成功')
          this.loadDeliverableList()
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    
    // 分页大小改变
    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.current = 1
      this.loadDeliverableList()
    },
    
    // 当前页改变
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadDeliverableList()
    },
    
    // 获取状态类型
    getStatusType(status) {
      const statusMap = {
        'IN_PROGRESS': 'primary',
        'COMPLETED': 'success',
        'PAUSED': 'warning',
        'CANCELLED': 'danger'
      }
      return statusMap[status] || 'info'
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成',
        'PAUSED': '已暂停',
        'CANCELLED': '已取消'
      }
      return statusMap[status] || status
    },
    
    // 获取交付物状态类型
    getDeliverableStatusType(status) {
      const statusMap = {
        'DRAFT': 'info',
        'REVIEW': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger'
      }
      return statusMap[status] || 'info'
    },
    
    // 获取交付物状态文本
    getDeliverableStatusText(status) {
      const statusMap = {
        'DRAFT': '草稿',
        'REVIEW': '审核中',
        'APPROVED': '已通过',
        'REJECTED': '已拒绝'
      }
      return statusMap[status] || status
    }
  }
}
</script>

<style lang="scss" scoped>
.project-detail {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .header-actions {
      .el-button {
        margin-left: 10px;
      }
    }
  }
  
  .detail-content {
    .info-card {
      margin-bottom: 20px;
      
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
      
      .info-item {
        margin-bottom: 15px;
        
        label {
          font-weight: bold;
          color: #606266;
          margin-right: 10px;
        }
        
        .description {
          margin-top: 10px;
          padding: 10px;
          background: #f5f7fa;
          border-radius: 4px;
          line-height: 1.6;
        }
      }
    }
    
    .deliverable-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
      
      .pagination-container {
        padding: 20px;
        text-align: right;
      }
    }
  }
}
</style> 