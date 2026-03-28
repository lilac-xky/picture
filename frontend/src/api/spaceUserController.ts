// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 添加空间成员 POST /api/spaceUser/add */
export async function addSpaceUser(
  body: API.SpaceUserAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultLong>('/api/spaceUser/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 删除空间成员 POST /api/spaceUser/delete */
export async function deleteSpaceUser(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/api/spaceUser/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 编辑空间角色 POST /api/spaceUser/edit */
export async function editSpaceUser(
  body: API.SpaceUserEditRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultBoolean>('/api/spaceUser/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取空间成员 POST /api/spaceUser/get */
export async function getSpaceUser(
  body: API.SpaceUserQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultSpaceUser>('/api/spaceUser/get', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取空间成员列表 POST /api/spaceUser/list */
export async function listSpaceUser(
  body: API.SpaceUserQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultListSpaceUserVO>('/api/spaceUser/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 查询用户加入的团队空间列表 POST /api/spaceUser/list/my */
export async function listMyTeamSpace(options?: { [key: string]: any }) {
  return request<API.ResultListSpaceUserVO>('/api/spaceUser/list/my', {
    method: 'POST',
    ...(options || {}),
  })
}
