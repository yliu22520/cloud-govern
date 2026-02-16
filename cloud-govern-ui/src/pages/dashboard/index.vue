<template>
  <div class="dashboard-page">
    <t-card title="欢迎使用 Cloud Govern">
      <p>这是一个轻量级微服务治理平台，提供统一的服务管理、配置管理、监控告警能力。</p>
    </t-card>

    <t-row :gutter="16" class="stat-row">
      <t-col :span="3">
        <t-card>
          <t-statistic title="注册服务" :value="0" />
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card>
          <t-statistic title="配置数量" :value="0" />
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card>
          <t-statistic title="健康实例" :value="0" />
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card>
          <t-statistic title="告警数量" :value="0" />
        </t-card>
      </t-col>
    </t-row>
  </div>
</template>

<script setup lang="ts">
import { Card as TCard, Row as TRow, Col as TCol, Statistic as TStatistic } from 'tdesign-vue-next';
</script>

<style scoped lang="less">
.dashboard-page {
  .stat-row {
    margin-top: 16px;
  }
}
</style>
