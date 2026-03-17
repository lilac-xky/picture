// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 添加空间 POST /api/space/add */
export async function addSpace(body: API.SpaceAddRequest, options?: { [key: string]: any }) {
  return request<API.ResultLong>('/api/space/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 删除空间 POST /api/space/delete */
export async function deleteSpace(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/api/space/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 编辑空间 POST /api/space/edit */
export async function editSpace(body: API.SpaceEditRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/api/space/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取空间(仅管理员) GET /api/space/get */
export async function getSpaceById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSpaceByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.ResultSpace>('/api/space/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 获取空间 GET /api/space/get/vo */
export async function getSpaceVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSpaceVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.ResultSpaceVO>('/api/space/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 获取空间等级列表 GET /api/space/list/level */
export async function listSpaceLevel(options?: { [key: string]: any }) {
  return request<API.ResultListSpaceLevel>('/api/space/list/level', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 获取空间列表(仅管理员) POST /api/space/list/page */
export async function listSpaceByPage(
  body: API.SpaceQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultPageSpace>('/api/space/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取空间列表 POST /api/space/list/page/vo */
export async function listSpaceVoByPage(
  body: API.SpaceQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultPageSpaceVO>('/api/space/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 更新空间(管理员) POST /api/space/update */
export async function updateSpace(body: API.SpaceUpdateRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/api/space/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
