import request from '@/utils/request'

// 查询数据库配置信息列表
export function listDatabase(query) {
  return request({
    url: '/report/database/list',
    method: 'get',
    params: query
  })
}

// 查询数据库配置信息列表
export function listDatabaseOptions() {
  return request({
    url: '/report/database/list/options',
    method: 'get',
  })
}

// 查询数据库配置信息详细
export function getDatabase(id) {
  return request({
    url: '/report/database/' + id,
    method: 'get'
  })
}

// 新增数据库配置信息
export function addDatabase(data) {
  return request({
    url: '/report/database',
    method: 'post',
    data: data
  })
}

// 修改数据库配置信息
export function updateDatabase(data) {
  return request({
    url: '/report/database',
    method: 'put',
    data: data
  })
}

// 删除数据库配置信息
export function delDatabase(id) {
  return request({
    url: '/report/database/' + id,
    method: 'delete'
  })
}

// 获取数据库类型
export function getDataType() {
  return request({
    url: '/report/database/getDataType',
    method: 'get'
  })
}

// 获取连接池类型
export function getPoolType() {
  return request({
    url: '/report/database/getPoolType',
    method: 'get'
  })
}

// 连接测试
export function testConnection(data) {
  return request({
    url: '/report/database/testConnection',
    method: 'post',
    data
  })
}

