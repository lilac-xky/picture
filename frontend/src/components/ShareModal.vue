<template>
    <div>
        <a-modal v-model:open="visible" :title="title" :footer="false" @cancel="closeModel">
            <h4>复制分享链接</h4>
            <a-typography-link copyable>
                {{ link }}
            </a-typography-link>
            <div style="margin-bottom: 16px"></div>
            <h4>手机扫码查看</h4>
            <a-qrcode :value="link" />
        </a-modal>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface Props {
    title: string;
    link: string;
}
const props = withDefaults(defineProps<Props>(), {
    title: '分享图片',
    link: 'https://tanghc.xyz/',
})

// 模态框可见性
const visible = ref(false)

// 打开弹窗
const openModel = () => {
    visible.value = true
}

// 关闭弹窗
const closeModel = () => {
    visible.value = false
}

// 向外暴露方法
defineExpose({
    openModel,
})
</script>