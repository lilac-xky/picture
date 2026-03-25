// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 获取空间图片分类使用情况 POST /api/space/analyze/category */
export async function getSpaceCategoryAnalyze(
  body: API.SpaceCategoryAnalyzeRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultListSpaceCategoryAnalyzeResponse>('/api/space/analyze/category', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取空间图片排行排行(管理员) POST /api/space/analyze/rank */
export async function getSpaceRankAnalyze(
  body: API.SpaceRankAnalyzeRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultListSpace>('/api/space/analyze/rank', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取空间图片大小使用情况 POST /api/space/analyze/size */
export async function getSpaceSizeAnalyze(
  body: API.SpaceSizeAnalyzeRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultListSpaceSizeAnalyzeResponse>('/api/space/analyze/size', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取空间图片标签使用情况 POST /api/space/analyze/tag */
export async function getSpaceTagAnalyze(
  body: API.SpaceTagAnalyzeRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultListSpaceTagAnalyzeResponse>('/api/space/analyze/tag', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取空间使用情况 POST /api/space/analyze/usage */
export async function getSpaceUsageAnalyze(
  body: API.SpaceUsageAnalyzeRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultSpaceUsageAnalyzeResponse>('/api/space/analyze/usage', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取空间图片用户使用情况 POST /api/space/analyze/user */
export async function getSpaceUserAnalyze(
  body: API.SpaceUserAnalyzeRequest,
  options?: { [key: string]: any }
) {
  return request<API.ResultListSpaceUserAnalyzeResponse>('/api/space/analyze/user', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
