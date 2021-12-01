package com.sdyx.report.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DataSourceDTO {

    /**
     * 连接池名称
     */
    @NotBlank
    private String poolName;

    /**
     * 驱动类类名
     */
    @NotBlank
    private String driverClassName;

    /**
     * JDBC url 地址
     */
    @NotBlank
    private String url;

    /**
     * JDBC 用户名
     */
    @NotBlank
    private String username;

    /**
     * JDBC 密码
     */
    @NotBlank
    private String password;
}