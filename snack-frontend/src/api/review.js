import request from '@/utils/request'

// 添加评价
export function addReview(data) {
  return request({
    url: '/review/add',
    method: 'post',
    data
  })
}

// 获取商品评价列表
export function getProductReviews(productId, params) {
  return request({
    url: `/review/product/${productId}`,
    method: 'get',
    params
  })
}

// 删除用户自己的评价
export function deleteUserReview(reviewId) {
  return request({
    url: `/review/delete/${reviewId}`,
    method: 'delete'
  })
}
