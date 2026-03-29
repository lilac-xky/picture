<template>
    <a-modal class="image-cropper" v-model:open="visible" title="编辑图片" :footer="false" @cancel="closeModal">
        <vue-cropper ref="cropperRef" :img="imageUrl" :autoCrop="true" :fixedBox="false" :centerBox="true"
            :canMoveBox="true" :info="true" outputType="png" />
        <div style="margin-bottom: 16px;"></div>
        <!-- 协同编辑操作 -->
        <div v-if="isTeamSpace">
            <a-space>
                <a-button v-if="editingUser" disabled> {{ editingUser.userName }}正在编辑</a-button>
                <a-button v-if="canEnterEdit" type="primary" ghost @click="enterEdit">进入编辑</a-button>
                <a-button v-if="canExitEdit" danger ghost @click="exitEdit">退出编辑</a-button>
            </a-space>
        </div>
        <!-- 图片操作 -->
        <div>
            <a-space>
                <a-button @click="rotateLeft" :disabled="!canEdit">向左旋转</a-button>
                <a-button @click="rotateRight" :disabled="!canEdit">向右旋转</a-button>
                <a-button @click="changeScale(1)" :disabled="!canEdit">放大</a-button>
                <a-button @click="changeScale(-1)" :disabled="!canEdit">缩小</a-button>
                <a-button type="primary" :loading="loading" :disabled="!canEdit" @click="handleConfirm">
                    确认
                </a-button>
            </a-space>
        </div>
    </a-modal>
</template>

<script setup lang="ts">
import { uploadPicture } from '@/api/pictureController'
import { PICTURE_EDIT_ACTION_ENUM, PICTURE_EDIT_MESSAGE_TYPE_ENUM } from '@/constant/picture'
import { SPACE_TYPE_ENUM } from '@/constant/space'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import PictureEditWebSocket from '@/utils/pictureEditWebSocket'
import { message } from 'ant-design-vue'
import { computed, onUnmounted, ref, watchEffect } from 'vue'

interface Props {
    imageUrl?: string
    picture?: API.PictureVO
    spaceId?: any
    space?: API.SpaceVO
    onSuccess?: (newPicture: API.PictureVO) => void
}
const props = defineProps<Props>()

// 是否是团队空间
const isTeamSpace = computed(() => {
    return props.space?.spaceType === SPACE_TYPE_ENUM.TEAM;
})

// 弹窗是否可见
const visible = ref(false)

// 打开弹窗
const openModal = () => {
    visible.value = true
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
    editAction(PICTURE_EDIT_ACTION_ENUM.ROTATE_LEFT)
}

// 向右旋转
const rotateRight = () => {
    cropperRef.value.rotateRight()
    editAction(PICTURE_EDIT_ACTION_ENUM.ROTATE_RIGHT)
}

// 缩放
const changeScale = (num: number) => {
    cropperRef.value.changeScale(num)
    if (num > 0) {
        editAction(PICTURE_EDIT_ACTION_ENUM.ZOOM_IN)
    } else {
        editAction(PICTURE_EDIT_ACTION_ENUM.ZOOM_OUT)
    }
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

// 获取登录用户信息
const loginUserStore = useLoginUserStore()
let loginUser = loginUserStore.loginUser

const editingUser = ref<API.UserVO>()

// 是否可以进入编辑模式
const canEnterEdit = computed(() => {
    return !editingUser.value
})

// 是否可以退出编辑模式
const canExitEdit = computed(() => {
    if (!isTeamSpace.value) {
        return true
    }
    return editingUser.value?.id === loginUser.id
})

// 是否可以编辑
const canEdit = computed(() => {
    return editingUser.value?.id === loginUser.id
})

// 协同编辑 WebSocket
let websocket: PictureEditWebSocket | null
// 初始化 WebSocket 连接
const initWebsocket = () => {
    const pictureId = props.picture?.id
    if (!pictureId || !visible.value) {
        return
    }
    if (websocket) {
        websocket.disconnect()
    }
    websocket = new PictureEditWebSocket(pictureId)
    websocket.connect()
    
    // 监听 WebSocket 消息
    websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.INFO, (msg) => {
        console.log('收到通知消息：', msg)
        message.info(msg.message)
    })

    // 监听 WebSocket 错误
    websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.ERROR, (msg) => {
        console.log('收到错误消息：', msg)
        message.error(msg.message)
    })

    // 监听 WebSocket 进入编辑状态
    websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.ENTER_EDIT, (msg) => {
        console.log('收到进入编辑状态消息：', msg)
        message.info(msg.message)
        editingUser.value = msg.user
    })

    // 监听 WebSocket 退出编辑状态
    websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.EDIT_ACTION, (msg) => {
        console.log('收到编辑操作消息：', msg)
        message.info(msg.message)
        switch (msg.editAction) {
            case PICTURE_EDIT_ACTION_ENUM.ROTATE_LEFT:
                cropperRef.value.rotateLeft()
                break
            case PICTURE_EDIT_ACTION_ENUM.ROTATE_RIGHT:
                cropperRef.value.rotateRight()
                break
            case PICTURE_EDIT_ACTION_ENUM.ZOOM_IN:
                cropperRef.value.changeScale(1)
                break
            case PICTURE_EDIT_ACTION_ENUM.ZOOM_OUT:
                cropperRef.value.changeScale(-1)
                break
        }
    })
    // 监听 WebSocket 退出编辑状态
    websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.EXIT_EDIT, (msg) => {
        console.log('收到退出编辑状态消息：', msg)
        message.info(msg.message)
        editingUser.value = undefined
    })
}

// 监听 visible 变化，初始化 WebSocket 连接
watchEffect(() => {
    if (isTeamSpace.value) {
        initWebsocket()
    }
})

// 组件卸载时断开 WebSocket 连接
onUnmounted(() => {
    if (websocket) {
        websocket.disconnect()
    }
    editingUser.value = undefined
})

// 关闭弹窗
const closeModal = () => {
    visible.value = false
    if (websocket) {
        websocket.disconnect()
    }
    editingUser.value = undefined
}

// 进入编辑模式
const enterEdit = () => {
    if (websocket) {
        websocket.sendMessage({
            type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.ENTER_EDIT,
        })
    }
}

// 退出编辑模式
const exitEdit = () => {
    if (websocket) {
        websocket.sendMessage({
            type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.EXIT_EDIT,
        })
    }
}

// 编辑操作
const editAction = (action: string) => {
    if (websocket) {
        websocket.sendMessage({
            type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.EDIT_ACTION,
            editAction: action,
        })
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