<template>
    <a-modal class="image-cropper" v-model:open="visible" title="编辑图片" :footer="false" @cancel="closeModal">
        <vue-cropper ref="cropperRef" :img="imageUrl" :autoCrop="true" :fixedBox="false" :centerBox="true"
            :canMoveBox="true" :info="true" outputType="png" />
        <div style="margin-bottom: 16px;"></div>
        <!-- 图片操作 -->
        <div>
            <a-space>
                <a-button @click="rotateLeft">向左旋转</a-button>
                <a-button @click="rotateRight">向右旋转</a-button>
                <a-button @click="changeScale(1)">放大</a-button>
                <a-button @click="changeScale(-1)">缩小</a-button>
                <a-button type="primary" :loading="loading" @click="handleConfirm">确认</a-button>
            </a-space>
        </div>
    </a-modal>
</template>

<script setup lang="ts">
import { uploadPicture } from '@/api/pictureController'
import { message } from 'ant-design-vue'
import { ref } from 'vue'

interface Props {
    imageUrl?: string
    picture?: API.PictureVO
    spaceId?: any
    onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()

// 弹窗是否可见
const visible = ref(false)

// 打开弹窗
const openModal = () => {
    visible.value = true
}

// 关闭弹窗
const closeModal = () => {
    visible.value = false
}

// 编辑器组件的引用
defineExpose({
    openModal,
})

// 编辑器组件的引用
const cropperRef = ref()

// 向左旋转
const rotateLeft = () => {
    cropperRef.value.rotateLeft()
}

// 向右旋转
const rotateRight = () => {
    cropperRef.value.rotateRight()
}

// 缩放
const changeScale = (num: number) => {
    cropperRef.value.changeScale(num)
}

// 确认
const loading = ref<boolean>(false)

// 确认按钮点击事件
const handleConfirm = () => {
    cropperRef.value.getCropBlob((blob: Blob) => {
        const fileName = (props.picture?.name || 'image') + '.png'
        const file = new File([blob], fileName, { type: blob.type })
        // 上传图片
        handleUpload({ file })
    })
}

// 图片上传
const handleUpload = async ({ file }: any) => {
    loading.value = true
    try {
        const params: API.PictureUploadRequest = props.picture ? { id: props.picture.id } : {}
        params.spaceId = props.spaceId
        const res = await uploadPicture(params, {}, file)
        if (res.data.code === 200 && res.data.data) {
            message.success('图片上传成功')
            props.onSuccess?.(res.data.data)
            closeModal();
        } else {
            message.error('图片上传失败，' + res.data.msg)
        }
    } catch (error) {
        message.error('图片上传失败')
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.image-cropper {
    text-align: center;
}

.image-cropper .vue-cropper {
    height: 400px;
}
</style>