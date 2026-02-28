<template>
    <div class="PictureManage">
        <a-flex justify="space-between">
            <h2>图片管理</h2>
            <a-space>
                <a-button type="primary" :href="'/add_picture'">创建图片</a-button>
                <a-button type="primary" :href="'/add_picture/batch'" ghost>批量创建图片</a-button>
            </a-space>
        </a-flex>
        <div style="margin-bottom: 16px;"></div>
        <!-- 查询表单 -->
        <a-form :model="searchParams" layout="inline" @finish="doSearch">
            <a-form-item label="关键词">
                <a-input v-model:value="searchParams.searchText" placeholder="从名称和简介中搜索" allow-clear />
            </a-form-item>
            <a-form-item label="类型">
                <a-input v-model:value="searchParams.category" placeholder="请输入类型" allow-clear />
            </a-form-item>
            <a-form-item label="标签">
                <a-select v-model:value="searchParams.tags" mode="tags" placeholder="请输入标签" style="min-width: 180px"
                    allow-clear />
            </a-form-item>
            <a-form-item label="审核状态">
                <a-select v-model:value="searchParams.reviewStatus" placeholder="请选择审核状态" style="min-width: 180px"
                    :options="PIC_REVIEW_STATUS_OPTIONS" allow-clear>
                </a-select>
            </a-form-item>
            <a-form-item>
                <a-button type="primary" html-type="submit">查询</a-button>
            </a-form-item>
        </a-form>
        <div style="margin-top: 16px;"></div>
        <!-- 表格列定义 -->
        <a-table :columns="columns" :data-source="dataList" :pagination="pagination" @change="doTableChange">
            <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'url'">
                    <a-image :src="record.url" width="120px" />
                </template>
                <template v-if="column.dataIndex === 'tags'">
                    <a-space wrap>
                        <a-tag v-for="tag in JSON.parse(record.tags || '[]')" :key="tag">{{ tag }}</a-tag>
                    </a-space>
                </template>
                <template v-if="column.dataIndex === 'picInfo'">
                    <div>格式：{{ record.picFormat }}</div>
                    <div>宽度：{{ record.picWidth }}</div>
                    <div>高度：{{ record.picHeigh }}</div>
                    <div>宽高比：{{ record.picScale }}</div>
                    <div>大小：{{ (record.picSize / 1024).toFixed(2) }}KB</div>
                </template>
                <template v-if="column.dataIndex === 'reviewMessage'">
                    <div>审核状态：{{ PIC_REVIEW_STATUS_MAP[record.reviewStatus as keyof typeof PIC_REVIEW_STATUS_MAP] }}
                    </div>
                    <div>审核信息：{{ record.reviewMessage }}</div>
                    <div>审核人：{{ record.reviewerId }}</div>
                    <div v-if="record.reviewTime">审核时间：{{ dayjs(record.reviewTime).format('YYYY-MM-DD HH:mm:ss') }}
                    </div>
                </template>
                <template v-if="column.dataIndex === 'createTime'">
                    {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
                </template>
                <template v-if="column.dataIndex === 'editTime'">
                    {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
                </template>
                <template v-else-if="column.key === 'action'">
                    <a-space wrap>
                        <a-button v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.PASS" type="link"
                            @click="handleReview(record, PIC_REVIEW_STATUS_ENUM.PASS)">通过</a-button>
                        <a-button v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.REJECT" type="link" danger
                            @click="handleReview(record, PIC_REVIEW_STATUS_ENUM.REJECT)">拒绝</a-button>
                        <a-button type="primary" :href="`/add_picture?id=${record.id}`">编辑</a-button>
                        <a-button danger @click="doDelete(record.id)">删除</a-button>
                    </a-space>
                </template>
            </template>
        </a-table>
    </div>
</template>
<script lang="ts" setup>
import { deletePicture, doPictureReview, listPictureByPage } from '@/api/pictureController';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';
import { computed, onMounted, reactive, ref } from 'vue';
import { PIC_REVIEW_STATUS_ENUM, PIC_REVIEW_STATUS_MAP, PIC_REVIEW_STATUS_OPTIONS } from '@/constant/picture';

// 表格列定义
const columns = [
    {
        title: 'Id',
        dataIndex: 'id',
        width: 80,
    },
    {
        title: '图片',
        dataIndex: 'url',
    },
    {
        title: '图片名称',
        dataIndex: 'name',
    },
    {
        title: '简介',
        dataIndex: 'introduction',
    },
    {
        title: '类型',
        dataIndex: 'category',
    },
    {
        title: '标签',
        dataIndex: 'tags',
    }, {
        title: '图片信息',
        dataIndex: 'picInfo',
    },
    {
        title: '用户Id',
        dataIndex: 'userId',
    },
    {
        title: '审核信息',
        dataIndex: 'reviewMessage',
    },
    {
        title: '创建时间',
        dataIndex: 'createTime',
    },
    {
        title: '编辑时间',
        dataIndex: 'editTime',
    },
    {
        title: '操作',
        key: 'action',
    },
];

// 数据
const dataList = ref<API.Picture[]>([]);
const total = ref(0);

// 查询参数
const searchParams = reactive<API.PictureQueryRequest>({
    current: 1,
    pageSize: 10,
    sortField: 'createTime',
    sortOrder: 'descend',
});

// 分页
const pagination = computed(() => {
    return {
        current: searchParams.current,
        pageSize: searchParams.pageSize,
        total: total.value,
        showSizeChanger: true,
        showTotal: (total: number) => `总共 ${total} 条`,
    }
});

// 表格变化事件
const doTableChange = (page: any) => {
    searchParams.current = page.current;
    searchParams.pageSize = page.pageSize;
    fetchData();
}

// 获取数据
const fetchData = async () => {
    try {
        const res: any = await listPictureByPage({
            ...searchParams,
        });
        dataList.value = res.data.data.records || [];
        total.value = Number(res.data.data.total) || 0;
    }
    catch (error) {
        dataList.value = [];
        total.value = 0;
    }
}

// 搜索
const doSearch = () => {
    searchParams.current = 1;
    fetchData();
}

// 删除
const doDelete = async (id: number) => {
    if (!id) {
        return;
    }
    const res = await deletePicture({ id });
    if (res.data.code === 200 && res.data.data) {
        message.success('删除成功');
        fetchData();
    } else {
        message.error('删除失败');
    }
}

// 审核
const handleReview = async (record: API.Picture, reviewStatus: number) => {
    const reviewMessage = reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? '管理员审核通过' : '管理员审核拒绝';
    const res = await doPictureReview({ id: record.id, reviewStatus, reviewMessage });
    if (res.data.code === 200 && res.data.data) {
        message.success('审核成功');
        fetchData();
    } else {
        message.error('审核失败' + (res.data.msg));
    }
}

// 页面加载时获取数据
onMounted(() => {
    fetchData();
});
</script>

<style></style>