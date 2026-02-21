import { http } from '@/utils/request';

// 角色信息
export interface Role {
  id: number;
  roleName: string;
  roleKey: string;
  description: string;
  status: number;
  sort: number;
  createTime: string;
  updateTime: string;
  menuIds: number[];
}

// 分页结果
export interface PageResult<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
}

// 角色查询参数
export interface RoleQuery {
  roleName?: string;
  roleKey?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

// 角色创建/更新参数
export interface RoleFormData {
  roleName: string;
  roleKey: string;
  description?: string;
  status?: number;
  sort?: number;
  menuIds?: number[];
}

// 角色列表
export function getRoleList(params: RoleQuery): Promise<PageResult<Role>> {
  return http.get<PageResult<Role>>('/system/role/list', { params });
}

// 所有角色列表
export function getAllRoles(): Promise<Role[]> {
  return http.get<Role[]>('/system/role/all');
}

// 角色详情
export function getRoleById(id: number): Promise<Role> {
  return http.get<Role>(`/system/role/${id}`);
}

// 创建角色
export function createRole(data: RoleFormData): Promise<number> {
  return http.post<number>('/system/role', data);
}

// 更新角色
export function updateRole(id: number, data: RoleFormData): Promise<void> {
  return http.put<void>(`/system/role/${id}`, data);
}

// 删除角色
export function deleteRole(id: number): Promise<void> {
  return http.delete<void>(`/system/role/${id}`);
}

// 更新状态
export function updateRoleStatus(id: number, status: number): Promise<void> {
  return http.put<void>(`/system/role/${id}/status`, null, { params: { status } });
}
