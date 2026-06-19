import request from '@/utils/request'

/**
 * 获取启用的轮播图列表（用户端）
 */
export function getBannerList() {
  return request({
    url: '/banner/list',
    method: 'get'
  })
}

/**
 * 分页查询轮播图（管理员端）
 */
export function getAdminBannerList(params) {
  return request({
    url: '/admin/banner/list',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询轮播图
 */
export function getBannerById(id) {
  return request({
    url: `/admin/banner/${id}`,
    method: 'get'
  })
}

/**
 * 添加轮播图
 */
export function addBanner(data) {
  return request({
    url: '/admin/banner/add',
    method: 'post',
    data
  })
}

/**
 * 更新轮播图
 */
export function updateBanner(data) {
  return request({
    url: '/admin/banner/update',
    method: 'put',
    data
  })
}

/**
 * 删除轮播图
 */
export function deleteBanner(id) {
  return request({
    url: `/admin/banner/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除轮播图
 */
export function deleteBannerBatch(ids) {
  return request({
    url: '/admin/banner/deleteBatch',
    method: 'delete',
    data: ids
  })
}

/**
 * 更新轮播图状态
 */
export function updateBannerStatus(id, status) {
  return request({
    url: '/admin/banner/updateStatus',
    method: 'put',
    params: { id, status }
  })
}
