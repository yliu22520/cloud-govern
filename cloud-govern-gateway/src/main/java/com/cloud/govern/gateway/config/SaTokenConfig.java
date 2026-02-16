package com.cloud.govern.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 配置
 *
 * @author cloud-govern
 */
@Configuration
public class SaTokenConfig {

    /**
     * 注册 Sa-Token 全局过滤器
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截路由
                .addInclude("/**")
                // 排除路由（不需要登录验证）
                .addExclude(
                        "/auth/login",
                        "/auth/captcha",
                        "/auth/register",
                        "/actuator/**",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/doc.html"
                )
                // 认证函数：每次请求执行
                .setAuth(obj -> {
                    // 登录校验
                    SaRouter.match("/**", r -> StpUtil.checkLogin());
                })
                // 异常处理
                .setError(e -> SaResult.error(e.getMessage()));
    }

}
