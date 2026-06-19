import request from '@/utils/request'

// 获取收藏列表
export function getFavoriteList(params) {
  return request({
    url: '/favorite/list',
    method: 'get',
    params
  })
}

// 添加收藏
export function addFavorite(productId) {
  return request({
    url: '/favorite/add',
    method: 'post',
    data: { productId }
  })
}

// 取消收藏
export function deleteFavorite(id) {
  return request({
    url: `/favorite/delete/${id}`,
    method: 'delete'
  })
}

// 取消收藏（通过商品ID）
export function removeFavorite(productId) {
  return request({
    url: `/favorite/remove/${productId}`,
    method: 'delete'
  })
}

// 检查是否已收藏
export function checkFavorite(productId) {
  return request({
    url: `/favorite/check/${productId}`,
    method: 'get'
  })
}

