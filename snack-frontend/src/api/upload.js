import axios from 'axios'
import { ElMessage } from 'element-plus'

// 上传文件
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return axios({
    url: '/api/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.message || '上传失败')
      return Promise.reject(new Error(res.message || '上传失败'))
    }
  }).catch(error => {
    console.error('上传错误:', error)
    ElMessage.error('上传失败')
    return Promise.reject(error)
  })
}

