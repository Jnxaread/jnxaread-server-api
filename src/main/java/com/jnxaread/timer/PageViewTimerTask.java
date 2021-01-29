package com.jnxaread.timer;

import com.jnxaread.bean.Access;
import com.jnxaread.service.AccessService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.Date;

/**
 * @author 未央
 * @create 2019-11-05 19:38
 */
@Controller
public class PageViewTimerTask {

    @Resource
    private AccessService accessService;

    @Resource
    private ServletContext context;

    @Scheduled(cron = " 0 0/12 * * * ? ")
    public void savePageViewCount() {

        Integer total = (Integer) context.getAttribute("total");
        Integer PC = (Integer) context.getAttribute("PC");
        Integer android = (Integer) context.getAttribute("android");
        Integer iOS = (Integer) context.getAttribute("iOS");
        Integer others = (Integer) context.getAttribute("others");

        if (total == null) {
            total = 0;
        }
        if (PC == null) {
            PC = 0;
        }
        if (android == null) {
            android = 0;
        }
        if (iOS == null) {
            iOS = 0;
        }
        if (others == null) {
            others = 0;
        }

        context.setAttribute("total", 0);
        context.setAttribute("PC", 0);
        context.setAttribute("android", 0);
        context.setAttribute("iOS", 0);
        context.setAttribute("others", 0);

        Access newAccess = new Access();
        newAccess.setTotal(total);
        newAccess.setPC(PC);
        newAccess.setAndroid(android);
        newAccess.setiOS(iOS);
        newAccess.setOthers(others);
        newAccess.setCreateTime(new Date());

        accessService.addAccess(newAccess);

    }

}
