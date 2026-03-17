<template>
    <div class="SpaceManage">
        <a-flex justify="space-between">
            <h2>空间管理</h2>
            <a-space>
                <a-button type="primary" :href="'/add_space'">创建空间</a-button>
            </a-space>
        </a-flex>
        <div style="margin-bottom: 16px;"></div>
        <!-- 查询表单 -->
        <a-form :model="searchParams" layout="inline" @finish="doSearch">
            <a-form-item label="空间名称">
                <a-input v-model:value="searchParams.spaceName" placeholder="请输入空间名称" allow-clear />
            </a-form-item>
            <a-form-item label="空间级别">
                <a-select v-model:value="searchParams.spaceLevel" placeholder="请选择空间级别" style="min-width: 180px"
                    :options="SPACE_LEVEL_OPTIONS" allow-clear>
                </a-select>
            </a-form-item>
            <a-form-item label="用户id">
                <a-input v-model:value="searchParams.userId" placeholder="请输入用户id" allow-clear />
            </a-form-item>
            <a-form-item>
                <a-button type="primary" html-type="submit">查询</a-button>
            </a-form-item>
        </a-form>
        <div style="margin-top: 16px;"></div>
        <!-- 表格列定义 -->
        <a-table :columns="columns" :data-source="dataList" :pagination="pagination" @change="doTableChange">
            <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'spaceLevel'">
                    {{ SPACE_LEVEL_MAP[record.spaceLevel] }}
                </template>
                <template v-if="column.dataIndex === 'spaceUseInfo'">
                    <div>大小：{{ formatSize(record.totalSize) }} / {{ formatSize(record.maxSize) }}</div>
                    <div>数量：{{ record.totalCount }} / {{ record.maxCount }}</div>
                </template>
                <template v-if="column.dataIndex === 'createTime'">
                    {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
                </template>
                <template v-if="column.dataIndex === 'editTime'">
                    {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
                </template>
                <template v-if="column.key === 'action'">
                    <a-space wrap>
                        <a-button type="link" :href="`/add_space?id=${record.id}`">编辑</a-button>
                        <a-button type="link" danger @click="doDelete(record.id)">删除</a-button>
                    </a-space>
                </template>
            </template>
        </a-table>
    </div>
</template>
<script lang="ts" setup>
import { deleteSpace, listSpaceByPage } from '@/api/spaceController';
import { SPACE_LEVEL_MAP, SPACE_LEVEL_OPTIONS } from '@/constant/space';
import { formatSize } from '@/utils';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';
import { computed, onMounted, reactive, ref } from 'vue';

// 表格列定义
const columns = [
    {
        title: 'id',
        dataIndex: 'id',
        width: 80,
    },
    {
        title: '空间名称',
        dataIndex: 'spaceName',
    },
    {
        title: '空间级别',
        dataIndex: 'spaceLevel',
    },
    {
        title: '使用情况',
        dataIndex: 'spaceUseInfo',
    },
    {
        title: '用户 id',
        dataIndex: 'userId',
        width: 80,
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
]

// 数据
const dataList = ref<API.Space[]>([]);
const total = ref(0);

// 查询参数
const searchParams = reactive<API.SpaceQueryRequest>({
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
        const res: any = await listSpaceByPage({
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
    const res = await deleteSpace({ id });
    if (res.data.code === 200 && res.data.data) {
        message.success('删除成功');
        fetchData();
    } else {
        message.error('删除失败');
    }
}

// 页面加载时获取数据
onMounted(() => {
    fetchData();
});
</script>

<style></style>