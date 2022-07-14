<template>
  <div class="app-container">
    <el-alert
      v-if="customLayout"
      title="拖拽调整位置或拖动右下角调整大小"
      type="info"
      :closable="false"
      style="font-weight: bold"
      center
    >
    </el-alert>
    <grid-layout
      :layout.sync="layout"
      :col-num="24"
      :row-height="40"
      :is-draggable="customLayout"
      :is-resizable="customLayout"
      :vertical-compact="true"
      :use-css-transforms="true"
      @layout-updated="layoutUpdatedEvent"
    >
      <el-form
        ref="queryForm"
        :model="queryParams"
        label-width="80px"
      >
        <grid-item
          v-if="showSearch && queryColumn.length > 0"
          v-for="item in queryColumn"
          :x="layout.find(l => l.i === item.sqlField).x"
          :y="layout.find(l => l.i === item.sqlField).y"
          :w="layout.find(l => l.i === item.sqlField).w"
          :h="layout.find(l => l.i === item.sqlField).h"
          :i="layout.find(l => l.i === item.sqlField).i"
          :min-w="layout.find(l => l.i === item.sqlField).minW"
          :key="item.id"
        >
          <el-form-item :label="item.columnName" :prop="item.sqlField" :key="item.sqlField">
            <el-select
              v-if="item.htmlType === 'select' && item.dictType"
              style="width: 100%"
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
              size="small"
              style="width: 100%"
              v-else-if="item.htmlType === 'datetime' && item.queryType === 'BETWEEN'"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              @change="handleQuery"
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
        </grid-item>
        <grid-item
          v-if="layout.find(l => l.i === '#search')"
          :x="layout.find(l => l.i === '#search').x"
          :y="layout.find(l => l.i === '#search').y"
          :w="layout.find(l => l.i === '#search').w"
          :h="layout.find(l => l.i === '#search').h"
          :i="layout.find(l => l.i === '#search').i"
          :min-w="layout.find(l => l.i === '#search').minW"
        >
          <div style="display: inline-block; line-height: 36px; text-align: center; width: 100%">
            <el-button icon="el-icon-search" size="mini" type="primary" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            <el-button size="mini" @click="doLayout" v-if="!customLayout">自定义布局</el-button>
            <el-button size="mini" type="danger" plain @click="saveLayout" v-else :loading="updateLayoutLoading">保存并关闭
            </el-button>
          </div>
        </grid-item>
      </el-form>
      <grid-item
        v-if="layout.find(l => l.i === '#table')"
        :x="layout.find(l => l.i === '#table').x"
        :y="layout.find(l => l.i === '#table').y"
        :w="layout.find(l => l.i === '#table').w"
        :h="layout.find(l => l.i === '#table').h"
        :i="layout.find(l => l.i === '#table').i"
        :min-w="layout.find(l => l.i === '#table').minW"
        :min-h="layout.find(l => l.i === '#table').minH"
      >
        <!--        <el-row :gutter="10" class="mb8">-->
        <!--          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>-->
        <!--        </el-row>-->
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
      </grid-item>
    </grid-layout>
  </div>
</template>

<script>
import { getReportSqlInfo, updateLayout } from '@/api/report/reportSql'
import { getDicts } from '@/api/system/dict/data'
import { format } from '@/utils/date'
import { reportList } from '@/api/report/report'
import { GridLayout, GridItem } from 'vue-grid-layout'
import { deepClone } from '@/utils'

export default {
  name: 'generalReport',
  components: {
    GridLayout,
    GridItem
  },
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
      dataTimePicker: {},
      // 栅格的初始布局
      layout: [],
      // 调整后布局
      updateLayout: [],
      updateLayoutLoading: false,
      // 搜索框布局
      searchLayout: {},
      // 表格布局
      tableLayout: {},
      // 自定义布局开关
      customLayout: false
    }
  },
  mounted() {
    this.reportId = this.$route.query.reportId
    this.getReportColumn()
    this.getList()
  },
  methods: {
    getList() {
      if (Object.keys(this.dataTimePicker).length && Object.values(this.dataTimePicker).every(v => !!v)) {
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
      getReportSqlInfo({ id: this.reportId }).then(ret => {
        let { reportSqlColumns, layout } = ret.data
        let queryCol = []
        reportSqlColumns.forEach(item => {
          // 可查询字段
          if (item.isQuery === '1') {
            queryCol.push(item)
          }
          // 字典
          if (item.dictType) {
            console.info('获取字典：' + item.dictType)
            getDicts(item.dictType).then(ret => {
              this.$set(this.dicts, item.sqlField, ret.data)
            })
          }
        })
        this.layout = deepClone(layout)
        this.queryColumn = queryCol
        this.reportSqlColumn = reportSqlColumns
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dataTimePicker = {}
      this.resetForm('queryForm')
      let form = {}
      this.queryColumn.forEach(item => {
        form[item.sqlField] = ''
      })
      this.queryParams = form
      this.handleQuery()
    },
    /** 自定义布局 */
    doLayout() {
      this.updateLayout = []
      this.customLayout = true
    },
    /** 格式化配置了字段的字段 */
    formatDictLabel(dictObj, field) {
      let dict
      if (dictObj && field) {
        dict = dictObj.find(item => item.dictValue === field)
      }
      return dict.dictLabel || field
    },
    /** 布局更新或栅格元素的位置重新计算 */
    layoutUpdatedEvent(newLayout) {
      this.updateLayout = newLayout
    },
    saveLayout() {
      if (this.updateLayout && this.updateLayout.length) {
        this.updateLayoutLoading = true
        updateLayout(this.reportId, this.updateLayout).then(_ => {
          this.$message.success('保存成功')
          this.customLayout = false
        }).finally(_ => {
          this.updateLayoutLoading = false
        })
      } else {
        this.customLayout = false
      }
    }
  }
}
</script>

<style scoped>

</style>
