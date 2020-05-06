package com.jnxaread.controller;

import com.jnxaread.bean.Fiction;
import com.jnxaread.bean.User;
import com.jnxaread.entity.UnifiedResult;
import com.jnxaread.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author 未央
 * @create 2020-05-06 15:06
 */
@RestController
@RequestMapping("/library")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @PostMapping("/new/fiction")
    public UnifiedResult createFiction(HttpSession session,Fiction newFiction) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return UnifiedResult.build(400, "请登录后再创建作品！", null);
        }
        newFiction.setUserId(user.getId());
        newFiction.setCreateTime(new Date());
        int fictionId = libraryService.addFiction(newFiction);
        return UnifiedResult.ok(fictionId);
    }

}
