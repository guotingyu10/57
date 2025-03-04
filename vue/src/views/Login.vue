<template>
  <div class="container">
    <div style="width: 380px; padding: 30px; background-color: rgba(255, 255, 255, .6); border-radius: 5px;">
      <div style="text-align: center; font-size: 24px; margin-bottom: 30px; color: #333">欢迎使用灾情救援系统</div>
      <el-form ref="formRef" :model="form" :rules="rules">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" prefix-icon="el-icon-user"
                    size="medium"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" placeholder="请输入密码" prefix-icon="el-icon-lock" show-password
                    size="medium"></el-input>
        </el-form-item>
        <el-form-item prop="role">
          <el-select v-model="form.role" size="medium" style="width: 100%">
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="救助员" value="VOLUNTEER"></el-option>
            <el-option label="普通用户" value="USER"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <div style="display: flex">
            <el-input v-model="form.code" placeholder="请输入验证码" size="medium" style="flex: 1"></el-input>
            <div style="flex: 1; margin-left: 10px"><img :src="codeImg" style="width: 100%; height: 35px"
                                                         @click="getCode()"/></div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button size="medium" style="width: 100%;" type="primary" @click="login">登 录</el-button>
        </el-form-item>
        <div style="display: flex; align-items: center">
          <div style="flex: 1"></div>
          <div style="flex: 1; text-align: right">
            还没有账号？请 <a href="/register">注册</a>
          </div>
        </div>
      </el-form>
    </div>
    <el-dialog :close-on-click-modal="false" :show-close="false" :visible.sync="dialogVisible" destroy-on-close
               title="郑重声明" width="40%">
      <div style="font-size: 16px; line-height: 26px; margin-bottom: 20px; text-align: justify">
        本平台已经对本项目申请了<b style="color: #000">软件著作权</b>，完善了所有该项目资源相关的法律文件材料。
        <b style="color: #ff2424">本平台付费用户可以学习该项目自己使用，禁止在其他平台做转手或者倒卖，禁止在私域未经授权分享源码，禁止上传代码到github、gitee、gitlab等代码托管平台。</b>
        我们公司的法务会在各大平台（例如：闲鱼、小红书、B站、CSDN等社交媒体平台）不定期检索，
        一旦发现倒卖或转手的现象，我们会第一时间收集好证据，向你发送律师函。
        <b style="color: #000">现在互联网所有账号都是实名制，我们可以明确追溯到你。请大家不要做违法的事情，不要因为一时糊涂给自己的人生轨迹抹上一层黑。</b>
      </div>
      <div style="margin-top: 10px; font-size: 16px; color: #000">
        本项目唯一官方平台：<b>项目实战网（<a href="https://www.javaxmsz.cn" target="_blank">https://www.javaxmsz.cn</a>）</b>
      </div>
      <div style="margin-top: 5px; font-size: 16px"></div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible=false">我已明确法律规则，承诺本项目仅限个人使用</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      dialogVisible: true,
      form: {role: 'ADMIN'},
      rules: {
        username: [
          {required: true, message: '请输入账号', trigger: 'blur'},
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
        ]
      },
      codeImg: '',
      uuid: '',
    }
  },
  created() {
    this.getCode()
  },
  methods: {
    getCode() {
      this.uuid = generateUUID()
      this.codeImg = this.$baseUrl + '/validateCode?key=' + this.uuid
    },
    login() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          // 验证通过
          this.form.key = this.uuid
          this.$request.post('/login', this.form).then(res => {
            if (res.code === '200') {
              localStorage.setItem("xm-user", JSON.stringify(res.data))  // 存储用户数据
              this.$router.push('/')  // 跳转主页
              this.$message.success('登录成功')
            } else {
              this.getCode()
              this.$message.error(res.msg)
            }
          })
        }
      })
    }
  }
}

// 生成UUID
function generateUUID() {
  let d = new Date().getTime();
  if (window.performance && typeof window.performance.now === "function") {
    d += performance.now(); //use high-precision timer if available
  }
  let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    let r = (d + Math.random() * 16) % 16 | 0;
    d = Math.floor(d / 16);
    return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
  });
  return uuid;
}
</script>

<style scoped>
.container {
  height: 100vh;
  overflow: hidden;
  background-image: url("@/assets/imgs/bg.jpg");
  background-size: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

a {
  color: #2a60c9;
}
</style>