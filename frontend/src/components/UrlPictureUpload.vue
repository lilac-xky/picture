<template>
    <div class="url-picture-upload">
        <a-input-group compact>
            <a-input v-model:value="fileUrl" style="width: calc(100% - 100px)" placeholder="请输入图片URL" />
            <a-button type="primary"style="width: 100px;" :loading="loading" @click="handleUpload">上传</a-button>
        </a-input-group>
        <img v-if="picture?.url" :src="picture?.url" alt="uploaded picture" style="margin-top: 16px;" />
    </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { message } from 'ant-design-vue';
import { uploadPictureByUrl } from '@/api/pictureController';

interface Props {
    picture?: API.PictureVO;
    onSuccess?: (newPicture: API.PictureVO) => void;
}

const props = defineProps<Props>();
const fileUrl = ref<string | null>(null);
const loading = ref<boolean>(false);

// 上传图片
const handleUpload = async () => {
    loading.value = true;
    const params: API.PictureUploadRequest = { fileUrl: fileUrl.value ?? undefined };
    if(props.picture){
        params.id = props.picture.id;
    }
    const res = await uploadPictureByUrl(params);
    if (res.data?.data) {
        message.success('上传成功');
        props.onSuccess?.(res.data.data);
    } else {
        message.error('上传失败，请重试');
    }
    loading.value = false;
}
</script>

<style scoped>
.url-picture-upload {
    margin-bottom: 16px;
}

.url-picture-upload img {
    max-width: 100%;
    max-height: 480px;
}

.url-picture-upload img-wrapper {
    text-align: center;
    margin-top: 16px;
}
</style>
