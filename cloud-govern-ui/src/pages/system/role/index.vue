<template>
  <div class="role-page">
    <!-- 搜索表单 -->
    <t-card class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="角色名称" name="roleName">
          <t-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
        </t-form-item>
        <t-form-item label="权限字符" name="roleKey">
          <t-input v-model="searchForm.roleKey" placeholder="请输入权限字符" clearable />
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
          <span>角色列表</span>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><t-icon name="add" /></template>
            新增角色
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
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-popconfirm content="确定删除该角色吗？" @confirm="handleDelete(row)">
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
      width="600px"
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
        <t-form-item label="角色名称" name="roleName">
          <t-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </t-form-item>
        <t-form-item label="权限字符" name="roleKey">
          <t-input v-model="formData.roleKey" placeholder="请输入权限字符" />
        </t-form-item>
        <t-form-item label="描述" name="description">
          <t-textarea v-model="formData.description" placeholder="请输入描述" :maxlength="255" />
        </t-form-item>
        <t-form-item label="排序" name="sort">
          <t-input-number v-model="formData.sort" :min="0" :max="999" />
        </t-form-item>
        <t-form-item label="菜单权限" name="menuIds">
          <t-tree
            v-model:value="formData.menuIds"
            :data="menuTreeData"
            checkable
            :check-strictly="false"
            :keys="{ value: 'id', label: 'menuName', children: 'children' }"
            hover
            expand-all
          />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-radio-group v-model="formData.status">
            <t-radio :value="1">启用</t-radio>
            <t-radio :value="0">禁用</t-radio>
          </t-radio-group>
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
  Textarea as TTextarea,
  InputNumber as TInputNumber,
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
  Tree as TTree,
  MessagePlugin,
} from 'tdesign-vue-next';
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import {
  getRoleList,
  createRole,
  updateRole,
  deleteRole,
  type Role,
  type RoleQuery,
  type RoleFormData,
} from '@/api/role';
import { getAllMenus, type Menu } from '@/api/menu';

// 搜索表单
const searchForm = reactive<RoleQuery>({
  roleName: '',
  roleKey: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10,
});

// 表格数据
const tableData = ref<Role[]>([]);
const loading = ref(false);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 表格列配置
const columns: PrimaryTableCol[] = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'roleName', title: '角色名称', width: 150 },
  { colKey: 'roleKey', title: '权限字符', width: 150 },
  { colKey: 'description', title: '描述', width: 200, ellipsis: true },
  { colKey: 'sort', title: '排序', width: 80 },
  { colKey: 'status', title: '状态', width: 80, cell: 'status' },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'operation', title: '操作', width: 150, cell: 'operation', fixed: 'right' },
];

// 弹窗相关
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const formRef = ref();

const dialogTitle = computed(() => (isEdit.value ? '编辑角色' : '新增角色'));

const formData = reactive<RoleFormData>({
  roleName: '',
  roleKey: '',
  description: '',
  status: 1,
  sort: 0,
  menuIds: [],
});

const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' as const }],
  roleKey: [{ required: true, message: '请输入权限字符', trigger: 'blur' as const }],
};

// 菜单树数据
const menuTreeData = ref<Menu[]>([]);

// 当前编辑的角色ID
let currentRoleId = 0;

// 加载数据
async function loadData() {
  loading.value = true;
  try {
    const result = await getRoleList(searchForm);
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

// 加载菜单树
async function loadMenuTree() {
  try {
    menuTreeData.value = await getAllMenus();
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
    roleName: '',
    roleKey: '',
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
  currentRoleId = 0;
  Object.assign(formData, {
    roleName: '',
    roleKey: '',
    description: '',
    status: 1,
    sort: 0,
    menuIds: [],
  });
  dialogVisible.value = true;
}

// 编辑
function handleEdit(row: Role) {
  isEdit.value = true;
  currentRoleId = row.id;
  Object.assign(formData, {
    roleName: row.roleName,
    roleKey: row.roleKey,
    description: row.description,
    status: row.status,
    sort: row.sort,
    menuIds: row.menuIds || [],
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
      await updateRole(currentRoleId, formData);
      MessagePlugin.success('更新成功');
    } else {
      await createRole(formData);
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
async function handleDelete(row: Role) {
  try {
    await deleteRole(row.id);
    MessagePlugin.success('删除成功');
    loadData();
  } catch {
    // 错误已在拦截器中处理
  }
}

onMounted(() => {
  loadData();
  loadMenuTree();
});
</script>

<style scoped lang="less">
.role-page {
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
