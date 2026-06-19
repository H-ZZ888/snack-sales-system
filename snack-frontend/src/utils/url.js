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

export function getImageUrl(path, fallback = '') {
  if (!path) return fallback
  if (path.startsWith('http')) return path
  if (path.startsWith('/uploads/')) {
    const base = import.meta.env.BASE_URL || './'
    const clean = base.endsWith('/') ? base : base + '/'
    return clean + 'uploads/' + path.replace(/^\/uploads\//, '')
  }
  if (path.startsWith('images/') || path.startsWith('./images/')) {
    const base = import.meta.env.BASE_URL || './'
    const clean = base.endsWith('/') ? base : base + '/'
    return clean + path.replace(/^\.\//, '')
  }
  return getApiUrl(path)
}

export function getUploadUrl() {
  return getApiUrl('/api/file/upload')
}
