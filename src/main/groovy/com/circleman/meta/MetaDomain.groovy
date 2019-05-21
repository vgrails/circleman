package com.circleman.meta

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

/**
 * 模型元数据
 */
@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(includeNames = true)
class MetaDomain{
    /** 名称 */
    String name
    /** 包名 */
    String pkg
    /** 翻译 */
    String locale
    /** 属性 */ //["name", "type", "locale", "flex", "widget", "nullable", "validator", "defaulted"]
    List<MetaField> fields = [new MetaField().type("long").name("id").locale("编号").flex(1)]
    /** 排序 */
    Set<String> sortable = []
    /** 搜索 */
    Set<String> searchable = []
    /** 关系 */
    Set<String> associations = []

    private Map<String, MetaField> fieldsMap=[:]

    /**
     * 获取属性
     * @param name
     * @return
     */
    synchronized MetaField getMetaField(String name){
        if(fieldsMap.size()==0){
            for(MetaField f in fields){
                fieldsMap[f.name]=f
            }
        }

        return fieldsMap[name]
    }
}