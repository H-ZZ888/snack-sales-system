<template>
  <div class="admin-dashboard">
    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);">
              <el-icon :size="32"><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalOrders || 0 }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4ECDC4 0%, #44A08D 100%);">
              <el-icon :size="32"><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalProducts || 0 }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #A8EDEA 0%, #6DD5ED 100%);">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #FFA07A 0%, #FF7F50 100%);">
              <el-icon :size="32"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ statistics.totalSales || 0 }}</div>
              <div class="stat-label">销售总额</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><TrendCharts /></el-icon>
                销售额趋势图
              </span>
              <el-radio-group v-model="dateRange" size="small" @change="loadStatistics">
                <el-radio-button label="week">最近7天</el-radio-button>
                <el-radio-button label="month">最近30天</el-radio-button>
                <el-radio-button label="all">全部</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container" ref="salesChartRef"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Histogram /></el-icon>
                商品销量排行榜
              </span>
            </div>
          </template>
          <div class="chart-container" ref="productRankChartRef"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><PieChart /></el-icon>
                商品分类销售占比
              </span>
            </div>
          </template>
          <div class="chart-container" ref="categoryChartRef"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  ShoppingCart,
  Goods,
  User,
  Money,
  TrendCharts,
  PieChart,
  Histogram
} from '@element-plus/icons-vue'
import {getSalesStatistics, getUserStatistics} from '@/api/admin'
import * as echarts from 'echarts'

const statistics = ref({
  totalOrders: 0,
  totalProducts: 0,
  totalUsers: 0,
  totalSales: 0
})

const dateRange = ref('month')
const salesChartRef = ref(null)
const productRankChartRef = ref(null)
const categoryChartRef = ref(null)

// 计算日期范围
const getDateRange = () => {
  const end = new Date()
  const start = new Date()
  
  if (dateRange.value === 'week') {
    start.setDate(end.getDate() - 7)
  } else if (dateRange.value === 'month') {
    start.setDate(end.getDate() - 30)
  } else {
    return { startDate: null, endDate: null }
  }
  
  const formatDate = (date) => {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }
  
  return {
    startDate: formatDate(start),
    endDate: formatDate(end)
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const { startDate, endDate } = getDateRange()
    const params = {}
    if (startDate && endDate) {
      params.startDate = startDate
      params.endDate = endDate
    }
    
    const [salesRes, userRes] = await Promise.all([
      getSalesStatistics(params),
      getUserStatistics()
    ])
    
    // 更新统计数据
    statistics.value = {
      totalOrders: salesRes.data.totalOrders || 0,
      totalProducts: salesRes.data.totalProducts || 0,
      totalUsers: userRes.data.totalUsers || 0,
      totalSales: salesRes.data.totalSales || 0
    }
    
    // 初始化图表
    await nextTick()
    initSalesChart(salesRes.data)
    initProductRankChart(salesRes.data)
    initCategoryChart(salesRes.data)
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

// 初始化销售趋势图表
const initSalesChart = (data) => {
  const chart = echarts.init(salesChartRef.value)
  const dates = Object.keys(data.salesByDate || {}).sort()
  const sales = dates.map(date => data.salesByDate[date])
  
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#FF7F50',
      borderWidth: 2,
      textStyle: {
        color: '#333',
        fontSize: 14
      },
      padding: [10, 15],
      formatter: (params) => {
        return `<div style="font-weight: 600;">${params[0].name}</div>
                <div style="margin-top: 5px;">销售额: <span style="color: #FF7F50; font-weight: bold;">¥${params[0].value}</span></div>`
      }
    },
    xAxis: {
      type: 'category',
      data: dates,
      boundaryGap: false,
      axisLabel: {
        rotate: 45,
        color: '#666',
        fontSize: 12
      },
      axisLine: {
        lineStyle: {
          color: '#e0e0e0'
        }
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      name: '销售额(元)',
      nameTextStyle: {
        color: '#666',
        fontSize: 13,
        padding: [0, 0, 0, 0]
      },
      axisLabel: {
        color: '#666',
        fontSize: 12,
        formatter: '¥{value}'
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0',
          type: 'dashed'
        }
      },
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      }
    },
    series: [{
      data: sales,
      type: 'line',
      smooth: 0.4,
      smoothMonotone: 'x',
      symbol: 'circle',
      symbolSize: 8,
      showSymbol: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
          offset: 0,
          color: 'rgba(255, 127, 80, 0.35)'
        }, {
          offset: 0.5,
          color: 'rgba(255, 127, 80, 0.15)'
        }, {
          offset: 1,
          color: 'rgba(255, 127, 80, 0.02)'
        }])
      },
      itemStyle: {
        color: '#FF7F50',
        borderColor: '#fff',
        borderWidth: 3,
        shadowBlur: 5,
        shadowColor: 'rgba(255, 127, 80, 0.3)'
      },
      lineStyle: {
        width: 3,
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{
          offset: 0,
          color: '#FF6B6B'
        }, {
          offset: 1,
          color: '#FFA07A'
        }]),
        shadowBlur: 8,
        shadowColor: 'rgba(255, 127, 80, 0.3)',
        shadowOffsetY: 3
      },
      emphasis: {
        focus: 'series',
        itemStyle: {
          color: '#FF6B6B',
          borderColor: '#fff',
          borderWidth: 3,
          shadowBlur: 10,
          shadowColor: 'rgba(255, 107, 107, 0.5)',
          scale: 1.3
        }
      }
    }],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '12%',
      containLabel: true
    }
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

// 初始化商品销量排行图表
const initProductRankChart = (data) => {
  const chart = echarts.init(productRankChartRef.value)
  
  // 获取商品销量数据并排序
  const productRanking = data.productRanking || []
  const products = productRanking.map(item => item.productName)
  const quantities = productRanking.map(item => item.quantity)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#ddd',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      },
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params) => {
        return `${params[0].name}<br/>销量: ${params[0].value}件`
      }
    },
    xAxis: {
      type: 'value',
      name: '销量(件)',
      nameTextStyle: {
        color: '#666'
      },
      axisLabel: {
        color: '#666'
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0'
        }
      }
    },
    yAxis: {
      type: 'category',
      data: products,
      axisLabel: {
        color: '#666'
      },
      axisLine: {
        lineStyle: {
          color: '#ddd'
        }
      }
    },
    series: [{
      data: quantities,
      type: 'bar',
      barWidth: '60%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{
          offset: 0,
          color: '#4ECDC4'
        }, {
          offset: 1,
          color: '#44A08D'
        }]),
        borderRadius: [0, 5, 5, 0]
      },
      label: {
        show: true,
        position: 'right',
        color: '#666',
        formatter: '{c}件'
      }
    }],
    grid: {
      left: '3%',
      right: '10%',
      bottom: '3%',
      top: '5%',
      containLabel: true
    }
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

// 初始化商品分类销售占比图表
const initCategoryChart = (data) => {
  const chart = echarts.init(categoryChartRef.value)
  const categoryData = Object.entries(data.amountByCategory || {}).map(([name, value]) => ({
    name,
    value
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#ddd',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      },
      formatter: (params) => {
        return `${params.name}<br/>销售额: ¥${params.value}<br/>占比: ${params.percent}%`
      }
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: {
        color: '#666'
      }
    },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['40%', '50%'],
      data: categoryData,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.3)'
        }
      },
      label: {
        formatter: '{b}\n{d}%',
        color: '#666'
      },
      labelLine: {
        lineStyle: {
          color: '#ddd'
        }
      }
    }],
    color: ['#FF6B6B', '#4ECDC4', '#45B7D1', '#FFA07A', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E2']
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 16px;
  overflow: hidden;
  border: none;
  transition: all 0.3s ease;
}

.stat-card :deep(.el-card__body) {
  padding: 24px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 8px;
  font-family: 'Arial', sans-serif;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 16px;
  border: none;
  transition: all 0.3s ease;
}

.chart-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-bottom: 2px solid #e8ecf1;
  padding: 20px 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-title .el-icon {
  font-size: 22px;
  color: #FF7F50;
}

.chart-container {
  width: 100%;
  height: 350px;
  padding: 10px;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
}

.chart-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

:deep(.el-radio-button__inner) {
  border-radius: 4px;
  padding: 8px 15px;
}

:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: #FF7F50;
  border-color: #FF7F50;
}
</style>

