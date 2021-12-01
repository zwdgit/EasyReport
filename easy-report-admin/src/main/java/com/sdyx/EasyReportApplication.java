package com.sdyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author EasyReport
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EasyReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyReportApplication.class, args);
        System.out.println("EasyReport启动成功 \n");
    }
}
