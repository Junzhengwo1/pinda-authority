package com.itheima.pinda.authority.biz.service.auth.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.pinda.auth.server.utils.JwtTokenServerUtils;
import com.itheima.pinda.auth.utils.JwtUserInfo;
import com.itheima.pinda.auth.utils.Token;
import com.itheima.pinda.authority.biz.service.auth.ResourceService;
import com.itheima.pinda.authority.biz.service.auth.UserService;
import com.itheima.pinda.authority.dto.auth.LoginDTO;
import com.itheima.pinda.authority.dto.auth.ResourceQueryDTO;
import com.itheima.pinda.authority.dto.auth.UserDTO;
import com.itheima.pinda.authority.entity.auth.Resource;
import com.itheima.pinda.authority.entity.auth.User;
import com.itheima.pinda.base.R;
import com.itheima.pinda.common.constant.CacheKey;
import com.itheima.pinda.dozer.DozerUtils;
import com.itheima.pinda.exception.code.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huajieli
 * @create 2021-10-06 18:16
 * <p>
 * 认证管理器类
 */
@Slf4j
@Service
public class AuthManager {

    @Autowired
    private UserService userService;
    @Autowired
    private DozerUtils dozerUtils;
    @Autowired
    private JwtTokenServerUtils jwtTokenServerUtils;
    @Autowired
    private ResourceServiceImpl resourceServiceImpl;
    @Autowired
    private CacheChannel cacheChannel;

    /**
     * 登陆认证
     *
     * @param account
     * @param password
     * @return
     */
    public R<LoginDTO> login(String account, String password) {
        //校验账号、密码是否正确
        R<User> userR = checkAccountAndPassword(account, password);
        if (userR.getIsError()) {
            return R.fail("认证失败");
        }
        //为用户生成jwt令牌
        User data = userR.getData();
        JwtUserInfo jwtUserInfo = new JwtUserInfo(data.getId(), data.getAccount(), data.getName(), data.getOrgId(), data.getStationId());
        Token token = jwtTokenServerUtils.generateUserToken(jwtUserInfo, null);
        //查询当前用户拥有得的权限
        List<Resource> visibleResource = resourceServiceImpl.findVisibleResource(ResourceQueryDTO.builder().userId(data.getId()).build());
        log.info("当前用户拥有的权限：" + visibleResource);

        List<String> codePermission = null;
        if (visibleResource != null && !visibleResource.isEmpty()) {
            //将用户对应的权限进行返回（给前端使用,只要Resource中的code）
            codePermission = visibleResource.stream().map(Resource::getCode).collect(Collectors.toList());
            //将用户对应的权限进行返回缓存（给网关使用,需要Resource中的method+url组合起来）
            List<String> gateWayPermission = visibleResource.stream().map(resource -> {
                return resource.getMethod() + resource.getUrl();
            }).collect(Collectors.toList());
            //缓存网关使用的权限数据
            cacheChannel.set(CacheKey.USER_RESOURCE, data.getId().toString(), gateWayPermission);
        }

        LoginDTO loginDTO = LoginDTO.builder()
                .user(dozerUtils.map(userR.getData(), UserDTO.class))
                .token(token)
                .permissionsList(codePermission).build();
        return R.success(loginDTO);
    }

    private R<User> checkAccountAndPassword(String account, String password) {
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getAccount, account));
        //将传来的密码加密
        String md2Hex = DigestUtils.md2Hex(password);
        if (user == null || !user.getPassword().equals(md2Hex)) {
            //认证失败
            return R.fail(ExceptionCode.JWT_USER_INVALID);
        }
        //认证成功
        return R.success(user);
    }
}
