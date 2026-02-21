import { http } from '@/utils/request';

// 菜单信息
export interface Menu {
  id: number;
  parentId: number;
  menuName: string;
  menuType: number;
  path: string;
  component: string;
  perms: string;
  icon: string;
  sort: number;
  visible: number;
  status: number;
  createTime: string;
  updateTime: string;
  children: Menu[];
}

// 菜单查询参数
export interface MenuQuery {
  menuName?: string;
  menuType?: number;
  status?: number;
}

// 菜单创建/更新参数
export interface MenuFormData {
  parentId?: number;
  menuName: string;
  menuType: number;
  path?: string;
  component?: string;
  perms?: string;
  icon?: string;
  sort?: number;
  visible?: number;
  status?: number;
}

// 菜单列表（树形）
export function getMenuList(params?: MenuQuery): Promise<Menu[]> {
  return http.get<Menu[]>('/system/menu/list', { params });
}

// 所有菜单列表（树形）
export function getAllMenus(): Promise<Menu[]> {
  return http.get<Menu[]>('/system/menu/all');
}

// 菜单详情
export function getMenuById(id: number): Promise<Menu> {
  return http.get<Menu>(`/system/menu/${id}`);
}

// 创建菜单
export function createMenu(data: MenuFormData): Promise<number> {
  return http.post<number>('/system/menu', data);
}

// 更新菜单
export function updateMenu(id: number, data: MenuFormData): Promise<void> {
  return http.put<void>(`/system/menu/${id}`, data);
}

// 删除菜单
export function deleteMenu(id: number): Promise<void> {
  return http.delete<void>(`/system/menu/${id}`);
}

// 更新状态
export function updateMenuStatus(id: number, status: number): Promise<void> {
  return http.put<void>(`/system/menu/${id}/status`, null, { params: { status } });
}
