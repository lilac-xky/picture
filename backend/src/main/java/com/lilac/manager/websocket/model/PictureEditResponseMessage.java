package com.lilac.manager.websocket.model;

import com.lilac.domain.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片编辑响应消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureEditResponseMessage {

    /**
     * 响应类型
     */
    private String type;

    /**
     * 响应内容
     */
    private String message;

    /**
     * 编辑动作
     */
    private String editAction;

    /**
     * 用户信息
     */
    private UserVO user;
}