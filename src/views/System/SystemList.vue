<template>
  <div class="system-list">
    <div class="page-header">
      <h2>系统管理</h2>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>
        新增系统
      </el-button>
    </div>

    <!-- 系统列表 -->
    <el-card>
      <el-table
        :data="systemList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="systemName" label="系统名称" />
        <el-table-column prop="systemCode" label="系统编码" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="showEditDialog(scope.row)">
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="系统名称" prop="systemName">
          <el-input v-model="form.systemName" placeholder="请输入系统名称" />
        </el-form-item>
        <el-form-item label="系统编码" prop="systemCode">
          <el-input v-model="form.systemCode" placeholder="请输入系统编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入系统描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSystemList, createSystem, updateSystem, deleteSystem } from '@/api/system'
import { formatDate } from '@/utils/date'

export default {
  name: 'SystemList',
  setup() {
    const loading = ref(false)
    const systemList = ref([])
    const dialogVisible = ref(false)
    const submitting = ref(false)
    const isEdit = ref(false)
    const formRef = ref()

    const form = reactive({
      id: null,
      systemName: '',
      systemCode: '',
      description: '',
      status: 1
    })

    const rules = {
      systemName: [
        { required: true, message: '请输入系统名称', trigger: 'blur' }
      ],
      systemCode: [
        { required: true, message: '请输入系统编码', trigger: 'blur' }
      ]
    }

    const dialogTitle = computed(() => {
      return isEdit.value ? '编辑系统' : '新增系统'
    })

    // 加载系统列表
    const loadSystemList = async () => {
      loading.value = true
      try {
        const response = await getSystemList()
        systemList.value = response.data || []
      } catch (error) {
        ElMessage.error('加载系统列表失败')
        console.error('加载系统列表失败:', error)
      } finally {
        loading.value = false
      }
    }

    // 显示新增对话框
    const showCreateDialog = () => {
      isEdit.value = false
      dialogVisible.value = true
    }

    // 显示编辑对话框
    const showEditDialog = (row) => {
      isEdit.value = true
      Object.assign(form, row)
      dialogVisible.value = true
    }

    // 重置表单
    const resetForm = () => {
      formRef.value?.resetFields()
      Object.assign(form, {
        id: null,
        systemName: '',
        systemCode: '',
        description: '',
        status: 1
      })
    }

    // 提交表单
    const handleSubmit = async () => {
      try {
        await formRef.value.validate()
        submitting.value = true

        if (isEdit.value) {
          await updateSystem(form)
          ElMessage.success('系统更新成功')
        } else {
          await createSystem(form)
          ElMessage.success('系统创建成功')
        }

        dialogVisible.value = false
        loadSystemList()
      } catch (error) {
        ElMessage.error(isEdit.value ? '系统更新失败' : '系统创建失败')
        console.error('提交失败:', error)
      } finally {
        submitting.value = false
      }
    }

    // 删除系统
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除系统"${row.systemName}"吗？`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        await deleteSystem(row.id)
        ElMessage.success('系统删除成功')
        loadSystemList()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('系统删除失败')
          console.error('删除失败:', error)
        }
      }
    }

    onMounted(() => {
      loadSystemList()
    })

    return {
      loading,
      systemList,
      dialogVisible,
      submitting,
      isEdit,
      formRef,
      form,
      rules,
      dialogTitle,
      formatDate,
      showCreateDialog,
      showEditDialog,
      resetForm,
      handleSubmit,
      handleDelete
    }
  }
}
</script>

<style lang="scss" scoped>
.system-list {
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

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}
</style> 