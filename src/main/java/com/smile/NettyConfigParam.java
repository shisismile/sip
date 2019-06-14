package com.smile;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author shimingen
 * @date 2019/6/14 10:35
 */
@Configuration
@ConfigurationProperties(prefix = "server")
@Data
public class NettyConfigParam {
    private Boolean enabled;
    private String bindAddress;
    private Integer bindPort;
    private String multicastAddress;
    private String network;
    private NettyParam netty;
}

@Configuration
@ConfigurationProperties(prefix = "server.netty")
@Data
class NettyParam {
    private String leakDetectorLevel;
    private Integer bossGroupThreadCount;
    private Integer workerGroupThreadCount;
    private Integer maxPayloadSize;
    private String keepalive;
    private String backLog;
    private Integer threadNums;
}