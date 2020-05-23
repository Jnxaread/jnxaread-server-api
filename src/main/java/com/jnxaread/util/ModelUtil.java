package com.jnxaread.util;

import com.jnxaread.bean.Chapter;
import com.jnxaread.bean.wrap.*;
import com.jnxaread.model.*;

/**
 * @author 未央
 * @create 2020-05-23 18:04
 */
public class ModelUtil {

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
        if (chapter.getContent() != null) {
            chapterModel.setContent(chapter.getContent());
        }
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
        if (chapterWrap.getUsername() != null) {
            chapterModel.setAuthor(chapterWrap.getUsername());
        }
        chapterModel.setCreateTime(chapterWrap.getCreateTime());
        chapterModel.setNumber(chapterWrap.getNumber());
        chapterModel.setTitle(chapterWrap.getTitle());
        chapterModel.setWordCount(chapterWrap.getWordCount());
        chapterModel.setCommentCount(chapterWrap.getCommentCount());
        chapterModel.setViewCount(chapterWrap.getViewCount());
        if (chapterWrap.getContent() != null) {
            chapterModel.setContent(chapterWrap.getContent());
        }
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
     * 将ReplyWrap封装到ReplyModel
     *
     * @param wrapReply
     * @return
     */
    public static ReplyModel getReplyModel(ReplyWrap wrapReply) {
        ReplyModel replyModel = new ReplyModel();
        replyModel.setUsername(wrapReply.getUsername());
        replyModel.setCreateTime(wrapReply.getCreateTime());
        replyModel.setFloor(wrapReply.getFloor());
        replyModel.setQuote(wrapReply.getQuote());
        replyModel.setContent(wrapReply.getContent());
        return replyModel;
    }

}
