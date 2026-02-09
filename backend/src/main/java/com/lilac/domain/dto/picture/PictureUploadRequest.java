package com.lilac.domain.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureUploadRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id(用于修改)
     */
    private Long id;
}
