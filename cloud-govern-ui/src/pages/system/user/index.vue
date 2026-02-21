<template>
  <div class="user-page">
    <!-- 搜索表单 -->
    <t-card class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="用户名" name="username">
          <t-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </t-form-item>
        <t-form-item label="昵称" name="nickname">
          <t-input v-model="searchForm.nickname" placeholder="请输入昵称" clearable />
        </t-form-item>
        <t-form-item label="手机号" name="phone">
          <t-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <t-option :value="1" label="启用" />
            <t-option :value="0" label="禁用" />
          </t-select>
        </t-form-item>
        <t-form-item>
          <t-button theme="primary" type="submit">查询</t-button>
          <t-button theme="default" type="reset" style="margin-left: 8px">重置</t-button>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 数据表格 -->
    <t-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><t-icon name="add" /></template>
            新增用户
          </t-button>
        </div>
      </template>

      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag :theme="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </t-tag>
        </template>
        <template #roleNames="{ row }">
          <template v-if="row.roleNames && row.roleNames.length > 0">
            <t-tag v-for="name in row.roleNames" :key="name" theme="primary" variant="light" style="margin-right: 4px">
              {{ name }}
            </t-tag>
          </template>
          <span v-else>-</span>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="warning" @click="handleResetPwd(row)">重置密码</t-link>
            <t-popconfirm content="确定删除该用户吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      :confirm-btn="{ content: '确定', loading: submitLoading }"
      @confirm="handleSubmit"
    >
      <t-form
        ref="formRef"
        :data="formData"
        :rules="formRules"
        label-align="right"
        label-width="80px"
      >
        <t-form-item label="用户名" name="username">
          <t-input v-model="formData.username" placeholder="请输入用户名" :disabled="isEdit" />
        </t-form-item>
        <t-form-item v-if="!isEdit" label="密码" name="password">
          <t-input v-model="formData.password" type="password" placeholder="请输入密码" />
        </t-form-item>
        <t-form-item label="昵称" name="nickname">
          <t-input v-model="formData.nickname" placeholder="请输入昵称" />
        </t-form-item>
        <t-form-item label="邮箱" name="email">
          <t-input v-model="formData.email" placeholder="请输入邮箱" />
        </t-form-item>
        <t-form-item label="手机号" name="phone">
          <t-input v-model="formData.phone" placeholder="请输入手机号" />
        </t-form-item>
        <t-form-item label="角色" name="roleIds">
          <t-select v-model="formData.roleIds" placeholder="请选择角色" multiple clearable>
            <t-option v-for="role in roleOptions" :key="role.id" :value="role.id" :label="role.roleName" />
          </t-select>
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-radio-group v-model="formData.status">
            <t-radio :value="1">启用</t-radio>
            <t-radio :value="0">禁用</t-radio>
          </t-radio-group>
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 重置密码弹窗 -->
    <t-dialog
      v-model:visible="resetPwdVisible"
      header="重置密码"
      :confirm-btn="{ content: '确定', loading: resetPwdLoading }"
      @confirm="handleResetPwdSubmit"
    >
      <t-form :data="resetPwdForm" label-align="right" label-width="80px">
        <t-form-item label="用户名">
          <t-input :value="resetPwdForm.username" disabled />
        </t-form-item>
        <t-form-item label="新密码" name="password">
          <t-input v-model="resetPwdForm.password" type="password" placeholder="请输入新密码" />
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import {
  Card as TCard,
  Form as TForm,
  FormItem as TFormItem,
  Input as TInput,
  Select as TSelect,
  Option as TOption,
  Button as TButton,
  Table as TTable,
  Tag as TTag,
  Space as TSpace,
  Link as TLink,
  Dialog as TDialog,
  RadioGroup as TRadioGroup,
  Radio as TRadio,
  Icon as TIcon,
  Popconfirm as TPopconfirm,
  MessagePlugin,
} from 'tdesign-vue-next';
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import {
  getUserList,
  createUser,
  updateUser,
  deleteUser,
  resetPassword,
  type User,
  type UserQuery,
  type UserFormData,
} from '@/api/user';
import { getAllRoles, type Role } from '@/api/role';

// 搜索表单
const searchForm = reactive<UserQuery>({
  username: '',
  nickname: '',
  phone: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10,
});

// 表格数据
const tableData = ref<User[]>([]);
const loading = ref(false);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 表格列配置
const columns: PrimaryTableCol[] = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'username', title: '用户名', width: 120 },
  { colKey: 'nickname', title: '昵称', width: 120 },
  { colKey: 'phone', title: '手机号', width: 140 },
  { colKey: 'email', title: '邮箱', width: 200, ellipsis: true },
  { colKey: 'roleNames', title: '角色', width: 200, cell: 'roleNames' },
  { colKey: 'status', title: '状态', width: 80, cell: 'status' },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'operation', title: '操作', width: 200, cell: 'operation', fixed: 'right' },
];

// 弹窗相关
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const formRef = ref();

const dialogTitle = computed(() => (isEdit.value ? '编辑用户' : '新增用户'));

const formData = reactive<UserFormData>({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  status: 1,
  roleIds: [],
});

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' as const }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' as const }],
};

// 角色选项
const roleOptions = ref<Role[]>([]);

// 重置密码弹窗
const resetPwdVisible = ref(false);
const resetPwdLoading = ref(false);
const resetPwdForm = reactive({
  id: 0,
  username: '',
  password: '',
});

// 当前编辑的用户ID
let currentUserId = 0;

// 加载数据
async function loadData() {
  loading.value = true;
  try {
    const result = await getUserList(searchForm);
    tableData.value = result.list;
    pagination.total = result.total;
    pagination.current = result.pageNum;
    pagination.pageSize = result.pageSize;
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false;
  }
}

// 加载角色选项
async function loadRoleOptions() {
  try {
    roleOptions.value = await getAllRoles();
  } catch {
    // 错误已在拦截器中处理
  }
}

// 搜索
function handleSearch() {
  searchForm.pageNum = 1;
  pagination.current = 1;
  loadData();
}

// 重置
function handleReset() {
  Object.assign(searchForm, {
    username: '',
    nickname: '',
    phone: '',
    status: undefined,
    pageNum: 1,
    pageSize: 10,
  });
  handleSearch();
}

// 分页变化
function handlePageChange(pageInfo: PageInfo) {
  searchForm.pageNum = pageInfo.current;
  searchForm.pageSize = pageInfo.pageSize;
  loadData();
}

// 新增
function handleAdd() {
  isEdit.value = false;
  currentUserId = 0;
  Object.assign(formData, {
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    status: 1,
    roleIds: [],
  });
  dialogVisible.value = true;
}

// 编辑
function handleEdit(row: User) {
  isEdit.value = true;
  currentUserId = row.id;
  Object.assign(formData, {
    username: row.username,
    password: '',
    nickname: row.nickname,
    email: row.email,
    phone: row.phone,
    status: row.status,
    roleIds: row.roleIds || [],
  });
  dialogVisible.value = true;
}

// 提交
async function handleSubmit() {
  const valid = await formRef.value?.validate();
  if (valid !== true) return;

  submitLoading.value = true;
  try {
    if (isEdit.value) {
      await updateUser(currentUserId, formData);
      MessagePlugin.success('更新成功');
    } else {
      await createUser(formData);
      MessagePlugin.success('创建成功');
    }
    dialogVisible.value = false;
    loadData();
  } catch {
    // 错误已在拦截器中处理
  } finally {
    submitLoading.value = false;
  }
}

// 删除
async function handleDelete(row: User) {
  try {
    await deleteUser(row.id);
    MessagePlugin.success('删除成功');
    loadData();
  } catch {
    // 错误已在拦截器中处理
  }
}

// 重置密码
function handleResetPwd(row: User) {
  resetPwdForm.id = row.id;
  resetPwdForm.username = row.username;
  resetPwdForm.password = '';
  resetPwdVisible.value = true;
}

// 重置密码提交
async function handleResetPwdSubmit() {
  if (!resetPwdForm.password) {
    MessagePlugin.warning('请输入新密码');
    return;
  }
  resetPwdLoading.value = true;
  try {
    await resetPassword(resetPwdForm.id, resetPwdForm.password);
    MessagePlugin.success('密码重置成功');
    resetPwdVisible.value = false;
  } catch {
    // 错误已在拦截器中处理
  } finally {
    resetPwdLoading.value = false;
  }
}

onMounted(() => {
  loadData();
  loadRoleOptions();
});
</script>

<style scoped lang="less">
.user-page {
  .search-card {
    margin-bottom: 16px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
