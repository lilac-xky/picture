<template>
    <div id="globalHeader">
        <a-row :wrap="false">
            <a-col flex="200px">
                <router-link to="/">
                    <div class="title-bar">
                        <img src="../assets/logo.png" alt="logo" class="logo"></img>
                        <div class="title">郄汐云图库</div>
                    </div>
                </router-link>
            </a-col>
            <a-col flex="auto">
                <a-menu v-model:selectedKeys="current" mode="horizontal" :items="items" @click="doMenuClick" /></a-col>
            <a-col flex="120px">
                <div class="user-login-status">
                    <div v-if="loginUserStore.loginUser.id">
                        {{ loginUserStore.loginUser.username ?? '用户' }}
                    </div>
                    <div v-else>
                        <a-button type="primary" href="/user/login">登录</a-button>
                    </div>
                </div>
            </a-col>
        </a-row>
    </div>
</template>

<script setup lang="ts">
import { h, ref } from 'vue';
import { HomeOutlined } from '@ant-design/icons-vue';
import type { MenuProps } from 'ant-design-vue';
import { useRouter } from 'vue-router';
import { useLoginUserStore } from '@/stores/useLoginUserStore';

const loginUserStore = useLoginUserStore();

const items = ref<MenuProps['items']>([
    {
        key: '/',
        icon: () => h(HomeOutlined),
        label: '主页',
    },
    {
        key: '/about',
        label: '关于',
    },
    {
        key: '/contact',
        label: '联系',
    },
]);

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
#globalHeader .title-bar {
    display: flex;
    align-items: center;
}

.title {
    color: black;
    font-size: 18px;
    margin-left: 5px;
}

.logo {
    height: 48px;
}
</style>