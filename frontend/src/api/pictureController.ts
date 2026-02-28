// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 删除图片 POST /api/picture/delete */
export async function deletePicture(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/api/picture/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 编辑图片 POST /api/picture/edit */
export async function editPicture(body: API.PictureEditRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/api/picture/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取图片(仅管理员) GET /api/picture/get */
export async function getPictureById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPictureByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.ResultPicture>('/api/picture/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 获取图片 GET /api/picture/get/vo */
export async function getPictureVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPictureVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.ResultPictureVO>('/api/picture/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 获取图片列表(仅管理员) POST /api/picture/list/page */
export async function listPictureByPage(
  body: API.PictureQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultPagePicture>('/api/picture/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取图片列表 POST /api/picture/list/page/vo */
export async function listPictureVoByPage(
  body: API.PictureQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultPagePictureVO>('/api/picture/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 图片审核 POST /api/picture/review */
export async function doPictureReview(
  body: API.PictureReviewRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultBoolean>('/api/picture/review', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取标签分类 GET /api/picture/tag_category */
export async function listPictureTagCategory(options?: { [key: string]: any }) {
  return request<API.ResultPictureTagCategory>('/api/picture/tag_category', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 更新图片 POST /api/picture/update */
export async function updatePicture(
  body: API.PictureUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultBoolean>('/api/picture/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 上传图片 POST /api/picture/upload */
export async function uploadPicture(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.uploadPictureParams,
  body: {},
  file?: File,
  options?: { [key: string]: any }
) {
  const formData = new FormData()

  if (file) {
    formData.append('file', file)
  }

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele]

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''))
        } else {
          formData.append(ele, new Blob([JSON.stringify(item)], { type: 'application/json' }))
        }
      } else {
        formData.append(ele, item)
      }
    }
  })

  return request<API.ResultPictureVO>('/api/picture/upload', {
    method: 'POST',
    params: {
      ...params,
    },
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    ...(options || {}),
  })
}

/** 图片批量上传 POST /api/picture/upload/batch */
export async function uploadPictureByBatch(
  body: API.PictureUploadByBatchRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultInteger>('/api/picture/upload/batch', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 上传图片 POST /api/picture/upload/url */
export async function uploadPictureByUrl(
  body: API.PictureUploadRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultPictureVO>('/api/picture/upload/url', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
