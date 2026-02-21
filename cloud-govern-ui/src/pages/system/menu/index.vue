<template>
  <div class="menu-page">
    <!-- 搜索表单 -->
    <t-card class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="菜单名称" name="menuName">
          <t-input v-model="searchForm.menuName" placeholder="请输入菜单名称" clearable />
        </t-form-item>
        <t-form-item label="菜单类型" name="menuType">
          <t-select v-model="searchForm.menuType" placeholder="请选择类型" clearable style="width: 120px">
            <t-option :value="1" label="目录" />
            <t-option :value="2" label="菜单" />
            <t-option :value="3" label="按钮" />
          </t-select>
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
          <span>菜单列表</span>
          <t-button theme="primary" @click="handleAdd(0)">
            <template #icon><t-icon name="add" /></template>
            新增菜单
          </t-button>
        </div>
      </template>

      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        row-key="id"
        :tree="{ childrenKey: 'children', treeNodeColumnIndex: 1 }"
        hover
      >
        <template #menuType="{ row }">
          <t-tag v-if="row.menuType === 1" theme="primary">目录</t-tag>
          <t-tag v-else-if="row.menuType === 2" theme="success">菜单</t-tag>
          <t-tag v-else-if="row.menuType === 3" theme="warning">按钮</t-tag>
        </template>
        <template #visible="{ row }">
          <t-tag :theme="row.visible === 1 ? 'success' : 'default'">
            {{ row.visible === 1 ? '显示' : '隐藏' }}
          </t-tag>
        </template>
        <template #status="{ row }">
          <t-tag :theme="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link v-if="row.menuType !== 3" theme="primary" @click="handleAdd(row.id)">新增</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-popconfirm content="确定删除该菜单吗？" @confirm="handleDelete(row)">
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
        <t-form-item label="上级菜单" name="parentId">
          <t-tree-select
            v-model="formData.parentId"
            :data="menuTreeOptions"
            :keys="{ value: 'id', label: 'menuName', children: 'children' }"
            placeholder="请选择上级菜单"
            clearable
          />
        </t-form-item>
        <t-form-item label="菜单名称" name="menuName">
          <t-input v-model="formData.menuName" placeholder="请输入菜单名称" />
        </t-form-item>
        <t-form-item label="菜单类型" name="menuType">
          <t-radio-group v-model="formData.menuType">
            <t-radio :value="1">目录</t-radio>
            <t-radio :value="2">菜单</t-radio>
            <t-radio :value="3">按钮</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item v-if="formData.menuType !== 3" label="路由路径" name="path">
          <t-input v-model="formData.path" placeholder="请输入路由路径" />
        </t-form-item>
        <t-form-item v-if="formData.menuType === 2" label="组件路径" name="component">
          <t-input v-model="formData.component" placeholder="请输入组件路径" />
        </t-form-item>
        <t-form-item label="权限标识" name="perms">
          <t-input v-model="formData.perms" placeholder="请输入权限标识" />
        </t-form-item>
        <t-form-item v-if="formData.menuType !== 3" label="图标" name="icon">
          <t-input v-model="formData.icon" placeholder="请输入图标名称" />
        </t-form-item>
        <t-form-item label="排序" name="sort">
          <t-input-number v-model="formData.sort" :min="0" :max="999" />
        </t-form-item>
        <t-form-item v-if="formData.menuType !== 3" label="是否可见" name="visible">
          <t-radio-group v-model="formData.visible">
            <t-radio :value="1">显示</t-radio>
            <t-radio :value="0">隐藏</t-radio>
          </t-radio-group>
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
  TreeSelect as TTreeSelect,
  MessagePlugin,
} from 'tdesign-vue-next';
import type { PrimaryTableCol } from 'tdesign-vue-next';
import {
  getMenuList,
  createMenu,
  updateMenu,
  deleteMenu,
  getAllMenus,
  type Menu,
  type MenuQuery,
  type MenuFormData,
} from '@/api/menu';

// 搜索表单
const searchForm = reactive<MenuQuery>({
  menuName: '',
  menuType: undefined,
  status: undefined,
});

// 表格数据
const tableData = ref<Menu[]>([]);
const allMenus = ref<Menu[]>([]);
const loading = ref(false);

// 表格列配置
const columns: PrimaryTableCol[] = [
  { colKey: 'menuName', title: '菜单名称', width: 200, ellipsis: true },
  { colKey: 'menuType', title: '类型', width: 80, cell: 'menuType' },
  { colKey: 'path', title: '路由路径', width: 200, ellipsis: true },
  { colKey: 'component', title: '组件路径', width: 200, ellipsis: true },
  { colKey: 'perms', title: '权限标识', width: 150, ellipsis: true },
  { colKey: 'icon', title: '图标', width: 100 },
  { colKey: 'sort', title: '排序', width: 80 },
  { colKey: 'visible', title: '可见', width: 80, cell: 'visible' },
  { colKey: 'status', title: '状态', width: 80, cell: 'status' },
  { colKey: 'operation', title: '操作', width: 200, cell: 'operation', fixed: 'right' },
];

// 弹窗相关
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const formRef = ref();

const dialogTitle = computed(() => (isEdit.value ? '编辑菜单' : '新增菜单'));

const formData = reactive<MenuFormData>({
  parentId: 0,
  menuName: '',
  menuType: 1,
  path: '',
  component: '',
  perms: '',
  icon: '',
  sort: 0,
  visible: 1,
  status: 1,
});

const formRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' as const }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' as const }],
};

// 菜单树选项（用于选择上级菜单）
const menuTreeOptions = computed(() => {
  const options = [{ id: 0, menuName: '根目录', children: [] as Menu[] }];
  // 过滤掉按钮类型
  const filterButton = (menus: Menu[]): Menu[] => {
    return menus
      .filter((m) => m.menuType !== 3)
      .map((m) => ({
        ...m,
        children: m.children ? filterButton(m.children) : [],
      }));
  };
  options[0].children = filterButton(allMenus.value);
  return options;
});

// 当前编辑的菜单ID
let currentMenuId = 0;

// 加载数据
async function loadData() {
  loading.value = true;
  try {
    tableData.value = await getMenuList(searchForm);
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false;
  }
}

// 加载所有菜单（用于选择上级菜单）
async function loadAllMenus() {
  try {
    allMenus.value = await getAllMenus();
  } catch {
    // 错误已在拦截器中处理
  }
}

// 搜索
function handleSearch() {
  loadData();
}

// 重置
function handleReset() {
  Object.assign(searchForm, {
    menuName: '',
    menuType: undefined,
    status: undefined,
  });
  handleSearch();
}

// 新增
function handleAdd(parentId: number) {
  isEdit.value = false;
  currentMenuId = 0;
  Object.assign(formData, {
    parentId,
    menuName: '',
    menuType: parentId === 0 ? 1 : 2,
    path: '',
    component: '',
    perms: '',
    icon: '',
    sort: 0,
    visible: 1,
    status: 1,
  });
  dialogVisible.value = true;
}

// 编辑
function handleEdit(row: Menu) {
  isEdit.value = true;
  currentMenuId = row.id;
  Object.assign(formData, {
    parentId: row.parentId,
    menuName: row.menuName,
    menuType: row.menuType,
    path: row.path,
    component: row.component,
    perms: row.perms,
    icon: row.icon,
    sort: row.sort,
    visible: row.visible,
    status: row.status,
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
      await updateMenu(currentMenuId, formData);
      MessagePlugin.success('更新成功');
    } else {
      await createMenu(formData);
      MessagePlugin.success('创建成功');
    }
    dialogVisible.value = false;
    loadData();
    loadAllMenus();
  } catch {
    // 错误已在拦截器中处理
  } finally {
    submitLoading.value = false;
  }
}

// 删除
async function handleDelete(row: Menu) {
  try {
    await deleteMenu(row.id);
    MessagePlugin.success('删除成功');
    loadData();
    loadAllMenus();
  } catch {
    // 错误已在拦截器中处理
  }
}

onMounted(() => {
  loadData();
  loadAllMenus();
});
</script>

<style scoped lang="less">
.menu-page {
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
