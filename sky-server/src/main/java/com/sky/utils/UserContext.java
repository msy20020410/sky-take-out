package com.sky.utils;

public class UserContext {

    private static final ThreadLocal<Long> USER_HOLDER = new ThreadLocal<>();

    public static void setCurrentUser(Long userId) {
        USER_HOLDER.set(userId);
    }

    public static Long getCurrentUser() {
        return USER_HOLDER.get();
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
