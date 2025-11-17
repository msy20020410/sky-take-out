package com.sky.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sky.utils.UserContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long userId = UserContext.getCurrentUser();

        // 自动填充时间
        strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);

        // 自动填充创建人
        strictInsertFill(metaObject, "createUser", Long.class, userId);
        strictInsertFill(metaObject, "updateUser", Long.class, userId);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long userId = UserContext.getCurrentUser();

        // 自动填充更新时间
        strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);

        // 自动填充更新人
        strictInsertFill(metaObject, "updateUser", Long.class, userId);
    }
}
