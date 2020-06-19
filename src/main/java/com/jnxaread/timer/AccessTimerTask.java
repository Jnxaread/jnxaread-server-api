package com.jnxaread.timer;

import com.jnxaread.bean.Access;
import com.jnxaread.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletContext;
import java.util.Date;

/**
 * @author 未央
 * @create 2019-11-05 19:38
 */
@Controller
public class AccessTimerTask {

    @Autowired
    private AccessService accessService;

    @Autowired
    private ServletContext context;

    @Scheduled(cron = " 0 0/15 * * * ? ")
    public void saveAccessCount() {

        Integer totalAccess = (Integer) context.getAttribute("totalAccess");
        Integer pcAccess = (Integer) context.getAttribute("pcAccess");
        Integer androidAccess = (Integer) context.getAttribute("androidAccess");
        Integer iosAccess = (Integer) context.getAttribute("iosAccess");
        Integer othersAccess = (Integer) context.getAttribute("othersAccess");

        if (totalAccess == null) {
            totalAccess = 0;
        }
        if (pcAccess == null) {
            pcAccess = 0;
        }
        if (androidAccess == null) {
            androidAccess = 0;
        }
        if (iosAccess == null) {
            iosAccess = 0;
        }
        if (othersAccess == null) {
            othersAccess = 0;
        }

        context.setAttribute("totalAccess", 0);
        context.setAttribute("pcAccess", 0);
        context.setAttribute("androidAccess", 0);
        context.setAttribute("iosAccess", 0);
        context.setAttribute("othersAccess", 0);

        Access newAccess = new Access();
        newAccess.setTotal(totalAccess);
        newAccess.setPC(pcAccess);
        newAccess.setAndroid(androidAccess);
        newAccess.setiOS(iosAccess);
        newAccess.setOthers(othersAccess);
        newAccess.setCreateTime(new Date());

        accessService.addAccess(newAccess);

    }

}
