package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.dominant.iface.CategoryDao;
import com.rbkmoney.newway.domain.tables.pojos.Category;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

public class CategoryHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private CategoryDao categoryDao;

    @Test
    public void test() {
        Category category = random(Category.class);
        category.setCurrent(true);
        categoryDao.save(category);
        categoryDao.updateNotCurrent(category.getCategoryId());
    }
}
