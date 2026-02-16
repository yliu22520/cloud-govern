package com.cloud.govern.common.utils;

import com.cloud.govern.common.enums.ErrorCode;
import com.cloud.govern.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

/**
 * 断言工具类
 *
 * @author cloud-govern
 */
public class AssertUtils {

    private AssertUtils() {
    }

    /**
     * 断言不为空
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, message);
        }
    }

    /**
     * 断言不为空
     */
    public static void notNull(Object obj, ErrorCode errorCode) {
        if (obj == null) {
            throw new BusinessException(errorCode);
        }
    }

    /**
     * 断言字符串不为空
     */
    public static void notBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, message);
        }
    }

    /**
     * 断言字符串不为空
     */
    public static void notBlank(String str, ErrorCode errorCode) {
        if (StringUtils.isBlank(str)) {
            throw new BusinessException(errorCode);
        }
    }

    /**
     * 断言为 true
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new BusinessException(ErrorCode.OPERATION_FAIL, message);
        }
    }

    /**
     * 断言为 true
     */
    public static void isTrue(boolean condition, ErrorCode errorCode) {
        if (!condition) {
            throw new BusinessException(errorCode);
        }
    }

    /**
     * 断言为 false
     */
    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new BusinessException(ErrorCode.OPERATION_FAIL, message);
        }
    }

    /**
     * 断言为 false
     */
    public static void isFalse(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throw new BusinessException(errorCode);
        }
    }

}
