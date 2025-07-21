import { createStore } from 'vuex'
import system from './modules/system'
import project from './modules/project'
import deliverable from './modules/deliverable'

export default createStore({
  modules: {
    system,
    project,
    deliverable
  }
}) 