package org.example.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.server.codec.OrderFrameDecoder;
import org.example.server.codec.OrderFrameEncoder;
import org.example.server.codec.OrderProtocolDecoder;
import org.example.server.codec.OrderProtocolEncoder;
import org.example.server.handler.AuthHandler;
import org.example.server.handler.MetricsHandler;
import org.example.server.handler.OrderServerProcessHandler;
import org.example.server.handler.ServerIdleCheckHandler;

/**
 * @author one
 * @date 2020/04/12
 */
@Slf4j
public class Server {
    @SneakyThrows
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        // 配置优化
        serverBootstrap.option(NioChannelOption.SO_BACKLOG, 1024);
        serverBootstrap.childOption(NioChannelOption.TCP_NODELAY, true);

        LoggingHandler infoLogHandler = new LoggingHandler(LogLevel.INFO);
        serverBootstrap.handler(infoLogHandler);

        // 线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(new DefaultThreadFactory("boss"));
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(new DefaultThreadFactory("worker"));
        UnorderedThreadPoolEventExecutor businessGroup = new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("business"));
        NioEventLoopGroup eventLoopGroupForTrafficShaping = new NioEventLoopGroup(new DefaultThreadFactory("TS"));

        try {
            serverBootstrap.group(bossGroup, workerGroup);
            LoggingHandler debugLogHandler = new LoggingHandler(LogLevel.DEBUG);

            // 度量
            MetricsHandler metricsHandler = new MetricsHandler();

            // 流控
            GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(eventLoopGroupForTrafficShaping,
                    10 * 1024 * 1024, 10 * 1024 * 1024);

            // ip 过滤
            IpFilterRule ipRule = new IpSubnetFilterRule("127.1.1.1", 16, IpFilterRuleType.REJECT);
            RuleBasedIpFilter ruleBasedIpFilter = new RuleBasedIpFilter(ipRule);

            // 简单授权
            AuthHandler authHandler = new AuthHandler();

            // SSL 安全认证
            SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
            log.info("证书位置 {}", selfSignedCertificate.certificate().toString());
            SslContext sslContext = SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey())
                    .build();

            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    pipeline.addLast("debugLogHandler", debugLogHandler);
                    pipeline.addLast("ipFilter", ruleBasedIpFilter);
                    pipeline.addLast("trafficShapingHandler", globalTrafficShapingHandler);
                    pipeline.addLast("metricHandler", metricsHandler);

//                pipeline.addLast("sslHandler", sslContext.newHandler(ch.alloc()));

                    pipeline.addLast("idleHandler", new ServerIdleCheckHandler());

                    // 帧数据和协议编解码处理
                    pipeline.addLast("frameDecoder", new OrderFrameDecoder());
                    pipeline.addLast("frameEncoder", new OrderFrameEncoder());
                    pipeline.addLast("protocolDecoder", new OrderProtocolDecoder());
                    pipeline.addLast("protocolEncoder", new OrderProtocolEncoder());


                    // 写数据优化
                    pipeline.addLast("infoLogHandler", infoLogHandler);
                    pipeline.addLast("flushEnhance", new FlushConsolidationHandler(10, true));
                    pipeline.addLast("authHandler", authHandler);
                    pipeline.addLast(businessGroup, new OrderServerProcessHandler());
                }
            });
            ChannelFuture sync = serverBootstrap.bind(8090).sync();
            sync.channel().closeFuture().get();
        } catch (Exception e) {
            log.error("服务发生异常", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            businessGroup.shutdownGracefully();
            eventLoopGroupForTrafficShaping.shutdownGracefully();
        }
    }
}
