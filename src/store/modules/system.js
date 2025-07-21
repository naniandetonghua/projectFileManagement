import { systemApi } from '@/api/system'

const state = {
  systems: [],
  loading: false
}

const mutations = {
  SET_SYSTEMS(state, systems) {
    state.systems = systems
  },
  SET_LOADING(state, loading) {
    state.loading = loading
  },
  ADD_SYSTEM(state, system) {
    state.systems.push(system)
  },
  UPDATE_SYSTEM(state, updatedSystem) {
    const index = state.systems.findIndex(s => s.id === updatedSystem.id)
    if (index !== -1) {
      state.systems.splice(index, 1, updatedSystem)
    }
  },
  DELETE_SYSTEM(state, systemId) {
    state.systems = state.systems.filter(s => s.id !== systemId)
  }
}

const actions = {
  async fetchSystems({ commit }) {
    commit('SET_LOADING', true)
    try {
      const response = await systemApi.getSystems()
      commit('SET_SYSTEMS', response.data)
    } catch (error) {
      console.error('获取系统列表失败:', error)
    } finally {
      commit('SET_LOADING', false)
    }
  },
  
  async createSystem({ commit }, systemData) {
    try {
      const response = await systemApi.createSystem(systemData)
      commit('ADD_SYSTEM', response.data)
      return response.data
    } catch (error) {
      console.error('创建系统失败:', error)
      throw error
    }
  },
  
  async updateSystem({ commit }, { id, data }) {
    try {
      const response = await systemApi.updateSystem(id, data)
      commit('UPDATE_SYSTEM', response.data)
      return response.data
    } catch (error) {
      console.error('更新系统失败:', error)
      throw error
    }
  },
  
  async deleteSystem({ commit }, systemId) {
    try {
      await systemApi.deleteSystem(systemId)
      commit('DELETE_SYSTEM', systemId)
    } catch (error) {
      console.error('删除系统失败:', error)
      throw error
    }
  }
}

const getters = {
  getSystemById: (state) => (id) => {
    return state.systems.find(s => s.id === id)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
} 