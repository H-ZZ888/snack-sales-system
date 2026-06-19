import request from '@/utils/request'

// 获取购物车列表
export function getCartList() {
  return request({
    url: '/cart/list',
    method: 'get'
  })
}

// 添加到购物车
export function addToCart(data) {
  return request({
    url: '/cart/add',
    method: 'post',
    data
  })
}

// 更新购物车数量
export function updateCart(data) {
  return request({
    url: '/cart/update',
    method: 'put',
    data
  })
}

// 删除购物车商品
export function deleteCart(id) {
  return request({
    url: `/cart/delete/${id}`,
    method: 'delete'
  })
}

// 清空购物车
export function clearCart() {
  return request({
    url: '/cart/clear',
    method: 'delete'
  })
}

