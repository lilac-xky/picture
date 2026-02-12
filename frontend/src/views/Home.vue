<template>
  <div id="home">
    <!-- 搜索框 -->
    <div class="search-bar">
      <a-input-search v-model:value="searchParams.searchText" placeholder="海量图片搜索" enterButton="搜索" size="large"
        @search="doSearch" />
    </div>

    <!-- 分类和标签 -->
    <a-tabs v-model:active-key="selectedCategory" @change="doSearch">
      <a-tab-pane key="all" tab="全部分类" />
      <a-tab-pane v-for="category in categoryList" :key="category" :tab="category" />
    </a-tabs>
    <div class="tag-bar">
      <span style="margin-right: 8px;">标签：</span>
      <a-space :size="[0, 8]" wrap>
        <a-checkable-tag v-for="(tag, index) in tagList" :key="tag" v-model:checked="selectedTags[index]"
          @click="doSearch">
          {{ tag }}
        </a-checkable-tag>
      </a-space>
    </div>

    <!-- 图片列表 -->
    <a-list :grid="{ gutter: 16, xs: 1, sm: 2, md: 4, lg: 4, xl: 5, xxl: 6 }" :data-source="dataList"
      :pagination="pagination" :loading="loading">
      <template #renderItem="{ item: picture }">
        <a-list-item style="padding: 0;">
          <!-- 单张图片 -->
          <a-card hoverable @click="doClickPickture(picture)">
            <template #cover>
              <img :alt="picture.name" :src="picture.url" style="height: 180px;object-fit: cover;" />
            </template>
            <a-card-meta :title="picture.name">
              <template #description>
                <a-flex>
                  <a-tag color="green">{{ picture.category ?? '默认' }}</a-tag>
                  <a-tag v-for="tag in picture.tags" :key="tag">{{ tag }}</a-tag>
                </a-flex>
              </template>
            </a-card-meta>
          </a-card>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>

<script setup lang="ts">
import { listPictureTagCategory, listPictureVoByPage } from '@/api/pictureController';
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

const loading = ref<boolean>(true);

// 数据
const dataList = ref<API.Picture[]>([]);
const total = ref(0);

// 查询参数
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
});

// 分页
const pagination = computed(() => {
  return {
    current: searchParams.current,
    pageSize: searchParams.pageSize,
    total: total.value,
    onChange: (page: number, pageSize: number) => {
      searchParams.current = page;
      searchParams.pageSize = pageSize;
      fetchData();
    }
  }
});

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true;
    // 构建查询参数
    const params = {
      ...searchParams,
      tags: [] as string[],
    }
    if (selectedCategory.value !== 'all') {
      params.category = selectedCategory.value;
    }
    selectedTags.value.forEach((userTag, index) => {
      if (userTag && tagList.value[index]) {
        params.tags?.push(tagList.value[index]);
      }
    });
    const res: any = await listPictureVoByPage(params);
    dataList.value = res.data.data.records || [];
    total.value = Number(res.data.data.total) || 0;
    loading.value = false;
  } catch (error) {
    dataList.value = [];
    total.value = 0;
  }
}

// 搜索
const doSearch = () => {
  searchParams.current = 1;
  fetchData();
}

// 标签和分类列表
const categoryList = ref<string[]>([]);
const selectedCategory = ref<string>('all');
const tagList = ref<string[]>([]);
const selectedTags = ref<boolean[]>([]);

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategory();
  tagList.value = res.data?.data?.tagList || [];
  categoryList.value = res.data?.data?.categoryList || [];
};

const router = useRouter();
// 点击图片跳转
const doClickPickture = (picture: API.Picture) => {
  router.push(`/picture/${picture.id}`);
}

onMounted(() => {
  fetchData();
  getTagCategoryOptions();
});
</script>

<style scoped>
#home {
  margin-bottom: 16px;
}

#home .search-bar {
  max-width: 480px;
  margin: 0 auto 16px;
}

#home .tag-bar {
  margin-bottom: 16px;
}
</style>