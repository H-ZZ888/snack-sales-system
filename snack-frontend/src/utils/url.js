/**
 * 获取完整的 API 地址
 * @param {string} path - 接口路径，如 '/api/uploads/xxx' 或 '/uploads/xxx'
 * @returns {string} 完整 URL
 */
export function getApiUrl(path) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  const base = import.meta.env.VITE_API_BASE || '/api'
  const baseUrl = base.startsWith('http') ? base : (typeof window !== 'undefined' ? window.location.origin + base : base)
  if (path.startsWith('/api')) {
    const clean = baseUrl.replace(/\/api$/, '')
    return clean + path
  }
  return baseUrl + path
}

/**
 * 获取图片/文件完整 URL
 * @param {string} path - 图片路径
 * @param {string} fallback - 路径为空时的回退图片，默认空字符串
 * @returns {string} 完整 URL
 */
export function getImageUrl(path, fallback = '') {
  if (!path) return fallback
  if (path.startsWith('http')) return path
  return getApiUrl(path)
}

/**
 * 获取文件上传接口地址
 * @returns {string}
 */
export function getUploadUrl() {
  return getApiUrl('/api/file/upload')
}
