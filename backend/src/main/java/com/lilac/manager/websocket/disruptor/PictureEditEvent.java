package com.lilac.manager.websocket.disruptor;

import com.lilac.domain.entity.User;
import com.lilac.manager.websocket.model.PictureEditRequestMessage;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * 图片编辑事件
 */
@Data
public class PictureEditEvent {

    /**
     * 图片编辑请求消息
     */
    private PictureEditRequestMessage pictureEditRequestMessage;

    /**
     * 用户会话
     */
    private WebSocketSession session;
    
    /**
     * 用户信息
     */
    private User user;

    /**
     * 图片id
     */
    private Long pictureId;

}
