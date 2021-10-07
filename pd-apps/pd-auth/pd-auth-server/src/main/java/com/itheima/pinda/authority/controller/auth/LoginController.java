package com.itheima.pinda.authority.controller.auth;

/**
 * @author huajieli
 * @create 2021-10-05 22:11
 * <p>
 * 登录认证控制器
 */

import com.itheima.pinda.authority.dto.auth.LoginDTO;
import com.itheima.pinda.authority.dto.auth.LoginParamDTO;
import com.itheima.pinda.authority.biz.service.auth.ValidateCodeService;
import com.itheima.pinda.authority.biz.service.auth.impl.AuthManager;
import com.itheima.pinda.base.BaseController;
import com.itheima.pinda.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录
 */
@RestController
@RequestMapping("/anno")
@Api(value = "UserAuthController", tags = "登录")
@Slf4j
public class LoginController extends BaseController {
    @Autowired
    private ValidateCodeService validateCodeService;
    @Autowired
    private AuthManager authManager;

    @PostMapping("/login")
    @ApiOperation(notes = "这是可省略的notes", value = "登录")
    public R<LoginDTO> login(@Validated @RequestBody LoginParamDTO loginParamDTO) {
        //校验验证码是否正确
        boolean check = validateCodeService.check(loginParamDTO.getKey(), loginParamDTO.getCode());
        if (check) {
            //验证码校验通过，执行具体的登录认证逻辑
            R<LoginDTO> login = authManager.login(loginParamDTO.getAccount(), loginParamDTO.getPassword());
            return login;
        }

        return null;
    }

    @ApiOperation(value = "验证码", notes = "验证码")
    @GetMapping(value = "/captcha", produces = "image/png")
    public void captcha(@RequestParam(value = "key",required = true) String key, HttpServletResponse response) throws IOException {
        this.validateCodeService.create(key, response);
    }

}
