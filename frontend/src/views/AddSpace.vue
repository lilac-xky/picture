<template>
    <div id="addSpace">
        <h2 style="margin-bottom: 16px;">
            {{ route.query?.id ? '编辑空间' : '添加空间' }}
        </h2>
        <!-- 空间信息表单 -->
        <a-form layout="vertical" name="spaceForm" :model="spaceForm" @finish="handleSubmit">
            <a-form-item name="spaceName" label="空间名称">
                <a-input v-model:value="spaceForm.spaceName" placeholder="请输入空间名称" />
            </a-form-item>
            <a-form-item name="spaceLevel" label="空间级别">
                <a-select v-model:value="spaceForm.spaceLevel" placeholder="请选择空间级别" style="min-width: 180px"
                    :options="SPACE_LEVEL_OPTIONS" allow-clear>
                </a-select>
            </a-form-item>
            <a-form-item>
                <a-button type="primary" html-type="submit" :loading="loading" style="width: 100%;">提交</a-button>
            </a-form-item>
        </a-form>
        <!-- 空间级别展示 -->
        <a-card title="空间级别介绍">
            <a-typography-paragraph>
                * 目前仅支持普通版空间，如需要升级空间联系管理员。
            </a-typography-paragraph>
            <a-typography-paragraph v-for="spaceLevel in spaceLevelList">
                {{ spaceLevel.text }}：大小{{ formatSize(spaceLevel.maxSize) }}, 数量{{ spaceLevel.maxCount }}
            </a-typography-paragraph>
        </a-card>
    </div>
</template>

<script setup lang="ts">
import { addSpace, getSpaceVoById, listSpaceLevel, updateSpace } from '@/api/spaceController';
import { SPACE_LEVEL_OPTIONS } from '@/constant/space';
import { formatSize } from '@/utils';
import { message } from 'ant-design-vue';
import { onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

// 表单数据类型
interface SpaceFormType {
    spaceName?: string;
    spaceLevel?: number;
}

const space = ref<API.SpaceVO>();
const spaceForm = reactive<SpaceFormType>({});
const loading = ref(false);

const spaceLevelList = ref<API.SpaceLevel[]>([]);

// 获取空间级别列表
const fetchSpaceLevelList = async () => {
    const res = await listSpaceLevel();
    if (res.data?.code === 200 && res.data?.data) {
        spaceLevelList.value = res.data?.data;
    } else {
        message.error('获取空间级别列表失败，请重试');
    }
};

const router = useRouter();
// 提交表单
const handleSubmit = async (values: any) => {
    loading.value = true;
    const spaceId = space.value?.id;
    let res;
    if (spaceId) {
        res = await updateSpace({
            id: spaceId,
            ...spaceForm,
        })
    } else {
        res = await addSpace({
            ...spaceForm,
        });
    }
    if (res.data?.data) {
        message.success('操作成功');
        router.push(`/space/${res.data?.data}`);
    } else {
        message.error('操作失败，请重试');
    }
    loading.value = false;
};

// 获取空间信息
const route = useRoute();
const getOldSpace = async () => {
    const id = route.query.id;
    if (id) {
        const res = await getSpaceVoById({ id: id as any });
        space.value = res.data?.data;
        spaceForm.spaceName = res.data?.data?.spaceName;
        spaceForm.spaceLevel = res.data?.data?.spaceLevel;
    }
};

onMounted(() => {
    getOldSpace();
    fetchSpaceLevelList();
});
</script>

<style scoped>
#addSpace {
    max-width: 720px;
    margin: 0 auto;
}
</style>