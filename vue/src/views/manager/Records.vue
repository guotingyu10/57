<template>
  <div>
    <div class="search">
      <el-input v-model="volName" placeholder="请输入关键字查询" style="width: 200px"></el-input>

      <el-button plain style="margin-left: 10px" type="info" @click="load(1)">查询</el-button>
      <el-button plain style="margin-left: 10px" type="warning" @click="reset">重置</el-button>
    </div>

    <div v-if="user.role === 'ADMIN'" class="operation">
      <el-button plain type="danger" @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" strip @selection-change="handleSelectionChange">
        <el-table-column v-if="user.role === 'ADMIN'" align="center" type="selection" width="55"></el-table-column>
        <el-table-column align="center" label="序号" prop="id" sortable width="70"></el-table-column>
        <el-table-column label="救助员名称" prop="volName"></el-table-column>
        <el-table-column label="状态" prop="status">
          <template v-slot="scope">
            <el-tag v-if="scope.row.status === '待救援'" type="warning">待救援</el-tag>
            <el-tag v-if="scope.row.status === '救援中'" type="info">救援中</el-tag>
            <el-tag v-if="scope.row.status === '已救援'" type="success">已救援</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="出警时间" prop="time"></el-table-column>
        <el-table-column label="事件详情" prop="helpId">
          <template v-slot="scope">
            <el-button @click="view(scope.row.helpId)">查看详情</el-button>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="comment"></el-table-column>
        <el-table-column align="center" label="操作" width="220">
          <template v-slot="scope">
            <el-button v-if="scope.row.status === '待救援' && user.role === 'VOLUNTEER'" plain size="mini" type="primary"
                       @click="changeStatus(scope.row, '救援中')">应答
            </el-button>
            <el-button v-if="scope.row.status === '救援中' && user.role === 'VOLUNTEER'" plain size="mini" type="success"
                       @click="changeStatus(scope.row, '已救援')">完成
            </el-button>
            <el-button v-if="user.role === 'ADMIN'" plain size="mini" type="danger" @click="del(scope.row.id)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            :current-page="pageNum"
            :page-size="pageSize"
            :page-sizes="[5, 10, 20]"
            :total="total"
            background
            layout="total, prev, pager, next"
            @current-change="handleCurrentChange">
        </el-pagination>
      </div>
    </div>

    <el-dialog :close-on-click-modal="false" :visible.sync="fromVisible1" destroy-on-close title="救援信息" width="40%">
      <el-form label-width="120px">
        <el-form-item label="事故名称">
          <div>{{ help.name }}</div>
        </el-form-item>
        <el-form-item label="事故内容">
          <div>{{ help.content }}</div>
        </el-form-item>
        <el-form-item label="现场照片">
          <div><img :src="help.img" alt="" style="width: 200px"></div>
        </el-form-item>
        <el-form-item label="事故地点">
          <div>{{ help.location }}</div>
        </el-form-item>
        <el-form-item label="求救人名称">
          <div>{{ help.userName }}</div>
        </el-form-item>
        <el-form-item label="求救人电话">
          <div>{{ help.phone }}</div>
        </el-form-item>
        <el-form-item label="求救时间">
          <div>{{ help.time }}</div>
        </el-form-item>
      </el-form>
    </el-dialog>

    <el-dialog :close-on-click-modal="false" :visible.sync="fromVisible" destroy-on-close title="出警信息" width="40%">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="padding-right: 50px">
        <el-form-item label="救助员ID" prop="volId">
          <el-input v-model="form.volId" placeholder="救助员ID"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-input v-model="form.status" placeholder="状态"></el-input>
        </el-form-item>
        <el-form-item label="出警时间" prop="time">
          <el-input v-model="form.time" placeholder="出警时间"></el-input>
        </el-form-item>
        <el-form-item label="事件ID" prop="helpId">
          <el-input v-model="form.helpId" placeholder="事件ID"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="comment">
          <el-input v-model="form.comment" placeholder="备注"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>


  </div>
</template>
<script>
export default {
  volName: "Records",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      volName: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {},
      ids: [],
      help: {},
      fromVisible1: false
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    view(helpId) {
      this.$request.get('/help/selectById/' + helpId).then(res => {
        this.help = res.data || {}
        this.fromVisible1 = true
      })
    },
    changeStatus(row, status) {
      this.$confirm('您确定操作吗？', '确认操作', {type: 'info'}).then(res => {
        row.status = status
        this.$request.put('/records/update', row).then(res => {
          if (res.code === '200') {  // 表示成功保存
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/records/update' : '/records/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('保存成功')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/records/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/records/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/records/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          volName: this.volName,
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list
          this.total = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    reset() {
      this.volName = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>
/deep/ .el-form-item__label {
  font-weight: bold;
}
</style>