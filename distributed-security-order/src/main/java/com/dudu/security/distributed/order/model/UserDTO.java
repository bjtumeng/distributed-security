package com.dudu.security.distributed.order.model;

import lombok.Data;

/**
 * @Description:
 * @author:zhaomeng
 * @date:2021/10/24 12:35 下午
 */
@Data
public class UserDTO {

    /**
     * 用户id
     */
    private String id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 姓名
     */
    private String fullname;
}

