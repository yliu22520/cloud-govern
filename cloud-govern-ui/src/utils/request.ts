import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { MessagePlugin } from 'tdesign-vue-next';

// 响应数据结构
export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { data } = response;

    // 业务成功
    if (data.code === 200) {
      return data.data as any;
    }

    // 业务失败 - 兼容 msg 和 message 字段
    const errorMsg = (data as any).msg || data.message || '请求失败';
    MessagePlugin.error(errorMsg);
    return Promise.reject(new Error(errorMsg));
  },
  (error) => {
    const { response } = error;

    if (response) {
      // 兼容 msg 和 message 字段
      const errorMsg = response.data?.msg || response.data?.message || '请求失败';
      switch (response.status) {
        case 401:
          MessagePlugin.warning('登录已过期，请重新登录');
          localStorage.removeItem('token');
          window.location.href = '/login';
          break;
        case 403:
          MessagePlugin.error('无权限访问');
          break;
        case 404:
          MessagePlugin.error('资源不存在');
          break;
        case 500:
          MessagePlugin.error(errorMsg);
          break;
        default:
          MessagePlugin.error(errorMsg);
      }
    } else {
      MessagePlugin.error('网络错误，请检查网络连接');
    }

    return Promise.reject(error);
  }
);

// 封装请求方法
export const http = {
  get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return request.get(url, config);
  },

  post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return request.post(url, data, config);
  },

  put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return request.put(url, data, config);
  },

  patch<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return request.patch(url, data, config);
  },

  delete<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return request.delete(url, config);
  },
};

export default request;
