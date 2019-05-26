package com.circleman.meta

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import groovy.util.logging.Slf4j
import org.hibernate.boot.model.relational.SimpleAuxiliaryDatabaseObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat

import static com.circleman.core.BaseApp.*
import java.util.regex.Pattern

/**
 * 属性元数据
 */

@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(includeNames = true, ignoreNulls = true)
@Slf4j(category = "MetaField")
class MetaField implements GroovyInterceptable{
    //类型匹配表
    static private Set<String> numbericSet = ["byte", "short", "int", "long", "float", "double", "Integer", "Long", "Float", "Double"]

    //数据类型允许约束映射表
    final static private Set<String> commons =["name", "type", "locale", "flex", "widget"]
    final static private Set<String> numbers =["min", "max", "unique"]
    final static private Set<String> decimals =["decimalSize"]

    final static private Map<String, Set<String>> typeConstraintSets=[
        byte:       commons + numbers,
        short:      commons + numbers,
        int:        commons + numbers,
        Integer:    commons + numbers + "nullable",
        long:       commons + numbers,
        Long:       commons + numbers + "nullable",
        float:      commons + numbers + decimals,
        Float:      commons + numbers + "nullable" + decimals,
        double:     commons + numbers + decimals,
        Double:     commons + numbers  + "nullable" + decimals,
        Boolean:    commons + "nullable",
        char:       commons + ["min", "max", "inList", "unique"],
        String:     commons + ["min", "max", "unique", "email", "mobile", "mask", "inList", "matches", "nullable"],
        Date:       commons + ["format", "min", "max", "unique", "nullable"]
    ]

    final static private Set<String> defaultOutputConstraints =["nullable", "unique"]

    static private Set<String> constraintSets=[]

    /** 名称 */
    String name
    /** 类型 */
    String type
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
    Boolean nullable
    /** 值表 */
    List<Object> inList
    /** 正则 */
    Pattern matches
    /** 唯一 */
    Boolean unique

    boolean isNumberic(){
        if(numbericSet.contains(type)){
            return true
        }

        return false
    }

    /**
     * 校验约束和值是否合法，自动将无效的属性置空，并自动给与默认值
     * @return
     */
    synchronized boolean validate(){

        boolean output = true

        if(!constraintSets){
            typeConstraintSets.values().each{ Set<String> values->
                constraintSets.addAll(values)
            }
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
            log.error "元属性:${name}.flex:${flex}不合法"
            output = false
            flex = 1
        }

        //默认使用属性名
        if(!locale){
            log.warn "元属性:${name}.locale:${locale}未设置"
            locale = name
            output = false
        }

        if(inList){
            for(Object attr in inList) {
                if(type == "int" && this[attr].class.simpleName == "Integer") continue
                if(type == "long" && this[attr].class.simpleName == "Long") continue
                if(type == "float" && this[attr].class.simpleName == "Float") continue
                if(type == "double" && this[attr].class.simpleName == "Double") continue

                if (attr.class.simpleName != type) {
                    log.error "元属性:${name}.inList:${inList}不合法"
                    output = false
                }
            }
        }

        if((type !='Boolean')) {
            for (String attr in ["min", "max"]) {
                if (this[attr] == null) continue

                if(type == "int" && this[attr].class.simpleName == "Integer") continue
                if(type == "long" && this[attr].class.simpleName == "Long") continue
                if(type == "float" && this[attr].class.simpleName == "Float") continue
                if(type == "double" && this[attr].class.simpleName == "Double") continue
                if(type == "char" && this[attr].class.simpleName == "Character") continue

                if ((type == "String" && this[attr].class.simpleName != "Integer") || (this[attr].class.simpleName != type && type != "String")) {
                    log.error "元属性:${name}.${attr}:期待${type}, 实际：${this[attr]} 类型不合法"
                    output = false
                }
            }
        }

        if(min!=null && max != null && min > max){
            log.error "元属性:${name} min > max值不合法"
            output = false
        }

        //日期格式校验
        if(type == "Date" && format != null){
            try {
                new SimpleDateFormat(format)
            }catch(Exception e){
                log.error "元属性:${name}.format:${format}值不合法"
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
        if(type || name){
            return "    ${type} ${name}"
        }else{
            log.error "元属性:type, name未设定"
            return null
        }
    }

    /**
     * 生成类属性约束文本，尽可能减少需要数据库处理的约束
     * @return
     */
    String toConstraint(){
        if(!name){
            log.error "元属性:name未设定"
            return null
        }

        String output = "       ${name} "
        List<String> outputConstraints = []

        defaultOutputConstraints.each{ String c->
            if(this[c]!=null){
                outputConstraints.add(c)
            }
        }

        outputConstraints.eachWithIndex{ String c, int index ->
            if(this[c]!= null) {
                output += "${c}:${this[c]}"

                if(index < (outputConstraints.size()-1)) {
                    output += ", "
                }
            }
        }

        return output
    }
}

