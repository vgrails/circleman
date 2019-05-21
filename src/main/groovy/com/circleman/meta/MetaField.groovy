package com.circleman.meta

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import static com.circleman.core.BaseApp.*
import java.util.regex.Pattern

/**
 * 属性元数据
 */

@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(includeNames = true, ignoreNulls = true)
class MetaField implements GroovyInterceptable{

    //日志相关
    static private Logger log = LoggerFactory.getLogger(MetaField)

    //类型匹配表
    static private Set<String> numbericSet = ["byte", "short", "int", "long", "float", "double", "Integer", "Float", "Double"]
    static private Set<String> booleanSet = ["boolean", "Boolean"]
    static private Set<String> characterSet = ['char', 'String']

    /** 名称 */
    String name
    /** 类型 */
    String type
    /** 翻译 */
    String locale
    /** 表格占比 */
    int flex=1

    //数据类型允许约束映射表
    final static private Set<String> commonConstraints=["name", "type", "locale", "flex", "widget", "nullable", "validator", "initial"]
    final static private Set<String> numberConstraints=["min", "max", "unique"]
    final static private Set<String> decimalConstraints=["decimalSize"]

    final static private Map<String, Set<String>> typeConstraintSets=[
        byte:       commonConstraints + numberConstraints,
        short:      commonConstraints + numberConstraints,
        int:        commonConstraints + numberConstraints,
        long:       commonConstraints + numberConstraints,
        Integer:    commonConstraints + numberConstraints,
        float:      commonConstraints + numberConstraints + decimalConstraints,
        Float:      commonConstraints + numberConstraints + decimalConstraints,
        double:     commonConstraints + numberConstraints + decimalConstraints,
        Double:     commonConstraints + numberConstraints + decimalConstraints,
        boolean:    commonConstraints + ["widget"],
        Boolean:    commonConstraints + ["widget"],
        char:       commonConstraints + ["min", "max", "inList", "unique"],
        String:     commonConstraints + ["min", "max", "unique", "blank", "email", "mobile", "mask", "inList", "matches"],
        Date:       commonConstraints + ["format", "min", "max", "unique", "inList"]
    ]

    final static private Set<String> domainOutputConstraints =["nullable", "unique"]

    static private Set<String> constraintSets=[]

    /** 控件 */
    String widget
    /** 格式 */
    String format

    /** 最小 */
    Object min
    /** 最大 */
    Object max
    /** 精确位数 */
    Integer decimalSize

    /** 空格 */
    Boolean blank
    /** 邮件 */
    Boolean email
    /** 手机 */
    Boolean mobile
    /** 遮罩（密码） */
    Boolean mask
    /** 可空 */
    Boolean nullable=false
    /** 值表 */
    List<Object> inList
    /** 正则 */
    Pattern matches
    /** 唯一 */
    Boolean unique
    /** 校验(是否提供值校验) */
    Boolean validator
    /** 默认(是否提供默认值生成) */
    Boolean initial

    boolean isNumberic(){
        if(type in numbericSet){
            return true
        }

        return false
    }

    boolean isBoolean(){
        if(type in booleanSet){
            return true
        }

        return false
    }

    boolean isCharacter(){
        if(type in characterSet){
            return true
        }

        return false
    }

    /**
     * 校验约束和值是否合法，自动将无效的属性置空
     * @return
     */
    synchronized boolean validate(){

        boolean output = true

        if(!constraintSets){
            typeConstraintSets.values().each{ Set<String> values->
                constraintSets.addAll(values)
            }

            log.info constraintSets.toString()
        }

        Set<String> notAllowedAttributes = []


        //------------------------------------------------------
        // 是否存在不合法约束 （不合法约束 = 全量约束 - 合法约束)
        notAllowedAttributes.addAll(constraintSets)
        notAllowedAttributes.removeAll(typeConstraintSets[type])

        notAllowedAttributes.each{String key->
            if(this[key] != null){
                log.error "元属性:${name}(类型:${type})不支持:${key}约束"
                this[key] = null

                output = false
            }
        }
        //------------------------------------------------------
        // 是否存在不合法参数值
        if(flex <= 0 || flex >= 8){
            flex = 1

            log.error "元属性:${name}.flex:${flex}不合法"

            output = false
        }

        //默认使用属性名
        if(!locale){
            log.error "元属性:${name}.locale:${locale}不合法"
            locale = name
            output = false
        }

        if(inList){
            inList.each{ Object value ->
                if(value.class.simpleName != type){
                    log.error "元属性:${name}.inList:${inList}不合法"
                    return
                }
            }

            if(output == false){
                inList = null
            }
        }

        if(min != null) {
            if((type == "String" && min.class.simpleName != "Integer") || (min.class.simpleName != type && type != "String")){
                log.error "元属性:${name}.min:${min}不合法"
                output = false
            }
        }

        if(max != null) {
            if((type == "String" && max.class.simpleName != "Integer") || (max.class.simpleName != type && type != "String")){
                log.error "元属性:${name}.max:${max}不合法"
                output = false
            }
        }

        return output
    }

    /**
     * 生成类属性定义文本
     * @return
     */
    String toField(){
        return "${type} ${name}"
    }

    /**
     * 生成类属性约束文本，尽可能减少需要数据库处理的约束
     * @return
     */
    String toConstraint(){
        String output = "${name} "

        domainOutputConstraints.eachWithIndex{String c, int index ->
            if(this[c]!= null) {
                output += "${c}:${this[c]}"
                output += ", "
            }
        }

        //移除末尾的", "
        output = output[0..-3]

        return output
    }
}

