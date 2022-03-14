package com.jnxaread.interceptor;

import com.jnxaread.constant.UnifiedCode;
import com.jnxaread.container.AccessIPContainer;
import com.jnxaread.exception.GlobalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 访问限制拦截器
 * 对每一次访问的IP进行识别，判断访问IP是否为恶意IP
 * 如果为恶意IP，则加入恶意IP名单进行封禁处理
 * <p>
 * 对于IP访问频率的处理：
 * 对于访问次数在300次以内的IP，直接放行
 * 对于访问次数在300次到1500次以内的IP【
 * 如果访问频率高于15次/秒，则为恶意IP，直接封禁
 * 如果访问频率在10到15之间，则封禁60分钟
 * 如果访问频率在8到10之间，则封禁15分钟
 * 如果访问频率在5到8之间，则封禁3分钟
 * 】
 * 对于访问次数在1500次到3600次以内的IP【
 * 如果访问频率大于3次/秒，直接封禁
 * 】
 * 对于访问次数超过3600次的IP【
 * 如果访问频率大于1次/秒，直接封禁
 * 】
 *
 * @Author 未央
 * @Create 2021-01-27 22:47
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger("access");
    @Value("${jnxaread.environment.current}")
    private String ENV_CURRENT;

    // 安全访问次数
    private static final long SAFE_ACCESS_COUNT = 300;
    // 警告访问次数
    private static final long WARN_ACCESS_COUNT = 1500;
    // 危险访问次数
    private static final long DANGER_ACCESS_COUNT = 3600;

    // 恶意请求访问频率
    private static final double VICIOUS_SECOND_ACCESS_RATE = 15.0;
    // 高访问频率
    private static final double HIGH_SECOND_ACCESS_RATE = 10.0;
    // 中访问频率
    private static final double MID_SECOND_ACCESS_RATE = 8.0;
    // 低访问频率
    private static final double LOW_SECOND_ACCESS_RATE = 5.0;
    // 分钟内访问频率，单位：次/秒
    private static final double MINUTE_ACCESS_RATE = 3.0;
    // 小时内访问频率，单位：次/秒
    private static final double HOUR_ACCESS_RATE = 1.0;

    // 高频访问限制时长
    private static final long HIGH_LIMIT_TIME = 60 * 60 * 1000;
    // 中频访问限制时长
    private static final long MID_LIMIT_TIME = 15 * 60 * 1000;
    // 低频访问限制时长
    private static final long LOW_LIMIT_TIME = 3 * 60 * 1000;

    private static final UnifiedCode passCheckForbidden = UnifiedCode.PASS_CHECK_FORBIDDEN;
    private static final UnifiedCode passCheckLimited = UnifiedCode.PASS_CHECK_LIMITED;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取单例的访问IP容器
        AccessIPContainer accessIPContainer = AccessIPContainer.getAccessIpContainer();
        // 从容器中获取恶意IP列表
        ArrayList<String> viciousIPList = accessIPContainer.getViciousIPList();
        // 根据工程运行环境动态获取用户IP
        String clientAddr;
        if (this.ENV_CURRENT.equals("develop")) {
            clientAddr = request.getRemoteAddr();
        } else {
            clientAddr = request.getHeader("X-Real-IP");
        }
        // 如果恶意IP列表中包含该用户IP，则直接返回
        if (viciousIPList.contains(clientAddr)) {
            throw new GlobalException(passCheckForbidden.getCode());
        }
        // 如果限制访问IP列表中包含该用户IP，则直接返回
        ConcurrentHashMap<String, Long> limitedIPMap = accessIPContainer.getLimitedIPMap();
        if (limitedIPMap.containsKey(clientAddr)) {
            throw new GlobalException(passCheckLimited.getCode());
        }

        // 获取用户IP访问信息Map
        ConcurrentHashMap<String, long[]> accessIPMap = accessIPContainer.getAccessIPMap();

        long[] accessInfo;
        long currentTime = System.currentTimeMillis();
        // 如果该IP之前访问过
        if (accessIPMap.containsKey(clientAddr)) {
            accessInfo = accessIPMap.get(clientAddr);
            long accessCount = accessInfo[0];

            // 如果该IP的访问次数低于安全访问次数，则访问次数+1后放行
            if (accessCount < SAFE_ACCESS_COUNT) {
                accessInfo[0] = accessCount + 1;
                return true;
            }

            long accessTime = accessInfo[1];
            long intervalTime = currentTime - accessTime;
            // 计算该IP的访问频率
            double accessRate = (double) accessCount / intervalTime * 1000;

            String accessMsg;
            accessMsg = clientAddr + "-" + accessCount + "-" + accessTime;
            // 如果访问次数高于安全次数且低于警告次数
            if (accessCount < WARN_ACCESS_COUNT) {
                // 如果访问频率高于恶意IP访问频率，直接将该IP加入恶意IP列表并返回
                if (accessRate > VICIOUS_SECOND_ACCESS_RATE) {
                    viciousIPList.add(clientAddr);
                    logger.error(accessMsg);
                    throw new GlobalException(passCheckForbidden.getCode());
                }
                if (accessRate > HIGH_SECOND_ACCESS_RATE) {
                    limitedIPMap.put(clientAddr, currentTime + HIGH_LIMIT_TIME);
                    logger.warn(accessMsg + "-" + HIGH_LIMIT_TIME);
                    throw new GlobalException(passCheckLimited.getCode());
                }
                if (accessRate > MID_SECOND_ACCESS_RATE) {
                    limitedIPMap.put(clientAddr, currentTime + MID_LIMIT_TIME);
                    logger.warn(accessMsg + "-" + MID_LIMIT_TIME);
                    throw new GlobalException(passCheckLimited.getCode());
                }
                if (accessRate > LOW_SECOND_ACCESS_RATE) {
                    limitedIPMap.put(clientAddr, currentTime + LOW_LIMIT_TIME);
                    logger.warn(accessMsg + "-" + LOW_LIMIT_TIME);
                    throw new GlobalException(passCheckLimited.getCode());
                }
            } else if (accessCount < DANGER_ACCESS_COUNT) {
                if (accessRate > MINUTE_ACCESS_RATE) {
                    viciousIPList.add(clientAddr);
                    logger.error(accessMsg);
                    throw new GlobalException(passCheckForbidden.getCode());
                }
            } else {
                if (accessRate > HOUR_ACCESS_RATE) {
                    viciousIPList.add(clientAddr);
                    logger.error(accessMsg);
                    throw new GlobalException(passCheckForbidden.getCode());
                }
            }
            accessInfo[0] = accessCount + 1;
            return true;
        }

        accessInfo = new long[2];
        accessInfo[0] = 1L;
        accessInfo[1] = currentTime;
        accessIPMap.put(clientAddr, accessInfo);

        //0.0.0.0.0.1
        logger.info(clientAddr);

        return true;
    }
}
