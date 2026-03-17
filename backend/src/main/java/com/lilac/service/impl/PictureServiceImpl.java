package com.lilac.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lilac.domain.dto.file.UploadPictureResult;
import com.lilac.domain.dto.picture.*;
import com.lilac.domain.entity.Picture;
import com.lilac.domain.entity.Space;
import com.lilac.domain.entity.User;
import com.lilac.domain.vo.PictureVO;
import com.lilac.domain.vo.UserVO;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.enums.PictureReviewStatusEnum;
import com.lilac.exception.BusinessException;
import com.lilac.manager.CosManager;
import com.lilac.manager.upload.FilePictureUpload;
import com.lilac.manager.upload.PictureUploadTemplate;
import com.lilac.manager.upload.UrlPictureUpload;
import com.lilac.mapper.PictureMapper;
import com.lilac.service.PictureService;
import com.lilac.service.SpaceService;
import com.lilac.service.UserService;
import com.lilac.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 图片服务实现类
 * @author lilac
 */
@Service
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Resource
    private UserService userService;
    @Resource
    private FilePictureUpload filePictureUpload;
    @Resource
    private UrlPictureUpload urlPictureUpload;
    @Autowired
    private CosManager cosManager;
    @Resource
    private SpaceService spaceService;
    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 上传图片
     * @param inputSource 图片源
     * @param pictureUploadRequest 图片上传参数
     * @param loginUser 登录用户
     * @return 图片信息
     */
    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        // 检验参数
        ThrowUtils.throwIf(loginUser == null, HttpsCodeEnum.UNAUTHORIZED);
        // 校验空间是否存在
        Long spaceId = pictureUploadRequest.getSpaceId();
        if(spaceId != null){
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, HttpsCodeEnum.NOT_FOUND_ERROR, "空间不存在");
            // 校验空间权限,仅空间管理员才能上传图片
            if(!loginUser.getId().equals(space.getUserId())){
                throw new BusinessException(HttpsCodeEnum.UNAUTHORIZED, "无空间权限");
            }
            // 校验额度
            if(space.getTotalCount() >= space.getMaxCount()){
                throw new BusinessException(HttpsCodeEnum.OPERATION_ERROR, "空间数量不足");
            }
            if(space.getTotalSize() >= space.getMaxSize()){
                throw new BusinessException(HttpsCodeEnum.OPERATION_ERROR, "空间大小不足");
            }
        }
        // 判断是新增还是删除
        Long pictureId = null;
        if(pictureUploadRequest != null){
            pictureId = pictureUploadRequest.getId();
        }
        // 判断图片是否存在
        if(pictureId != null){
            Picture oldPicture = this.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, HttpsCodeEnum.NOT_FOUND_ERROR, "图片不存在");
            // 仅允许本人和管理员上传图片
            if(!loginUser.getId().equals(oldPicture.getUserId()) && !userService.isAdmin(loginUser)){
                throw new BusinessException(HttpsCodeEnum.UNAUTHORIZED);
            }
            // 校验空间是否一致
            if(spaceId == null){
                if(oldPicture.getSpaceId() != null){
                    spaceId = oldPicture.getSpaceId();
                }
            }else{
                if(ObjUtil.notEqual(oldPicture.getSpaceId(), spaceId)){
                    throw new BusinessException(HttpsCodeEnum.OPERATION_ERROR, "图片空间不一致");
                }
            }
        }
        // 上传图片，得到图片信息
        String uploadPathPrefix;
        if(spaceId != null){
            uploadPathPrefix = String.format("space/%s",  spaceId);
        }else{
            uploadPathPrefix = String.format("public/%s", loginUser.getId());
        }
        // 根据inputSource类型，选择上传方式
        PictureUploadTemplate pictureUploadTemplate = inputSource instanceof MultipartFile ? filePictureUpload : urlPictureUpload;
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
        // 构造信息
        Picture picture = new Picture();
        picture.setSpaceId(spaceId);
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
        // 获取图片名
        String picName = uploadPictureResult.getPickName();
        if (pictureUploadRequest != null && StrUtil.isNotBlank(pictureUploadRequest.getPicName())){
            picName = pictureUploadRequest.getPicName();
        }
        picture.setName(picName);
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeigh(uploadPictureResult.getPicHeigh());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        this.fillReviewParams(picture, loginUser);
        // 判断更新还是新增
        if(pictureId != null){
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        // 事务
        Long finalSpaceId = spaceId;
        transactionTemplate.execute(status -> {
            // 插入图片
            boolean saveOrUpdate = this.saveOrUpdate(picture);
            ThrowUtils.throwIf(!saveOrUpdate, HttpsCodeEnum.OPERATION_ERROR, "图片上传失败");
            // 更新空间额度
            if (finalSpaceId != null) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, finalSpaceId)
                        .setSql("totalSize = totalSize + " + picture.getPicSize())
                        .setSql("totalCount = totalCount + 1")
                        .update();
                ThrowUtils.throwIf(!update, HttpsCodeEnum.OPERATION_ERROR, "额度更新失败");
            }
            return picture;
        });
        return PictureVO.objToVo(picture);
    }

    /**
     * 获取查询条件
     * @param pictureQueryRequest 用户查询条件
     * @return 查询条件
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if(pictureQueryRequest == null){
            return queryWrapper;
        }
        // 获取查询参数
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeigh = pictureQueryRequest.getPicHeigh();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        Long spaceId = pictureQueryRequest.getSpaceId();
        boolean nullSpaceId = pictureQueryRequest.isNullSpaceId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        if(StrUtil.isNotBlank(searchText)){
            queryWrapper.and(qw -> qw.like("name", searchText).or()
                    .like("introduction", searchText));
        }
        // 创建查询条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.isNull(nullSpaceId, "spaceId");
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeigh), "picHeigh", picHeigh);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        if(CollUtil.isNotEmpty(tags)){
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 图片脱敏
     * @param picture 图片
     * @param request 请求
     * @return 图片信息
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 获取用户信息
        Long userId = picture.getUserId();
        if(userId != null && userId > 0L){
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    /**
     * 分页获取图片封装
     * @param picturePage 图片分页
     * @param request 请求
     * @return 图片信息
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if(CollUtil.isEmpty(pictureList)){
            return pictureVOPage;
        }
        // 获取用户信息
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 填充用户信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if(userIdUserListMap.containsKey(userId)){
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    /**
     * 图片校验
     * @param picture 图片
     */
    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, HttpsCodeEnum.PARAMS_ERROR);
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 参数校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), HttpsCodeEnum.PARAMS_ERROR, "id不能为空");
        if(StrUtil.isNotBlank(url)){
            ThrowUtils.throwIf(url.length() > 1024, HttpsCodeEnum.PARAMS_ERROR, "url过长");
        }
        if(StrUtil.isNotBlank(introduction)){
            ThrowUtils.throwIf(introduction.length() > 800, HttpsCodeEnum.PARAMS_ERROR, "简介过长");
        }
    }

    /**
     * 图片审核
     * @param pictureReviewRequest 图片审核参数
     * @param loginUser 登录用户
     */
    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 校验参数
        ThrowUtils.throwIf(pictureReviewRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        String reviewMessage = pictureReviewRequest.getReviewMessage();
        if(id == null || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)){
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR);
        }
        // 判断图片是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        // 校验审核是否重复
        if(oldPicture.getReviewStatus().equals(reviewStatus)){
            throw new BusinessException(HttpsCodeEnum.OPERATION_ERROR, "图片已审核过");
        }
        // 数据库操作
        Picture newPicture = new Picture();
        BeanUtils.copyProperties(pictureReviewRequest, newPicture);
        newPicture.setReviewerId(loginUser.getId());
        newPicture.setReviewTime(new Date());
        boolean result = this.updateById(newPicture);
        ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR);
    }

    /**
     * 填充审核参数
     * @param picture 图片
     * @param loginUser 登录用户
     */
    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        if(userService.isAdmin(loginUser)){
            // 管理员自动过审
            picture.setReviewerId(loginUser.getId());
            picture.setReviewTime(new Date());
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewMessage("管理员自动过审");
        }else {
            // 非管理员审核
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }

    /**
     * 上传图片批量
     *
     * @param pictureUploadByBatchRequest 图片上传参数
     * @param loginUser                   登录用户
     * @return 图片数量
     */
    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        // 检验参数
        String searchText = pictureUploadByBatchRequest.getSearchText();
        Integer count = pictureUploadByBatchRequest.getCount();
        ThrowUtils.throwIf(count > 30, HttpsCodeEnum.PARAMS_ERROR, "数量不能超过30");
        String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
        if(StrUtil.isBlank(namePrefix)){
            namePrefix = searchText;
        }
        // 抓取内容
        String fetchUrl = String.format("https://cn.bing.com/images/search?q=%s&count=%s", searchText, count);
        Document document = null;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            log.error("抓取内容失败", e);
            throw new BusinessException(HttpsCodeEnum.OPERATION_ERROR, "抓取内容失败");
        }
        // 解析内容
        Element div = document.getElementsByClass("dgControl").first();
        if(ObjUtil.isEmpty(div)){
            throw new BusinessException(HttpsCodeEnum.OPERATION_ERROR, "解析内容失败");
        }
        Elements isElementList = div.select("img.mimg");
        int uploadCount = 0;
        for (Element imgElement : isElementList) {
            String fileUrl = imgElement.attr("src");
            if(StrUtil.isBlank(fileUrl)){
                log.info("图片地址为空，已跳过：{}",fileUrl);
                continue;
            }
            // 处理图片地址，防止转义或对象存储冲突问题
            int index = fileUrl.indexOf("?");
            if(index > -1){
                fileUrl = fileUrl.substring(0, index);
            }
            // 上传图片
            PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
            pictureUploadRequest.setFileUrl(fileUrl);
            pictureUploadRequest.setPicName(namePrefix + (uploadCount + 1));
            try {
                PictureVO pictureVO = this.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
                log.info("上传图片成功,id：{}",pictureVO.getId());
                uploadCount++;
            } catch (Exception e) {
                log.error("上传图片失败", e);
                continue;
            }
            if (uploadCount >= count) {
                break;
            }
        }
        return uploadCount;
    }

    /**
     * 清理图片文件
     * @param picture 图片
     */
//    @Async
    @Override
    public void clearPictureFile(Picture picture) {
        String picUrl = picture.getUrl();
        // 图片被其他图片引用，不删除
        long count = this.lambdaQuery().eq(Picture::getUrl, picUrl).count();
        if(count > 1){
            return;
        }
        cosManager.deleteObject(picUrl);
        // 删除缩略图
        String picThumbnailUrl = picture.getThumbnailUrl();
        if(StrUtil.isNotBlank(picThumbnailUrl)){
            cosManager.deleteObject(picThumbnailUrl);
        }
    }

    /**
     * 检查图片权限
     * @param loginUser 登录用户
     * @param picture 图片
     */
    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        Long spaceId = picture.getSpaceId();
        Long loginUserId = loginUser.getId();
        if(spaceId == null){
            // 共有图库
            if(!picture.getUserId().equals(loginUserId) && !userService.isAdmin(loginUser)){
                throw new BusinessException(HttpsCodeEnum.UNAUTHORIZED);
            }
        }else {
            // 私有空间
            if(!picture.getUserId().equals(loginUserId)){
                throw new BusinessException(HttpsCodeEnum.UNAUTHORIZED);
            }
        }
    }

    /**
     * 删除图片
     *
     * @param pictureId 图片id
     * @param loginUser 登录用户
     */
    @Override
    public void deletePicture(long pictureId, User loginUser) {
        ThrowUtils.throwIf(pictureId <= 0, HttpsCodeEnum.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, HttpsCodeEnum.UNAUTHORIZED);
        // 获取图片
        Picture oldPicture = this.getById(pictureId);
        ThrowUtils.throwIf(oldPicture == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        // 校验权限
        checkPictureAuth(loginUser, oldPicture);
        // 事务
        transactionTemplate.execute(status -> {
            // 删除
            boolean result = this.removeById(pictureId);
            ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR);
            // 更新空间额度
            Long spaceId = oldPicture.getSpaceId();
            if (spaceId != null) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, spaceId)
                        .setSql("totalSize = totalSize - " + oldPicture.getPicSize())
                        .setSql("totalCount = totalCount - 1")
                        .update();
                ThrowUtils.throwIf(!update, HttpsCodeEnum.OPERATION_ERROR, "额度更新失败");
            }
            return true;
        });
        // 清理图片文件
        clearPictureFile(oldPicture);
    }

    /**
     * 编辑图片
     *
     * @param pictureEditRequest 图片编辑参数
     * @param loginUser          登录用户
     */
    @Override
    public void editPicture(PictureEditRequest pictureEditRequest, User loginUser) {
        // 转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        picture.setEditTime(new Date());
        // 校验
        validPicture(picture);
        // 获取当前用户
        long id = pictureEditRequest.getId();
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        // 校验权限
        checkPictureAuth(loginUser, oldPicture);
        // 填充审核参数
        fillReviewParams(picture, loginUser);
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR);
    }
}
