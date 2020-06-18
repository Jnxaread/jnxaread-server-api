package com.jnxaread.controller;

import com.jnxaread.bean.Reply;
import com.jnxaread.bean.Topic;
import com.jnxaread.bean.User;
import com.jnxaread.bean.wrap.ReplyWrap;
import com.jnxaread.bean.wrap.TopicWrap;
import com.jnxaread.entity.UnifiedResult;
import com.jnxaread.entity.UserLevel;
import com.jnxaread.model.ReplyModel;
import com.jnxaread.model.TopicModel;
import com.jnxaread.service.ForumService;
import com.jnxaread.util.ContentUtil;
import com.jnxaread.util.ModelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private ForumService forumService;

    @Autowired
    private UserLevel userLevel;

    /**
     * 发帖
     *
     * @param session
     * @param newTopic
     * @return
     */
    @PostMapping("/new/topic")
    public UnifiedResult submitTopic(HttpSession session, Topic newTopic) {
        User user = (User) session.getAttribute("user");
        if (user.getLocked()) {
            return UnifiedResult.build(400, "您已被封禁，无法发帖", null);
        }

        String regLabel = "^[\\u4e00-\\u9fa5]{2,4}$";
        Pattern pattern = Pattern.compile(regLabel);
        Matcher matcher = pattern.matcher(newTopic.getLabel());
        if (!matcher.matches()) {
            return UnifiedResult.build(400, "帖子标签为2至4位汉字", null);
        }

        if (newTopic.getTitle().length() < 4 || newTopic.getTitle().length() > 35) {
            return UnifiedResult.build(400, "帖子标题的长度为4至35个字符", null);
        }

        //校验帖子内容是否为空
        boolean inspection = ContentUtil.inspection(newTopic.getContent());
        if (!inspection) {
            return UnifiedResult.build(400, "帖子内容不能为空！", null);
        }

        if (newTopic.getContent().length() > 16384) {
            return UnifiedResult.build(400, "帖子内容的长度不得超过16000个字符", null);
        }

        //设置帖子作者id
        newTopic.setUserId(user.getId());
        //设置发布时间
        newTopic.setCreateTime(new Date());
        //将帖子写入数据库
        int id = forumService.addTopic(newTopic);

        return UnifiedResult.ok(id);
    }

    /**
     * 回复
     *
     * @param session
     * @param newReply
     * @return
     */
    @PostMapping("/new/reply")
    public UnifiedResult submitReply(HttpSession session, Reply newReply) {
        User user = (User) session.getAttribute("user");
        if (user.getLocked()) {
            return UnifiedResult.build(400, "您已被封禁，无法回复", null);
        }

        //校验回复内容是否为空
        boolean inspection = ContentUtil.inspection(newReply.getContent());
        if (!inspection) {
            return UnifiedResult.build(400, "回复内容不能为空！", null);
        }

        //对回复内容长度进行校验，如果内容长度超过11264，则返回错误信息
        if (newReply.getContent().length() > 11264) {
            return UnifiedResult.build(400, "回复的长度不得超过11000个字符", null);
        }

        //设置回复作者id
        newReply.setUserId(user.getId());
        //设置发布时间
        newReply.setCreateTime(new Date());
        //将回复写入数据库
        int result = forumService.addReply(newReply);
        if (result == 0) {
            return UnifiedResult.ok();
        } else if (result == 1) {
            return UnifiedResult.build(400, "帖子不存在", null);
        } else if (result == 2) {
            return UnifiedResult.build(400, "帖子已被锁定，无法回复", null);
        } else {
            return UnifiedResult.build(400, "引用的回复不存在", null);
        }
    }

    /**
     * 获取帖子详情
     *
     * @param id
     * @return
     */
    @PostMapping("/detail/topic")
    public UnifiedResult getTopic(Integer id, Integer page) {
        if (id == null || page == null) {
            return UnifiedResult.build(400, "参数错误", null);
        }
        Map<String, Object> topicMap = new HashMap<>();

        TopicWrap wrapTopic = forumService.getTopicWrap(id);
        if (wrapTopic == null) {
            return UnifiedResult.build(400, "帖子不存在", null);
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
     * 获取帖子列表
     *
     * @return
     */
    @PostMapping("/list/topic")
    @ResponseBody
    public UnifiedResult getTopicList(HttpSession session, Integer userId, Integer page) {
        if (page == null) return UnifiedResult.build(400, "参数错误", null);

        User user = (User) session.getAttribute("user");
        Integer level;
        if (user == null) level = 0;
        else level = user.getLevel();

        Map<String, Object> map = new HashMap<>();
        List<TopicWrap> topicWrapList = forumService.getTopicWrapList(level, page);

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
     * 获取用户的回复列表
     *
     * @param session
     * @param userId
     * @return
     */
    @PostMapping("/list/reply")
    public UnifiedResult getUserReplyList(HttpSession session, Integer userId) {
        if (userId == null) return UnifiedResult.build(400, "参数错误", null);

        User user = (User) session.getAttribute("user");
        Integer level;
        if (user == null) level = 0;
        else level = user.getLevel();

        List<ReplyWrap> replyWrapList = forumService.getReplyWrapListByUserId(userId, level);
        List<ReplyModel> replyModelList = new ArrayList<>();
        replyWrapList.forEach(replyWrap -> {
            ReplyModel replyModel = ModelUtil.getReplyModel(replyWrap);
            replyModelList.add(replyModel);
        });
        return UnifiedResult.ok(replyModelList);
    }

}
