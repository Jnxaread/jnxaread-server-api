package com.jnxaread.controller;

import com.jnxaread.bean.wrap.NoticeWrap;
import com.jnxaread.entity.UnifiedResult;
import com.jnxaread.model.NoticeModel;
import com.jnxaread.service.NoticeService;
import com.jnxaread.util.ModelUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 未央
 * @create 2020-05-05 18:00
 */
@RestController
@RequestMapping("/forum")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    /**
     * 获取公告列表接口
     *
     * @return 公告列表
     */
    @RequestMapping("/list/notice")
    public UnifiedResult getNoticeList() {
        List<NoticeWrap> noticeWrapList = noticeService.getNoticeWrapList();

        ArrayList<NoticeModel> noticeModelList = new ArrayList<>();
        noticeWrapList.forEach(noticeWrap -> {
            NoticeModel noticeModel = ModelUtil.getNoticeModel(noticeWrap);
            noticeModelList.add(noticeModel);
        });

        return UnifiedResult.ok(noticeModelList);
    }

    /**
     * 获取公告详情接口
     *
     * @param id 公告id
     * @return 公告详细内容
     */
    @RequestMapping("/detail/notice")
    public UnifiedResult getNotice(Integer id) {
        if (id == null) {
            return UnifiedResult.build(400, "参数错误", null);
        }
        NoticeWrap noticeWrap = noticeService.getNoticeWrap(id);
        if (noticeWrap == null) {
            return UnifiedResult.build(400, "公告不存在", null);
        }

        NoticeModel noticeModel = ModelUtil.getNoticeModel(noticeWrap);
        return UnifiedResult.ok(noticeModel);
    }

}
