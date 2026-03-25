<template>
    <div class="space-usage-analyze">
        <a-flex gap="middle">
            <a-card title="存储空间" style="width: 50%">
                <div style="height: 320px; text-align: center;">
                    <h3>{{ formatSize(data.usedSize) }} / {{ data.maxSize ? formatSize(data.maxSize) : '无限制' }}</h3>
                    <a-progress type="dashboard" :percent="data.sizeUsageRatio ?? 0" />
                </div>
            </a-card>
            <a-card title="图片数量" style="width: 50%">
                <div style="height: 320px; text-align: center;">
                    <h3>{{ data.usedCount }} / {{ data.maxCount ?? '无限制' }} </h3>
                    <a-progress type="dashboard" :percent="data.countUsageRatio ?? 0" />
                </div>
            </a-card>
        </a-flex>
    </div>
</template>

<script lang="ts" setup>
import { getSpaceUsageAnalyze } from '@/api/spaceAnalyzeController';
import { formatSize } from '@/utils';
import { message } from 'ant-design-vue';
import { ref, watchEffect } from 'vue';

interface Props {
    queryAll?: boolean
    queryPublic?: boolean
    spaceId?: any
}
const props = withDefaults(defineProps<Props>(), {
    queryAll: false,
    queryPublic: false,
})

// 图表配置
const data = ref<API.SpaceUsageAnalyzeResponse>({})
const loading = ref(false)

// 获取数据
const fetchData = async () => {
    loading.value = true
    const res = await getSpaceUsageAnalyze({
        queryAll: props.queryAll,
        queryPublic: props.queryPublic,
        spaceId: props.spaceId,
    })
    if (res.data.code === 200 && res.data.data) {
        data.value = res.data.data
    } else {
        message.error('获取数据失败，' + res.data.msg)
    }
    loading.value = false
}

watchEffect(() => {
    fetchData()
})
</script>

<style scoped></style>