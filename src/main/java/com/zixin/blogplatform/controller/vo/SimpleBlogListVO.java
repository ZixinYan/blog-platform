package com.zixin.blogplatform.controller.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SimpleBlogListVO implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    private Long blogId;

    private String blogTitle;
}
