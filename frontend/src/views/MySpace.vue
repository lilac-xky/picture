<template>
    <div id="mySpace">
        <p>正在跳转，请稍后</p>
    </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { listSpaceVoByPage } from '@/api/spaceController'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 检查用户是否有个人空间
const checkUserSpace = async () => {
    const loginUser = loginUserStore.loginUser
    if (!loginUser?.id) {
        router.replace('/user/login')
        return
    }

    // 获取用户空间信息
    const res = await listSpaceVoByPage({
        userId: loginUser.id,
        current: 1,
        pageSize: 1,
        spaceType: 0,
    })

    if (res.data.code === 200) {
        // 用户有空间
        const records = res.data.data?.records || []
        if (records.length > 0) {
            const space = records[0]
            router.replace(`/space/${space?.id}`)
        } else {
            // 用户没有空间,跳转到创建空间页面
            router.replace('/add_space')
            message.warn('请先创建空间')
        }
    } else {
        message.error('加载我的空间失败,' + res.data.msg)
    }
}

// 在页面加载时检查用户空间
onMounted(() => {
    checkUserSpace()
})
</script>

<style scoped></style>