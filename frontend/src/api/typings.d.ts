declare namespace API {
  type DeleteRequest = {
    /** id */
    id?: number
  }

  type getPictureByIdParams = {
    /** 图片id */
    id?: number
  }

  type getPictureVOByIdParams = {
    /** 图片id */
    id?: number
  }

  type getUserByIdParams = {
    /** 用户id */
    id?: number
  }

  type getUserVOByIdParams = {
    /** 用户id */
    id?: number
  }

  type LoginUserVO = {
    /** id */
    id?: number
    /** 账号 */
    userAccount?: string
    /** 用户昵称 */
    userName?: string
    /** 用户头像 */
    userAvatar?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色(user/admin) */
    userRole?: string
    /** 编辑时间 */
    editTime?: string
    /** 创建时间 */
    createTime?: string
    /** 更新时间 */
    updateTime?: string
  }

  type OrderItem = {
    column?: string
    asc?: boolean
  }

  type PagePicture = {
    records?: Picture[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: boolean
    searchCount?: boolean
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
  }

  type PagePictureVO = {
    records?: PictureVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: boolean
    searchCount?: boolean
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
  }

  type PageUserVO = {
    records?: UserVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: boolean
    searchCount?: boolean
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
  }

  type Picture = {
    /** id */
    id?: number
    /** 图片url */
    url?: string
    /** 图片名称 */
    name?: string
    /** 简介 */
    introduction?: string
    /** 分类 */
    category?: string
    /** 标签(JSON数组) */
    tags?: string
    /** 图片体积 */
    picSize?: number
    /** 图片宽度 */
    picWidth?: number
    /** 图片高度 */
    picHeigh?: number
    /** 图片宽高比例 */
    picScale?: number
    /** 图片格式 */
    picFormat?: string
    /** 创建用户id */
    userId?: number
    /** 审核状态：0-待审核; 1-通过; 2-拒绝 */
    reviewStatus?: number
    /** 审核信息 */
    reviewMessage?: string
    /** 审核人id */
    reviewerId?: number
    /** 审核时间 */
    reviewTime?: string
    /** 创建时间 */
    createTime?: string
    /** 编辑时间 */
    editTime?: string
    /** 更新时间 */
    updateTime?: string
    /** 是否删除 */
    isDelete?: string
  }

  type PictureEditRequest = {
    /** id */
    id?: number
    /** 图片名称 */
    name?: string
    /** 简介 */
    introduction?: string
    /** 分类 */
    category?: string
    /** 标签(JSON数组) */
    tags?: string[]
  }

  type PictureQueryRequest = {
    /** 当前页 */
    current?: number
    /** 页面大小 */
    pageSize?: number
    /** 排序字段 */
    sortField?: string
    /** 排序顺序（默认：升序） */
    sortOrder?: string
    /** id */
    id?: number
    /** 图片名称 */
    name?: string
    /** 简介 */
    introduction?: string
    /** 分类 */
    category?: string
    /** 标签(JSON数组) */
    tags?: string[]
    /** 图片体积 */
    picSize?: number
    /** 图片宽度 */
    picWidth?: number
    /** 图片高度 */
    picHeigh?: number
    /** 图片宽高比例 */
    picScale?: number
    /** 图片格式 */
    picFormat?: string
    /** 创建用户id */
    userId?: number
    /** 审核状态：0-待审核; 1-通过; 2-拒绝 */
    reviewStatus?: number
    /** 审核信息 */
    reviewMessage?: string
    /** 审核人id */
    reviewerId?: number
    /** 审核时间 */
    reviewTime?: string
    /** 搜索关键词 */
    searchText?: string
  }

  type PictureReviewRequest = {
    /** id */
    id?: number
    /** 审核状态：0-待审核; 1-通过; 2-拒绝 */
    reviewStatus?: number
    /** 审核信息 */
    reviewMessage?: string
  }

  type PictureTagCategory = {
    /** 标签列表 */
    tagList?: string[]
    /** 分类列表 */
    categoryList?: string[]
  }

  type PictureUpdateRequest = {
    /** id */
    id?: number
    /** 图片名称 */
    name?: string
    /** 简介 */
    introduction?: string
    /** 分类 */
    category?: string
    /** 标签(JSON数组) */
    tags?: string[]
  }

  type PictureUploadByBatchRequest = {
    /** 搜索词 */
    searchText?: string
    /** 文件名前缀 */
    namePrefix?: string
    /** 抓取数量 */
    count?: number
  }

  type PictureUploadRequest = {
    /** id(用于修改) */
    id?: number
    /** 图片url */
    fileUrl?: string
    /** 图片名称 */
    picName?: string
  }

  type PictureVO = {
    /** id */
    id?: number
    /** 图片url */
    url?: string
    /** 图片名称 */
    name?: string
    /** 简介 */
    introduction?: string
    /** 分类 */
    category?: string
    /** 标签(JSON数组) */
    tags?: string[]
    /** 图片体积 */
    picSize?: number
    /** 图片宽度 */
    picWidth?: number
    /** 图片高度 */
    picHeigh?: number
    /** 图片宽高比例 */
    picScale?: number
    /** 图片格式 */
    picFormat?: string
    /** 创建用户id */
    userId?: number
    /** 创建时间 */
    createTime?: string
    /** 编辑时间 */
    editTime?: string
    /** 更新时间 */
    updateTime?: string
    /** 创建用户信息 */
    user?: UserVO
  }

  type ResultBoolean = {
    code?: number
    msg?: string
    data?: boolean
  }

  type ResultInteger = {
    code?: number
    msg?: string
    data?: number
  }

  type ResultLoginUserVO = {
    code?: number
    msg?: string
    data?: LoginUserVO
  }

  type ResultLong = {
    code?: number
    msg?: string
    data?: number
  }

  type ResultPagePicture = {
    code?: number
    msg?: string
    data?: PagePicture
  }

  type ResultPagePictureVO = {
    code?: number
    msg?: string
    data?: PagePictureVO
  }

  type ResultPageUserVO = {
    code?: number
    msg?: string
    data?: PageUserVO
  }

  type ResultPicture = {
    code?: number
    msg?: string
    data?: Picture
  }

  type ResultPictureTagCategory = {
    code?: number
    msg?: string
    data?: PictureTagCategory
  }

  type ResultPictureVO = {
    code?: number
    msg?: string
    /** 图片VO */
    data?: PictureVO
  }

  type ResultUser = {
    code?: number
    msg?: string
    data?: User
  }

  type ResultUserVO = {
    code?: number
    msg?: string
    /** 创建用户信息 */
    data?: UserVO
  }

  type uploadPictureParams = {
    /** id(用于修改) */
    id?: number
    /** 图片url */
    fileUrl?: string
  }

  type User = {
    /** id */
    id?: number
    /** 账号 */
    userAccount?: string
    /** 密码 */
    userPassword?: string
    /** 用户昵称 */
    userName?: string
    /** 用户头像 */
    userAvatar?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色(user/admin) */
    userRole?: string
    /** 编辑时间 */
    editTime?: string
    /** 创建时间 */
    createTime?: string
    /** 更新时间 */
    updateTime?: string
    /** 是否删除 */
    isDelete?: number
  }

  type UserAddRequest = {
    /** 账号 */
    userAccount?: string
    /** 用户昵称 */
    userName?: string
    /** 用户头像 */
    userAvatar?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色(user/admin) */
    userRole?: string
  }

  type UserLoginRequest = {
    /** 用户账户 */
    userAccount?: string
    /** 用户密码 */
    userPassword?: string
  }

  type UserQueryRequest = {
    /** 当前页 */
    current?: number
    /** 页面大小 */
    pageSize?: number
    /** 排序字段 */
    sortField?: string
    /** 排序顺序（默认：升序） */
    sortOrder?: string
    /** id */
    id?: number
    /** 账号 */
    userAccount?: string
    /** 用户昵称 */
    userName?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色(user/admin) */
    userRole?: string
  }

  type UserRegisterRequest = {
    /** 用户账户 */
    userAccount?: string
    /** 用户密码 */
    userPassword?: string
    /** 校验密码 */
    checkPassword?: string
  }

  type UserUpdateRequest = {
    /** id */
    id?: number
    /** 账号 */
    userAccount?: string
    /** 用户昵称 */
    userName?: string
    /** 用户头像 */
    userAvatar?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色(user/admin) */
    userRole?: string
  }

  type UserVO = {
    /** id */
    id?: number
    /** 账号 */
    userAccount?: string
    /** 用户昵称 */
    userName?: string
    /** 用户头像 */
    userAvatar?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色(user/admin) */
    userRole?: string
    /** 创建时间 */
    createTime?: string
  }
}
