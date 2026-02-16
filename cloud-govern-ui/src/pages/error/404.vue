<template>
  <div class="error-page">
    <t-result theme="error" title="404" description="抱歉，您访问的页面不存在">
      <template #extra>
        <t-button theme="primary" @click="router.push('/')">返回首页</t-button>
      </template>
    </t-result>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();
</script>

<style scoped lang="less">
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style>
