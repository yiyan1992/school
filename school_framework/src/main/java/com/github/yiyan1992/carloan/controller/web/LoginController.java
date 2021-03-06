package com.github.yiyan1992.carloan.controller.web;

import com.github.yiyan1992.carloan.entity.request.LoginQuery;
import com.github.yiyan1992.carloan.entity.base.Response;
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
        return Response.success("");
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
        return Response.success(subject.getSession().getId());
    }
}
