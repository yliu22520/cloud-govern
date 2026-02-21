<template>
  <div class="error-page">
    <div class="result-container">
      <div class="result-icon">404</div>
      <div class="result-title">抱歉，您访问的页面不存在</div>
      <t-button theme="primary" @click="router.push('/')">返回首页</t-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { Button as TButton } from 'tdesign-vue-next';

const router = useRouter();
</script>

<style scoped lang="less">
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.result-container {
  text-align: center;
}

.result-icon {
  font-size: 72px;
  font-weight: bold;
  color: #e34d59;
  margin-bottom: 16px;
}

.result-title {
  font-size: 18px;
  color: #666;
  margin-bottom: 24px;
}
</style>
