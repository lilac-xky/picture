import axios from 'axios';

const request = axios.create({
    baseURL: 'http://localhost:9090',
    timeout: 60000,
    withCredentials: true,
});

// 请求拦截器
axios.interceptors.request.use(function (config) {
    return config;
}, function (error) {
    return Promise.reject(error);
});

// 响应拦截器
axios.interceptors.response.use(function onFulfilled(response) {
    const { data } = response;
    // TODO: 根据项目实际情况修改
    if (data.code !== 200) {
        return Promise.reject(data);
    }
    return response;
}, function onRejected(error) {
    return Promise.reject(error);
});

export default request;