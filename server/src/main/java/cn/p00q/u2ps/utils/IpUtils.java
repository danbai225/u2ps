package cn.p00q.u2ps.utils;

import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
/**
 * @author danbai
 * @date 2019-10-14 15:50
 */

public class IpUtils {
    public static String GetIPByCtx(ChannelHandlerContext ctx){
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
    }
}
