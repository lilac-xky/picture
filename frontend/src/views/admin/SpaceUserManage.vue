<template>
    <div class="SpaceUserManage">
        <a-flex justify="space-between">
            <h2>空间成员管理</h2>
            <a-space>
                <a-button type="primary" :href="'/add_space'">创建空间</a-button>
                <a-button type="primary" ghost href="/space_analyze?queryPublic=1">
                    分析公共图库
                </a-button>
                <a-button type="primary" ghost href="/space_analyze?queryAll=1">
                    分析全空间
                </a-button>
            </a-space>
        </a-flex>
        <div style="margin-bottom: 16px;"></div>
        <!-- 添加成员表单 -->
        <a-form layout="inline" :model="formData" @finish="handleSubmit">
            <a-form-item label="用户 id" name="userId">
                <a-input v-model:value="formData.userId" placeholder="请输入用户 id" allow-clear />
            </a-form-item>
            <a-form-item>
                <a-button type=" primary" html-type="submit">添加用户</a-button>
            </a-form-item>
        </a-form>
        <div style="margin-top: 16px;"></div>
        <!-- 表格列定义 -->
        <a-table :columns="columns" :data-source="dataList">
            <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'userInfo'">
                    <a-space>
                        <a-avatar :src="record.user?.userAvatar" />
                        {{ record.user?.userName }}
                    </a-space>
                </template>
                <template v-if="column.dataIndex === 'spaceRole'">
                    <a-select v-model:value="record.spaceRole" :options="SPACE_ROLE_OPTIONS"
                        @change="(value: string) => editSpaceRole(value, record)" />
                </template>
                <template v-else-if="column.dataIndex === 'createTime'">
                    {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
                </template>
                <template v-else-if="column.key === 'action'">
                    <a-space wrap>
                        <a-button type="link" danger @click="doDelete(record.id)">删除
                        </a-button>
                    </a-space>
                </template>
            </template>
        </a-table>
    </div>
</template>
<script lang="ts" setup>
import { addSpaceUser, deleteSpaceUser, editSpaceUser, listSpaceUser } from '@/api/spaceUserController';
import { SPACE_ROLE_OPTIONS } from '@/constant/space';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';
import { onMounted, reactive, ref } from 'vue';

// 表格列定义
const columns = [
    {
        title: '用户',
        dataIndex: 'userInfo',
    },
    {
        title: '角色',
        dataIndex: 'spaceRole',
    },
    {
        title: '创建时间',
        dataIndex: 'createTime',
    },
    {
        title: '操作',
        key: 'action',
    },
]

interface Props {
    id: any
}
const props = defineProps<Props>()

// 数据
const dataList = ref<API.SpaceUserVO[]>([]);

// 添加成员表单数据
const formData = reactive<API.SpaceUserAddRequest>({})

// 提交表单
const handleSubmit = async () => {
    const spaceId = props.id
    if (!spaceId) {
        return
    }
    const res = await addSpaceUser({
        spaceId,
        ...formData,
    })
    if (res.data.code === 200) {
        message.success('添加成功')
        formData.userId = undefined
        fetchData()
    } else {
        message.error('添加失败，' + res.data.msg)
    }
}

// 获取数据
const fetchData = async () => {
    const spaceId = props.id
    if (!spaceId) {
        return
    }
    const res = await listSpaceUser({
        spaceId,
    })
    if (res.data.data) {
        dataList.value = res.data.data ?? []
    } else {
        message.error('获取数据失败，' + res.data.msg)
    }
}

// 编辑空间成员角色
const editSpaceRole = async (value: string, record: API.SpaceUserVO) => {
    const res = await editSpaceUser({
        id: record.id,
        spaceRole: value,
    })
    if (res.data.code === 200) {
        message.success('修改成功')
    } else {
        message.error('修改失败，' + res.data.msg)
    }
}

// 删除
const doDelete = async (id: number) => {
    if (!id) {
        return
    }
    const res = await deleteSpaceUser({ id })
    if (res.data.code === 200) {
        message.success('删除成功')
        fetchData()
    } else {
        message.error('删除失败')
    }
}

// 页面加载时获取数据
onMounted(() => {
    fetchData();
});
</script>

<style></style>