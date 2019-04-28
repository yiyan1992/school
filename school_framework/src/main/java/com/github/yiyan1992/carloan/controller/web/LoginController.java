package com.github.yiyan1992.carloan.controller.web;

import com.github.yiyan1992.carloan.entity.query.LoginQuery;
import com.github.yiyan1992.carloan.entity.response.Response;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

/**
 * @author admin
 */
@RestController
public class LoginController {

    /**
     * 用来测试用户是否已经登陆
     *
     * @return
     */
    @RequiresAuthentication
    @GetMapping(value = "/")
    public Response checkLogin() {
        return Response.SUCCESS("");
    }

    /**
     * 登陆
     *
     * @param login
     * @return
     */
    @PostMapping(value = "/login")
    public Response login(LoginQuery login) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(login.getUsername(), login.getPassword());
        subject.login(token);
        return Response.SUCCESS(subject.getSession().getId());
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @PostMapping(value = "/home")
    public Response home() {
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipals().getPrimaryPrincipal();
        return Response.of(200, username);
    }
}
