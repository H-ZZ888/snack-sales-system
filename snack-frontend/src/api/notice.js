import request from '@/utils/request'

// 获取公告列表
export function getNoticeList(params) {
  return request({
    url: '/notice/list',
    method: 'get',
    params
  })
}

// 获取公告详情
export function getNoticeDetail(id) {
  return request({
    url: `/notice/detail/${id}`,
    method: 'get'
  })
}

