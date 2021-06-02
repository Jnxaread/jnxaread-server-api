package com.jnxaread.controller;

import com.jnxaread.bean.*;
import com.jnxaread.bean.wrap.ChapterWrap;
import com.jnxaread.bean.wrap.CommentWrap;
import com.jnxaread.bean.wrap.FictionWrap;
import com.jnxaread.entity.UnifiedResult;
import com.jnxaread.entity.UserLevel;
import com.jnxaread.model.CategoryModel;
import com.jnxaread.model.ChapterModel;
import com.jnxaread.model.CommentModel;
import com.jnxaread.model.FictionModel;
import com.jnxaread.service.LibraryService;
import com.jnxaread.util.ContentUtil;
import com.jnxaread.util.ModelUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

    @Resource
    private LibraryService libraryService;

    @Resource
    private UserLevel userLevel;

    /**
     * 分页获取用户作品列表
     *
     * @param session 请求的session
     * @param userId  用户ID
     * @param page    页码
     * @return 统一响应结构，包含作品列表及作品数量数据
     */
    @PostMapping("/list/fiction")
    public UnifiedResult getFictionList(HttpSession session, Integer userId, Integer page) {
        if (userId == null || page == null) return UnifiedResult.build("400", "参数错误", null);

        User user = (User) session.getAttribute("user");
        Integer level;
        if (user == null) {
            level = 0;
        } else {
            if (user.getId().equals(userId)) {
                level = userLevel.getLevelArr()[userLevel.getLevelArr().length - 1];
            } else {
                level = user.getLevel();
            }
        }

        List<FictionWrap> fictionWrapList = libraryService.getFictionWrapList(userId, level, page, 30);

        ArrayList<FictionModel> fictionModelList = new ArrayList<>();
        fictionWrapList.forEach(fictionWrap -> {
            FictionModel fictionModel = ModelUtil.getFictionModel(fictionWrap);
            fictionModelList.add(fictionModel);
        });

        long fictionCount = libraryService.getFictionCountByUserId(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("fictions", fictionModelList);
        map.put("fictionCount", fictionCount);

        return UnifiedResult.ok(map);
    }

    /**
     * 分页获取我的作品列表接口
     *
     * @param session 请求的session
     * @param page    页码
     * @return 统一响应结构，包含作品列表及作品数量数据
     */
    @PostMapping("/list/fiction/own")
    public UnifiedResult getOwnFictionList(HttpSession session, Integer page) {
        if (page == null) return UnifiedResult.build("400", "参数错误", null);
        User user = (User) session.getAttribute("user");
        List<FictionWrap> fictionWrapList = libraryService.getOwnFictionWrapList(user.getId(), page);

        ArrayList<FictionModel> fictionModelList = new ArrayList<>();
        fictionWrapList.forEach(fictionWrap -> {
            FictionModel fictionModel = ModelUtil.getFictionModel(fictionWrap);
            fictionModelList.add(fictionModel);
        });

        long fictionCount = libraryService.getOwnFictionCount(user.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("fictionList", fictionModelList);
        map.put("fictionCount", fictionCount);

        return UnifiedResult.ok(map);
    }

    /**
     * 首页最新更新作品查询接口
     *
     * @param session 请求的session
     * @return 统一响应结构，包含最新更新作品列表数据
     */
    @PostMapping("/list/fiction/latest")
    public UnifiedResult getLatestFictionList(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Integer level;
        if (user == null) {
            level = 0;
        } else {
            level = user.getLevel();
        }
        List<FictionWrap> fictionWrapList = libraryService.getFictionWrapList(0, level, 1, 3);
        ArrayList<FictionModel> fictionModelList = new ArrayList<>();
        fictionWrapList.forEach(fictionWrap -> {
            String[] queryTags = fictionWrap.getTags();
            int length = queryTags.length;
            if (length > 3) {
                length = 3;
            }
            String[] tags = new String[length];
            for (int i = 0; i < queryTags.length && i < length; i++) {
                tags[i] = queryTags[i];
            }
            fictionWrap.setTags(tags);
            FictionModel fictionModel = ModelUtil.getFictionModel(fictionWrap);
            fictionModelList.add(fictionModel);
        });
        return UnifiedResult.ok(fictionModelList);
    }

    /**
     * 查看作品目录接口
     *
     * @param session   请求的session
     * @param fictionId 作品ID
     * @return 统一响应结构，包含章节列表数据
     */
    @PostMapping("/list/chapter")
    public UnifiedResult getChapterList(HttpSession session, Integer fictionId) {
        if (fictionId == null) {
            return UnifiedResult.build("400", "参数错误", null);
        }

        User user = (User) session.getAttribute("user");
        List<Chapter> chapterList;
        if (user == null) {
            chapterList = libraryService.getChapterList(fictionId, 0, 0);
        } else {
            chapterList = libraryService.getChapterList(fictionId, user.getId(), user.getLevel());
        }
        List<ChapterModel> chapterModelList = new ArrayList<>();
        chapterList.forEach(chapter -> {
            ChapterModel chapterModel = ModelUtil.getChapterModel(chapter);
            chapterModelList.add(chapterModel);
        });
        return UnifiedResult.ok(chapterModelList);
    }

    /**
     * 获取所有作品类别接口
     * 该接口不需要进行权限校验
     *
     * @return 统一响应结构，包含类别列表数据
     */
    @PostMapping("/list/category")
    public UnifiedResult getCategoryList() {
        List<Category> categoryList = libraryService.getCategoryList();
        List<CategoryModel> categoryModelList = new ArrayList<>();
        categoryList.forEach(category -> {
            CategoryModel categoryModel = ModelUtil.getCategoryModel(category);
            categoryModelList.add(categoryModel);
        });
        return UnifiedResult.ok(categoryModelList);
    }

    /**
     * 获取作品类别ID列表
     *
     * @return 作品类别ID列表
     */
    private List<Integer> getCategoryIdList() {
        List<Category> categoryList = libraryService.getCategoryList();
        ArrayList<Integer> categoryIdList = new ArrayList<>();
        categoryList.forEach(category -> categoryIdList.add(category.getId()));
        return categoryIdList;
    }

    /**
     * 创建作品接口
     * 用户新建作品需要调用此接口
     * 调用此接口需要用户具有登录权限
     * 【标签】
     *
     * @param session    请求的session
     * @param newFiction 新增作品信息
     * @return 统一响应结构，包含新增作品的id
     */
    @PostMapping("/new/fiction")
    public UnifiedResult createFiction(HttpSession session, FictionWrap newFiction) {
        Integer categoryId = newFiction.getCategoryId();
        if (categoryId == null) {
            return UnifiedResult.build("400", "作品类别不能为空", null);
        }
        List<Integer> categoryIdList = getCategoryIdList();
        if (!categoryIdList.contains(categoryId)) {
            return UnifiedResult.build("400", "作品类别不存在", null);
        }

        User user = (User) session.getAttribute("user");
        newFiction.setUserId(user.getId());
        newFiction.setCreateTime(new Date());
        int fictionId = libraryService.addFiction(newFiction);
        //获取作品的标签数组
        String[] tags = newFiction.getTags();
        if (tags != null) {
            //遍历标签数组，将每个标签封装成标签对象写入数据库中
            for (String tag : tags) {
                //考虑到存在英文标签的情况，所以对于标签中的空格不进行处理
                if (tag.length() > 11) {
                    return UnifiedResult.build("406", "参数错误", null);
                } else if (tag.contains(" ")) {
                    return UnifiedResult.build("407", "参数错误", null);
                }
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
     * 用户发布新章节需要调用此接口
     * 此接口需要进行用户权限校验，只有作品的作者才能发表该作品的章节
     *
     * @param session    请求的session
     * @param newChapter 新增章节信息
     * @return 统一响应结构，包含新增章节的id
     */
    @PostMapping("/new/chapter")
    public UnifiedResult createChapter(HttpSession session, ChapterWrap newChapter) {
        User user = (User) session.getAttribute("user");
        newChapter.setUserId(user.getId());
        int wordCount = ContentUtil.getWordCount(newChapter.getContent());
        newChapter.setWordCount(wordCount);
        newChapter.setCreateTime(new Date());
        int chapterId = libraryService.addChapter(newChapter);
        if (chapterId > 0) {
            return UnifiedResult.ok(chapterId);
        } else if (chapterId == -1) {
            return UnifiedResult.build("400", "参数错误", null);
        } else if (chapterId == -2) {
            return UnifiedResult.build("400", "章节号已存在", null);
        } else {
            return UnifiedResult.build("400", "章节号错误", null);
        }
    }

    /**
     * 查看作品详情接口
     *
     * @param id 作品id
     * @return 统一响应结构，包含作品详情信息和评论列表
     */
    @PostMapping("/detail/fiction")
    public UnifiedResult getFiction(Integer id) {
        if (id == null) {
            return UnifiedResult.build("400", "参数错误", null);
        }
        Map<String, Object> fictionMap = new HashMap<>();

        FictionWrap fictionWrap = libraryService.getFictionWrap(id);
        FictionModel fictionModel = ModelUtil.getFictionModel(fictionWrap);
        fictionMap.put("fiction", fictionModel);

        Chapter chapter0 = libraryService.getChapterByNumber(id, -1);
        List<CommentWrap> commentWrapList = libraryService.getCommentWrapList(chapter0.getId());
        ArrayList<CommentModel> commentModelList = new ArrayList<>();
        commentWrapList.forEach(commentWrap -> {
            CommentModel commentModel = ModelUtil.getCommentModel(commentWrap);
            commentModelList.add(commentModel);
        });
        fictionMap.put("comments", commentModelList);

        return UnifiedResult.ok(fictionMap);
    }

    /**
     * 获取作品简要信息接口
     *
     * @param id 作品id
     * @return 统一响应结构，包含作品简略信息数据
     */
    @PostMapping("/brief/fiction")
    public UnifiedResult getFictionBrief(Integer id) {
        if (id == null) return UnifiedResult.build("400", "参数错误", null);
        FictionWrap fictionWrap = libraryService.getFictionWrap(id);
        FictionModel fictionModel = ModelUtil.getFictionModel(fictionWrap);
        return UnifiedResult.ok(fictionModel);
    }

    /**
     * 查看章节详情接口
     * 此接口返回章节详情和章节的评论列表；
     * 此接口为用户阅读章节时调用；
     * 此接口不需要进行权限校验。
     *
     * @param id 章节id
     * @return 统一响应结构，包含章节详情信息和评论列表
     */
    @PostMapping("/detail/chapter")
    public UnifiedResult getChapterDetail(Integer id) {
        if (id == null) return UnifiedResult.build("400", "参数错误", null);
        ChapterWrap chapterWrap = libraryService.getChapterWrap(id);
        if (chapterWrap == null) return UnifiedResult.build("400", "章节不存在", null);
        ChapterModel chapterModel = ModelUtil.getChapterModel(chapterWrap);
        List<CommentWrap> commentWrapList = libraryService.getCommentWrapList(id);
        List<CommentModel> commentModelList = new ArrayList<>();
        commentWrapList.forEach(commentWrap -> {
            CommentModel commentModel = ModelUtil.getCommentModel(commentWrap);
            commentModelList.add(commentModel);
        });

        Map<String, Object> map = new HashMap<>();
        map.put("chapter", chapterModel);
        map.put("comments", commentModelList);

        return UnifiedResult.ok(map);
    }

    /**
     * 根据章节号获取章节详情
     *
     * @param fictionId 作品id
     * @param number    章节号
     * @return 统一响应结构，包含章节详情信息和评论列表数据
     */
    @PostMapping("/detail/chapter/number")
    public UnifiedResult getChapterByNumber(Integer fictionId, Integer number) {
        if (fictionId == null || number == null) {
            return UnifiedResult.build("400", "参数错误", null);
        }
        ChapterWrap chapterWrap = libraryService.getChapterWrapByNumber(fictionId, number);
        ChapterModel chapterModel = ModelUtil.getChapterModel(chapterWrap);
        List<CommentWrap> commentWrapList = libraryService.getCommentWrapList(chapterWrap.getId());
        List<CommentModel> commentModelList = new ArrayList<>();
        commentWrapList.forEach(commentWrap -> {
            CommentModel commentModel = ModelUtil.getCommentModel(commentWrap);
            commentModelList.add(commentModel);
        });

        Map<String, Object> map = new HashMap<>();
        map.put("chapter", chapterModel);
        map.put("comments", commentModelList);
        return UnifiedResult.ok(map);
    }

    /**
     * 根据章节ID获取章节（包括content）接口
     * 此接口用户修改章节时获取章节内容，需要对调用此接口的用户ID进行校验。
     * 只有该章节的作者才能获取该章节。
     *
     * @param id 章节id
     * @return 统一响应结构，包含章节详情数据
     */
    @PostMapping("/brief/chapter")
    public UnifiedResult getChapter(HttpSession session, Integer id) {
        if (id == null) return UnifiedResult.build("400", "参数错误", null);
        User user = (User) session.getAttribute("user");
        Chapter chapter = libraryService.getChapter(id);
        if (!user.getId().equals(chapter.getUserId())) {
            return UnifiedResult.build("400", "参数错误", null);
        }
        ChapterModel chapterModel = ModelUtil.getChapterModel(chapter);
        return UnifiedResult.ok(chapterModel);
    }

    /**
     * 发表评论接口
     * 【评论】
     *
     * @param newComment 评论参数
     * @return 统一响应结构
     */
    @PostMapping("/new/comment")
    public UnifiedResult addComment(HttpSession session, Comment newComment) {
        if (newComment.getFictionId() == null) {
            return UnifiedResult.build("400", "参数错误", null);
        }
        User user = (User) session.getAttribute("user");
        Fiction fiction = libraryService.getFiction(newComment.getFictionId());
        //如果用户等级低于作品的限制等级，则返回错误信息
        if (user.getLevel() < fiction.getRestricted()) {
            return UnifiedResult.build("400", "参数错误", null);
        }
        if (newComment.getChapterId() == null) {
            Chapter chapter0 = libraryService.getChapterByNumber(newComment.getFictionId(), -1);
            newComment.setChapterId(chapter0.getId());
        } else {
            Chapter chapter = libraryService.getChapter(newComment.getChapterId());
            //如果章节id和作品id不匹配或者用户等级低于章节的限制等级，则返回错误信息
            if (!chapter.getFictionId().equals(newComment.getFictionId()) || user.getLevel() < chapter.getRestricted()) {
                return UnifiedResult.build("400", "参数错误", null);
            }
        }
        newComment.setUserId(user.getId());
        newComment.setCreateTime(new Date());
        int result = libraryService.addComment(newComment);
        switch (result) {
            case 0:
                return UnifiedResult.ok();
            case 1:
                return UnifiedResult.build("400", "章节不存在", null);
            default:
                return UnifiedResult.build("500", "未知错误", null);
        }
    }

    /**
     * 获取用户评论列表
     *
     * @param userId 用户id
     * @return 统一响应结构，包含用户评论列表
     */
    @PostMapping("/list/comment")
    public UnifiedResult getUserCommentList(HttpSession session, Integer userId) {
        if (userId == null) return UnifiedResult.build("400", "参数错误", null);

        User user = (User) session.getAttribute("user");
        Integer level;
        if (user == null) level = 0;
        else level = user.getLevel();

        List<CommentWrap> commentWrapList = libraryService.getCommentWrapListByUserId(userId, level);
        List<CommentModel> commentModelList = new ArrayList<>();
        commentWrapList.forEach(commentWrap -> {
            CommentModel commentModel = ModelUtil.getCommentModel(commentWrap);
            commentModelList.add(commentModel);
        });
        return UnifiedResult.ok(commentModelList);
    }

    /**
     * 修改章节接口
     *
     * @param session       请求的session
     * @param editedChapter 被更新过的章节数据
     * @return 统一响应结构，包含章节id数据
     */
    @PostMapping("/edit/chapter")
    public UnifiedResult editChapter(HttpSession session, Chapter editedChapter) {
        User user = (User) session.getAttribute("user");
        Chapter chapter = libraryService.getChapter(editedChapter.getId());
        if (!user.getId().equals(chapter.getUserId())) {
            return UnifiedResult.build("405", "参数错误", null);
        }
        Chapter updatedChapter = new Chapter();
        updatedChapter.setId(editedChapter.getId());
        updatedChapter.setTitle(editedChapter.getTitle());
        updatedChapter.setContent(editedChapter.getContent());
        updatedChapter.setRestricted(editedChapter.getRestricted());
        libraryService.updateChapter(updatedChapter);
        return UnifiedResult.ok(editedChapter.getId());
    }

    /**
     * 隐藏或显示章节接口
     * 此接口需要进行身份校验
     *
     * @param session 请求的session
     * @param id      章节id
     * @param hide    是否隐藏
     * @return 统一响应结构
     */
    @PostMapping("/hide/chapter")
    public UnifiedResult hideChapter(HttpSession session, Integer id, Boolean hide) {
        if (id == null || hide == null) return UnifiedResult.build("400", "参数错误", null);
        User user = (User) session.getAttribute("user");
        int result = libraryService.hideChapter(id, user.getId(), hide);
        switch (result) {
            case 0:
                return UnifiedResult.ok();
            case 1:
                return UnifiedResult.build("400", "章节不存在", null);
            case 2:
            case 3:
                return UnifiedResult.build("400", "参数错误", null);
            default:
                return UnifiedResult.build("400", "未知错误", null);
        }
    }

    /**
     * 删除章节接口
     * 此接口需要进行用户身份校验
     *
     * @param session 请求的session
     * @param id      章节id
     * @return 统一响应结构
     */
    @PostMapping("/delete/chapter")
    public UnifiedResult deleteChapter(HttpSession session, Integer id) {
        if (id == null) return UnifiedResult.build("400", "参数错误", null);
        User user = (User) session.getAttribute("user");
        int result = libraryService.deleteChapter(id, user.getId());
        switch (result) {
            case 0:
                return UnifiedResult.ok();
            case 1:
                return UnifiedResult.build("400", "章节不存在", null);
            case 2:
                return UnifiedResult.build("400", "参数错误", null);
            default:
                return UnifiedResult.build("400", "未知错误", null);
        }
    }

}
