package com.jnxaread.controller;

import com.jnxaread.entity.UnifiedResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * 页面浏览统计Controller
 * 用户每进入一个前端页面则增加一个pageView
 *
 * @Author 未央
 * @Create 2021-01-18 13:47
 */
@RestController
@RequestMapping("/access/counter")
public class AccessController {

    /**
     * 页面浏览计数器
     * 前端页面每次跳转都请求一次该接口，从而增加一个PV
     *
     * @param request http请求对象
     * @return 统一响应结构
     */
    @PostMapping("/pageView")
    public UnifiedResult pageViewCount(HttpServletRequest request) {
        ServletContext context = request.getServletContext();
        Integer totalAccess = (Integer) context.getAttribute("totalAccess");
        Integer pcAccess = (Integer) context.getAttribute("pcAccess");
        Integer androidAccess = (Integer) context.getAttribute("androidAccess");
        Integer iosAccess = (Integer) context.getAttribute("iosAccess");
        Integer othersAccess = (Integer) context.getAttribute("othersAccess");

        if (totalAccess == null) {
            context.setAttribute("totalAccess", 1);
        } else {
            context.setAttribute("totalAccess", totalAccess + 1);
        }

        String terminal = request.getHeader("User-Agent");
        if (terminal == null) {
            terminal = "";
        }

        if (terminal.contains("Android") || terminal.contains("Adr") || terminal.contains("android")) {
            if (androidAccess == null) {
                context.setAttribute("androidAccess", 1);
            } else {
                context.setAttribute("androidAccess", androidAccess + 1);
            }
        } else if (terminal.contains("ios") || terminal.contains("iPhone") || terminal.contains("iPad") || terminal.contains("iPod")) {
            if (iosAccess == null) {
                context.setAttribute("iosAccess", 1);
            } else {
                context.setAttribute("iosAccess", iosAccess + 1);
            }
        } else {
            String[] others = {
                    "phone", "Mobile", "BlackBerry", "IEMobile", "MQQBrowser", "JUC", "Fennec", "wOSBrowser",
                    "BrowserNG", "WebOS", "Symbian", "Windows Phone", "pad", "pod"
            };
            boolean flag = true;
            for (String other : others) {
                if (terminal.contains(other)) {
                    if (othersAccess == null) {
                        context.setAttribute("othersAccess", 1);
                        flag = false;
                    } else {
                        context.setAttribute("othersAccess", othersAccess + 1);
                    }
                    break;
                }
            }
            if (flag) {
                if (pcAccess == null) {
                    context.setAttribute("pcAccess", 1);
                } else {
                    context.setAttribute("pcAccess", pcAccess + 1);
                }
            }
        }
        return UnifiedResult.ok();
    }
}
