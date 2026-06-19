import request from '@/utils/request'

// 获取商品列表
export function getProductList(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

// 获取商品详情
export function getProductDetail(id) {
  return request({
    url: `/product/detail/${id}`,
    method: 'get'
  })
}

// 按分类获取商品
export function getProductByCategory(categoryId, params) {
  return request({
    url: `/product/category/${categoryId}`,
    method: 'get',
    params
  })
}

// 搜索商品
export function searchProduct(params) {
  return request({
    url: '/product/search',
    method: 'get',
    params
  })
}

