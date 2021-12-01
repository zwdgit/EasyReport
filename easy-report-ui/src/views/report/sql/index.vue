<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :inline="true" :model="queryParams" label-width="120px">
      <el-form-item label="SQL名称" prop="sqlName">
        <el-input
          v-model="queryParams.sqlName"
          clearable
          placeholder="请输入SQL名称"
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目标数据库" prop="databaseId">
        <el-select v-model="queryParams.databaseId" clearable placeholder="请选择目标数据库" size="small" @change="handleQuery">
          <el-option v-for="item in databaseList" :key="item.id" :label="item.name" :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" size="mini" type="primary" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          icon="el-icon-plus"
          size="mini"
          type="primary"
          @click="handleAdd"
        >新增
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="reportSqlList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" min-width="50"/>
      <el-table-column align="center" label="名称" min-width="80" prop="sqlName"/>
      <el-table-column align="center" label="所属数据源" min-width="80" prop="databaseId" :formatter="formatDatebase"/>
      <el-table-column align="center" label="查询sql" min-width="120" prop="querySql"/>
      <el-table-column align="center" label="创建人" min-width="80" prop="createBy"/>
      <el-table-column align="center" label="报表链接（点击复制）" min-width="120" show-overflow-tooltip>
        <template slot-scope="scope">
          <span @click="() => copyLink(getReportLink(scope.row.id))">{{ getReportLink(scope.row.id) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" min-width="120" prop="createTime"/>
      <el-table-column align="center" label="最后修改人" min-width="80" prop="updateBy"/>
      <el-table-column align="center" label="最后修改时间" min-width="120" prop="updateTime"/>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" min-width="150">
        <template slot-scope="scope">
          <el-button
            icon="el-icon-tickets"
            size="mini"
            type="text"
            @click="() => handleOpenReport(getReportLink(scope.row.id))"
          >报表预览
          </el-button>
          <el-button
            icon="el-icon-s-operation"
            size="mini"
            type="text"
            @click="handleAddMenu(scope.row)"
          >添加菜单
          </el-button>
          <el-button
            icon="el-icon-edit"
            size="mini"
            type="text"
            @click="handleUpdate(scope.row)"
          >修改
          </el-button>
          <el-button
            icon="el-icon-delete"
            size="mini"
            type="text"
            @click="handleDelete(scope.row)"
          >删除
          </el-button>
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

    <!-- 添加或修改数据库配置信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="70%">
      <el-form ref="sqlForm" :model="form" label-width="120px" :rules="rules">
        <el-row>
          <el-col :span="6">
            <el-form-item label="名称" prop="sqlName">
              <el-input v-model="form.sqlName" size="small" placeholder="请输入名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="目标数据库" prop="databaseId">
              <el-select v-model="form.databaseId" clearable placeholder="请选择目标数据库" size="small">
                <el-option v-for="item in databaseList" :key="item.id" :label="item.name" :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4" style="text-align: center">
            <el-button size="small" @click="open = false" style="width: 80px">取 消</el-button>
            <el-button size="small" @click="submitForm" type="primary" style="width: 80px" :loading="saveBtnLoading">保
              存
            </el-button>
          </el-col>
        </el-row>
        <el-form-item label="报表SQL" prop="querySql">
          <el-collapse v-model="activeNames">
            <el-collapse-item title="编辑" name="1">
              <code-mirror ref="codemirror" v-model="form.querySql" :height="'300px'"
                           @changeTextarea="onTextareaChange($event)"
              ></code-mirror>
              <el-row style="background-color: #f7f7f7;padding-left: 30px">
                <el-button size="mini" icon="el-icon-menu" @click="formatSql">格式化Sql</el-button>
                <el-button size="mini" icon="el-icon-finished" @click="resolveSql">自动解析</el-button>
                <el-button size="mini" icon="el-icon-document-add" @click="addColumn">新增行</el-button>
                <el-button size="mini" icon="el-icon-delete" @click="delColumn">删除行</el-button>
              </el-row>
            </el-collapse-item>
          </el-collapse>
          <el-table :data="reportSqlColumn" @selection-change="handleColumnSelectionChange"
                    v-loading="sqlColumnTableLoading"
          >
            <el-table-column align="center" type="selection" width="55"/>
            <el-table-column align="center" label="查询字段" prop="sqlField">
              <template slot-scope="scope">
                <el-input size="small" v-model="scope.row.sqlField" placeholder="请输入查询字段"></el-input>
              </template>
            </el-table-column>
            <el-table-column align="center" label="别名" prop="columnName">
              <template slot-scope="scope">
                <el-input size="small" v-model="scope.row.columnName" placeholder="请输入字段别名"></el-input>
              </template>
            </el-table-column>
            <el-table-column align="center" label="是否列表字段" prop="isList">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.isList" true-label="1" false-label="0"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column align="center" label="是否导出字段" prop="isExport">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.isExport" true-label="1" false-label="0"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column align="center" label="是否查询字段" prop="isQuery">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.isQuery" true-label="1" false-label="0"
                             @change="(val) => onIsQueryChange(val, scope.row)"
                ></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column align="center" label="查询方式" prop="queryType">
              <template slot-scope="scope">
                <el-select v-model="scope.row.queryType" size="small" clearable
                           @change="(val) => onQueryTypeChange(val, scope.row)"
                >
                  <el-option label="=" value="EQ"/>
                  <el-option label="LIKE" value="LIKE"/>
                  <el-option label="BETWEEN" value="BETWEEN"/>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column align="center" label="显示类型" prop="htmlType">
              <template slot-scope="scope">
                <el-select v-model="scope.row.htmlType" size="small" clearable>
                  <el-option label="文本框" value="input"/>
                  <el-option label="下拉框" value="select"/>
                  <el-option label="日期控件" value="datetime"/>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column align="center" label="字典类型" prop="dictType">
              <template slot-scope="scope">
                <el-select v-model="scope.row.dictType" clearable filterable placeholder="请选择" size="small">
                  <el-option
                    v-for="dict in dictOptions"
                    :key="dict.dictType"
                    :label="dict.dictName"
                    :value="dict.dictType"
                  >
                    <span style="float: left">{{ dict.dictName }}</span>
                    <span style="float: right; color: #8492a6; font-size: 13px">{{ dict.dictType }}</span>
                  </el-option>
                </el-select>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 配置菜单对话框 -->
    <el-dialog title="配置菜单" :visible.sync="openMenu" width="680px" append-to-body>
      <el-form ref="menuForm" :model="menuForm" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <treeselect
                v-model="menuForm.parentId"
                :options="menuOptions"
                :normalizer="normalizer"
                :show-count="true"
                placeholder="选择上级菜单"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="menuForm.menuType">
                <el-radio label="M">目录</el-radio>
                <el-radio label="C">菜单</el-radio>
                <el-radio label="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item v-if="menuForm.menuType != 'F'" label="菜单图标">
              <el-popover
                placement="bottom-start"
                width="460"
                trigger="click"
                @show="$refs['iconSelect'].reset()"
              >
                <IconSelect ref="iconSelect" @selected="selected"/>
                <el-input slot="reference" v-model="menuForm.icon" placeholder="点击选择图标" readonly>
                  <svg-icon
                    v-if="menuForm.icon"
                    slot="prefix"
                    :icon-class="menuForm.icon"
                    class="el-input__icon"
                    style="height: 32px;width: 16px;"
                  />
                  <i v-else slot="prefix" class="el-icon-search el-input__icon"/>
                </el-input>
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="menuForm.menuName" placeholder="请输入菜单名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="menuForm.orderNum" controls-position="right" :min="0"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="menuForm.menuType != 'F'" prop="path">
              <span slot="label">
                <el-tooltip content="访问的路由地址，如：`user`" placement="top">
                <i class="el-icon-question"></i>
                </el-tooltip>
                路由地址
              </span>
              <el-input v-model="menuForm.path" placeholder="请输入路由地址"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitMenuForm">确 定</el-button>
        <el-button @click="cancelMenuForm">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDatabaseOptions } from '@/api/report/database'
import {
  addReportSql,
  delReportSql,
  getReportSqlColumn,
  listReportSql,
  parseSQLText,
  updateReportSql
} from '@/api/report/reportSql'
import codeMirror from '@/components/Report/CodeMirror'
import sqlFormatter from 'sql-formatter'
import { optionselect as getDictOptionselect } from '@/api/system/dict/type'
import { addMenu, listMenu } from '@/api/system/menu'
import IconSelect from '@/components/IconSelect'
import Treeselect from '@riophae/vue-treeselect'
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: 'reportSql',
  dicts: ['sys_show_hide', 'sys_normal_disable'],
  components: { codeMirror, IconSelect, Treeselect },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 选中查询列数组
      columns: [],
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        databaseId: null
      },
      // 表单参数
      form: {
        sqlName: '',
        databaseId: undefined,
        querySql: ''
      },
      // 表单校验
      rules: {
        sqlName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        databaseId: [{ required: true, message: '请选择目标数据库', trigger: 'change' }],
        querySql: [{ required: true, message: '请输入查询sql', trigger: 'blur' }]
      },
      // 目标数据库列表
      databaseList: [],
      reportSqlList: [],
      activeNames: '1',
      // 查询sql各字段配置
      reportSqlColumn: [],
      // 字典信息
      dictOptions: [],
      sqlColumnTableLoading: false,
      // 被选中的sql字段
      selectedColumns: [],
      // 保存按钮加载状态
      saveBtnLoading: false,
      // 报表链接前缀
      reportLinkPrefix: '/report/generalReport?reportId=',
      openMenu: false,
      // 添加菜单表单
      menuForm: {},
      // 菜单树选项
      menuOptions: [],
      reportId: ''
    }
  },
  created() {
    this.getList()
    listDatabaseOptions().then(ret => {
      this.databaseList = ret.data || []
    })
    /** 查询字典下拉列表 */
    getDictOptionselect().then(response => {
      this.dictOptions = response.data
    })
  },
  methods: {
    /** 查询数据库配置信息列表 */
    getList() {
      this.loading = true
      listReportSql(this.queryParams).then(ret => {
        this.reportSqlList = ret.rows
        this.total = ret.total
      }).finally(() => [
        this.loading = false
      ])
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        sqlName: '',
        databaseId: null,
        querySql: 'select * from sys_user'
      }
      this.resetForm('form')
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
    },
    formatDatebase(row) {
      let database = {}
      if (this.databaseList.length) {
        database = this.databaseList.find(item => {
          return item.id === row.databaseId
        })
      }
      return (database && database.name) || ''
    },
    /** 复制报表链接 */
    copyLink(link) {
      //如果需要回调：
      this.$copyText(link).then(e => {
        this.$message.success('复制成功')
      }, () => {
        this.$message.error('复制失败')
      })
    },
    // 查询列选中数据
    handleColumnSelectionChange(selection) {
      this.selectedColumns = selection.map(item => item.sqlField)
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.activeNames = '1'
      this.title = '添加SQL'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.open = true
      this.form = row
      this.sqlColumnTableLoading = true
      getReportSqlColumn({ id: row.id }).then(ret => {
        this.reportSqlColumn = ret.data
      }).finally(() => {
        this.sqlColumnTableLoading = false
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['sqlForm'].validate(valid => {
        if (valid) {
          this.reportSqlColumn.find(item => {
            if (!item.sqlField || !item.columnName) {
              return this.$message.error('查询字段和别名不能为空')
            }
          })
          this.saveBtnLoading = true
          if (this.form.id != null) { // 修改
            updateReportSql({
              reportSql: this.form,
              reportSqlColumn: this.reportSqlColumn
            }).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.getList()
              this.open = false
            }).finally(() => this.saveBtnLoading = false)
          } else { // 新增
            addReportSql({
              reportSql: this.form,
              reportSqlColumn: this.reportSqlColumn
            }).then(() => {
              this.$message.success('保存成功')
              this.getList()
              this.open = false
            }).finally(() => this.saveBtnLoading = false)
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal.confirm(`是否确认删除SQL: ${row.sqlName} ?`).then(() => {
        return delReportSql(row.id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      })
    },
    /** 格式化sql */
    formatSql() {
      this.$refs.codemirror.setValue(sqlFormatter.format(this.form.querySql))
    },
    /** 解析sql字段 */
    resolveSql() {
      if (!this.form.databaseId) {
        return this.$message.error('请先选择目标数据库')
      } else if (!this.form.querySql) {
        return this.$message.error('请先输入SQL')
      }
      parseSQLText(this.form).then(ret => {
        this.reportSqlColumn = ret.data
        if (this.reportSqlColumn.length > 5) {
          this.activeNames = ''
        }
      })
    },
    /** 新增行 */
    addColumn() {
      this.reportSqlColumn.push({
        sqlField: '',
        columnName: '',
        isList: '1',
        isExport: '1',
        isQuery: '0',
        queryType: '',
        htmlType: '',
        dictType: ''
      })
    },
    /** 删除行 */
    delColumn() {
      if (this.selectedColumns.length === 0) {
        return this.$message.error('请先选择删除行')
      }
      this.reportSqlColumn = this.reportSqlColumn.filter(item => {
        return this.selectedColumns.indexOf(item.sqlField) === -1
      })
    },
    onTextareaChange(val) {
      this.$set(this.form, 'querySql', val)
    },
    onIsQueryChange(val, row) {
      if (val === '1' && !row.queryType) {
        row.queryType = 'EQ'
      } else {
        row.queryType = null
      }
    },
    onQueryTypeChange(val, row) {
      if (val) {
        row.isQuery = '1'
        if (val === 'BETWEEN') {
          row.htmlType = 'datetime'
        }
      }
    },
    /** 拼接报表链接 */
    getReportLink(id) {
      return this.reportLinkPrefix + id
    },
    /** 查看报表 */
    handleOpenReport(link) {
      this.$router.push(link)
    },
    /** 菜单配置 */
    handleAddMenu(row) {
      this.reportId = row.id
      this.resetMenuForm()
      this.getTreeselect()
      this.menuForm.parentId = 0
      this.openMenu = true
    },
    /** 查询菜单下拉树结构 */
    getTreeselect() {
      listMenu().then(response => {
        this.menuOptions = []
        const menu = { menuId: 0, menuName: '主类目', children: [] }
        menu.children = this.handleTree(response.data, 'menuId')
        this.menuOptions.push(menu)
      })
    },
    // 表单重置
    resetMenuForm() {
      this.menuForm = {
        menuId: undefined,
        parentId: 0,
        menuName: undefined,
        icon: undefined,
        menuType: 'C',
        orderNum: undefined,
        isFrame: '1',
        isCache: '1',
        visible: '0',
        status: '0',
        component: 'report/gen/generalReport',
        query: '{"reportId": ' + this.reportId + '}'
      }
      this.resetForm('menuForm')
    },
    // 选择图标
    selected(name) {
      this.menuForm.icon = name
    },
    /** 转换菜单数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.menuId,
        label: node.menuName,
        children: node.children
      }
    },
    /** 提交菜单配置表单 */
    submitMenuForm() {
      this.$refs['menuForm'].validate(valid => {
        if (valid) {
          addMenu(this.menuForm).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.openMenu = false
          })
        }
      })
    },
    /** 取消菜单配置 */
    cancelMenuForm() {
      this.openMenu = false
      this.resetMenuForm()
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  ::v-deep .el-collapse-item__header {
    height: 36px !important;
    padding-left: 30px !important;
  }
}
</style>
