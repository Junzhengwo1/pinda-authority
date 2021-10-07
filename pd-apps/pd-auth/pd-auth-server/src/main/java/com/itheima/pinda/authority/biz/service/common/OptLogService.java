package com.itheima.pinda.authority.biz.service.common;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.pinda.authority.entity.common.OptLog;
import com.itheima.pinda.log.entity.OptLogDTO;

/**
 * @author huajieli
 * @create 2021-10-07 17:54
 *
 * 业务接口
 * 操作日志
 */
public interface OptLogService extends IService<OptLog> {
    /**
     * 保存日志
     * @param entity
     * @return
     */
    void save(OptLogDTO entity);
}
