package com.dudu.security.distributed.order.controller;

import com.dudu.security.distributed.order.model.UserDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @author:zhaomeng
 * @date:2021/10/17 8:28 下午
 */
@RestController
public class OrderController {
    @GetMapping(value = "/r1")
    //拥有p1权限方可访问此url
    @PreAuthorize("hasAnyAuthority('p1')")
    public String r1(){
        UserDTO userDTO = (UserDTO)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDTO.getUsername()+"访问资源1";
    }
}
