<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :inline="true" :model="queryParams" label-width="120px">
      <el-form-item label="数据源名称" prop="name">
        <el-input
          v-model="queryParams.name"
          clearable
          placeholder="请输入数据源名称"
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

    <el-table v-loading="loading" :data="databaseList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55"/>
      <el-table-column align="center" label="数据源名称" prop="name"/>
      <el-table-column align="center" label="连接 url" show-overflow-tooltip prop="jdbcUrl"/>
      <el-table-column align="center" label="数据源登录用户名" show-overflow-tooltip prop="user"/>
      <el-table-column align="center" label="数据源登录密码" show-overflow-tooltip prop="password"/>
      <el-table-column align="center" label="数据库类型" prop="dataType"/>
      <el-table-column align="center" label="连接池类型" prop="poolType"/>
      <el-table-column align="center" label="说明备注" prop="comment"/>
      <el-table-column align="center" label="创建人" prop="createBy"/>
      <el-table-column align="center" label="创建时间" prop="createTime"/>
      <el-table-column align="center" label="最后修改人" prop="updateBy"/>
      <el-table-column align="center" label="最后修改时间" prop="updateTime"/>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
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
    <el-dialog :title="title" :visible.sync="open" append-to-body width="60%">
      <h2 style="color: #6379bb"> | 基础信息</h2>
      <el-divider></el-divider>
      <el-row :gutter="15">
        <el-form ref="databaseForm" :model="form" :rules="rules" label-width="100px" size="small">
          <el-col :span="24">
            <el-form-item label="数据库名称" prop="name">
              <el-input v-model="form.name" :style="{width: '100%'}" clearable placeholder="请输入数据库名称"
                        prefix-icon="el-icon-edit"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="连接 url" prop="jdbcUrl">
              <el-input v-model="form.jdbcUrl" :style="{width: '100%'}" clearable placeholder="请输入连接 url"
                        prefix-icon="el-icon-link" show-word-limit
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名" prop="user">
              <el-input v-model="form.user" :style="{width: '100%'}" clearable placeholder="请输入用户名"
                        prefix-icon="el-icon-user"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" :style="{width: '100%'}" clearable placeholder="请输入密码"
                        prefix-icon="el-icon-key" show-word-limit
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据库类型" prop="dataType">
              <el-select v-model="form.dataType" :style="{width: '100%'}" clearable placeholder="请选择数据库类型">
                <el-option v-for="item in dataTypeOptions" :key="item.id" :label="item.name" :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="连接池类型" prop="poolType">
              <el-select v-model="form.poolType" :style="{width: '100%'}" clearable placeholder="请选择连接池类型"
                         @change="onPoolTypeChange"
              >
                <el-option v-for="item in poolTypeOptions" :key="item.id" :label="item.name" :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="comment">
              <el-input v-model="form.comment" :autosize="{minRows: 4, maxRows: 4}" :style="{width: '100%'}"
                        placeholder="请输入备注" type="textarea"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-form>
      </el-row>
      <h2 style="color: #6379bb"> | 配置选项</h2>
      <el-divider></el-divider>
      <el-table
        :data="poolMationData"
        style="width: 100%"
      >
        <el-table-column
          align="left"
          prop="configKey"
          label="配置项"
        >
        </el-table-column>
        <el-table-column
          align="left"
          prop="configValue"
          label="配置值"
        >
          <template slot-scope="scope">
            <el-switch
              v-if="(typeof scope.row.configValue) === 'boolean'"
              v-model="scope.row.configValue"
            >
            </el-switch>
            <el-input
              v-else
              size="small"
              placeholder="请输入配置值"
              v-model="scope.row.configValue"
              clearable
            >
            </el-input>
          </template>
        </el-table-column>
        <el-table-column
          align="left"
          prop="remark"
          label="备注"
        >
          <template slot-scope="scope">
            <el-input
              size="small"
              v-model="scope.row.remark"
              clearable
            >
            </el-input>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" style="text-align: center">
        <el-button size="small" @click="open = false">取消</el-button>
        <el-button size="small" type="primary" @click="connectTest" :loading="testBtnLoading">连接测试</el-button>
        <el-button size="small" type="primary" @click="submitForm" :loading="saveBtnLoading">保 存</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {
  addDatabase,
  delDatabase,
  getDatabase,
  getDataType,
  getPoolType,
  listDatabase,
  testConnection,
  updateDatabase
} from '@/api/report/database'

export default {
  name: 'Database',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 数据库配置信息表格数据
      databaseList: [],
      // 连接池类型选项
      poolTypeOptions: [],
      // 数据库类型选项
      dataTypeOptions: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null
      },
      // 表单参数
      form: {
        name: undefined,
        jdbcUrl: '',
        user: '',
        password: undefined,
        dataType: undefined,
        poolType: undefined,
        comment: undefined
      },
      // 表单校验
      rules: {
        name: [{ required: true, message: '请输入数据库名称', trigger: 'blur' }],
        jdbcUrl: [{ required: true, message: '请输入连接 url', trigger: 'blur' }],
        user: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        dataType: [{ required: true, message: '请选择数据库类型', trigger: 'change' }],
        poolType: [{ required: true, message: '请选择连接池类型', trigger: 'change' }]
      },
      // 连接池配置选项
      poolMationData: [],
      testBtnLoading: false,
      saveBtnLoading: false,
    }
  },
  created() {
    this.getList()
    getDataType().then(ret => {
      this.dataTypeOptions = ret.data
    })
    getPoolType().then(ret => {
      this.poolTypeOptions = ret.data
    })
  },
  methods: {
    /** 查询数据库配置信息列表 */
    getList() {
      this.loading = true
      listDatabase(this.queryParams).then(response => {
        this.databaseList = response.rows
        this.total = response.total
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
        // TODO
        name: '',
        jdbcUrl: '',
        user: '',
        password: '',
        dataType: '',
        poolType: '',
        id: null,
        // name: null,
        // jdbcUrl: null,
        // user: null,
        // password: null,
        // dataType: null,
        // poolType: null,
        options: null,
        comment: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
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
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加数据库配置信息'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getDatabase(id).then(ret => {
        this.form = ret.data
        this.poolMationData = JSON.parse(this.form.options)
        this.open = true
        this.title = '修改数据库配置信息'
      })
    },
    /** 监听连接池下拉框change事件 */
    onPoolTypeChange(val) {
      let poolType = this.poolTypeOptions.find(item => item.id === val)
      if (poolType) {
        let optionsObj = JSON.parse(poolType.options)
        let poolMationData = []
        Object.keys(optionsObj).forEach(key => {
          poolMationData.push({
            configKey: key,
            configValue: optionsObj[key],
            remark: ''
          })
        })
        this.poolMationData = poolMationData
      } else {
        this.poolMationData = []
      }
    },
    /** 连接测试 */
    connectTest() {
      this.$refs['databaseForm'].validate(valid => {
        if (valid) {
          this.testBtnLoading = true
          testConnection(this.form).then(ret => {
            if (ret && ret.code === 200) {
              this.$message.success('连接成功')
            } else {
              this.$message.error('连接失败')
            }
          }).finally(() => {
            this.testBtnLoading = false
          })
        }
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['databaseForm'].validate(valid => {
        if (valid) {
          this.saveBtnLoading = true
          this.form.options = JSON.stringify(this.poolMationData)
          if (this.form.id != null) {
            updateDatabase(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            }).finally(() => this.saveBtnLoading = false)
          } else {
            addDatabase(this.form).then(() => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            }).finally(() => this.saveBtnLoading = false)
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm(`是否确认删除数据源: ${row.name} ?`).then(() => {
        return delDatabase(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {
      })
    }
  }
}
</script>
