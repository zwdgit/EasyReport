<template>
  <div class="app-container">
    <el-form v-if="showSearch && queryColumn.length > 0" ref="queryForm" :inline="true" :model="queryParams"
             label-width="80px"
    >
      <el-form-item v-for="item in queryColumn" :label="item.columnName" :prop="item.sqlField" :key="item.sqlField">
        <el-select
          v-if="item.htmlType === 'select' && item.dictType"
          v-model="queryParams[item.sqlField]"
          clearable
          :placeholder="'请选择' + item.columnName"
          size="small"
          @change="handleQuery"
        >
          <el-option v-for="item in dicts[item.sqlField]" :key="item.dictValue" :label="item.dictLabel"
                     :value="item.dictValue"
          ></el-option>
        </el-select>
        <el-date-picker
          v-model="dataTimePicker[item.sqlField]"
          v-else-if="item.htmlType === 'datetime' && item.queryType === 'BETWEEN'"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
        >
        </el-date-picker>
        <el-input
          v-else
          v-model="queryParams[item.sqlField]"
          clearable
          :placeholder="'请输入' + item.columnName"
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" size="mini" type="primary" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="reportList">
      <el-table-column v-for="item in reportSqlColumn" v-if="item.isList === '1'" align="center"
                       :label="item.columnName" :key="item.sqlField" min-width="120"
      >
        <template slot-scope="scope">
          <span v-if="dicts[item.sqlField]">
              {{ formatDictLabel(dicts[item.sqlField], scope.row[item.sqlField]) }}
          </span>
          <span v-else>
              {{ scope.row[item.sqlField] }}
          </span>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :limit.sync="queryParams.pageSize"
      :page.sync="queryParams.pageNum"
      :total="total"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { getReportSqlColumn } from '@/api/report/reportSql'
import { getDicts } from '@/api/system/dict/data'
import { format } from '@/utils/date'
import { reportList } from '@/api/report/report'

export default {
  name: 'generalReport',
  data() {
    return {
      // 遮罩层
      loading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      reportId: '',
      // 报表列
      reportSqlColumn: [],
      // 查询列
      queryColumn: [],
      // 查询参数
      queryParams: {},
      // 下拉字典集合
      dicts: {},
      // 报表数据集合
      reportList: [],
      // 日期选择控件对象
      dataTimePicker: {}
    }
  },
  mounted() {
    this.reportId = this.$route.query.reportId
    this.getReportColumn()
    this.getList()
  },
  methods: {
    getList() {
      if (Object.keys(this.dataTimePicker).length) {
        Object.keys(this.dataTimePicker).forEach(key => {
          this.queryParams[key] = format(this.dataTimePicker[key][0])
          this.queryParams[key + 'SecValue'] = format(this.dataTimePicker[key][1])
        })
      }
      this.loading = true
      reportList(this.reportId, this.queryParams).then(ret => {
        this.reportList = ret.rows
        this.total = ret.total
      }).finally(() => this.loading = false)
    },
    // 获取列信息
    getReportColumn() {
      getReportSqlColumn({ id: this.reportId }).then(ret => {
        this.reportSqlColumn = ret.data
        let isQuery = []
        this.reportSqlColumn.forEach(item => {
          // 可查询字段
          if (item.isQuery === '1') {
            isQuery.push(item)
          }
          // 字典
          if (item.dictType) {
            console.info('获取字典：' + item.dictType)
            getDicts(item.dictType).then(ret => {
              this.$set(this.dicts, item.sqlField, ret.data)
            })
          }
        })
        this.queryColumn = isQuery
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 格式化配置了字段的字段 */
    formatDictLabel(dictObj, field) {
      let dict
      if (dictObj && field) {
        dict = dictObj.find(item => item.dictValue === field)
      }
      return dict.dictLabel || field
    }
  }
}
</script>

<style scoped>

</style>
