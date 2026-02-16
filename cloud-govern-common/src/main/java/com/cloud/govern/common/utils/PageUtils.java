package com.cloud.govern.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.govern.common.constant.CommonConstants;

/**
 * 分页工具类
 *
 * @author cloud-govern
 */
public class PageUtils {

    private PageUtils() {
    }

    /**
     * 构建分页对象
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param <T>      类型
     * @return 分页对象
     */
    public static <T> Page<T> buildPage(Long pageNum, Long pageSize) {
        return buildPage(pageNum, pageSize, null, null);
    }

    /**
     * 构建分页对象
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param orderBy  排序字段
     * @param isAsc    是否升序
     * @param <T>      类型
     * @return 分页对象
     */
    public static <T> Page<T> buildPage(Long pageNum, Long pageSize, String orderBy, Boolean isAsc) {
        if (pageNum == null || pageNum < 1) {
            pageNum = (long) CommonConstants.DEFAULT_PAGE_NUM;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = (long) CommonConstants.DEFAULT_PAGE_SIZE;
        }
        if (pageSize > CommonConstants.MAX_PAGE_SIZE) {
            pageSize = (long) CommonConstants.MAX_PAGE_SIZE;
        }

        Page<T> page = new Page<>(pageNum, pageSize);

        if (orderBy != null && !orderBy.isEmpty()) {
            if (Boolean.TRUE.equals(isAsc)) {
                page.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.asc(orderBy));
            } else {
                page.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.desc(orderBy));
            }
        }

        return page;
    }

}
