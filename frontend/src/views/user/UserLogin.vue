<template>
    <div id="userLogin">
        <h2 class="title">用户登录</h2>
        <div class="desc">智能协同云图库-郄汐</div>
        <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
            <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入用户名!' }]">
                <a-input v-model:value="formState.userAccount" placeholder="请输入用户名" />
            </a-form-item>

            <a-form-item name="userPassword"
                :rules="[{ required: true, message: '请输入密码!' }, { min: 6, message: '密码至少6位!' }]">
                <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
            </a-form-item>

            <div class="tips">
                没有账号？
                <router-link to="/user/register">去注册</router-link>
            </div>

            <a-form-item>
                <a-button type="primary" html-type="submit" style="width: 100%;">登录 </a-button>
            </a-form-item>
        </a-form>
    </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';
import { login } from '@/api/userController';
import { useLoginUserStore } from '@/stores/useLoginUserStore';
import { message } from 'ant-design-vue';
import router from '@/router';

// 表单数据
const formState = reactive<API.UserLoginRequest>({
    userAccount: '',
    userPassword: ''
});


const loginUserStore = useLoginUserStore();

// 提交登录
const handleSubmit = async (values: any) => {
    try {
        const res = await login(values);
        await loginUserStore.fetchLoginUser();
        message.success('登录成功');
        router.push({
            path: '/',
            replace: true
        });
    } catch (error) {
    }
};
</script>

<style>
#userLogin {
    max-width: 360px;
    margin: 0 auto;
}

.title {
    text-align: center;
    margin-bottom: 16px;
}

.desc {
    text-align: center;
    color: #bbb;
    margin-bottom: 16px;
}

.tips {
    color: #bbb;
    text-align: right;
    font-size: 13px;
    margin-bottom: 16px;
}
</style>