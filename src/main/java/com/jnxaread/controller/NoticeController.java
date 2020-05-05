package com.jnxaread.controller;

import com.jnxaread.bean.model.NoticeModel;
import com.jnxaread.bean.wrap.NoticeWrap;
import com.jnxaread.entity.UnifiedResult;
import com.jnxaread.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 未央
 * @create 2020-05-05 18:00
 */
@RestController
@RequestMapping("/forum")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @RequestMapping("/list/notice")
    public UnifiedResult getNoticeList() {
        List<NoticeWrap> noticeWrapList = noticeService.getNoticeWrapList();

        ArrayList<NoticeModel> noticeModelList = new ArrayList<>();
        noticeWrapList.forEach(noticeWrap -> {
            NoticeModel noticeModel = getNoticeModel(noticeWrap);
            noticeModelList.add(noticeModel);
        });

        return UnifiedResult.ok(noticeModelList);
    }

    @RequestMapping("/detail/notice")
    public UnifiedResult getNotice(Integer id) {
        if (id == null) {
            return UnifiedResult.build(400, "参数错误", null);
        }
        NoticeWrap noticeWrap = noticeService.getNoticeWrap(id);
        if (noticeWrap == null) {
            return UnifiedResult.build(400, "公告不存在", null);
        }

        NoticeModel noticeModel = getNoticeModel(noticeWrap);
        return UnifiedResult.ok(noticeModel);
    }

    /**
     * 将NoticeWrap封装到NoticeModel中
     *
     * @param noticeWrap
     * @return
     */
    public NoticeModel getNoticeModel(NoticeWrap noticeWrap) {
        NoticeModel noticeModel = new NoticeModel();
        noticeModel.setId(noticeWrap.getId());
        noticeModel.setLabel(noticeWrap.getLabel());
        noticeModel.setTitle(noticeWrap.getTitle());
        if (noticeWrap.getContent() != null) {
            noticeModel.setContent(noticeWrap.getContent());
        }
        noticeModel.setUsername(noticeWrap.getUsername());
        noticeModel.setCreateTime(noticeWrap.getCreateTime());
        noticeModel.setLastEdit(noticeWrap.getLastEdit());
        noticeModel.setLastTime(noticeWrap.getLastTime());
        noticeModel.setViewCount(noticeWrap.getViewCount());
        return noticeModel;
    }

}
