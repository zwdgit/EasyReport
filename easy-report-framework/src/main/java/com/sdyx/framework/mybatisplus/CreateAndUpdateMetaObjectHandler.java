package com.sdyx.framework.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sdyx.common.core.domain.model.LoginUser;
import com.sdyx.common.exception.ServiceException;
import com.sdyx.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import cn.hutool.http.HttpStatus;

import java.util.Date;

@Slf4j
public class CreateAndUpdateMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            //根据属性名字设置要填充的值
            if (metaObject.hasGetter("createTime")) {
                this.setFieldValByName("createTime" , new Date(), metaObject);
            }
            if (metaObject.hasGetter("createBy")) {
                this.setFieldValByName("createBy" , getLoginUsername(), metaObject);
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
        updateFill(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            if (metaObject.hasGetter("updateBy")) {
                this.setFieldValByName("updateBy" , getLoginUsername(), metaObject);
            }
            if (metaObject.hasGetter("updateTime")) {
                this.setFieldValByName("updateTime" , new Date(), metaObject);
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    /**
     * 获取登录用户名
     */
    private String getLoginUsername() {
        LoginUser loginUser;
        try {
            loginUser = SecurityUtils.getLoginUser();
        } catch (Exception e) {
            log.error("自动注入警告 => 用户未登录");
            return null;
        }
        return loginUser.getUsername();
    }

}
