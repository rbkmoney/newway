package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.DomainObject;
import com.rbkmoney.newway.dao.dominant.iface.CategoryDao;
import com.rbkmoney.newway.domain.tables.pojos.Category;
import com.rbkmoney.newway.poller.dominant.AbstractDominantHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(propagation = Propagation.REQUIRED)
    protected void insertDomainObject(DomainObject domainObject, Long versionId) {
        saveNewCategory(domainObject, versionId, true);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    protected void updateDomainObject(DomainObject domainObject, Long versionId) {
        categoryDao.updateNotCurrent(domainObject.getCategory().getRef().getId());
        saveNewCategory(domainObject, versionId, true);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    protected void removeDomainObject(DomainObject domainObject, Long versionId) {
        categoryDao.updateNotCurrent(domainObject.getCategory().getRef().getId());
        saveNewCategory(domainObject, versionId, false);
    }

    private void saveNewCategory(DomainObject domainObject, Long versionId, boolean current) {
        Category category = new Category();
        category.setVersionId(versionId);
        category.setCategoryId(domainObject.getCategory().getRef().getId());
        com.rbkmoney.damsel.domain.Category data = domainObject.getCategory().getData();
        category.setName(data.getName());
        category.setDescription(data.getDescription());
        if (data.isSetType()) {
            category.setType(data.getType().name());
        }
        category.setCurrent(current);
        categoryDao.save(category);
    }
}
