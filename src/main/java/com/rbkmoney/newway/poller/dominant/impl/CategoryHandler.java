package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.DomainObject;
import com.rbkmoney.newway.dao.dominant.iface.CategoryDao;
import com.rbkmoney.newway.domain.tables.pojos.Category;
import com.rbkmoney.newway.poller.dominant.AbstractDominantHandler;
import org.springframework.stereotype.Component;

@Component
public class CategoryHandler extends AbstractDominantHandler {

    private final CategoryDao categoryDao;

    public CategoryHandler(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    protected boolean acceptDomainObject(DomainObject domainObject) {
        return domainObject.isSetCategory();
    }

    @Override
    protected void insertDomainObject(DomainObject domainObject, Long versionId) {
        Category category = new Category();
        category.setVersionId(versionId);
        category.setCategoryId(domainObject.getCategory().getRef().getId());
        categoryDao.save(category);
    }

    @Override
    protected void updateDomainObject(DomainObject domainObject, Long versionId) {
        int categoryId = domainObject.getCategory().getRef().getId();
        Category category = categoryDao.get(categoryId);
        category.setVersionId(versionId);
        categoryDao.updateNotCurrent(categoryId);
        categoryDao.save(category);
    }

    @Override
    protected void removeDomainObject(DomainObject domainObject, Long versionId) {
        int categoryId = domainObject.getCategory().getRef().getId();
        Category category = categoryDao.get(categoryId);
        category.setVersionId(versionId);
        category.setCurrent(false);
        categoryDao.updateNotCurrent(categoryId);
        categoryDao.save(category);
    }
}
