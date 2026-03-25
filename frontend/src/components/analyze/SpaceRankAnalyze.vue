<template>
  <div class="space-rank-analyze">
    <a-card title="空间使用排行">
      <v-chart :option="options" style="height: 320px; max-width: 100%;" :loading="loading" />
    </a-card>
  </div>
</template>

<script lang="ts" setup>
import VChart from "vue-echarts";
import "echarts";
import { message } from "ant-design-vue";
import { getSpaceRankAnalyze } from "@/api/spaceAnalyzeController";
import { computed, ref, watchEffect } from "vue";

interface Props {
  queryAll?: boolean
  queryPublic?: boolean
  spaceId?: any
}
const props = withDefaults(defineProps<Props>(), {
  queryAll: false,
  queryPublic: false,
})

// 图表数据
const dataList = ref<API.Space[]>([])
const loading = ref(false)

// 获取数据
const fetchData = async () => {
  loading.value = true
  const res = await getSpaceRankAnalyze({
    topN: 10,
  })
  if (res.data.code === 200) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('获取数据失败，' + res.data.msg)
  }
  loading.value = false
}

// 监听属性变化，重新获取数据
watchEffect(() => {
  fetchData()
})

// 图表选项
const options = computed(() => {
  const spaceNames = dataList.value.map((item) => item.spaceName)
  const usageData = dataList.value.map((item) => ((item.totalSize ?? 0) / (1024 * 1024)).toFixed(2))

  return {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: spaceNames,
    },
    yAxis: {
      type: 'value',
      name: '空间使用量 (MB)',
    },
    series: [
      {
        name: '空间使用量 (MB)',
        type: 'bar',
        data: usageData,
        itemStyle: {
          color: '#5470C6',
        },
      },
    ],
  }
})
</script>

<style scoped></style>