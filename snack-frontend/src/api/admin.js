import request from '@/utils/request'

// 管理员登录
export const adminLogin = (data) => {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

// 管理员注册
export const adminRegister = (data) => {
  return request({
    url: '/admin/register',
    method: 'post',
    data
  })
}

// 获取管理员信息
export const getAdminInfo = () => {
  return request({
    url: '/admin/info',
    method: 'get'
  })
}

// 更新管理员信息
export const updateAdminInfo = (data) => {
  return request({
    url: '/admin/info',
    method: 'put',
    data
  })
}

// 修改密码
export const updateAdminPassword = (data) => {
  return request({
    url: '/admin/password',
    method: 'put',
    data
  })
}

// 获取管理员列表
export const getAdminList = (params) => {
  return request({
    url: '/admin/admin/list',
    method: 'get',
    params
  })
}

// 删除管理员
export const deleteAdmin = (id) => {
  return request({
    url: `/admin/admin/delete/${id}`,
    method: 'delete'
  })
}

// ============== 商品管理 ==============

// 获取商品列表
export const getProductList = (params) => {
  return request({
    url: '/admin/product/list',
    method: 'get',
    params
  })
}

// 添加商品
export const addProduct = (data) => {
  return request({
    url: '/admin/product/add',
    method: 'post',
    data
  })
}

// 更新商品
export const updateProduct = (data) => {
  return request({
    url: '/admin/product/update',
    method: 'put',
    data
  })
}

// 删除商品
export const deleteProduct = (id) => {
  return request({
    url: `/admin/product/delete/${id}`,
    method: 'delete'
  })
}

// 批量删除商品
export const batchDeleteProducts = (ids) => {
  return request({
    url: '/admin/product/batchDelete',
    method: 'delete',
    data: ids
  })
}

// 更新商品状态
export const updateProductStatus = (id, status) => {
  return request({
    url: `/admin/product/status/${id}`,
    method: 'put',
    data: { status }
  })
}

// ============== 分类管理 ==============

// 获取分类列表
export const getCategoryList = (params) => {
  return request({
    url: '/admin/category/list',
    method: 'get',
    params
  })
}

// 添加分类
export const addCategory = (data) => {
  return request({
    url: '/admin/category/add',
    method: 'post',
    data
  })
}

// 更新分类
export const updateCategory = (data) => {
  return request({
    url: '/admin/category/update',
    method: 'put',
    data
  })
}

// 删除分类
export const deleteCategory = (id) => {
  return request({
    url: `/admin/category/delete/${id}`,
    method: 'delete'
  })
}

// 批量删除分类
export const batchDeleteCategories = (ids) => {
  return request({
    url: '/admin/category/batchDelete',
    method: 'delete',
    data: ids
  })
}

// ============== 订单管理 ==============

// 获取订单列表
export const getOrderList = (params) => {
  return request({
    url: '/admin/order/list',
    method: 'get',
    params
  })
}

// 订单发货
export const shipOrder = (id) => {
  return request({
    url: `/admin/order/ship/${id}`,
    method: 'put'
  })
}

// 删除订单
export const deleteOrder = (id) => {
  return request({
    url: `/admin/order/delete/${id}`,
    method: 'delete'
  })
}

// 批量删除订单
export const batchDeleteOrders = (ids) => {
  return request({
    url: '/admin/order/batchDelete',
    method: 'delete',
    data: ids
  })
}

// 更新订单状态
export const updateOrderStatus = (id, orderStatus) => {
  return request({
    url: `/admin/order/updateStatus/${id}`,
    method: 'put',
    params: { orderStatus }
  })
}

// ============== 用户管理 ==============

// 获取用户列表
export const getUserList = (params) => {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}

// 更新用户状态
export const updateUserStatus = (data) => {
  return request({
    url: '/admin/user/status',
    method: 'put',
    data
  })
}

// 删除用户
export const deleteUser = (id) => {
  return request({
    url: `/admin/user/delete/${id}`,
    method: 'delete'
  })
}

// 批量删除用户
export const batchDeleteUsers = (ids) => {
  return request({
    url: '/admin/user/batchDelete',
    method: 'delete',
    data: ids
  })
}

// ============== 评价管理 ==============

// 获取评价列表
export const getReviewList = (params) => {
  return request({
    url: '/admin/review/list',
    method: 'get',
    params
  })
}

// 删除评价
export const deleteReview = (id) => {
  return request({
    url: `/admin/review/delete/${id}`,
    method: 'delete'
  })
}

// 批量删除评价
export const batchDeleteReviews = (ids) => {
  return request({
    url: '/admin/review/batchDelete',
    method: 'delete',
    data: ids
  })
}

// ============== 资讯管理 ==============

// 获取资讯列表
export const getArticleList = (params) => {
  return request({
    url: '/admin/article/list',
    method: 'get',
    params
  })
}

// 添加资讯
export const addArticle = (data) => {
  return request({
    url: '/admin/article/add',
    method: 'post',
    data
  })
}

// 更新资讯
export const updateArticle = (data) => {
  return request({
    url: '/admin/article/update',
    method: 'put',
    data
  })
}

// 删除资讯
export const deleteArticle = (id) => {
  return request({
    url: `/admin/article/delete/${id}`,
    method: 'delete'
  })
}

// 批量删除资讯
export const batchDeleteArticles = (ids) => {
  return request({
    url: '/admin/article/batchDelete',
    method: 'delete',
    data: ids
  })
}

// ============== 公告管理 ==============

// 获取公告列表
export const getNoticeList = (params) => {
  return request({
    url: '/admin/notice/list',
    method: 'get',
    params
  })
}

// 添加公告
export const addNotice = (data) => {
  return request({
    url: '/admin/notice/add',
    method: 'post',
    data
  })
}

// 更新公告
export const updateNotice = (data) => {
  return request({
    url: '/admin/notice/update',
    method: 'put',
    data
  })
}

// 删除公告
export const deleteNotice = (id) => {
  return request({
    url: `/admin/notice/delete/${id}`,
    method: 'delete'
  })
}

// 批量删除公告
export const batchDeleteNotices = (ids) => {
  return request({
    url: '/admin/notice/batchDelete',
    method: 'delete',
    data: ids
  })
}

// ============== 数据统计 ==============

// 获取销售统计
export const getSalesStatistics = (params) => {
  return request({
    url: '/admin/statistics/sales',
    method: 'get',
    params
  })
}

// 获取订单统计
export const getOrderStatistics = () => {
  return request({
    url: '/admin/statistics/order',
    method: 'get'
  })
}

// 获取评价统计
export const getReviewStatistics = () => {
  return request({
    url: '/admin/statistics/review',
    method: 'get'
  })
}

// 获取用户统计
export const getUserStatistics = () => {
  return request({
    url: '/admin/statistics/users',
    method: 'get'
  })
}

