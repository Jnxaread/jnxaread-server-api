package com.jnxaread.controller;

import com.jnxaread.bean.Chapter;
import com.jnxaread.bean.Comment;
import com.jnxaread.bean.Label;
import com.jnxaread.bean.User;
import com.jnxaread.bean.wrap.ChapterWrap;
import com.jnxaread.bean.wrap.CommentWrap;
import com.jnxaread.bean.wrap.FictionWrap;
import com.jnxaread.entity.UnifiedResult;
import com.jnxaread.model.CommentModel;
import com.jnxaread.model.FictionModel;
import com.jnxaread.service.LibraryService;
import com.jnxaread.util.ContentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 文库Controller
 *
 * @author 未央
 * @create 2020-05-06 15:06
 */
@RestController
@RequestMapping("/library")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    /**
     * 分页获取作品列表
     *
     * @param page
     * @return
     */
    @PostMapping("/list/fiction")
    public UnifiedResult getFictionList(Integer page) {
        if (page == null) {
            page = 1;
        }
        Map<String, Object> map = new HashMap<>();
        List<FictionWrap> fictionWrapList = libraryService.getFictionWrapList(page);

        /*
            遍历fictionWrapList，将fictionWrap封装到fictionModel中，并根据fictionId查询作品的标签，
            将查询到的标签封装到fictionModel的tags数组中
         */
        ArrayList<FictionModel> fictionModelList = new ArrayList<>();
        fictionWrapList.forEach(fictionWrap -> {
            FictionModel fictionModel = getFictionModel(fictionWrap);
            List<String> tagList = new ArrayList<>();
            List<Label> labelList = libraryService.getLabelByFictionId(fictionWrap.getId());
            labelList.forEach(label -> tagList.add(label.getLabel()));
            fictionModel.setTags(tagList.toArray(new String[labelList.size()]));
            fictionModelList.add(fictionModel);
        });

        long fictionCount = libraryService.getFictionCount();
        map.put("fictionList", fictionModelList);
        map.put("fictionCount", fictionCount);

        return UnifiedResult.ok(map);
    }

    /**
     * 创建作品接口
     *
     * @param session
     * @param newFiction
     * @return
     */
    @PostMapping("/new/fiction")
    public UnifiedResult createFiction(HttpSession session, FictionWrap newFiction) {
        User user = (User) session.getAttribute("user");
        newFiction.setUserId(user.getId());
        newFiction.setCreateTime(new Date());
        int fictionId = libraryService.addFiction(newFiction);
        //获取作品的标签数组
        String[] tags = newFiction.getTags();
        if (tags != null) {
            //遍历标签数组，将每个标签封装成标签对象写入数据库中
            for (String tag : tags) {
                Label label = new Label();
                label.setFictionId(fictionId);
                label.setUserId(user.getId());
                label.setLabel(tag);
                label.setCreateTime(new Date());
                libraryService.addLabel(label);
            }
        }
        return UnifiedResult.ok(fictionId);
    }

    /**
     * 创建章节接口
     *
     * @param session
     * @param newChapter
     * @return
     */
    @PostMapping("/new/chapter")
    public UnifiedResult createChapter(HttpSession session, ChapterWrap newChapter) {
        User user = (User) session.getAttribute("user");
        newChapter.setUserId(user.getId());
        int wordCount = ContentUtil.getWordCount(newChapter.getContent());
        newChapter.setWordCount(wordCount);
        newChapter.setCreateTime(new Date());
        int chapterId = libraryService.addChapter(newChapter);
        return UnifiedResult.ok(chapterId);
    }

    /**
     * 查看帖子详情接口
     *
     * @param id
     * @param page
     * @return
     */
    @PostMapping("/detail/fiction")
    public UnifiedResult getFiction(Integer id, Integer page) {
        if (id == null) {
            return UnifiedResult.build(400, "参数错误", null);
        }
        Map<String, Object> fictionMap = new HashMap<>();

        FictionWrap fictionWrap = libraryService.getFictionWrap(id);
        FictionModel fictionModel = getFictionModel(fictionWrap);
        fictionMap.put("fiction", fictionModel);

        Chapter chapter0 = libraryService.getChapterByNumber(id, 0);
        List<CommentWrap> commentWrapList = libraryService.getCommentWrapList(chapter0.getId());
        ArrayList<CommentModel> commentModelList = new ArrayList<>();
        commentWrapList.forEach(commentWrap -> {
            CommentModel commentModel = getCommentModel(commentWrap);
            commentModelList.add(commentModel);
        });
        fictionMap.put("comments", commentModelList);

        return UnifiedResult.ok(fictionMap);
    }

    /**
     * 查看章节详情接口
     *
     * @param id
     * @return
     */
    @PostMapping("/detail/chapter")
    public UnifiedResult getChapter(Integer id) {
        ChapterWrap chapterWrap = libraryService.getChapterWrap(id);
        return UnifiedResult.ok(chapterWrap);
    }

    /**
     * 发表评论接口
     *
     * @param newComment
     * @return
     */
    @PostMapping("/new/comment")
    public UnifiedResult addComment(HttpSession session, Comment newComment) {
        User user = (User) session.getAttribute("user");
        if (newComment.getChapterId() == null) {
            Chapter chapter0 = libraryService.getChapterByNumber(newComment.getFictionId(), 0);
            newComment.setChapterId(chapter0.getId());
        }
        newComment.setUserId(user.getId());
        newComment.setCreateTime(new Date());
        int result = libraryService.addComment(newComment);
        switch (result) {
            case 0:
                return UnifiedResult.ok();
            case 1:
                return UnifiedResult.build(400, "章节不存在", null);
            default:
                return UnifiedResult.build(500, "未知错误", null);
        }
    }

    /**
     * 将FictionWrap封装到FictionModel
     *
     * @param fictionWrap
     * @return
     */
    public FictionModel getFictionModel(FictionWrap fictionWrap) {
        FictionModel fictionModel = new FictionModel();
        fictionModel.setId(fictionWrap.getId());
        fictionModel.setCategory(fictionWrap.getCategory());
        fictionModel.setAuthor(fictionWrap.getUsername());
        fictionModel.setCreateTime(fictionWrap.getCreateTime());
        fictionModel.setTitle(fictionWrap.getTitle());
        fictionModel.setIntroduction(fictionWrap.getIntroduction());
        fictionModel.setChapterCount(fictionWrap.getChapterCount());
        fictionModel.setWordCount(fictionWrap.getWordCount());
        fictionModel.setCommentCount(fictionWrap.getCommentCount());
        fictionModel.setViewCount(fictionWrap.getViewCount());
        return fictionModel;
    }

    /**
     * 将CommentWrap封装到CommentModel
     *
     * @param commentWrap
     * @return
     */
    public CommentModel getCommentModel(CommentWrap commentWrap) {
        CommentModel commentModel = new CommentModel();
        commentModel.setUsername(commentWrap.getUsername());
        commentModel.setCreateTime(commentWrap.getCreateTime());
        commentModel.setContent(commentWrap.getContent());
        return commentModel;
    }

}
