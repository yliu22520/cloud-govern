<template>
  <t-layout class="app-layout">
    <t-aside class="app-aside">
      <div class="logo">
        <h1>Cloud Govern</h1>
      </div>
      <t-menu theme="dark" :value="activeMenu">
        <t-menu-item value="dashboard" @click="router.push('/dashboard')">
          <template #icon>
            <t-icon name="home" />
          </template>
          首页
        </t-menu-item>
      </t-menu>
    </t-aside>

    <t-layout>
      <t-header class="app-header">
        <div class="header-left">
          <t-breadcrumb>
            <t-breadcrumb-item>首页</t-breadcrumb-item>
          </t-breadcrumb>
        </div>
        <div class="header-right">
          <t-dropdown :options="userOptions" @click="handleUserClick">
            <t-button variant="text">
              <template #icon>
                <t-icon name="user-circle" />
              </template>
              {{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}
              <t-icon name="chevron-down" />
            </t-button>
          </t-dropdown>
        </div>
      </t-header>

      <t-content class="app-content">
        <router-view />
      </t-content>
    </t-layout>
  </t-layout>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  Layout as TLayout,
  Aside as TAside,
  Header as THeader,
  Content as TContent,
  Menu as TMenu,
  MenuItem as TMenuItem,
  Icon as TIcon,
  Breadcrumb as TBreadcrumb,
  BreadcrumbItem as TBreadcrumbItem,
  Dropdown as TDropdown,
  Button as TButton,
} from 'tdesign-vue-next';
import { useUserStore } from '@/store/user';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const activeMenu = computed(() => {
  const path = route.path;
  if (path.startsWith('/dashboard')) return 'dashboard';
  return '';
});

const userOptions = [
  { content: '个人中心', value: 'profile' },
  { content: '退出登录', value: 'logout' },
];

async function handleUserClick({ value }: { value: string }) {
  if (value === 'logout') {
    await userStore.logoutAction();
    router.push('/login');
  }
}

onMounted(() => {
  if (!userStore.userInfo) {
    userStore.fetchUserInfo();
  }
});
</script>

<style scoped lang="less">
.app-layout {
  height: 100vh;
}

.app-aside {
  width: 220px;
  background: #001529;

  .logo {
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.1);

    h1 {
      margin: 0;
      color: #fff;
      font-size: 18px;
      font-weight: 600;
    }
  }
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.app-content {
  padding: 24px;
  background: #f5f5f5;
  overflow: auto;
}
</style>
