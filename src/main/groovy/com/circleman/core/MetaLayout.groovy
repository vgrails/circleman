package com.circleman.core


import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

/**
 * 布局/组件元数据
 */
@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(includeNames = true)
class MetaLayout {
    /** 标识 */
    String id
    /** 类型 */
    String type
    /** 属性 */
    Map<String, Object> attributes = [:]
    /** 子布局 */
    List<MetaLayout> subLayouts = []

    private Map<String, MetaLayout> subLayoutMap =[:]
}