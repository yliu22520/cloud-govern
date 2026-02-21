import { http } from '@/utils/request';

// 用户信息
export interface User {
  id: number;
  username: string;
  nickname: string;
  email: string;
  phone: string;
  avatar: string;
  status: number;
  createTime: string;
  updateTime: string;
  roleIds: number[];
  roleNames: string[];
}

// 分页结果
export interface PageResult<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
}

// 用户查询参数
export interface UserQuery {
  username?: string;
  nickname?: string;
  phone?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

// 用户创建/更新参数
export interface UserFormData {
  username: string;
  password?: string;
  nickname?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  status?: number;
  roleIds?: number[];
}

// 用户列表
export function getUserList(params: UserQuery): Promise<PageResult<User>> {
  return http.get<PageResult<User>>('/system/user/list', { params });
}

// 用户详情
export function getUserById(id: number): Promise<User> {
  return http.get<User>(`/system/user/${id}`);
}

// 创建用户
export function createUser(data: UserFormData): Promise<number> {
  return http.post<number>('/system/user', data);
}

// 更新用户
export function updateUser(id: number, data: UserFormData): Promise<void> {
  return http.put<void>(`/system/user/${id}`, data);
}

// 删除用户
export function deleteUser(id: number): Promise<void> {
  return http.delete<void>(`/system/user/${id}`);
}

// 重置密码
export function resetPassword(id: number, password: string): Promise<void> {
  return http.put<void>(`/system/user/${id}/password`, null, { params: { password } });
}

// 更新状态
export function updateUserStatus(id: number, status: number): Promise<void> {
  return http.put<void>(`/system/user/${id}/status`, null, { params: { status } });
}
