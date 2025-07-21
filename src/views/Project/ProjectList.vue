<template>
  <div class="project-list">
    <div class="page-header">
      <h2>项目管理</h2>
      <el-button type="primary" @click="handleAdd">
        <i class="el-icon-plus"></i> 新增项目
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="项目名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入项目名称"
            clearable
            @keyup.enter.native="handleSearch"
          />
        </el-form-item>
        <el-form-item label="所属系统">
          <el-select v-model="searchForm.systemId" placeholder="请选择系统" clearable>
            <el-option
              v-for="system in systemList"
              :key="system.id"
              :label="system.systemName"
              :value="system.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="项目状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已暂停" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 项目列表 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="projectList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="项目名称" min-width="150" />
        <el-table-column prop="code" label="项目编码" width="120" />
        <el-table-column prop="systemName" label="所属系统" width="120" />
        <el-table-column prop="type" label="项目类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 1 ? 'danger' : 'info'">
              {{ scope.row.type === 1 ? '重大' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="项目描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="manager" label="项目经理" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120">
          <template slot-scope="scope">
            {{ formatDate(scope.row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="endDate" label="结束日期" width="120">
          <template slot-scope="scope">
            {{ formatDate(scope.row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleView(scope.row)">查看</el-button>
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
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
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="projectForm"
        :model="projectForm"
        :rules="projectRules"
        label-width="100px"
      >
        <el-form-item label="所属系统" prop="systemId">
          <el-select v-model="projectForm.systemId" placeholder="请选择系统" style="width: 100%">
            <el-option
              v-for="system in systemList"
              :key="system.id"
              :label="system.systemName"
              :value="system.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="projectForm.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目编码" prop="code">
          <el-input v-model="projectForm.code" placeholder="请输入项目编码" />
        </el-form-item>
        <el-form-item label="项目类型" prop="type">
          <el-select v-model="projectForm.type" placeholder="请选择项目类型" style="width: 100%">
            <el-option label="重大" :value="1" />
            <el-option label="普通" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="projectForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入项目描述"
          />
        </el-form-item>
        <el-form-item label="项目经理" prop="manager">
          <el-input v-model="projectForm.manager" placeholder="请输入项目经理" />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="projectForm.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="projectForm.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <el-select v-model="projectForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已暂停" :value="3" />
            <el-option label="已取消" :value="4" />
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
import { formatDate, formatDateTime } from '@/utils/date'
import { getSystemList } from '@/api/system'

export default {
  name: 'ProjectList',
  data() {
    return {
      loading: false,
      submitLoading: false,
      dialogVisible: false,
      isEdit: false,
      systemList: [],
      searchForm: {
        name: '',
        systemId: null,
        status: null
      },
      projectForm: {
        id: null,
        systemId: null,
        name: '',
        code: '',
        type: 2,
        description: '',
        manager: '',
        startDate: '',
        endDate: '',
        status: 1
      },
      projectRules: {
        systemId: [
          { required: true, message: '请选择所属系统', trigger: 'change' }
        ],
        name: [
          { required: true, message: '请输入项目名称', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入项目编码', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择项目类型', trigger: 'change' }
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
      projectList: [],
      pagination: {
        current: 1,
        size: 10,
        total: 0
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑项目' : '新增项目'
    }
  },
  created() {
    this.loadSystemList()
    this.loadProjectList()
  },
  methods: {
    ...mapActions('project', ['getProjectList', 'createProject', 'updateProject', 'deleteProject']),
    
    formatDate,
    formatDateTime,
    
    // 加载系统列表
    async loadSystemList() {
      try {
        const response = await getSystemList()
        this.systemList = response.data
      } catch (error) {
        this.$message.error('加载系统列表失败')
      }
    },
    
    // 加载项目列表
    async loadProjectList() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.current - 1,
          size: this.pagination.size,
          name: this.searchForm.name,
          status: this.searchForm.status,
          systemId: this.searchForm.systemId
        }
        const response = await this.getProjectList(params)
        this.projectList = response.content
        this.pagination.total = response.totalElements
      } catch (error) {
        this.$message.error('加载项目列表失败')
      } finally {
        this.loading = false
      }
    },
    
    // 搜索
    handleSearch() {
      this.pagination.current = 1
      this.loadProjectList()
    },
    
    // 重置搜索
    handleReset() {
      this.searchForm = {
        name: '',
        systemId: null,
        status: null
      }
      this.handleSearch()
    },
    
    // 新增项目
    handleAdd() {
      this.isEdit = false
      this.projectForm = {
        id: null,
        systemId: null,
        name: '',
        code: '',
        type: 2,
        description: '',
        manager: '',
        startDate: '',
        endDate: '',
        status: 1
      }
      this.dialogVisible = true
    },
    
    // 编辑项目
    handleEdit(row) {
      this.isEdit = true
      this.projectForm = { ...row }
      this.dialogVisible = true
    },
    
    // 查看项目
    handleView(row) {
      this.$router.push(`/project/detail/${row.id}`)
    },
    
    // 删除项目
    handleDelete(row) {
      this.$confirm('确定要删除该项目吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await this.deleteProject(row.id)
          this.$message.success('删除成功')
          this.loadProjectList()
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    
    // 提交表单
    handleSubmit() {
      this.$refs.projectForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            if (this.isEdit) {
              await this.updateProject(this.projectForm)
              this.$message.success('更新成功')
            } else {
              await this.createProject(this.projectForm)
              this.$message.success('创建成功')
            }
            this.dialogVisible = false
            this.loadProjectList()
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
      this.$refs.projectForm.resetFields()
    },
    
    // 分页大小改变
    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.current = 1
      this.loadProjectList()
    },
    
    // 当前页改变
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadProjectList()
    },
    
    // 获取状态类型
    getStatusType(status) {
      const statusMap = {
        1: 'primary',
        2: 'success',
        3: 'warning',
        4: 'danger'
      }
      return statusMap[status] || 'info'
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        1: '进行中',
        2: '已完成',
        3: '已暂停',
        4: '已取消'
      }
      return statusMap[status] || status
    }
  }
}
</script>

<style lang="scss" scoped>
.project-list {
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