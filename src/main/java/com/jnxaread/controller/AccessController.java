package com.jnxaread.controller;

import com.jnxaread.common.UnifiedResult;
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
        Integer total = (Integer) context.getAttribute("total");
        Integer PC = (Integer) context.getAttribute("PC");
        Integer android = (Integer) context.getAttribute("android");
        Integer iOS = (Integer) context.getAttribute("iOS");
        Integer others = (Integer) context.getAttribute("others");

        if (total == null) {
            context.setAttribute("total", 1);
        } else {
            context.setAttribute("total", total + 1);
        }

        String terminal = request.getHeader("User-Agent");
        if (terminal == null) {
            terminal = "";
        }

        if (terminal.contains("Android") || terminal.contains("Adr") || terminal.contains("android")) {
            if (android == null) {
                context.setAttribute("android", 1);
            } else {
                context.setAttribute("android", android + 1);
            }
        } else if (terminal.contains("ios") || terminal.contains("iPhone") || terminal.contains("iPad") || terminal.contains("iPod")) {
            if (iOS == null) {
                context.setAttribute("iOS", 1);
            } else {
                context.setAttribute("iOS", iOS + 1);
            }
        } else {
            String[] clients = {
                    "phone", "Mobile", "BlackBerry", "IEMobile", "MQQBrowser", "JUC", "Fennec", "wOSBrowser",
                    "BrowserNG", "WebOS", "Symbian", "Windows Phone", "pad", "pod"
            };
            boolean flag = true;
            for (String client : clients) {
                if (terminal.contains(client)) {
                    if (others == null) {
                        context.setAttribute("others", 1);
                        flag = false;
                    } else {
                        context.setAttribute("others", others + 1);
                    }
                    break;
                }
            }
            if (flag) {
                if (PC == null) {
                    context.setAttribute("PC", 1);
                } else {
                    context.setAttribute("PC", PC + 1);
                }
            }
        }
        return UnifiedResult.ok();
    }

    /**
     * 测试优雅停机
     *
     * @return 访问结果
     */
//    @GetMapping("/test")
    public UnifiedResult testGracefulShutdown() {
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return UnifiedResult.ok();
    }

}
