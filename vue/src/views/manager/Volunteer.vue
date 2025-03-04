<template>
  <div>
    <div class="search">
      <el-input v-model="name" placeholder="请输入名称关键字查询" style="width: 200px"></el-input>
      <el-button plain style="margin-left: 10px" type="info" @click="load(1)">查询</el-button>
      <el-button plain style="margin-left: 10px" type="warning" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button plain type="primary" @click="handleAdd">新增</el-button>
      <el-button plain type="danger" @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" strip @selection-change="handleSelectionChange">
        <el-table-column align="center" type="selection" width="55"></el-table-column>
        <el-table-column align="center" label="序号" prop="id" sortable width="70"></el-table-column>
        <el-table-column label="用户名" prop="username"></el-table-column>
        <el-table-column label="密码" prop="password"></el-table-column>
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="个人照片" prop="avatar">
          <template v-slot="scope">
            <el-image v-if="scope.row.avatar" :preview-src-list="[scope.row.avatar]" :src="scope.row.avatar"
                      style="width: 50px"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="电话" prop="phone"></el-table-column>
        <el-table-column label="生日" prop="birth"></el-table-column>
        <el-table-column label="性别" prop="sex"></el-table-column>
        <el-table-column label="介绍" prop="info"></el-table-column>
        <el-table-column label="身份证照片" prop="cardImg">
          <template v-slot="scope">
            <el-image v-if="scope.row.cardImg" :preview-src-list="[scope.row.cardImg]" :src="scope.row.cardImg"
                      style="width: 50px"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="身份证号码" prop="cardNo"></el-table-column>
        <el-table-column label="角色" prop="role"></el-table-column>
        <el-table-column label="是否空闲" prop="free"></el-table-column>
        <el-table-column align="center" label="操作" width="180">
          <template v-slot="scope">
            <el-button plain size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button plain size="mini" type="danger" @click="del(scope.row.id)">删除</el-button>
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

    <el-dialog :close-on-click-modal="false" :visible.sync="fromVisible" destroy-on-close title="志愿者信息"
               width="40%">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="padding-right: 50px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="名称"></el-input>
        </el-form-item>
        <el-form-item label="个人照片" prop="avatar">
          <el-upload
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              :on-success="handleAvatarSuccess"
              list-type="picture"
          >
            <el-button type="primary">上传个人照片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="电话"></el-input>
        </el-form-item>
        <el-form-item label="生日" prop="birth">
          <el-date-picker v-model="form.birth" format="yyyy-MM-dd"
                          style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio label="男"></el-radio>
            <el-radio label="女"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="介绍" prop="info">
          <el-input v-model="form.info" placeholder="介绍" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="身份证照片" prop="cardImg">
          <el-upload
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              :on-success="handleCardImgSuccess"
              list-type="picture"
          >
            <el-button type="primary">上传身份证照片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="身份证号码" prop="cardNo">
          <el-input v-model="form.cardNo" placeholder="身份证号码"></el-input>
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
  name: "Volunteer",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      name: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {},
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
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
            url: this.form.id ? '/volunteer/update' : '/volunteer/add',
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
        this.$request.delete('/volunteer/delete/' + id).then(res => {
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
        this.$request.delete('/volunteer/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/volunteer/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
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
      this.name = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleAvatarSuccess(response, file, fileList) {
      this.form.avatar = response.data
    },
    handleCardImgSuccess(response) {
      this.form.cardImg = response.data
    }
  }
}
</script>

<style scoped>

</style>