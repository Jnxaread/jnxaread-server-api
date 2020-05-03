package com.jnxaread.controller;

import com.jnxaread.bean.Topic;
import com.jnxaread.bean.User;
import com.jnxaread.bean.model.ReplyModel;
import com.jnxaread.bean.model.TopicModel;
import com.jnxaread.entity.UnifiedResult;
import com.jnxaread.service.BaseForumService;
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
    private BaseForumService forumService;

    /**
     * 发帖
     *
     * @param session
     * @param newTopic
     * @return
     */
    @PostMapping("/new/topic")
    @ResponseBody
    public UnifiedResult submitTopic(HttpSession session, Topic newTopic) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return UnifiedResult.build(400, "请登录后再发帖！", null);
        }
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

        String validate = newTopic.getContent();
        String validateA = validate.replaceAll(" ", "");
        String validateB = validateA.replaceAll("<p>", "");
        String validateC = validateB.replaceAll("</p>", "");
        String validateD = validateC.replaceAll("&nbsp;", "");
        String validateE = validateD.replaceAll("<br>", "");
        if (validateE.length() == 0) {
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
     * 获取帖子详情
     *
     * @param id
     * @return
     */
    @RequestMapping("/topic")
    @ResponseBody
    public UnifiedResult getTopicDetail(Integer id, Integer page) {
        if (id == null || page == null) {
            return UnifiedResult.build(400, "参数错误", null);
        }
        Map<String, Object> topicMap = new HashMap<>();

        TopicModel topicModel = forumService.getTopicModel(id);
        if (topicModel == null) {
            return UnifiedResult.build(400, "帖子不存在", null);
        }

//        TopicModel topicModel = getTopicModel(wrapTopic);

        List<ReplyModel> replyModelList = forumService.getReplyModelList(id, page);

//        ArrayList<ReplyModel> replyModelList = new ArrayList<>();
//        wrapReplyList.forEach(wrapReply -> {
//            ReplyModel replyModel = getReplyModel(wrapReply);
//            replyModelList.add(replyModel);
//        });

        long replyCount = forumService.getReplyCountByTopicId(id);

        topicMap.put("topic", topicModel);
        topicMap.put("replyList", replyModelList);
        topicMap.put("replyCount", replyCount);
        return UnifiedResult.ok(topicMap);
    }

}
