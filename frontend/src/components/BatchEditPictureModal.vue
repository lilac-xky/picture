<template>
    <div class="batch-edit-picture-modal">
        <a-modal v-model:open="visible" title="批量编辑图片" :footer="false" @cancel="closeModal">
            <a-typography-paragraph type="secondary">* 只对当前页面的图片生效</a-typography-paragraph>
            <!-- 表单项 -->
            <a-form layout="vertical" :model="formData" @finish="handleSubmit">
                <a-form-item label="分类" name="category">
                    <a-auto-complete v-model:value="formData.category" :options="categoryOptions" placeholder="请输入分类"
                        allowClear />
                </a-form-item>
                <a-form-item label="标签" name="tags">
                    <a-select v-model:value="formData.tags" :options="tagOptions" mode="tags" placeholder="请输入标签"
                        allowClear />
                </a-form-item>
                <a-form-item label="命名规则" name="nameRule">
                    <a-input v-model:value="formData.nameRule" placeholder="请输入命名规则，输入 {序号} 可动态生成" />
                </a-form-item>
                <a-form-item>
                    <a-button type="primary" html-type="submit">提交</a-button>
                </a-form-item>
            </a-form>
        </a-modal>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { editPictureByBatch, listPictureTagCategory } from '@/api/pictureController'

// 定义组件属性类型
interface Props {
    pictureList: API.PictureVO[]
    spaceId: string
    onSuccess: () => void
}
const props = withDefaults(defineProps<Props>(), {})

// 控制弹窗可见性
const visible = ref(false)

// 打开弹窗
const openModal = () => {
    visible.value = true
}

// 关闭弹窗
const closeModal = () => {
    visible.value = false
}

// 初始化表单数据
const formData = reactive<API.PictureEditByBatchRequest>({
    category: '',
    tags: [],
    nameRule: '',
})

// 分类和标签选项
const categoryOptions = ref<{ value: string; label: string }[]>([]);
const tagOptions = ref<{ value: string; label: string }[]>([]);

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
    const res = await listPictureTagCategory()
    if (res.data.code === 200 && res.data.data) {
        tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
            return {
                value: data,
                label: data,
            }
        })
        categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
            return {
                value: data,
                label: data,
            }
        })
    } else {
        message.error('加载选项失败，' + res.data.msg)
    }
}

// 提交表单
const handleSubmit = async (values: any) => {
    if (!props.pictureList) {
        return
    }
    const res = await editPictureByBatch({
        pictureIdList: props.pictureList.map((picture) => picture.id),
        spaceId: props.spaceId,
        ...values,
    })
    if (res.data.code === 200 && res.data.data) {
        message.success('操作成功')
        closeModal()
        props.onSuccess?.()
    } else {
        message.error('操作失败，' + res.data.msg)
    }
}


// 暴露函数给父组件
defineExpose({
    openModal,
})

onMounted(() => {
    getTagCategoryOptions()
})
</script>