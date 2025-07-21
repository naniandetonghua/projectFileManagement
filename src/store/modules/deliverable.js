import { getDeliverableList, getDeliverableById, createDeliverable, updateDeliverable, deleteDeliverable, getDeliverableStats } from '@/api/deliverable'

const state = {
  deliverableList: [],
  currentDeliverable: null,
  loading: false,
  pagination: {
    current: 1,
    size: 10,
    total: 0
  },
  stats: {}
}

const mutations = {
  SET_DELIVERABLE_LIST(state, list) {
    state.deliverableList = list
  },
  SET_CURRENT_DELIVERABLE(state, deliverable) {
    state.currentDeliverable = deliverable
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
  // 获取交付物列表
  async getDeliverableList({ commit }, params) {
    commit('SET_LOADING', true)
    try {
      const response = await getDeliverableList(params)
      commit('SET_DELIVERABLE_LIST', response.content)
      commit('SET_PAGINATION', {
        current: response.currentPage + 1,
        total: response.totalElements
      })
      return response
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 获取交付物详情
  async getDeliverableById({ commit }, id) {
    commit('SET_LOADING', true)
    try {
      const response = await getDeliverableById(id)
      commit('SET_CURRENT_DELIVERABLE', response)
      return response
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 创建交付物
  async createDeliverable({ dispatch }, deliverableData) {
    const response = await createDeliverable(deliverableData)
    // 刷新列表
    dispatch('getDeliverableList', { page: 0, size: 10 })
    return response
  },

  // 更新交付物
  async updateDeliverable({ dispatch }, deliverableData) {
    const response = await updateDeliverable(deliverableData)
    // 刷新列表
    dispatch('getDeliverableList', { page: 0, size: 10 })
    return response
  },

  // 删除交付物
  async deleteDeliverable({ dispatch }, id) {
    await deleteDeliverable(id)
    // 刷新列表
    dispatch('getDeliverableList', { page: 0, size: 10 })
  },

  // 获取交付物统计
  async getDeliverableStats({ commit }) {
    const response = await getDeliverableStats()
    commit('SET_STATS', response)
    return response
  }
}

const getters = {
  deliverableList: state => state.deliverableList,
  currentDeliverable: state => state.currentDeliverable,
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