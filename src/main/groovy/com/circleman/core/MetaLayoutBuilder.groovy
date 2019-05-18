package com.circleman.core

import groovy.transform.ToString

import static com.circleman.core.BaseApp.getMetaDomainMap

/**
 * 布局元数据构建器
 */
@ToString(includeNames = true, ignoreNulls = true)
class MetaLayoutBuilder extends BuilderSupport{

    protected void setParent(Object parent, Object child) {
            if(parent instanceof MetaLayout){
                parent.subLayouts << child
            }
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        attributes['type']=name.toString()
        MetaLayout metaLayout = new MetaLayout(attributes)
        return metaLayout

    }

    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        return null
    }

    @Override
    protected Object createNode(Object name) {
        return null
    }

    @Override
    protected Object createNode(Object name, Object value) {
        return null
    }
}