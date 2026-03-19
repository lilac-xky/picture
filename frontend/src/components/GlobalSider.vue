<template>
    <div id="globalSider">
        <a-layout-sider v-if="loginUserStore.loginUser.id" width="200" breakpoint="lg" collapsed-width="0">
            <a-menu v-model:selectedKeys="current" mode="inline" :items="menuItems" @click="doMenuClick" />
        </a-layout-sider>
    </div>
</template>

<script setup lang="ts">
import { h, ref } from 'vue';
import { PictureOutlined, UserOutlined } from '@ant-design/icons-vue';
import { useRouter } from 'vue-router';
import { useLoginUserStore } from '@/stores/useLoginUserStore';

const loginUserStore = useLoginUserStore();

// 菜单项
const menuItems = [
    {
        key: '/',
        icon: () => h(PictureOutlined),
        label: '公共图库',
    },
    {
        key: '/my_space',
        icon: () => h(UserOutlined),
        label: '个人空间',
    },
];

const router = useRouter();

// 当前高亮菜单
const current = ref<string[]>([]);
router.afterEach((to, from, next) => {
    current.value = [to.path];
});

// 路由跳转
const doMenuClick = ({ key }: { key: string }) => {
    router.push({
        path: key
    });
}
</script>

<style scoped>
#globalSider .ant-layout-sider{
    background-color: white;
}
</style>
