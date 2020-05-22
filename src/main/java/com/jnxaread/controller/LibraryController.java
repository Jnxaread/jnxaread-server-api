package com.jnxaread.controller;

import com.jnxaread.bean.Chapter;
import com.jnxaread.bean.Comment;
import com.jnxaread.bean.Label;
import com.jnxaread.bean.User;
import com.jnxaread.bean.wrap.ChapterWrap;
import com.jnxaread.bean.wrap.CommentWrap;
import com.jnxaread.bean.wrap.FictionWrap;
import com.jnxaread.entity.UnifiedResult;
import com.jnxaread.model.ChapterModel;
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
     * 分页获取用户作品列表
     *
     * @param page
     * @return
     */
    @PostMapping("/list/fiction")
    public UnifiedResult getFictionList(Integer userId, Integer page) {
        if (userId == null) {
            userId = 0;
        }
        if (page == null) {
            page = 1;
        }
        List<FictionWrap> fictionWrapList = libraryService.getFictionWrapList(userId, page);

        ArrayList<FictionModel> fictionModelList = new ArrayList<>();
        fictionWrapList.forEach(fictionWrap -> {
            FictionModel fictionModel = getFictionModel(fictionWrap);
            fictionModelList.add(fictionModel);
        });

        long fictionCount = libraryService.getFictionCountByUserId(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("fictionList", fictionModelList);
        map.put("fictionCount", fictionCount);

        return UnifiedResult.ok(map);
    }

    /**
     * 分页获取我的作品列表接口
     *
     * @param session
     * @param page
     * @return
     */
    @PostMapping("/list/fiction/own")
    public UnifiedResult getOwnFictionList(HttpSession session, Integer page) {
        if (page == null) {
            page = 1;
        }
        User user = (User) session.getAttribute("user");
        List<FictionWrap> fictionWrapList = libraryService.getOwnFictionWrapList(user.getId(), page);

        ArrayList<FictionModel> fictionModelList = new ArrayList<>();
        fictionWrapList.forEach(fictionWrap -> {
            FictionModel fictionModel = getFictionModel(fictionWrap);
            fictionModelList.add(fictionModel);
        });

        long fictionCount = libraryService.getOwnFictionCount(user.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("fictionList", fictionModelList);
        map.put("fictionCount", fictionCount);

        return UnifiedResult.ok(map);
    }

    /**
     * 查看作品目录接口
     *
     * @param fictionId
     * @return
     */
    @PostMapping("/list/chapter")
    public UnifiedResult getChapterList(Integer fictionId) {
        if (fictionId == null) {
            return UnifiedResult.build(400, "参数错误", null);
        }
        List<Chapter> chapterList = libraryService.getChapterList(fictionId);
        List<ChapterModel> chapterModelList = new ArrayList<>();
        chapterList.forEach(chapter -> {
            ChapterModel chapterModel = getChapterModel(chapter);
            chapterModelList.add(chapterModel);
        });
        return UnifiedResult.ok(chapterModelList);
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
     * 查看作品详情接口
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
     * 获取作品简要信息接口
     *
     * @param id
     * @return
     */
    @PostMapping("/brief/fiction")
    public UnifiedResult getFictionBrief(Integer id) {
        if (id == null) {
            return UnifiedResult.build(400, "参数错误", null);
        }
        FictionWrap fictionWrap = libraryService.getFictionWrap(id);
        FictionModel fictionModel = getFictionModel(fictionWrap);
        return UnifiedResult.ok(fictionModel);
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
        ChapterModel chapterModel = getChapterModel(chapterWrap);
        List<CommentWrap> commentWrapList = libraryService.getCommentWrapList(id);
        List<CommentModel> commentModelList = new ArrayList<>();
        commentWrapList.forEach(commentWrap -> {
            CommentModel commentModel = getCommentModel(commentWrap);
            commentModelList.add(commentModel);
        });

        Map<String, Object> map = new HashMap<>();
        map.put("chapter", chapterModel);
        map.put("comments", commentModelList);

        return UnifiedResult.ok(map);
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
        if (newComment.getFictionId() == null && newComment.getChapterId() != null) {
            Chapter chapter = libraryService.getChapter(newComment.getChapterId());
            newComment.setFictionId(chapter.getFictionId());
        } else if (newComment.getFictionId() != null && newComment.getChapterId() == null) {
            Chapter chapter0 = libraryService.getChapterByNumber(newComment.getFictionId(), 0);
            newComment.setChapterId(chapter0.getId());
        } else {
            return UnifiedResult.build(400, "参数错误", null);
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
     * 将Chapter封装到ChapterModel中
     *
     * @param chapter
     * @return
     */
    public ChapterModel getChapterModel(Chapter chapter) {
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
    public ChapterModel getChapterModel(ChapterWrap chapterWrap) {
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
    public CommentModel getCommentModel(CommentWrap commentWrap) {
        CommentModel commentModel = new CommentModel();
        commentModel.setUsername(commentWrap.getUsername());
        commentModel.setCreateTime(commentWrap.getCreateTime());
        commentModel.setContent(commentWrap.getContent());
        return commentModel;
    }

}
