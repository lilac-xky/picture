<template>
    <div id="addPicture">
        <h2 style="margin-bottom: 16px;">
            {{ route.query?.id ? '编辑图片' : '添加图片'}}
        </h2>
        <!-- 图片上传组件 -->
        <PictureUpload :picture="picture" :onSuccess="onSuccess" />
        <!-- 图片信息表单 -->
        <a-form v-if="picture" layout="vertical" name="pictureForm" :model="pictureForm" @finish="handleSubmit">
            <a-form-item name="name" label="图片名称">
                <a-input v-model:value="pictureForm.name" placeholder="请输入图片名称" />
            </a-form-item>
            <a-form-item name="introduction" label="图片描述">
                <a-textarea v-model:value="pictureForm.introduction" placeholder="请输入图片描述"
                    :auto-size="{ minRows: 2, maxRows: 5 }" allow-clear />
            </a-form-item>
            <a-form-item name="category" label="图片分类">
                <a-auto-complete v-model:value="pictureForm.category" :options="categoryOptions" placeholder="请输入图片分类" allow-clear />
            </a-form-item>
            <a-form-item name="tags" label="图片标签">
                <a-select v-model:value="pictureForm.tags" mode="tags" :options="tagOptions" placeholder="请输入图片标签" allow-clear />
            </a-form-item>
            <a-form-item>
                <a-button type="primary" html-type="submit" style="width: 100%;">上传</a-button>
            </a-form-item>
        </a-form>
    </div>
</template>

<script setup lang="ts">
import { editPicture, getPictureVoById, listPictureTagCategory } from '@/api/pictureController';
import PictureUpload from '@/components/PictureUpload.vue';
import { message } from 'ant-design-vue';
import { onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const picture = ref<API.PictureVO>();
const pictureForm = reactive<API.PictureEditRequest>({});

// 上传成功回调
const onSuccess = (newPicture: API.PictureVO) => {
    picture.value = newPicture;
    pictureForm.name = newPicture.name;
};

// 标签和分类选项
const categoryOptions = ref<{ value: string; label: string }[]>([]);
const tagOptions = ref<{ value: string; label: string }[]>([]);

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
    const res = await listPictureTagCategory();
    tagOptions.value = (res.data?.data?.tagList || []).map((data: string) => ({ value: data, label: data }));
    categoryOptions.value = (res.data?.data?.categoryList || []).map((data: string) => ({ value: data, label: data }));
};

const router = useRouter();
// 提交表单
const handleSubmit = async (values: any) => {
    const pictureId =  picture.value?.id;
    if(!pictureId) {
        return;
    }
    const res = await editPicture({
        id: pictureId,
        ...values,
    });
    if (res.data?.data) {
        message.success('编辑成功');
        router.push(`/picture/${pictureId}`);
    } else {
        message.error('编辑失败，请重试');
    }
};

// 获取图片信息
const route = useRoute();
const getOldPicture = async () => { 
    const id = route.query.id;
    if(id) {
        const res = await getPictureVoById({ id: id as any });
        picture.value = res.data?.data;
        pictureForm.name = res.data?.data?.name;
        pictureForm.introduction = res.data?.data?.introduction;
        pictureForm.category = res.data?.data?.category;
        pictureForm.tags = res.data?.data?.tags;
    }
};

onMounted(() => {
    getTagCategoryOptions();
    getOldPicture();
});
</script>

<style scoped>
#addPicture {
    max-width: 720px;
    margin: 0 auto;
}
</style>