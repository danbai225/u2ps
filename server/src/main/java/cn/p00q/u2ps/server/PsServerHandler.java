package cn.p00q.u2ps.server;
import cn.p00q.choice.Choice;
import cn.p00q.u2ps.bean.Msg;
import cn.p00q.u2ps.service.impl.PsServiceImpl;
import cn.p00q.u2ps.utils.IpUtils;
import cn.p00q.u2ps.utils.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: u2ps
 * @description: 服务响应处理
 * @author: DanBai
 * @create: 2020-08-01 17:50
 **/
@Slf4j
public class PsServerHandler extends ChannelInboundHandlerAdapter {
    private static ExecutorService pool;

    static {
        pool=new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }
    /**
     * 客户端连接会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        try {
            log.info("新连接加入:{}",IpUtils.GetIPByCtx(ctx));
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 客户端发消息会触发
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        PsServiceImpl psServer = SpringUtil.getBean(PsServiceImpl.class);
        if(psServer!=null){
            try {
                pool.execute(()->{
                    String jsonStr=String.valueOf(msg);
                    Msg msgObject = Msg.JsonStrToMsg(jsonStr);
                    new Choice()
                            .add(()->psServer.Authentication(ctx,msgObject),Msg.AuthenticationClient,Msg.AuthenticationServer)
                            .add(()->psServer.sendAllTunnel(ctx),Msg.AllTunnel)
                            .add(()->psServer.isNodePortUse(msgObject),Msg.IsPortUse)
                            .add(()->psServer.updateFlow(msgObject),Msg.UpdateFlow)
                            .add(()->psServer.TcpWeb(ctx,msgObject),Msg.TcpWeb)
                            .Default(()->{log.info(msgObject.toString());})
                            .execute(msgObject.getType());
                });
            }catch (Exception e){
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }else {
            ctx.close();
        }
    }

    /**
     * 发生异常触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        try {
            PsServiceImpl psServer = SpringUtil.getBean(PsServiceImpl.class);
            log.error("异常断开: {}", IpUtils.GetIPByCtx(ctx));
            if (psServer != null) {
                psServer.disconnect(ctx);
            }
            ctx.close();
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}

