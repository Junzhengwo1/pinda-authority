package com.itheima.pinda.authority.service;

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
}
