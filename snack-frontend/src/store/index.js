import { createStore } from 'vuex'

export default createStore({
  state: {
    user: {
      token: localStorage.getItem('token') || '',
      userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
    },
    cart: {
      count: 0
    }
  },
  
  getters: {
    isLogin: state => !!state.user.token,
    userInfo: state => state.user.userInfo,
    cartCount: state => state.cart.count
  },
  
  mutations: {
    SET_TOKEN(state, token) {
      state.user.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    
    SET_USER_INFO(state, userInfo) {
      state.user.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    
    CLEAR_USER(state) {
      state.user.token = ''
      state.user.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    },
    
    SET_CART_COUNT(state, count) {
      state.cart.count = count
    }
  },
  
  actions: {
    login({ commit }, { token, userInfo }) {
      commit('SET_TOKEN', token)
      commit('SET_USER_INFO', userInfo)
    },
    
    logout({ commit }) {
      commit('CLEAR_USER')
      commit('SET_CART_COUNT', 0)
    },
    
    updateCartCount({ commit }, count) {
      commit('SET_CART_COUNT', count)
    }
  },
  
  // 管理员模块
  modules: {
    admin: {
      namespaced: true,
      state: {
        token: localStorage.getItem('adminToken') || '',
        adminInfo: JSON.parse(localStorage.getItem('adminInfo') || '{}')
      },
      
      getters: {
        isLogin: state => !!state.token,
        adminInfo: state => state.adminInfo
      },
      
      mutations: {
        SET_TOKEN(state, token) {
          state.token = token
          if (token) {
            localStorage.setItem('adminToken', token)
          } else {
            localStorage.removeItem('adminToken')
          }
        },
        
        SET_ADMIN_INFO(state, adminInfo) {
          state.adminInfo = adminInfo
          localStorage.setItem('adminInfo', JSON.stringify(adminInfo))
        },
        
        CLEAR_ADMIN(state) {
          state.token = ''
          state.adminInfo = {}
          localStorage.removeItem('adminToken')
          localStorage.removeItem('adminInfo')
        }
      },
      
      actions: {
        login({ commit }, { token, adminInfo }) {
          commit('SET_TOKEN', token)
          commit('SET_ADMIN_INFO', adminInfo)
        },
        
        logout({ commit }) {
          commit('CLEAR_ADMIN')
        }
      }
    }
  }
})

