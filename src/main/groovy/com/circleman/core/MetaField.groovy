package com.circleman.core

import grails.gorm.validation.Constraint
import grails.gorm.validation.DefaultConstrainedProperty
import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import java.util.regex.Pattern

/**
 * 属性元数据
 */

@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(includeNames = true, ignoreNulls = true)
class MetaField{
    /** 名称 */
    String name
    /** 类型 */
    MetaType metaType
    /** 翻译 */
    String locale
    /** 表格占比 */
    int flex=1

    /** 控件 */
    String widget
    /** 格式 */
    String format

    /** 最小 */
    Object min
    /** 最大 */
    Object max
    /** 空格 */
    boolean blank
    /** 邮件 */
    boolean email
    /** 手机 */
    boolean mobile
    /** 遮罩（密码） */
    boolean mask
    /** 可空 */
    boolean nullable
    /** 值表 */
    List<String> inList
    /** 正则 */
    Pattern matches
    /** 唯一 */
    boolean unique
    /** 校验 */
    Closure validator
}