package com.circleman.meta

import com.circleman.core.BaseApp
import groovy.transform.ToString
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 模型元数据构建器
 */
@ToString(includeNames = true, ignoreNulls = true)
class MetaDomainBuilder extends BuilderSupport{
    //日志相关
    static Logger log = LoggerFactory.getLogger(MetaDomainBuilder)

    /**
     * 创建层次结构
     * @param parent 父
     * @param child 子
     */
    protected void setParent(Object parent, Object child) {
            if(parent instanceof MetaDomain && child instanceof MetaField){
                boolean duplicated = false

                for(MetaField f in parent.fields){
                    if(child.name == f.name){
                        duplicated = true
                        break
                    }
                }

                if(duplicated){
                    log.error "出现重名属性:${child.name}"
                }else {
                    parent.fields << child
                }
            }else{
                log.error "非法的层次结构 ${parent} ${child}"
            }
    }

    /**
     * 创建节点
     * @param name 创建类型
     * @param attributes 创建参数
     */
    protected Object createNode(Object name, Map attributes) {
        if(attributes == null || attributes.size() == 0 || attributes["name"]==null){
            log.error "非法的模型构建参数: ${attributes}"
        }else {
            switch (name.toString().toLowerCase()) {
                case "domain":
                    MetaDomain metaDomain = new MetaDomain(attributes)
                    BaseApp.metaDomainMap[attributes['name']] = metaDomain
                    return metaDomain

                case "field":
                    MetaField metaField = new MetaField(attributes)
                    return metaField

                default:
                    log.error "非法的模型构建参数: ${name}"
            }
        }

        return null
    }

    /* 无需实现 */
    protected Object createNode(Object name, Map attributes, Object value) {
        return null
    }

    /* 无需实现 */
    protected Object createNode(Object name) {
        return null
    }

    /* 无需实现 */
    protected Object createNode(Object name, Object value) {
        return null
    }
}