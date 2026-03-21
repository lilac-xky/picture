<template>
  <div class="picture-list">
    <!-- 图片列表 -->
    <a-list :grid="{ gutter: 16, xs: 1, sm: 2, md: 4, lg: 4, xl: 5, xxl: 6 }" :data-source="dataList"
      :loading="loading">
      <template #renderItem="{ item: picture }">
        <a-list-item style="padding: 0;">
          <!-- 单张图片 -->
          <a-card hoverable @click="doClickPicture(picture)">
            <template #cover>
              <img :alt="picture.name" :src="picture.thumbnailUrl ?? picture.url"
                style="height: 180px;object-fit: cover;" />
            </template>
            <a-card-meta :title="picture.name">
              <template #description>
                <a-flex>
                  <a-tag color="green">{{ picture.category ?? '默认' }}</a-tag>
                  <a-tag v-for="tag in picture.tags" :key="tag">{{ tag }}</a-tag>
                </a-flex>
              </template>
            </a-card-meta>
            <template v-if="showOp" #actions>
              <a-space @click="(e: MouseEvent) => doEdit(picture, e)">
                <EditOutlined />
                编辑
              </a-space>
              <a-space @click="(e: MouseEvent) => doDelete(picture, e)">
                <DeleteOutlined />
                删除
              </a-space>
              <a-space @click="(e: MouseEvent) => doShare(picture, e)">
                <ShareAltOutlined />
                分享
              </a-space>
            </template>
          </a-card>
        </a-list-item>
      </template>
    </a-list>
    <ShareModel ref="shareModelRef" :title="shareTitle" :link="shareLink"/>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { EditOutlined, DeleteOutlined, ShareAltOutlined } from '@ant-design/icons-vue'
import { deletePicture } from '@/api/pictureController'
import { message } from 'ant-design-vue'
import ShareModel from './ShareModal.vue'
import { ref } from 'vue'

interface Props {
  dataList?: API.PictureVO[]
  loading?: boolean
  showOp?: boolean
  onReload?: () => void
}

const props = withDefaults(defineProps<Props>(), {
  dataList: () => [],
  loading: false,
  showOp: false,
})

// 跳转至图片详情
const router = useRouter()
const doClickPicture = (picture: API.PictureVO) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}

// 编辑图片
const doEdit = (picture: API.PictureVO, e: MouseEvent) => {
  e.stopPropagation()
  router.push({
    path: "/add_picture",
    query: {
      id: picture.id,
      spaceId: picture.spaceId,
    }
  })
}

// 删除图片
const doDelete = async (picture: API.PictureVO, e: MouseEvent) => {
  e.stopPropagation()
  const id = picture.id
  if(!id) {
    return
  }
  const res = await deletePicture({ id });
  if (res.data.code === 200 && res.data.data) {
    message.success('删除成功');
    props.onReload?.();
  } else {
    message.error('删除失败');
  }
}

// 分享图片
const shareModelRef = ref()
const shareTitle = '分享图片'
const shareLink = ref<string>('')
const doShare = (picture: API.PictureVO, e: MouseEvent) => {
  e.stopPropagation()
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.id}`
  shareModelRef.value?.openModel()
}
</script>

<style scoped></style>