import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { login, logout, getUserInfo } from '@/api/auth';
import type { LoginParams, LoginResult, UserInfo } from '@/api/auth';

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '');
  const userInfo = ref<UserInfo | null>(null);

  const isLoggedIn = computed(() => !!token.value);

  async function loginAction(params: LoginParams): Promise<LoginResult> {
    const res = await login(params);
    token.value = res.accessToken;
    localStorage.setItem('token', res.accessToken);
    return res;
  }

  async function logoutAction() {
    try {
      await logout();
    } finally {
      token.value = '';
      userInfo.value = null;
      localStorage.removeItem('token');
    }
  }

  async function fetchUserInfo() {
    const res = await getUserInfo();
    userInfo.value = res;
    return res;
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    loginAction,
    logoutAction,
    fetchUserInfo,
  };
});
