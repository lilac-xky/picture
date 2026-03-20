<template>
    <div class="picture-search-form">
        <!-- 搜索表单 -->
        <a-form layout="inline" :model="searchParams" @finish="doSearch">
            <a-form-item label="关键词" name="searchText">
                <a-input v-model:value="searchParams.searchText" placeholder="从名称和简介搜索" allow-clear />
            </a-form-item>
            <a-form-item label="分类" name="category">
                <a-auto-complete style="min-width: 180px" v-model:value="searchParams.category"
                    :options="categoryOptions" placeholder="请输入分类" allow-clear />
            </a-form-item>
            <a-form-item label="标签" name="tags">
                <a-select style="min-width: 180px" v-model:value="searchParams.tags" :options="tagOptions" mode="tags"
                    placeholder="请输入标签" allowClear />
            </a-form-item>
            <a-form-item label="日期" name="">
                <a-range-picker show-time v-model:value="dateRange" :placeholder="['编辑开始日期', '编辑结束时间']"
                    format="YYYY/MM/DD HH:mm:ss" :presets="rangePresets" @change="onRangeChange" />
            </a-form-item>
            <a-form-item label="名称" name="name">
                <a-input v-model:value="searchParams.name" placeholder="请输入名称" allow-clear />
            </a-form-item>
            <a-form-item label="简介" name="introduction">
                <a-input v-model:value="searchParams.introduction" placeholder="请输入简介" allow-clear />
            </a-form-item>
            <a-form-item label="宽度" name="picWidth">
                <a-input-number v-model:value="searchParams.picWidth" />
            </a-form-item>
            <a-form-item label="高度" name="picHeigh">
                <a-input-number v-model:value="searchParams.picHeigh" />
            </a-form-item>
            <a-form-item label="格式" name="picFormat">
                <a-input v-model:value="searchParams.picFormat" placeholder="请输入格式" allow-clear />
            </a-form-item>
            <a-form-item>
                <a-space>
                    <a-button type="primary" html-type="submit">搜索</a-button>
                    <a-button html-type="reset" @click="doClear()">重置</a-button>
                </a-space>
            </a-form-item>
        </a-form>
    </div>
</template>
<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue';
import dayjs from 'dayjs';
import { message } from 'ant-design-vue';
import { listPictureTagCategory } from '@/api/pictureController';

interface Props {
    onSearch?: (searchParams: API.PictureQueryRequest) => void
}
const props = defineProps<Props>()

// 日期
const dateRange = ref<[]>([])

// 分类选项
const rangePresets = ref([
    { label: '过去 7 天', value: [dayjs().add(-7, 'd'), dayjs()] },
    { label: '过去 14 天', value: [dayjs().add(-14, 'd'), dayjs()] },
    { label: '过去 30 天', value: [dayjs().add(-30, 'd'), dayjs()] },
    { label: '过去 90 天', value: [dayjs().add(-90, 'd'), dayjs()] },
])

// 日期范围更改触发
const onRangeChange = (dates: any[], dateStrings: string[]) => {
    if (dates.length < 2) {
        searchParams.startEditTime = undefined
        searchParams.endEditTime = undefined
    } else {
        searchParams.startEditTime = dates[0].toDate()
        searchParams.endEditTime = dates[1].toDate()
    }
}

// 分类和标签选项
const categoryOptions = ref<{ value: string; label: string }[]>([]);
const tagOptions = ref<{ value: string; label: string }[]>([]);

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
    const res = await listPictureTagCategory()
    if (res.data.code === 200 && res.data.data) {
        tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
            return {
                value: data,
                label: data,
            }
        })
        categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
            return {
                value: data,
                label: data,
            }
        })
    } else {
        message.error('加载选项失败，' + res.data.msg)
    }
}

// 查询参数
const searchParams = reactive<API.PictureQueryRequest>({});

// 搜索
const doSearch = () => {
    props.onSearch?.(searchParams);
}

// 重置
const doClear = () => {
    Object.keys(searchParams).forEach(key => {
        searchParams[key as keyof API.PictureQueryRequest] = undefined;
    });
    dateRange.value = []
    props.onSearch?.(searchParams);
}

onMounted(() => {
    getTagCategoryOptions()
})
</script>

<style scoped>
.picture-search-form .ant-form-item {
    margin-top: 16px;
}
</style>