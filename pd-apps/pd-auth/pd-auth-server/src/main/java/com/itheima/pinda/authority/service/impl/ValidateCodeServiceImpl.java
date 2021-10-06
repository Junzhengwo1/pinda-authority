package com.itheima.pinda.authority.service.impl;

import com.itheima.pinda.authority.service.ValidateCodeService;
import com.itheima.pinda.common.constant.CacheKey;
import com.itheima.pinda.exception.BizException;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import net.oschina.j2cache.CacheChannel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author huajieli
 * @create 2021-10-05 22:21
 */

@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {
    @Autowired
    private CacheChannel cacheChannel;

    /**
     * 生成算术验证码，并进行缓存
     */
    @Override
    public void create(String key, HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(key)) {
            throw BizException.validFail("验证码key不能为空");
        }

        Captcha captcha = new ArithmeticCaptcha(115, 42);
        captcha.setCharType(2);
        String text = captcha.text();
        //将验证码进行缓存(会放到一级缓存和二级缓存中)
        cacheChannel.set(CacheKey.CAPTCHA,key,text);

        //将生成的图片验证码图片通过输出流写回到客户端浏览器
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);

        captcha.out(response.getOutputStream());
    }
}
