package com.jnxaread.timer;

import com.jnxaread.container.AccessIPContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 访问限制定时任务类
 * 用于初始化访问IP容器和受限IP容器
 *
 * @Author 未央
 * @Create 2021-01-29 10:01
 */
@Component
public class AccessLimitTimerTask {
    private final AccessIPContainer accessIPContainer = AccessIPContainer.getAccessIpContainer();

    /**
     * 每日0点清空AccessIPMap中的数据
     */
    @Scheduled(cron = " 0 0 0 * * ? ")
    public void initAccessIPMap() {
        ConcurrentHashMap<String, long[]> accessIPMap = accessIPContainer.getAccessIPMap();
        accessIPMap.clear();
    }

    /**
     * 每分钟过滤一次LimitedIPMap中的数据
     */
    @Scheduled(cron = " 0 */1 * * * ? ")
    public void initLimitedIPMap() {
        ConcurrentHashMap<String, Long> limitedIPMap = accessIPContainer.getLimitedIPMap();
        for (String key : limitedIPMap.keySet()) {
            Long limitTime = limitedIPMap.get(key);
            long currentTime = System.currentTimeMillis();
            if (limitTime < currentTime) {
                limitedIPMap.remove(key);
            }
        }
    }

}
