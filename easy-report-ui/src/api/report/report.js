import request from '@/utils/request'

// 查询数据库配置信息列表
export function reportList(reportId, query) {
  return request({
    url: '/report/list/' + reportId,
    method: 'get',
    params: query
  })
}
