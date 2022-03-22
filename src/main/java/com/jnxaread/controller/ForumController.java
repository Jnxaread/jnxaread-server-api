package com.jnxaread.controller;

import com.jnxaread.bean.Reply;
import com.jnxaread.bean.Topic;
import com.jnxaread.bean.User;
import com.jnxaread.bean.wrap.ReplyWrap;
import com.jnxaread.bean.wrap.TopicWrap;
import com.jnxaread.common.UnifiedResult;
import com.jnxaread.model.ReplyModel;
import com.jnxaread.model.TopicModel;
import com.jnxaread.service.ForumService;
import com.jnxaread.util.ContentUtil;
import com.jnxaread.util.ModelUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 论坛内容处理Controller
 *
 * @author 未央
 * @create 2020-05-03 22:31
 */
@RestController
@RequestMapping("/forum")
public class ForumController {

    @Resource
    private ForumService forumService;

    /**
     * 发帖
     *
     * @param session  请求session对象
     * @param newTopic 新帖子表单数据
     * @return 返回执行结果信息及新帖子id
     */
    @PostMapping("/new/topic")
    public UnifiedResult submitTopic(HttpSession session, Topic newTopic) {
        User user = (User) session.getAttribute("user");
        if (user.getLocked()) {
            return UnifiedResult.build("400", "您已被封禁，无法发帖");
        }

        //论坛模块上线版块系统后删除这项检查
        if (!newTopic.getBoardId().equals(1)) {
            return UnifiedResult.build("400", "参数错误");
        }

        String regLabel = "^[\\u4e00-\\u9fa5]{2,4}$";
        Pattern pattern = Pattern.compile(regLabel);
        Matcher matcher = pattern.matcher(newTopic.getLabel());
        if (!matcher.matches()) {
            return UnifiedResult.build("400", "帖子标签为2至4位汉字");
        }

        if (newTopic.getTitle().length() < 4 || newTopic.getTitle().length() > 35) {
            return UnifiedResult.build("400", "帖子标题的长度为4至35个字符");
        }

        //校验帖子内容是否为空
        boolean inspection = ContentUtil.inspection(newTopic.getContent());
        if (!inspection) {
            return UnifiedResult.build("400", "帖子内容不能为空！");
        }

        if (newTopic.getContent().length() > 16384) {
            return UnifiedResult.build("400", "帖子内容的长度不得超过16000个字符");
        }

        //设置帖子作者id
        newTopic.setUserId(user.getId());
        newTopic.setBoardId(1);
        //设置发布时间
        newTopic.setCreateTime(new Date());
        //将帖子写入数据库
        int id = forumService.addTopic(newTopic);

        return UnifiedResult.ok(id);
    }

    /**
     * 回复
     *
     * @param session  请求session对象
     * @param newReply 新回复表单数据
     * @return 执行结果
     */
    @PostMapping("/new/reply")
    public UnifiedResult submitReply(HttpSession session, Reply newReply) {
        User user = (User) session.getAttribute("user");
        if (user.getLocked()) {
            return UnifiedResult.build("400", "您已被封禁，无法回复");
        }

        //校验回复内容是否为空
        boolean inspection = ContentUtil.inspection(newReply.getContent());
        if (!inspection) {
            return UnifiedResult.build("400", "回复内容不能为空！");
        }

        //对回复内容长度进行校验，如果内容长度超过11264，则返回错误信息
        if (newReply.getContent().length() > 11264) {
            return UnifiedResult.build("400", "回复的长度不得超过11000个字符");
        }

        //设置回复作者id
        newReply.setUserId(user.getId());
        //设置发布时间
        newReply.setCreateTime(new Date());
        //将回复写入数据库
        forumService.addReply(newReply);
        return UnifiedResult.ok();
    }

    /**
     * 获取帖子详情接口
     *
     * @param id   帖子id
     * @param page 帖子回复页码
     * @return 帖子、回复列表和回复总数
     */
    @PostMapping("/detail/topic")
    public UnifiedResult getTopic(Integer id, Integer page) {
        if (id == null || page == null) {
            return UnifiedResult.build("400", "参数错误");
        }
        Map<String, Object> topicMap = new HashMap<>();

        TopicWrap wrapTopic = forumService.getTopicWrap(id);
        if (wrapTopic == null) {
            return UnifiedResult.build("400", "帖子不存在");
        }

        TopicModel topicModel = ModelUtil.getTopicModel(wrapTopic);

        List<ReplyWrap> wrapReplyList = forumService.getReplyWrapList(id, page);

        ArrayList<ReplyModel> replyModelList = new ArrayList<>();
        wrapReplyList.forEach(wrapReply -> {
            ReplyModel replyModel = ModelUtil.getReplyModel(wrapReply);
            replyModelList.add(replyModel);
        });

        long replyCount = forumService.getReplyCountByTopicId(id);

        topicMap.put("topic", topicModel);
        topicMap.put("replies", replyModelList);
        topicMap.put("replyCount", replyCount);
        return UnifiedResult.ok(topicMap);
    }

    /**
     * 获取帖子列表接口
     *
     * @param session 请求session对象
     * @param userId  用户id
     * @param page    列表页码
     * @return 帖子列表和帖子总数
     */
    @PostMapping("/list/topic")
    @ResponseBody
    public UnifiedResult getTopicList(HttpSession session, Integer userId, Integer page) {
        if (userId == null || page == null) return UnifiedResult.build("400", "参数错误");

        User user = (User) session.getAttribute("user");
        int level = user == null ? 0 : user.getLevel();

        Map<String, Object> map = new HashMap<>();
        List<TopicWrap> topicWrapList = forumService.getTopicWrapList(userId, level, 1, page, 45);

        /*
            将包装类中的一部分属性封装到响应实体模型中返回
         */
        ArrayList<TopicModel> topicModelList = new ArrayList<>();
        topicWrapList.forEach(wrapTopic -> {
            TopicModel topicModel = ModelUtil.getTopicModel(wrapTopic);
            topicModelList.add(topicModel);
        });

        long topicCount = forumService.getTopicCount();
        map.put("topicList", topicModelList);
        map.put("topicCount", topicCount);
        return UnifiedResult.ok(map);
    }

    /**
     * 获取最新更新的帖子接口
     *
     * @param session 请求session对象
     * @return 最新更新帖子列表
     */
    @PostMapping("/list/topic/latest")
    public UnifiedResult getLatestTopicList(HttpSession session) {
        User user = (User) session.getAttribute("user");

        int level = user == null ? 0 : user.getLevel();

        List<TopicWrap> topicWrapList = forumService.getTopicWrapList(0, level, 1, 1, 3);
        ArrayList<TopicModel> topicModelList = new ArrayList<>();
        topicWrapList.forEach(topicWrap -> {
            TopicModel topicModel = ModelUtil.getTopicModel(topicWrap);
            topicModelList.add(topicModel);
        });
        return UnifiedResult.ok(topicModelList);
    }

    /**
     * 获取用户的回复列表
     *
     * @param session 请求session对象
     * @param userId  用户id
     * @return 用户回复列表
     */
    @PostMapping("/list/reply")
    public UnifiedResult getUserReplyList(HttpSession session, Integer userId) {
        if (userId == null) return UnifiedResult.build("400", "参数错误");

        User user = (User) session.getAttribute("user");

        //如果用户未登录，则用户等级为0，否则获取该用户的等级
        int level = user == null ? 0 : user.getLevel();

        List<ReplyWrap> replyWrapList = forumService.getReplyWrapListByUserId(userId, level);
        List<ReplyModel> replyModelList = new ArrayList<>();
        replyWrapList.forEach(replyWrap -> {
            ReplyModel replyModel = ModelUtil.getReplyModel(replyWrap);
            replyModelList.add(replyModel);
        });
        return UnifiedResult.ok(replyModelList);
    }

}
