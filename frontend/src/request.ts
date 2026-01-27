import axios from 'axios';
import { message } from 'ant-design-vue';

const request = axios.create({
    baseURL: 'http://localhost:9090',
    timeout: 60000,
    withCredentials: true,
});

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器
request.interceptors.response.use(
    (response) => {
        const { data } = response;
        if (data.code !== 200) {
            message.error(data.msg || '请求出错');
            return Promise.reject(data);
        }
        return response;
    },
    (error) => {
        const { response, config } = error;
        if (response && response.data) {
            const backendData = response.data;
            if (config.url?.includes('/user/get/login')) {
                return Promise.reject(backendData);
            }
            const errMsg = backendData.msg || '系统异常';
            message.error(errMsg);
            return Promise.reject(backendData);
        }

        message.error('网络请求失败');
        return Promise.reject(error);
    }
);

export default request;