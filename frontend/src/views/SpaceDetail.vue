<template>
  <div id="spaceDetail">
    <!-- 空间信息 -->
    <a-flex justify="space-between">
      <h2>{{ space.spaceName }} ({{ SPACE_TYPE_MAP[space.spaceType as keyof typeof SPACE_TYPE_MAP] ?? '未知类型' }})</h2>
      <a-space size="middle">
        <a-button v-if="canUploadPicture" type="primary" :href="`/add_picture?spaceId=${id}`">创建图片</a-button>
        <a-button v-if="canManageSpaceUser && space.spaceType === 1" type="primary" ghost :icon="h(TeamOutlined)"
          :href="`/spaceUserManage/${id}`">
          成员管理
        </a-button>
        <a-button v-if="canManageSpaceUser" type="primary" ghost :icon="h(BarChartOutlined)"
          :href="`/space_analyze?spaceId=${id}`" target="_blank">
          空间分析
        </a-button>
        <a-button v-if="canEditPicture" :icon="h(EditOutlined)" @click="doBatchEdit"> 批量编辑</a-button>
        <a-tooltip :title="`占用空间 ${formatSize(space.totalSize)} / ${formatSize(space.maxSize)}`">
          <a-progress type="circle" :percent="usagePercent" :size="40" />
        </a-tooltip>
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px;"></div>
    <!-- 搜索表单 -->
    <PictureSearchForm :onSearch="onSearch" />
    <div style="margin-bottom: 16px;"></div>
    <!-- 图片列表 -->
    <PictureList :dataList="dataList" :loading="loading" :showOp="true" :onReload="fetchData" :canEdit="canEditPicture"
      :canDelete="canDeletePicture" />
    <a-pagination v-model:current="searchParams.current" v-model:page-size="searchParams.pageSize" :total="total"
      @change="onPageChange" style="text-align: right;" />
    <BatchEditPictureModal ref="batchEditPictureModalRef" :pictureList="dataList" :spaceId="String(id)"
      :onSuccess="onBatchEditPictureSuccess" />
  </div>
</template>

<script setup lang="ts">
import { listPictureVoByPage } from '@/api/pictureController';
import { getSpaceVoById } from '@/api/spaceController';
import { formatSize } from '@/utils';
import { message } from 'ant-design-vue';
import { computed, h, onMounted, ref, watch } from 'vue';
import { BarChartOutlined, EditOutlined, TeamOutlined } from '@ant-design/icons-vue';
import PictureList from '@/components/PictureList.vue';
import PictureSearchForm from '@/components/PictureSearchForm.vue';
import BatchEditPictureModal from '@/components/BatchEditPictureModal.vue'
import { SPACE_PERMISSION_ENUM, SPACE_TYPE_MAP } from '@/constant/space';

const props = defineProps<{
  id: string | number
}>()
const space = ref<API.SpaceVO>({})

// 创建权限校验通用函数
function createPermissionChecker(permission: string) {
  return computed(() => {
    return (space.value.permissionList ?? []).includes(permission)
  })
}
// 权限检查
const canManageSpaceUser = createPermissionChecker(SPACE_PERMISSION_ENUM.SPACE_USER_MANAGE)
const canUploadPicture = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_UPLOAD)
const canEditPicture = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_EDIT)
const canDeletePicture = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_DELETE)

// 获取空间详情
const fetchSpaceDetail = async () => {
  try {
    const res = await getSpaceVoById({
      id: props.id as any,
    })
    if (res.data.code === 200 && res.data.data) {
      space.value = res.data.data
    } else {
      message.error('获取空间详情失败，' + res.data.msg)
    }
  } catch (e: any) {
    message.error('获取空间详情失败：' + e.message)
  }
}

// 计算进度
const usagePercent = computed(() => {
  const total = Number(space.value.totalSize) || 0;
  const max = Number(space.value.maxSize) || 1;
  const percent = (total * 100) / max;
  return Number(percent.toFixed(1));
})

// 获取图片列表
const dataList = ref<API.PictureVO[]>([])
const total = ref<number>(0);
const loading = ref(true)

// 查询参数
const searchParams = ref<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 分页
const onPageChange = (page: number, pageSize: number) => {
  searchParams.value.current = page
  searchParams.value.pageSize = pageSize
  fetchData()
}

// 获取数据
const fetchData = async () => {
  loading.value = true

  const params = {
    spaceId: props.id,
    ...searchParams.value,
  }
  const res = await listPictureVoByPage(params)
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = Number(res.data.data.total) || 0;
  } else {
    message.error('获取数据失败，' + res.data.msg)
  }
  loading.value = false
}

// 搜索
const onSearch = (newSearchParams: API.PictureQueryRequest) => {
  searchParams.value = {
    ...searchParams.value,
    ...newSearchParams,
    current: 1,
  }
  fetchData();
};

// 批量编辑图片
const batchEditPictureModalRef = ref()
const onBatchEditPictureSuccess = () => {
  fetchData()
}
const doBatchEdit = () => {
  if (batchEditPictureModalRef.value) {
    batchEditPictureModalRef.value.openModal()
  }
}

// 监听空间ID变化，重新获取数据
watch(
  () => props.id,
  (newSpaceId) => {
    fetchSpaceDetail()
    fetchData()
  },
)

onMounted(() => {
  fetchSpaceDetail()
  fetchData()
})
</script>

<style scoped>
#spaceDetail {
  margin-bottom: 16px;
}
</style>