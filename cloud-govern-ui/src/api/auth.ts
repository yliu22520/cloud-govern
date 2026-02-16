import { http } from '@/utils/request';

// 登录参数
export interface LoginParams {
  username: string;
  password: string;
}

// 登录结果
export interface LoginResult {
  accessToken: string;
  userId: number;
  username: string;
  nickname: string;
  avatar: string;
  roles: string[];
  permissions: string[];
}

// 用户信息
export interface UserInfo {
  userId: number;
  username: string;
  nickname: string;
  email: string;
  phone: string;
  avatar: string;
  roles: string[];
  permissions: string[];
}

// 登录
export function login(params: LoginParams): Promise<LoginResult> {
  return http.post<LoginResult>('/auth/login', params);
}

// 退出登录
export function logout(): Promise<void> {
  return http.post('/auth/logout');
}

// 获取当前用户信息
export function getUserInfo(): Promise<UserInfo> {
  return http.get<UserInfo>('/auth/user/info');
}
