package com.jnxaread.controller;

import cn.hutool.core.date.DateUtil;
import com.jnxaread.bean.User;
import com.jnxaread.bean.wrap.UserWrap;
import com.jnxaread.constant.StatusCodeEnum;
import com.jnxaread.entity.UnifiedResult;
import com.jnxaread.entity.UserGrade;
import com.jnxaread.entity.UserLevel;
import com.jnxaread.model.UserModel;
import com.jnxaread.service.UserService;
import com.jnxaread.util.ModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户Controller
 * 用于处理用户相关的业务操作
 *
 * @author 未央
 * @create 2020-04-21 17:11
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserGrade userGrade;

    @Resource
    private UserLevel userLevel;

    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String usernameOfSender;

    @Value("${spring.mail.nickname}")
    private String nicknameOfSender;

    private final Logger logger = LoggerFactory.getLogger("login");

    /**
     * 用来存储发送过验证码的邮箱账号和发送时间
     * 如果验证码发送时间在两分钟之前，可以再次发送验证码
     * 否则不允许发送验证码
     */
    private final Map<String, Date> emailMap = new ConcurrentHashMap<>();

    /**
     * 用户登录接口
     *
     * @param request 请求request对象
     * @return 返回登录用户的信息
     */
    @PostMapping("/login")
    public UnifiedResult login(HttpServletRequest request) {
        HttpSession session = request.getSession();

        //先判断用户是否已经登录
        if (session.getAttribute("user") != null) {
            String code = StatusCodeEnum.ALREADY_LOGGED_IN.getCode();
            String desc = StatusCodeEnum.ALREADY_LOGGED_IN.getDescribe();
            return UnifiedResult.build(code, desc, null);
        }

        String account = request.getParameter("account");
        String password = request.getParameter("password");

        User user = userService.getUserByAccount(account);
        String ciphertext = DigestUtils.md5DigestAsHex(password.getBytes());
        if (user == null || !user.getPassword().equals(ciphertext.toUpperCase())) {
            String code = StatusCodeEnum.ACCOUNT_OR_PASSWORD_INVALID.getCode();
            String desc = StatusCodeEnum.ACCOUNT_OR_PASSWORD_INVALID.getDescribe();
            return UnifiedResult.build(code, desc, null);
        }
        session.setAttribute("user", user);


        user.setLoginCount(user.getLoginCount() + 1);
        user.setGrade(user.getGrade() + userGrade.getLogin());
        Integer[] gradeArr = userLevel.getGradeArr();
        for (int i = 0; i < gradeArr.length; i++) {
            if (gradeArr[i] > user.getGrade()) {
                user.setLevel(i - 1);
                break;
            }
        }
        userService.updateUser(user);

        String loginMsg = user.getId() + "-" + request.getHeader("X-Real-IP") + "-0-" + request.getHeader("User-Agent");
        logger.info(loginMsg);

        UserModel userModel = ModelUtil.getUserModel(user);
        return UnifiedResult.ok(userModel);
    }

    /**
     * 用户退出登录接口
     *
     * @param session 请求session对象
     * @return 返回成功信息
     */
    @PostMapping("/logout")
    public UnifiedResult logout(HttpSession session) {
        session.removeAttribute("user");
        return UnifiedResult.ok();
    }

    /**
     * 用户注册接口
     * 用户信息写入数据库后自动登录并返回用户信息
     *
     * @param request 请求request对象
     * @param newUser 注册用户表单数据
     * @return 返回用户信息
     */
    @PostMapping("/signup")
    public UnifiedResult signup(HttpServletRequest request, UserWrap newUser) {
        String regAccount = "^[a-zA-Z]([-_a-zA-Z0-9]{8,19})$";
        String regUsername = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]{2,12}$";
        String regEmail = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
//        String regMobile = "^1[34578]\\d{9}$";

        Pattern patternAccount = Pattern.compile(regAccount);
        Matcher matcherAccount = patternAccount.matcher(newUser.getAccount());
        if (!matcherAccount.matches()) {
            return UnifiedResult.build("400", "账号必须以英文字母开头，为字母、数字、下划线和中划线的组合，长度不得低于9位不得超过20位", null);
        }

        if (newUser.getPassword().length() < 9 || newUser.getPassword().length() > 32) {
            return UnifiedResult.build("400", "密码长度不得低于9位且不得超过32位", null);
        }

        Pattern patternUsername = Pattern.compile(regUsername);
        Matcher matcherUsername = patternUsername.matcher(newUser.getUsername());
        if (!matcherUsername.matches()) {
            return UnifiedResult.build("400", "用户名为汉字、字母、数字的组合且长度为2至12位", null);
        }

//        Pattern patternMobile = Pattern.compile(regMobile);
//        Matcher matcherMobile = patternMobile.matcher(newUser.getMobile());
//        if (!matcherMobile.matches()) {
//            return UnifiedResult.build("400", "手机号格式错误", null);
//        }

        Pattern patternEmail = Pattern.compile(regEmail);
        Matcher matcherEmail = patternEmail.matcher(newUser.getEmail());
        if (!matcherEmail.matches()) {
            return UnifiedResult.build("400", "邮箱格式错误", null);
        }

        if (newUser.getGender() != 0 && newUser.getGender() != 1 && newUser.getGender() != 2) {
            return UnifiedResult.build("400", "未知选项", null);
        }
        //验证邮箱验证码
        if (!newUser.getEmailCode().equals(request.getSession().getAttribute("emailCode"))) {
            return UnifiedResult.build("400", "验证码错误", null);
        }
        request.getSession().setAttribute("emailCode", null);

        User verifyAccount = userService.getUserByAccount(newUser.getAccount());
        if (verifyAccount != null) {
            return UnifiedResult.build("400", "账号已被注册", null);
        }

        User verifyUsername = userService.getUserByUsername(newUser.getUsername());
        if (verifyUsername != null) {
            return UnifiedResult.build("400", "用户名已被注册", null);
        }

        User verifyEmail = userService.getUserByEmail(newUser.getEmail());
        if (verifyEmail != null) {
            return UnifiedResult.build("400", "该邮箱已被绑定，无法注册", null);
        }

        //将密码进行md5加密
        String ciphertext = DigestUtils.md5DigestAsHex(newUser.getPassword().getBytes());
        newUser.setPassword(ciphertext.toUpperCase());

        newUser.setCreateTime(new Date());
        User user = userService.addUser(newUser);
        emailMap.remove(user.getEmail());

        //注册完成后自动登录
        request.setAttribute("username", user.getUsername());
        request.setAttribute("password", user.getPassword());

        return login(request);
    }

    /**
     * 生成6位邮件验证码
     *
     * @param session 请求session对象
     * @param email   目标邮箱地址
     * @return 返回执行结果
     */
    @PostMapping("/emailCode")
    public UnifiedResult getEmailCode(HttpSession session, String email) {
        Date date = emailMap.get(email);
        if (date != null && DateUtil.betweenMs(date, new Date()) < 120000) {
            return UnifiedResult.build("400", "请不要频繁获取验证码", null);
        }

        String regEmail = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        Pattern patternEmail = Pattern.compile(regEmail);
        Matcher matcherEmail = patternEmail.matcher(email);
        if (!matcherEmail.matches()) {
            return UnifiedResult.build("400", "邮箱格式错误", null);
        }

        // 生成6位随机验证码
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int r = random.nextInt(10);
            code.append(r);
        }

        // 发送邮箱验证码
        String content = "您的验证码为：" + code + "，此验证码十分钟内有效。";
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            message.setFrom(usernameOfSender, nicknameOfSender);
            message.setTo(email);
            message.setSubject("用户注册验证码");
            message.setText(content);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            String status = StatusCodeEnum.EMAIL_SENT_FAILED.getCode();
            String desc = StatusCodeEnum.EMAIL_SENT_FAILED.getDescribe();
            return UnifiedResult.build(status, desc, null);
        }

        //将邮箱账号和发送时间保存到Map中
        emailMap.put(email, new Date());

        //将邮箱验证码保存到session中
        session.setAttribute("emailCode", code.toString());
        //设置验证码的有效期为10分钟
        session.setMaxInactiveInterval(600);
        return UnifiedResult.ok();
    }

    /**
     * 修改用户基础信息接口
     * 此接口需要进行用户身份校验，
     * 只有所修改的用户信息的id与当前登录用户id相同时才允许修改
     *
     * @param changedUser 修改后的用户数据
     * @return 如果成功，返回更新后的用户数据
     */
    @PostMapping("/edit/baseInfo")
    public UnifiedResult changeBaseInfo(HttpSession session, User changedUser) {
        User user = (User) session.getAttribute("user");
        if (!user.getId().equals(changedUser.getId())) {
            return UnifiedResult.build("400", "参数错误", null);
        }
        userService.updateUser(changedUser);
        User updatedUser = userService.getUser(changedUser.getId());
        return UnifiedResult.ok(updatedUser);
    }

    /**
     * 修改用户密码接口
     * 此接口必须进行密码校验和邮箱验证码校验
     *
     * @param request 请求request对象
     * @return 返回执行结果信息
     */
    @PostMapping("/edit/password")
    public UnifiedResult changePassword(HttpServletRequest request) {
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String emailCode = request.getParameter("emailCode");
        if (oldPassword == null || newPassword == null || emailCode == null) {
            return UnifiedResult.build("401", "参数错误", null);
        }
        HttpSession session = request.getSession();
        if (!session.getAttribute("emailCode").equals(emailCode)) {
            return UnifiedResult.build("420", "验证码错误", null);
        }
        User user = (User) session.getAttribute("user");
        if (!user.getPassword().equals(oldPassword)) {
            return UnifiedResult.build("420", "密码错误", null);
        }
        user.setPassword(newPassword);
        userService.updateUser(user);
        return UnifiedResult.ok();
    }

}
