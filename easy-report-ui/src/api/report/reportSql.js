import request from '@/utils/request'

// 查询数据库配置信息列表
export function listReportSql(query) {
  return request({
    url: '/report/sql/list',
    method: 'get',
    params: query
  })
}

// 解析sql字段
export function parseSQLText(data) {
  return request({
    url: '/report/sql/parseSQLText',
    method: 'post',
    data: data
  })
}

// 新增报表查询sql
export function addReportSql(data) {
  return request({
    url: '/report/sql',
    method: 'post',
    data: data
  })
}

// 修改报表查询sql
export function updateReportSql(data) {
  return request({
    url: '/report/sql',
    method: 'put',
    data: data
  })
}

// 修改报表查询sql
export function delReportSql(id) {
  return request({
    url: '/report/sql/'+ id,
    method: 'delete',
  })
}

// 获取sql字段
export function getReportSqlInfo(query) {
  return request({
    url: '/report/sql/getReportSqlInfo',
    method: 'get',
    params: query
  })
}

// 解析sql字段
export function updateLayout(reportId, layout) {
  return request({
    url: '/report/sql/updateLayout/' + reportId,
    method: 'post',
    data: layout
  })
}
