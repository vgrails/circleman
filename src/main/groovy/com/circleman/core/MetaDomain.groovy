package com.circleman.core


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
    /** 属性 */
    List<MetaField> fields = [new MetaField().metaType(MetaType.LONG).name("id")]
    /** 排序 */
    List<String> sortable = []
    /** 搜索 */
    List<String> searchable = []
    /** 关系 */
    Set<String> associations = []

    private Map<String, MetaField> fieldsMap=[:]

    /**
     * 获取属性
     * @param name
     * @return
     */
    synchronized MetaField GetMetaField(String name){
        if(fieldsMap.size()==0){
            for(MetaField f in fields){
                fieldsMap[f.name]=f
            }
        }

        return fieldsMap[name]
    }
}