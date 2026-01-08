import { computed, ref } from 'vue';
import { defineStore } from 'pinia';

/**
 * 登录用户信息
 */
export const useLoginUserStore = defineStore('loginUser', () => {
    const loginUser = ref<any>(
        { username: '未登录' }
    );

    async function fetchLoginUser() {
        // TODO: 获取登录用户信息
        // const res = await getCurrentUser();
        // if (res.data.code === 200 && res.data.data) {
        //     loginUser.value = res;
        // }
    }

    function setLoginUser(newLoginUser: any) {
        loginUser.value = newLoginUser;
    }

    function logout() {
        loginUser.value = null;
    }

    return { loginUser, fetchLoginUser, logout };
})