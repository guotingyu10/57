<template>
  <div>
    <div class="card" style="padding: 15px">
      您好，{{ user?.name }}！欢迎使用本系统
      <el-button type="danger" size="medium" style="margin-left: 10px" @click="$router.push('/help')">我要救援</el-button>
    </div>

    <div style="display: flex; grid-gap: 10px; margin: 10px 0">

      <div style="flex: 1" class="card">
        <div style="margin-bottom: 30px; font-size: 20px; font-weight: bold">救援科普</div>
        <div v-for="item in knowledgeList" :key="item.id" style="margin-bottom: 10px">
          <div>
            <span @click="$router.push('/knowledgeDetail?id=' + item.id)" style="font-size: 18px; cursor: pointer">{{ item.title }}</span>
            <span style="margin-left: 20px; font-size: 12px; color: #666">{{ item.date }}</span>
          </div>
        </div>
        <div style="margin: 20px 0" v-if="total > pageSize">
          <el-pagination
              @current-change="handleCurrentChange"
              :current-page="pageNum"
              :page-sizes="[5, 10, 20]"
              :page-size="pageSize"
              layout="prev, pager, next"
              :total="total">
          </el-pagination>
        </div>
      </div>

      <div style="flex: 1" class="card">
        <div style="margin-bottom: 30px; font-size: 20px; font-weight: bold">公告列表</div>
        <div >
          <el-timeline  reverse slot="reference">
            <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time">
              <el-popover
                  placement="right"
                  width="200"
                  trigger="hover"
                  :content="item.content">
                <span slot="reference">{{ item.title }}</span>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: [],
      knowledgeList: [],
      pageNum: 1,
      pageSize: 5,
      total: 0
    }
  },
  created() {
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || []
    })

    this.load(1)
  },
  methods: {
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/knowledge/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        }
      }).then(res => {
        if (res.code === '200') {
          this.knowledgeList = res.data?.list
          this.total = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>
