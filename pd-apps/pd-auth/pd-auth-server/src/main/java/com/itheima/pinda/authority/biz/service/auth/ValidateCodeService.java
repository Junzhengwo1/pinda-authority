package com.itheima.pinda.authority.biz.service.auth;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author huajieli
 * @create 2021-10-05 22:20
 */

public interface ValidateCodeService {
    /**
     * 生成算术类型验证码
     * @param key
     * @param response
     * @throws IOException
     */
    void create(String key, HttpServletResponse response) throws IOException;

    /**
     * 校验验证码
     * @param key   前端上送 key
     * @param value 前端上送待校验值
     */
    boolean check(String key, String value);
}
