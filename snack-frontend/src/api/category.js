import request from '@/utils/request'

// 获取所有分类
export function getCategoryList() {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

