<template>
    <div id="userRegister">
        <h2 class="title">用户注册</h2>
        <div class="desc">智能协同云图库-郄汐</div>
        <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
            <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入用户名!' }]">
                <a-input v-model:value="formState.userAccount" placeholder="请输入用户名" />
            </a-form-item>

            <a-form-item name="userPassword"
                :rules="[{ required: true, message: '请输入密码!' }, { min: 6, message: '密码至少6位!' }]">
                <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
            </a-form-item>

            <a-form-item name="checkPassword"
                :rules="[{ required: true, message: '请再次输入密码!' }, { min: 6, message: '确认密码至少6位!' }]">
                <a-input-password v-model:value="formState.checkPassword" placeholder="请再次输入密码" />
            </a-form-item>

            <div class="tips">
                已有账号？
                <router-link to="/user/login">去登录</router-link>
            </div>

            <a-form-item>
                <a-button type="primary" html-type="submit" style="width: 100%;">注册 </a-button>
            </a-form-item>
        </a-form>
    </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';
import { register } from '@/api/userController';
import { message } from 'ant-design-vue';
import router from '@/router';

// 表单数据
const formState = reactive<API.UserRegisterRequest>({
    userAccount: '',
    userPassword: '',
    checkPassword: ''
});

// 提交表单
const handleSubmit = async (values: any) => {
    if (values.userPassword !== values.checkPassword) {
        message.error('密码不一致');
        return;
    }
    try {
        const res = await register(values);
        if (res) { 
            message.success('注册成功');
            router.push({
                path: '/user/login',
                replace: true
            });
        }
    } catch (error) {
    }
};
</script>

<style>
#userRegister {
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