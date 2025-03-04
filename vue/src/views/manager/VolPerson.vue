<template>
  <div>
    <el-card style="width: 50%">
      <el-form :model="vol" label-width="100px" style="padding-right: 50px">
        <div style="margin: 15px; text-align: center">
          <el-upload
              :action="$baseUrl + '/files/upload'"
              :on-success="handleAvatarSuccess"
              :show-file-list="false"
              class="avatar-uploader"
          >
            <img v-if="vol.avatar" :src="vol.avatar" class="avatar"/>
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </div>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="vol.username" disabled placeholder="用户名"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="vol.name" placeholder="姓名"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="vol.phone" placeholder="电话"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="vol.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item label="生日" prop="birth">
          <el-date-picker v-model="vol.birth" format="yyyy-MM-dd"
                          style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="vol.sex">
            <el-radio label="男"></el-radio>
            <el-radio label="女"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="介绍" prop="info">
          <el-input v-model="vol.info" placeholder="介绍" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="身份证照片" prop="cardImg">
          <el-upload
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: vol.token }"
              :on-success="handleCardImgSuccess"
              list-type="picture"
          >
            <el-button type="primary">上传身份证照片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="身份证号码" prop="cardNo">
          <el-input v-model="vol.cardNo" placeholder="身份证号码"></el-input>
        </el-form-item>
        <div style="text-align: center; margin-bottom: 20px">
          <el-button type="primary" @click="update">保 存</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "VolPerson",
  data() {
    return {
      vol: JSON.parse(localStorage.getItem('xm-user') || '{}')
    }
  },
  created() {

  },
  methods: {
    update() {
      // 保存当前的用户信息到数据库
      this.$request.put('/volunteer/update', this.vol).then(res => {
        if (res.code === '200') {
          // 成功更新
          this.$message.success('保存成功')
          // 更新浏览器缓存里的用户信息
          localStorage.setItem('xm-user', JSON.stringify(this.vol))
          // 触发父级的数据更新
          this.$emit('update:user')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAvatarSuccess(response, file, fileList) {
      // 把vol的头像属性换成上传的图片的链接
      this.$set(this.vol, 'avatar', response.data)
    },
    handleCardImgSuccess(response) {
      this.vol.cardImg = response.data
    }
  }
}
</script>

<style scoped>
/deep/ .el-form-item__label {
  font-weight: bold;
}

/deep/ .el-upload {
  border-radius: 50%;
}

/deep/ .avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border-radius: 50%;
}

/deep/ .avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
  border-radius: 50%;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 50%;
}
</style>