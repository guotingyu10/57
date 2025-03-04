<template>
  <div class="card">
    <comment :fid="0" module="message" />
  </div>
</template>

<script>
import Comment from "@/components/Comment";
export default {
  name: "UserComment",
  components: {Comment},
  data() {
    return {}
  },
  created() {

  },
  methods: {}
}
</script>

<style scoped>

</style>