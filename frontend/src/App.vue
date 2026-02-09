<template>
  <div id="app">
    <a-config-provider :locale="zhCN">
      <BasicLayout/>
    </a-config-provider>
  </div>
</template>

<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import zhCN from 'ant-design-vue/lib/locale/zh_CN';
</script>

<style>
</style>
