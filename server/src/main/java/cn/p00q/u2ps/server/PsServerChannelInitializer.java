package cn.p00q.u2ps.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @program: u2ps
 * @description: 服务器通道初始化
 * @author: DanBai
 * @create: 2020-08-01 17:49
 **/
@Slf4j
public class PsServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel){
        try {
            //添加编解码
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            //读超时时间设置为10s，0表示不监控
            socketChannel.pipeline().addLast(new IdleStateHandler(0, 0, 50, TimeUnit.SECONDS));
            //加入处理事件
            socketChannel.pipeline().addLast(new StringDecoder());
            socketChannel.pipeline().addLast(new PsServerHandler());
            socketChannel.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
            socketChannel.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }

    }
}