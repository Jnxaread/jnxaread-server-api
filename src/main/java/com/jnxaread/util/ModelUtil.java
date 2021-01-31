package com.jnxaread.util;

import com.jnxaread.bean.Category;
import com.jnxaread.bean.Chapter;
import com.jnxaread.bean.User;
import com.jnxaread.bean.wrap.*;
import com.jnxaread.model.*;

/**
 * 将所提供的实体对象转换为响应模型对象
 *
 * @author 未央
 * @create 2020-05-23 18:04
 */
public class ModelUtil {

    /**
     * 将User封装到UserModel
     *
     * @param user 需要转换的User对象
     * @return 封装好的UserModel对象
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
     * @param fictionWrap 需要转换的FictionWrap对象
     * @return 封装好的FictionModel对象
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
     * @param chapter 需要转换的Chapter对象
     * @return 封装好的ChapterModel对象
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
        chapterModel.setVisible(chapter.getVisible());
        return chapterModel;
    }

    /**
     * 将ChapterWrap封装到ChapterModel中
     *
     * @param chapterWrap 需要转换的ChapterWrap对象
     * @return 封装好的ChapterModel对象
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
     * @param commentWrap comment包装类
     * @return comment响应模型对象
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
     * @param topicWrap topic包装类对象
     * @return topic响应对象
     */
    public static TopicModel getTopicModel(TopicWrap topicWrap) {
        TopicModel topicModel = new TopicModel();
        topicModel.setId(topicWrap.getId());
        topicModel.setLabel(topicWrap.getLabel());
        topicModel.setTitle(topicWrap.getTitle());
        if (topicWrap.getContent() != null) {
            topicModel.setContent(topicWrap.getContent());
        }
        topicModel.setUsername(topicWrap.getUsername());
        topicModel.setCreateTime(topicWrap.getCreateTime());
        if (topicWrap.getLastReply() != null) {
            topicModel.setLastReply(topicWrap.getLastReply());
        }
        if (topicWrap.getLastSubmit() != null) {
            topicModel.setLastSubmit(topicWrap.getLastSubmit());
        }
        topicModel.setReplyCount(topicWrap.getReplyCount());
        topicModel.setViewCount(topicWrap.getViewCount());
        return topicModel;
    }

    /**
     * 将ReplyWrap封装到ReplyModel
     *
     * @param replyWrap reply包装类
     * @return reply响应对象
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

    /**
     * 将NoticeWrap封装到NoticeModel中
     *
     * @param noticeWrap notice包装类对象
     * @return notice响应对象
     */
    public static NoticeModel getNoticeModel(NoticeWrap noticeWrap) {
        NoticeModel noticeModel = new NoticeModel();
        noticeModel.setId(noticeWrap.getId());
        noticeModel.setLabel(noticeWrap.getLabel());
        noticeModel.setTitle(noticeWrap.getTitle());
        noticeModel.setContent(noticeWrap.getContent());
        noticeModel.setUsername(noticeWrap.getUsername());
        noticeModel.setCreateTime(noticeWrap.getCreateTime());
        noticeModel.setLastEdit(noticeWrap.getLastEdit());
        noticeModel.setLastTime(noticeWrap.getLastTime());
        noticeModel.setViewCount(noticeWrap.getViewCount());
        return noticeModel;
    }

    /**
     * 将Category封装到CategoryModel中
     *
     * @param category category对象
     * @return category响应对象
     */
    public static CategoryModel getCategoryModel(Category category) {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setId(category.getId());
        categoryModel.setName(category.getName());
        return categoryModel;
    }

}
