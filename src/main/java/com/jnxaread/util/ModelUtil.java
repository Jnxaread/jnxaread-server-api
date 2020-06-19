package com.jnxaread.util;

import com.jnxaread.bean.Chapter;
import com.jnxaread.bean.Reply;
import com.jnxaread.bean.User;
import com.jnxaread.bean.wrap.*;
import com.jnxaread.model.*;

/**
 * @author 未央
 * @create 2020-05-23 18:04
 */
public class ModelUtil {

    /**
     * 将User封装到UserModel
     *
     * @param user
     * @return
     */
    public static UserModel getUserModel(User user) {
        UserModel userModel = new UserModel();
        userModel.setId(user.getId());
        userModel.setAccount(user.getAccount());
        userModel.setUsername(user.getUsername());
        userModel.setEmail(user.getEmail());
        userModel.setGender(user.getGender());
        userModel.setFictionCount(user.getFictionCount());
        userModel.setChapterCount(user.getChapterCount());
        /*userModel.setCommentCount(user.getCommentCount());
        userModel.setTopicCount(user.getTopicCount());
        userModel.setReplyCount(user.getReplyCount());*/
        userModel.setLevel(user.getLevel());
        userModel.setComeFrom(user.getComeFrom());
        userModel.setSignature(user.getSignature());
        userModel.setIntroduction(user.getIntroduction());
        userModel.setCreateTime(user.getCreateTime());
        return userModel;
    }

    /**
     * 将FictionWrap封装到FictionModel
     *
     * @param fictionWrap
     * @return
     */
    public static FictionModel getFictionModel(FictionWrap fictionWrap) {
        FictionModel fictionModel = new FictionModel();
        fictionModel.setId(fictionWrap.getId());
        fictionModel.setCategory(fictionWrap.getCategory());
        fictionModel.setAuthor(fictionWrap.getUsername());
        fictionModel.setCreateTime(fictionWrap.getCreateTime());
        fictionModel.setTitle(fictionWrap.getTitle());
        fictionModel.setIntroduction(fictionWrap.getIntroduction());
        fictionModel.setTags(fictionWrap.getTags());
        fictionModel.setChapterCount(fictionWrap.getChapterCount());
        fictionModel.setWordCount(fictionWrap.getWordCount());
        fictionModel.setCommentCount(fictionWrap.getCommentCount());
        fictionModel.setViewCount(fictionWrap.getViewCount());
        fictionModel.setLastTime(fictionWrap.getLastTime());
        fictionModel.setLastNumber(fictionWrap.getLastNumber());
        fictionModel.setLastChapter(fictionWrap.getLastChapter());
        return fictionModel;
    }

    /**
     * 将Chapter封装到ChapterModel中
     *
     * @param chapter
     * @return
     */
    public static ChapterModel getChapterModel(Chapter chapter) {
        ChapterModel chapterModel = new ChapterModel();
        chapterModel.setId(chapter.getId());
        chapterModel.setFictionId(chapter.getFictionId());
        chapterModel.setCreateTime(chapter.getCreateTime());
        chapterModel.setNumber(chapter.getNumber());
        chapterModel.setTitle(chapter.getTitle());
        chapterModel.setWordCount(chapter.getWordCount());
        chapterModel.setCommentCount(chapter.getCommentCount());
        chapterModel.setViewCount(chapter.getViewCount());
        chapterModel.setRestricted(chapter.getRestricted());
        chapterModel.setContent(chapter.getContent());
        return chapterModel;
    }

    /**
     * 将ChapterWrap封装到ChapterModel中
     *
     * @param chapterWrap
     * @return
     */
    public static ChapterModel getChapterModel(ChapterWrap chapterWrap) {
        ChapterModel chapterModel = new ChapterModel();
        chapterModel.setId(chapterWrap.getId());
        chapterModel.setFictionId(chapterWrap.getFictionId());
        chapterModel.setAuthor(chapterWrap.getUsername());
        chapterModel.setCreateTime(chapterWrap.getCreateTime());
        chapterModel.setNumber(chapterWrap.getNumber());
        chapterModel.setTitle(chapterWrap.getTitle());
        chapterModel.setWordCount(chapterWrap.getWordCount());
        chapterModel.setCommentCount(chapterWrap.getCommentCount());
        chapterModel.setViewCount(chapterWrap.getViewCount());
        chapterModel.setRestricted(chapterWrap.getRestricted());
        chapterModel.setContent(chapterWrap.getContent());
        return chapterModel;
    }

    /**
     * 将CommentWrap封装到CommentModel
     *
     * @param commentWrap
     * @return
     */
    public static CommentModel getCommentModel(CommentWrap commentWrap) {
        CommentModel commentModel = new CommentModel();
        commentModel.setUsername(commentWrap.getUsername());
        commentModel.setCreateTime(commentWrap.getCreateTime());
        commentModel.setContent(commentWrap.getContent());
        commentModel.setFictionTitle(commentWrap.getFictionTitle());
        return commentModel;
    }

    /**
     * 将TopicWrap封装到TopicModel
     *
     * @param wrapTopic
     * @return
     */
    public static TopicModel getTopicModel(TopicWrap wrapTopic) {
        TopicModel topicModel = new TopicModel();
        topicModel.setId(wrapTopic.getId());
        topicModel.setLabel(wrapTopic.getLabel());
        topicModel.setTitle(wrapTopic.getTitle());
        if (wrapTopic.getContent() != null) {
            topicModel.setContent(wrapTopic.getContent());
        }
        topicModel.setUsername(wrapTopic.getUsername());
        topicModel.setCreateTime(wrapTopic.getCreateTime());
        if (wrapTopic.getLastReply() != null) {
            topicModel.setLastReply(wrapTopic.getLastReply());
        }
        if (wrapTopic.getLastSubmit() != null) {
            topicModel.setLastSubmit(wrapTopic.getLastSubmit());
        }
        topicModel.setReplyCount(wrapTopic.getReplyCount());
        topicModel.setViewCount(wrapTopic.getViewCount());
        return topicModel;
    }

    /**
     * 将Reply封装到ReplyModel中
     *
     * @param reply
     * @return
     */
    public static ReplyModel getReplyModel(Reply reply) {
        ReplyModel replyModel = new ReplyModel();
        replyModel.setId(reply.getId());
        replyModel.setCreateTime(reply.getCreateTime());
        replyModel.setFloor(reply.getFloor());
        replyModel.setContent(reply.getContent());
        return replyModel;
    }

    /**
     * 将ReplyWrap封装到ReplyModel
     *
     * @param replyWrap
     * @return
     */
    public static ReplyModel getReplyModel(ReplyWrap replyWrap) {
        ReplyModel replyModel = new ReplyModel();
        replyModel.setId(replyWrap.getId());
        replyModel.setUsername(replyWrap.getUsername());
        replyModel.setCreateTime(replyWrap.getCreateTime());
        replyModel.setFloor(replyWrap.getFloor());
        replyModel.setQuote(replyWrap.getQuote());
        if (replyWrap.getQuotedReply() != null) {
            ReplyModel quotedReplyModel = getReplyModel(replyWrap.getQuotedReply());
            replyModel.setQuotedReply(quotedReplyModel);
        }
        replyModel.setContent(replyWrap.getContent());
        replyModel.setTopicId(replyWrap.getTopicId());
        if (replyWrap.getTopicTitle() != null) {
            replyModel.setTopicTitle(replyWrap.getTopicTitle());
        }
        return replyModel;
    }

}
