<template>
  <div class="card" style="width: 50%">
    <div style="text-align: center; font-size: 20px; margin-bottom: 30px">救助员申请</div>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="padding-right: 50px">
      <el-form-item label="电话" prop="phone">
        <el-input v-model="form.phone" placeholder="电话"></el-input>
      </el-form-item>
      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="form.sex">
          <el-radio label="男"></el-radio>
          <el-radio label="女"></el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="生日" prop="birth">
        <el-date-picker v-model="form.birth" format="yyyy-MM-dd"
                        style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
      </el-form-item>
      <el-form-item label="身份证号码" prop="cardNo">
        <el-input v-model="form.cardNo" placeholder="身份证号码"></el-input>
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
      <el-form-item label="个人介绍" prop="info">
        <el-input v-model="form.info" placeholder="个人介绍" type="textarea"></el-input>
      </el-form-item>
      <el-form-item v-if="form.status" label="审核状态" prop="status">
        <strong v-if="form.status === '待审核'" style="color: orange">待审核</strong>
        <strong v-if="form.status === '通过'" style="color: #3FA50C">通过</strong>
        <strong v-if="form.status === '拒绝'" style="color: red">拒绝</strong>
      </el-form-item>
      <div style="text-align: center">
        <el-button size="medium" type="primary" @click="submit">提交申请</el-button>
      </div>
    </el-form>
  </div>
</template>

<script>
export default {
  name: "UserVolApply",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      form: {},
      rules: {}
    }
  },
  created() {
    this.load()
  },
  methods: {
    handleCardImgSuccess(response) {
      this.form.cardImg = response.data
    },
    load() {
      this.$request.get('/volApply/selectByUserId/' + this.user.id).then(res => {
        this.form = res.data || {}
      })
    },
    submit() {
      this.$request.post('/volApply/add', this.form).then(res => {
        if (res.code === '200') {  // 表示成功保存
          this.$message.success('提交成功')
          this.load()
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    }
  }
}
</script>

<style scoped>

</style>