<template>
    <div id="addPictureBatchBatch">
        <h2 style="margin-bottom: 16px;">
            批量创建图片
        </h2>
        <!-- 图片信息表单 -->
        <a-form layout="vertical" name="formData" :model="formData" @finish="handleSubmit">
            <a-form-item name="searchText" label="搜索关键词">
                <a-input v-model:value="formData.searchText" placeholder="请输入搜索关键词" />
            </a-form-item>
            <a-form-item name="count" label="抓取数量">
                <a-input-number v-model:value="formData.count" :min="1" :max="30" style="min-width: 180px;" placeholder="请输入抓取数量" />
            </a-form-item>
            <a-form-item name="namePrefix" label="图片名称前缀">
                <a-input v-model:value="formData.namePrefix" placeholder="请输入图片名称前缀" />
            </a-form-item>
            <a-form-item>
                <a-button type="primary" html-type="submit" style="width: 100%;" :loading="loading">执行任务</a-button>
            </a-form-item>
        </a-form>
    </div>
</template>

<script setup lang="ts">
import { editPicture, uploadPictureByBatch } from '@/api/pictureController';
import { message } from 'ant-design-vue';
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

const formData = reactive<API.PictureUploadByBatchRequest>({
    count: 10,
});

const loading = ref(false);

const router = useRouter();
// 提交表单
const handleSubmit = async (values: any) => {
    loading.value = true;
    const res = await uploadPictureByBatch({
        ...formData,
    });
    if (res.data?.data) {
        message.success('批量上传成功,共上传' + res.data.data + '张图片');
        router.push(`/`);
    } else {
        message.error('批量上传失败，请重试');
    }
    loading.value = false;
};
</script>

<style scoped>
#addPictureBatch {
    max-width: 720px;
    margin: 0 auto;
}
</style>