package org.talend.dataquality.semantic.datamasking.model;

import org.talend.dataquality.semantic.model.CategoryType;

public class CategoryValues {

    private String categoryId;

    private CategoryType type;

    private Object value;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
