package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.CategoryObject;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.CategoryDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Category;
import com.rbkmoney.newway.poller.dominant.AbstractDominantHandler;
import org.springframework.stereotype.Component;

@Component
public class CategoryHandler extends AbstractDominantHandler<CategoryObject, Category, Integer> {

    private final CategoryDaoImpl categoryDao;

    public CategoryHandler(CategoryDaoImpl categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    protected DomainObjectDao<Category, Integer> getDomainObjectDao() {
        return categoryDao;
    }

    @Override
    protected CategoryObject getObject() {
        return getDomainObject().getCategory();
    }

    @Override
    protected Integer getObjectRefId() {
        return getObject().getRef().getId();
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetCategory();
    }

    @Override
    public Category convertToDatabaseObject(CategoryObject categoryObject, Long versionId, boolean current) {
        Category category = new Category();
        category.setVersionId(versionId);
        category.setCategoryRefId(getObjectRefId());
        com.rbkmoney.damsel.domain.Category data = categoryObject.getData();
        category.setName(data.getName());
        category.setDescription(data.getDescription());
        if (data.isSetType()) {
            category.setType(data.getType().name());
        }
        category.setCurrent(current);
        return category;
    }
}