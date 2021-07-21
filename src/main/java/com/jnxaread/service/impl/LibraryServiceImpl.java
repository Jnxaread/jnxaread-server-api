package com.jnxaread.service.impl;

import com.jnxaread.bean.Category;
import com.jnxaread.service.LibraryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 未央
 * @create 2020-05-06 15:10
 */
@Service
public class LibraryServiceImpl extends BaseLibraryServiceImpl implements LibraryService {

    /**
     * 获取作品类别ID列表
     *
     * @return 作品类别ID列表
     */
    @Override
    public List<Integer> getCategoryIdList() {
        List<Category> categoryList = super.getCategoryList();
        ArrayList<Integer> categoryIdList = new ArrayList<>();
        categoryList.forEach(category -> categoryIdList.add(category.getId()));
        return categoryIdList;
    }
}
