import { getProjectList, getProjectById, createProject, updateProject, deleteProject, getProjectStats } from '@/api/project'

const state = {
  projectList: [],
  currentProject: null,
  loading: false,
  pagination: {
    current: 1,
    size: 10,
    total: 0
  },
  stats: {}
}

const mutations = {
  SET_PROJECT_LIST(state, list) {
    state.projectList = list
  },
  SET_CURRENT_PROJECT(state, project) {
    state.currentProject = project
  },
  SET_LOADING(state, loading) {
    state.loading = loading
  },
  SET_PAGINATION(state, pagination) {
    state.pagination = { ...state.pagination, ...pagination }
  },
  SET_STATS(state, stats) {
    state.stats = stats
  }
}

const actions = {
  // 获取项目列表
  async getProjectList({ commit }, params) {
    commit('SET_LOADING', true)
    try {
      const response = await getProjectList(params)
      commit('SET_PROJECT_LIST', response.content)
      commit('SET_PAGINATION', {
        current: response.currentPage + 1,
        total: response.totalElements
      })
      return response
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 获取项目详情
  async getProjectById({ commit }, id) {
    commit('SET_LOADING', true)
    try {
      const response = await getProjectById(id)
      commit('SET_CURRENT_PROJECT', response)
      return response
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 创建项目
  async createProject({ dispatch }, projectData) {
    const response = await createProject(projectData)
    // 刷新列表
    dispatch('getProjectList', { page: 0, size: 10 })
    return response
  },

  // 更新项目
  async updateProject({ dispatch }, projectData) {
    const response = await updateProject(projectData)
    // 刷新列表
    dispatch('getProjectList', { page: 0, size: 10 })
    return response
  },

  // 删除项目
  async deleteProject({ dispatch }, id) {
    await deleteProject(id)
    // 刷新列表
    dispatch('getProjectList', { page: 0, size: 10 })
  },

  // 获取项目统计
  async getProjectStats({ commit }) {
    const response = await getProjectStats()
    commit('SET_STATS', response)
    return response
  }
}

const getters = {
  projectList: state => state.projectList,
  currentProject: state => state.currentProject,
  loading: state => state.loading,
  pagination: state => state.pagination,
  stats: state => state.stats
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
} 