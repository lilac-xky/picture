<template>
    <div id="globalSider">
        <a-layout-sider v-if="loginUserStore.loginUser.id" width="200" breakpoint="lg" collapsed-width="0">
            <a-menu v-model:selectedKeys="current" mode="inline" :items="menuItems" @click="doMenuClick" />
        </a-layout-sider>
    </div>
</template>

<script setup lang="ts">
import { computed, h, ref, watch, watchEffect } from 'vue';
import { PictureOutlined, TeamOutlined, UserOutlined } from '@ant-design/icons-vue';
import { useRoute, useRouter } from 'vue-router';
import { useLoginUserStore } from '@/stores/useLoginUserStore';
import { SPACE_TYPE_ENUM } from '@/constant/space';
import { message } from 'ant-design-vue';
import { listMyTeamSpace } from '@/api/spaceUserController';

const loginUserStore = useLoginUserStore();

// 固定菜单项
const fixedMenuItems = [
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
    {
        key: '/add_space?type=' + SPACE_TYPE_ENUM.TEAM,
        icon: () => h(TeamOutlined),
        label: '创建团队',
    },
];

// 团队空间列表
const teamSpaceList = ref<API.SpaceUserVO[]>([])
const menuItems = computed(() => {
    // 如果没有团队空间，则只显示固定菜单项
    if (teamSpaceList.value.length < 1) {
        return fixedMenuItems;
    }
    // 团队空间菜单项
    const teamSpaceSubMenus = teamSpaceList.value.map((spaceUser) => {
        const space = spaceUser.space
        return {
            key: '/space/' + spaceUser.spaceId,
            label: space?.spaceName,
        }
    })
    const teamSpaceMenuGroup = {
        type: 'group',
        label: '我的团队',
        key: 'teamSpace',
        children: teamSpaceSubMenus,
    }
    return [...fixedMenuItems, teamSpaceMenuGroup]
})

const route = useRoute();
const router = useRouter();

// 获取我的团队空间列表
const fetchTeamSpaceList = async () => {
    const res = await listMyTeamSpace()
    if (res.data.code === 200 && res.data.data) {
        teamSpaceList.value = res.data.data
    } else {
        message.error('加载我的团队空间失败，' + res.data.msg)
    }
}

// 当前高亮菜单
const current = ref<string[]>([]);
router.afterEach((to, from, next) => {
    current.value = [to.path];
});

// 路由跳转
const doMenuClick = ({ key }: { key: string }) => {
    router.push(key)
}

// 监听用户登录状态变化
watchEffect(() => {
    // 当用户登录状态变化时，重新获取团队空间列表
    if (loginUserStore.loginUser.id) {
        fetchTeamSpaceList()
    }
})

// 监听路由变化，刷新团队空间列表
watch(
    () => route.path,
    () => {
        if (loginUserStore.loginUser.id) {
            fetchTeamSpaceList()
        }
    }
)
</script>

<style scoped>
#globalSider .ant-layout-sider {
    background-color: white;
}
</style>
