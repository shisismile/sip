package com.smile;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.*;
import java.util.*;


/**
 * TODO
 *
 * @author shimingen
 * @date 2019/6/14 10:32
 */
@Configuration
@Slf4j
@AllArgsConstructor
public class NettyConfig {

    private NettyConfigParam nettyConfigParam;

    @Bean(name = "eventLoopGroup",destroyMethod = "shutdownGracefully")
    public EventLoopGroup eventLoopGroup() {
        final Optional<Integer> threadNums = Optional.ofNullable(nettyConfigParam.getNetty().getThreadNums());
        EventLoopGroup group = new EpollEventLoopGroup(threadNums.orElse(5));
        return group;
    }

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        final Optional<Integer> bossGroupThreadCount = Optional.ofNullable(nettyConfigParam.getNetty().getBossGroupThreadCount());
        return new NioEventLoopGroup(bossGroupThreadCount.orElse(1));
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        final Optional<Integer> workerGroupThreadCount = Optional.ofNullable(nettyConfigParam.getNetty().getWorkerGroupThreadCount());
        return new NioEventLoopGroup(workerGroupThreadCount.orElse(2));
    }
    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpPort() {
        final Optional<Integer> bindPort = Optional.ofNullable(nettyConfigParam.getBindPort());
        return new InetSocketAddress(bindPort.orElse(9000));
    }

    @Bean(name = "tcpChannelOptions")
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        final Optional<String> backLog = Optional.ofNullable(nettyConfigParam.getNetty().getBackLog());
        final Optional<String> keepalive = Optional.ofNullable(nettyConfigParam.getNetty().getKeepalive());
        Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>(16);
        options.put(ChannelOption.SO_KEEPALIVE, keepalive.orElse("SO_KEEPALIVE"));
        options.put(ChannelOption.SO_BACKLOG, backLog.orElse("SO_BACKLOG"));
        return options;
    }

    @Bean(name = "udpChannelOptions")
    public Map<ChannelOption<?>, Object> udpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>(16);
        options.put(ChannelOption.SO_BROADCAST, true);
        options.put(ChannelOption.SO_REUSEADDR, true);
        return options;
    }

    @SuppressWarnings("unchecked")
    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(null);
        Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
        for (@SuppressWarnings("rawtypes")ChannelOption option : keySet) {
            b.option(option, tcpChannelOptions.get(option));
        }
        return b;
    }
    @Bean(name = "udpServerBootstrap")
    public Bootstrap bootstrap0(@Qualifier("eventLoopGroup") EventLoopGroup eventLoopGroup,@Qualifier("udpChannelOptions") Map<ChannelOption<?>, Object> udpChannelOptions) throws SocketException, UnknownHostException {
        final Optional<String> multicastAddress = Optional.ofNullable(nettyConfigParam.getMulticastAddress());
        final Optional<Integer> bindPort = Optional.ofNullable(nettyConfigParam.getBindPort());
        Bootstrap bootstrap=new Bootstrap();
        InetAddress localAddress = InetAddress.getByName(multicastAddress.orElse("239.21.208.200"));
        bootstrap.group(eventLoopGroup).channel(NioDatagramChannel.class)
                .handler(null);
        Set<ChannelOption<?>> keySet = udpChannelOptions.keySet();
        for (@SuppressWarnings("rawtypes")ChannelOption option : keySet) {
            bootstrap.option(option, udpChannelOptions.get(option));
        }
        bootstrap.localAddress(localAddress,bindPort.orElse(9000));
        return bootstrap;
    }

}
