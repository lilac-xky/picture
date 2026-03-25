<template>
  <div class="space-tag-analyze">
    <a-card title="图库标签词云">
      <v-chart :option="options" style="height: 320px; max-width: 100%;" :loading="loading" />
    </a-card>
  </div>
</template>

<script lang="ts" setup>
import VChart from "vue-echarts";
import "echarts";
import 'echarts-wordcloud';
import { message } from "ant-design-vue";
import { getSpaceTagAnalyze } from "@/api/spaceAnalyzeController";
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
const dataList = ref<API.SpaceTagAnalyzeResponse[]>([])
const loading = ref(false)

// 获取数据
const fetchData = async () => {
  loading.value = true
  const res = await getSpaceTagAnalyze({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
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
  const tagData = dataList.value.map((item) => ({
    name: item.tag,
    value: item.count,
  }))

  return {
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => `${params.name}: ${params.value} 次`,
    },
    series: [
      {
        type: 'wordCloud',
        gridSize: 10,
        sizeRange: [12, 50],
        rotationRange: [-90, 90],
        shape: 'circle',
        textStyle: {
          color: () =>
            `rgb(${Math.round(Math.random() * 255)}, ${Math.round(
              Math.random() * 255,
            )}, ${Math.round(Math.random() * 255)})`,
        },
        data: tagData,
      },
    ],
  }
})
</script>

<style scoped></style>