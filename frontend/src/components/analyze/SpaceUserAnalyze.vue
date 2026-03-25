<template>
  <div class="space-user-analyze">
    <a-card title="空间图片用户分析">
      <v-chart :option="options" style="height: 320px; max-width: 100%;" :loading="loading" />
      <template #extra>
        <a-space>
          <a-segmented v-model:value="timeDimension" :options="timeDimensionOptions" />
          <a-input-search placeholder="请输入用户 id" enter-button="搜索用户" @search="doSearch" />
        </a-space>
      </template>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
import VChart from "vue-echarts";
import "echarts";
import { message } from "ant-design-vue";
import { getSpaceUserAnalyze } from "@/api/spaceAnalyzeController";
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
const dataList = ref<API.SpaceUserAnalyzeResponse[]>([])
const loading = ref(false)

// 用户Id
const userId = ref<any>()

// 搜索用户
const doSearch = (value: string) => {
  userId.value = value
}

// 时间维度
const timeDimension = ref<string>('day')
const timeDimensionOptions = [
  {
    label: '日',
    value: 'day',
  },
  {
    label: '周',
    value: 'week',
  },
  {
    label: '月',
    value: 'month',
  },
]

// 获取数据
const fetchData = async () => {
  loading.value = true
  const res = await getSpaceUserAnalyze({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
    timeDimension: timeDimension.value,
    userId: userId.value,
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
  const periods = dataList.value.map((item) => item.period)
  const counts = dataList.value.map((item) => item.count)

  return {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: periods, name: '时间区间' },
    yAxis: { type: 'value', name: '上传数量' },
    series: [
      {
        name: '上传数量',
        type: 'line',
        data: counts,
        smooth: true,
        emphasis: {
          focus: 'series',
        },
      },
    ],
  }
})
</script>

<style scoped></style>