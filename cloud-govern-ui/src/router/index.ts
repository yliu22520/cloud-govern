import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { useUserStore } from '@/store/user';

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/login/index.vue'),
    meta: { title: '登录', requiresAuth: false },
  },
  {
    path: '/',
    component: () => import('@/layouts/default.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/pages/dashboard/index.vue'),
        meta: { title: '首页', icon: 'home' },
      },
      {
        path: 'system',
        name: 'System',
        meta: { title: '系统管理', icon: 'setting' },
        children: [
          {
            path: 'user',
            name: 'User',
            component: () => import('@/pages/system/user/index.vue'),
            meta: { title: '用户管理', icon: 'user' },
          },
          {
            path: 'role',
            name: 'Role',
            component: () => import('@/pages/system/role/index.vue'),
            meta: { title: '角色管理', icon: 'user-group' },
          },
          {
            path: 'menu',
            name: 'Menu',
            component: () => import('@/pages/system/menu/index.vue'),
            meta: { title: '菜单管理', icon: 'menu' },
          },
        ],
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/pages/error/404.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫
router.beforeEach((to, _from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title || 'Cloud Govern'} - 微服务治理平台`;

  const userStore = useUserStore();

  // 不需要登录的页面
  if (to.meta.requiresAuth === false) {
    next();
    return;
  }

  // 检查登录状态
  if (!userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } });
    return;
  }

  next();
});

export default router;
