<template>
  <div class="deliverable-list">
    <div class="page-header">
      <h2>交付物管理</h2>
      <el-button type="primary" @click="handleAdd">
        <i class="el-icon-plus"></i> 新增交付物
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="交付物名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入交付物名称"
            clearable
            @keyup.enter.native="handleSearch"
          />
        </el-form-item>
        <el-form-item label="所属项目">
          <el-select v-model="searchForm.projectId" placeholder="请选择项目" clearable>
            <el-option
              v-for="project in projectList"
              :key="project.id"
              :label="project.name"
              :value="project.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="交付物类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <el-option label="文档" value="DOCUMENT" />
            <el-option label="设计稿" value="DESIGN" />
            <el-option label="代码" value="CODE" />
            <el-option label="测试报告" value="TEST_REPORT" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="审核中" value="REVIEW" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 交付物列表 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="deliverableList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="交付物名称" min-width="200" />
        <el-table-column prop="projectName" label="所属项目" width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag size="small">{{ getTypeText(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
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
        <el-table-column prop="uploader" label="上传人" width="100" />
        <el-table-column label="操作" width="250" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleView(scope.row)">查看</el-button>
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="success" @click="handleDownload(scope.row)">下载</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="deliverableForm"
        :model="deliverableForm"
        :rules="deliverableRules"
        label-width="100px"
      >
        <el-form-item label="交付物名称" prop="name">
          <el-input v-model="deliverableForm.name" placeholder="请输入交付物名称" />
        </el-form-item>
        <el-form-item label="所属项目" prop="projectId">
          <el-select v-model="deliverableForm.projectId" placeholder="请选择项目" style="width: 100%">
            <el-option
              v-for="project in projectList"
              :key="project.id"
              :label="project.name"
              :value="project.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="交付物类型" prop="type">
          <el-select v-model="deliverableForm.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="文档" value="DOCUMENT" />
            <el-option label="设计稿" value="DESIGN" />
            <el-option label="代码" value="CODE" />
            <el-option label="测试报告" value="TEST_REPORT" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="deliverableForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="文件" prop="file">
          <el-upload
            ref="upload"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :data="uploadData"
            :before-upload="beforeUpload"
            :on-success="onUploadSuccess"
            :on-error="onUploadError"
            :file-list="fileList"
            :limit="1"
            accept=".doc,.docx,.pdf,.zip,.rar,.jpg,.png,.gif"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传文档/图片/压缩包文件，且不超过50MB</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="deliverableForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="审核中" value="REVIEW" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import { formatDateTime, formatFileSize } from '@/utils/date'

export default {
  name: 'DeliverableList',
  data() {
    return {
      loading: false,
      submitLoading: false,
      dialogVisible: false,
      isEdit: false,
      searchForm: {
        name: '',
        projectId: '',
        type: '',
        status: ''
      },
      deliverableForm: {
        id: null,
        name: '',
        projectId: '',
        type: '',
        description: '',
        status: 'DRAFT',
        filePath: '',
        fileName: '',
        fileSize: 0
      },
      deliverableRules: {
        name: [
          { required: true, message: '请输入交付物名称', trigger: 'blur' }
        ],
        projectId: [
          { required: true, message: '请选择所属项目', trigger: 'change' }
        ],
        type: [
          { required: true, message: '请选择交付物类型', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      },
      deliverableList: [],
      projectList: [],
      fileList: [],
      uploadUrl: '/api/deliverables/upload',
      uploadHeaders: {},
      uploadData: {},
      pagination: {
        current: 1,
        size: 10,
        total: 0
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑交付物' : '新增交付物'
    }
  },
  created() {
    this.loadDeliverableList()
    this.loadProjectList()
  },
  methods: {
    ...mapActions('deliverable', ['getDeliverableList', 'createDeliverable', 'updateDeliverable', 'deleteDeliverable']),
    ...mapActions('project', ['getProjectList']),
    
    formatDateTime,
    formatFileSize,
    
    // 加载交付物列表
    async loadDeliverableList() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.current - 1,
          size: this.pagination.size,
          name: this.searchForm.name,
          projectId: this.searchForm.projectId,
          type: this.searchForm.type,
          status: this.searchForm.status
        }
        const response = await this.getDeliverableList(params)
        this.deliverableList = response.content
        this.pagination.total = response.totalElements
      } catch (error) {
        this.$message.error('加载交付物列表失败')
      } finally {
        this.loading = false
      }
    },
    
    // 加载项目列表
    async loadProjectList() {
      try {
        const response = await this.getProjectList({ page: 0, size: 1000 })
        this.projectList = response.content
      } catch (error) {
        this.$message.error('加载项目列表失败')
      }
    },
    
    // 搜索
    handleSearch() {
      this.pagination.current = 1
      this.loadDeliverableList()
    },
    
    // 重置搜索
    handleReset() {
      this.searchForm = {
        name: '',
        projectId: '',
        type: '',
        status: ''
      }
      this.handleSearch()
    },
    
    // 新增交付物
    handleAdd() {
      this.isEdit = false
      this.deliverableForm = {
        id: null,
        name: '',
        projectId: '',
        type: '',
        description: '',
        status: 'DRAFT',
        filePath: '',
        fileName: '',
        fileSize: 0
      }
      this.fileList = []
      this.dialogVisible = true
    },
    
    // 编辑交付物
    handleEdit(row) {
      this.isEdit = true
      this.deliverableForm = { ...row }
      this.fileList = row.fileName ? [{
        name: row.fileName,
        url: row.filePath
      }] : []
      this.dialogVisible = true
    },
    
    // 查看交付物
    handleView(row) {
      this.$router.push(`/deliverable/detail/${row.id}`)
    },
    
    // 下载文件
    handleDownload(row) {
      window.open(`/api/deliverables/${row.id}/download`, '_blank')
    },
    
    // 删除交付物
    handleDelete(row) {
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
    
    // 上传前检查
    beforeUpload(file) {
      const isLt50M = file.size / 1024 / 1024 < 50
      if (!isLt50M) {
        this.$message.error('上传文件大小不能超过 50MB!')
        return false
      }
      return true
    },
    
    // 上传成功
    onUploadSuccess(response, file) {
      this.deliverableForm.filePath = response.data.filePath
      this.deliverableForm.fileName = file.name
      this.deliverableForm.fileSize = file.size
      this.$message.success('文件上传成功')
    },
    
    // 上传失败
    onUploadError() {
      this.$message.error('文件上传失败')
    },
    
    // 提交表单
    handleSubmit() {
      this.$refs.deliverableForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            if (this.isEdit) {
              await this.updateDeliverable(this.deliverableForm)
              this.$message.success('更新成功')
            } else {
              await this.createDeliverable(this.deliverableForm)
              this.$message.success('创建成功')
            }
            this.dialogVisible = false
            this.loadDeliverableList()
          } catch (error) {
            this.$message.error(this.isEdit ? '更新失败' : '创建失败')
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    
    // 对话框关闭
    handleDialogClose() {
      this.$refs.deliverableForm.resetFields()
      this.fileList = []
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
    
    // 获取类型文本
    getTypeText(type) {
      const typeMap = {
        'DOCUMENT': '文档',
        'DESIGN': '设计稿',
        'CODE': '代码',
        'TEST_REPORT': '测试报告',
        'OTHER': '其他'
      }
      return typeMap[type] || type
    },
    
    // 获取状态类型
    getStatusType(status) {
      const statusMap = {
        'DRAFT': 'info',
        'REVIEW': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger'
      }
      return statusMap[status] || 'info'
    },
    
    // 获取状态文本
    getStatusText(status) {
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
.deliverable-list {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      color: #303133;
    }
  }
  
  .search-area {
    background: #fff;
    padding: 20px;
    border-radius: 4px;
    margin-bottom: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    
    .search-form {
      margin: 0;
    }
  }
  
  .table-container {
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    
    .pagination-container {
      padding: 20px;
      text-align: right;
    }
  }
}
</style> 