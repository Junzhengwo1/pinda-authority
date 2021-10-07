package com.itheima.pinda.authority.biz.service.common.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.pinda.authority.biz.dao.auth.common.OptLogMapper;
import com.itheima.pinda.authority.biz.service.common.OptLogService;
import com.itheima.pinda.authority.entity.common.OptLog;
import com.itheima.pinda.dozer.DozerUtils;
import com.itheima.pinda.log.entity.OptLogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author huajieli
 * @create 2021-10-07 17:56
 */
@Service
public class OptLogServiceImpl extends ServiceImpl<OptLogMapper,OptLog> implements OptLogService {
    @Autowired
    private DozerUtils dozer;

    @Override
    public void save(OptLogDTO optLogDTO) {
        baseMapper.insert(dozer.map(optLogDTO, OptLog.class));
    }
}
