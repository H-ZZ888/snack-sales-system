import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

const routes = [
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/Products.vue'),
        meta: { title: '商品列表' }
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/ProductDetail.vue'),
        meta: { title: '商品详情' }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/Cart.vue'),
        meta: { title: '购物车', requireAuth: true }
      },
      {
        path: 'order/confirm',
        name: 'OrderConfirm',
        component: () => import('@/views/OrderConfirm.vue'),
        meta: { title: '确认订单', requireAuth: true }
      },
      {
        path: 'articles',
        name: 'Articles',
        component: () => import('@/views/Articles.vue'),
        meta: { title: '资讯列表' }
      },
      {
        path: 'article/:id',
        name: 'ArticleDetail',
        component: () => import('@/views/ArticleDetail.vue'),
        meta: { title: '资讯详情' }
      },
      {
        path: 'notices',
        name: 'Notices',
        component: () => import('@/views/Notices.vue'),
        meta: { title: '系统公告' }
      },
      {
        path: 'user',
        component: () => import('@/views/user/UserLayout.vue'),
        meta: { requireAuth: true },
        children: [
          {
            path: 'center',
            name: 'UserCenter',
            component: () => import('@/views/user/UserCenter.vue'),
            meta: { title: '个人中心', requireAuth: true }
          },
          {
            path: 'orders',
            name: 'UserOrders',
            component: () => import('@/views/user/UserOrders.vue'),
            meta: { title: '我的订单', requireAuth: true }
          },
          {
            path: 'favorites',
            name: 'UserFavorites',
            component: () => import('@/views/user/UserFavorites.vue'),
            meta: { title: '我的收藏', requireAuth: true }
          },
          {
            path: 'addresses',
            name: 'UserAddresses',
            component: () => import('@/views/user/UserAddresses.vue'),
            meta: { title: '收货地址', requireAuth: true }
          }
        ]
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '用户登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '用户注册' }
  },
  // 管理员端路由
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/AdminLogin.vue'),
    meta: { title: '管理员登录' }
  },
  {
    path: '/admin/register',
    name: 'AdminRegister',
    component: () => import('@/views/admin/AdminRegister.vue'),
    meta: { title: '管理员注册' }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requireAdminAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/AdminDashboard.vue'),
        meta: { title: '数据统计', requireAdminAuth: true }
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/AdminProducts.vue'),
        meta: { title: '商品管理', requireAdminAuth: true }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/AdminCategories.vue'),
        meta: { title: '分类管理', requireAdminAuth: true }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/AdminOrders.vue'),
        meta: { title: '订单管理', requireAdminAuth: true }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/AdminUsers.vue'),
        meta: { title: '用户管理', requireAdminAuth: true }
      },
      {
        path: 'reviews',
        name: 'AdminReviews',
        component: () => import('@/views/admin/AdminReviews.vue'),
        meta: { title: '评价管理', requireAdminAuth: true }
      },
      {
        path: 'articles',
        name: 'AdminArticles',
        component: () => import('@/views/admin/AdminArticles.vue'),
        meta: { title: '资讯管理', requireAdminAuth: true }
      },
      {
        path: 'notices',
        name: 'AdminNotices',
        component: () => import('@/views/admin/AdminNotices.vue'),
        meta: { title: '公告管理', requireAdminAuth: true }
      },
      {
        path: 'banners',
        name: 'AdminBanners',
        component: () => import('@/views/admin/BannerManage.vue'),
        meta: { title: '轮播图管理', requireAdminAuth: true }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/admin/AdminProfile.vue'),
        meta: { title: '个人中心', requireAdminAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 零食销售管理系统` : '零食销售管理系统'
  
  // 判断是否需要管理员登录
  if (to.meta.requireAdminAuth) {
    if (store.state.admin.token) {
      next()
    } else {
      next({
        path: '/admin/login',
        query: { redirect: to.fullPath }
      })
    }
  }
  // 判断是否需要用户登录
  else if (to.meta.requireAuth) {
    if (store.state.user.token) {
      next()
    } else {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
  } else {
    next()
  }
})

export default router

