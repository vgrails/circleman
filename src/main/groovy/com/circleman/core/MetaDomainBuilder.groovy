package com.circleman.core


import groovy.transform.ToString
import static com.circleman.core.BaseApp.*

/**
 * 模型元数据构建器
 */
@ToString(includeNames = true, ignoreNulls = true)
class MetaDomainBuilder extends BuilderSupport{

    protected void setParent(Object parent, Object child) {
            if(parent instanceof MetaDomain){
                parent.fields << child
            }
    }


    @Override
    protected Object createNode(Object name, Map attributes) {
        switch (name.toString().toLowerCase()){
            case "domain":
                MetaDomain metaDomain= new MetaDomain(attributes)
                metaDomainMap[attributes['name']]=metaDomain
                return metaDomain
                break
            case "field":
                MetaField metaField = new MetaField(attributes)

                return metaField

                break
            default:
                println "非法的模型构建参数: ${name}"
        }

        return null
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