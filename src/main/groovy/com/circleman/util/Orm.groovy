package com.circleman.util

import com.circleman.meta.MetaDomain
import com.circleman.meta.MetaField
import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import groovy.util.logging.Slf4j
import static com.circleman.Bootstrap.*
import static com.circleman.core.BaseApp.metaDomainMap
import static com.circleman.util.EnvironmentAwareConfig.PRODUCTION
import static com.circleman.util.EnvironmentAwareConfig.env


@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(includeNames = true)
@Slf4j(category = "Orm")
/**
 * 支持单一条件操作的ORM工具
 */
class Orm {

    /** 模型 */
    String domain
    /** 属性 create, update */
    Map<String, Object> attributes = [:]

    /** ID */
    Long id
    /** 分页 */
    Integer max
    /** 跳过记录 */
    Integer offset

    /** 排序字段 */
    String orderBy
    /** 排序方向 */
    String direction


    /** 过滤字段 */
    String filter
    /** 过滤操作符 */
    String op
    /** 过滤参数 */
    Object p1
    /** 过滤参数 */
    Object p2

    static Map<String, String> opMap=[
        lt:"<",
        le:"<=",
        eq:"=",
        ge:">=",
        gt:">",
        ne:"!=",
        like:"like",
        between:"between"
    ]

    static Map<String, Set<String>> allowedAttributes = [
        count: ["domain", "filter", "op", "p1", "p2"],
        query: ["domain", "filter", "op", "p1", "p2", "max", "offset", "orderBy", "direction"],
        create: ["domain", "attributes"],
        update: ["domain", "attributes", "id"],
        delete: ["domain", "id"],
        get:["domain", "id"]
    ]

    static Set<String> allAttributes = []

    /**
     * 参数校验
     * @return
     */
    synchronized boolean validate(String type){
        boolean output = true

        //生产环境不做校验
        if(env == PRODUCTION){
            return output
        }

        if(!type && allowedAttributes.keySet().contains(type)==false){
            log.error "非法的操作类型:${type}"
            output = false
        }

        if(!domain && metaDomainMap[domain]==null){
            log.error "非法的模型名称:${domain}"
            output = false
        }

        if(metaDomainMap[domain].validate() ==false){
            log.error "元模型异常:${domain} ${metaDomainMap[domain].toString()}"
            output = false
        }

        //获得全量属性清单
        if(allAttributes.size() == 0){
            for( Set<String> allow in allowedAttributes.values()){
                allAttributes.addAll(allow)
            }
        }

        //对不在许可列表的属性进行空检查
        Set<String> notAllowed = allAttributes - allowedAttributes[type]

        for(String attr in notAllowed){
            if(this[attr] != null && this[attr] != [:] && this[attr] != []){
                log.error "当前操作:${type}不支持对${attr}属性赋值:${this[attr]}"
                output = false
            }
        }

        //属性必须是domain的字段
        List<String> domainAttrs = metaDomainMap[domain].fields*.name
        for(String attr in attributes.keySet()){
            if(domainAttrs.contains(attr) == false){
                log.error "当前模型:${domain}不包含属性:${attr} ${domainAttrs.toString()}"
                output = false
            }
        }

        //排序字段是domain的字段
        if(orderBy && domainAttrs.contains(orderBy) == false){
            log.error "非法排序字段:${orderBy}"
            output = false
        }

        //排序方向检查
        if(direction && ["asc", "desc"].contains(direction)== false){
            log.error "排序方向值:${direction}异常"
            output = false
        }

        //过滤条件校验
        if(filter) {
            if (domainAttrs.contains(filter) == false) {
                log.error "非法过滤字段:${filter}"
                output = false
            }

            //过滤OP校验
            if(opMap.keySet().contains(op) == false && opMap.values().contains(op)== false){
                log.error "非法过滤OP:${op}"
                output = false
            }

            if(metaDomainMap[domain].getMetaField(filter).type == 'Boolean'){
                if (['ne', 'eq', '!=', "="].contains(op) == false) {
                    log.error "非法过滤OP:${op}，针对Boolean类型属性${filter}"
                    output = false
                }

                if([null, true, false].contains(p1) == false){
                    log.error "非法过滤值p1:${p1}，针对Boolean类型属性${filter}"
                    output = false
                }
            }


            if(p1 == null || (op == "between" && p2 == null)) {
                log.error "非法过滤参数:${p1} ${p2}"
                output = false
            }
        }

        return output
    }

    /**
     * 创建
     * @return
     */
    long create(){

        if(validate("create")== false){
            return -1
        }

        Class clazz = clazzMap[domain]

        clazz.withTransaction {
            def instance = clazz.newInstance(attributes)
            instance.save(flush:true)
            id = instance.id
        }

        return id
    }
    /**
     * 更新
     * @return
     */
    long update(){
        if(validate("update")== false){
            return -1
        }

        Class clazz = clazzMap[domain]

        clazz.withTransaction {
            def instance = clazz.get(id)

            attributes.each{String key, value->
                instance[key] = value
            }

            instance.save()
        }

        return id
    }

    boolean delete(){
        if(validate("delete")== false){
            return false
        }
        Class clazz = clazzMap[domain]

        String hql="delete from ${domain} where id = ${id}"

        if(env != PRODUCTION) {
            log.info("${hql};")
        }

        clazz.withTransaction {
            clazz.executeUpdate(hql)
        }

        if(env != PRODUCTION) {
            log.info("${hql}; successful")
        }

        return true
    }

    /**
     * 计数
     * @return
     */
    long count(){
        if(validate("count")== false){
            return
        }

        Class clazz = clazzMap[domain]
        MetaDomain metaDomain = metaDomainMap[domain]
        Map<String, Object> params = [:]

        String hql="select count(*) from ${domain}"

        if(filter) {
            hql = "${hql} where ${filter} ${opMap[op]?:op} "

            MetaField f = metaDomain.getMetaField(filter)

            if(f.numberic == true || f.type == 'Boolean'){
                hql = "${hql}${p1}"
            }else if(f.type in ["String", "char"]){

                String value = p1

                if(f.type == "String" && op == "like") {
                    value = "%${value}%"
                }

                hql = "${hql}\'${value}\'"
            }else if(f.type == "Date"){

                if(op != 'between') {
                    hql = "${hql}:${filter}"
                    params[filter] = p1
                }else{
                    hql = "${hql}:${filter}_left and :${filter}_right"
                    params["${filter}_left"] = p1
                    params["${filter}_right"] = p2
                }
            }else{
                log.error ">>>> 未处理的数据类型：${f.type}"
            }
        }

        List result = []

        clazz.withTransaction {
            result = clazz.executeQuery(hql, params)
        }

        return result[0]
    }

    /**
     * 查询支持分页
     * @return
     */
    List query(){
        if(validate("query")== false){
            return null
        }
        Class clazz = clazzMap[domain]
        MetaDomain metaDomain = metaDomainMap[domain]

        Map<String, Object> params = [:]

        String hql="from ${domain}"
        if(filter) {
            hql = "${hql} where ${filter} ${opMap[op]?:op} "

            MetaField f = metaDomain.getMetaField(filter)

            if(f.numberic == true || f.type == 'Boolean'){
                hql = "${hql}${p1}"
            }else if(f.type in ["String", "char"]){

                String value = p1

                if(f.type == "String" && op == "like") {
                    value = "%${value}%"
                }

                hql = "${hql}\'${value}\'"
            }else if(f.type == "Date"){

                if(op != 'between') {
                    hql = "${hql}:${filter}"
                    params[filter] = p1
                }else{
                    hql = "${hql}:${filter}_left and :${filter}_right"
                    params["${filter}_left"] = p1
                    params["${filter}_right"] = p2
                }
            }else{
                log.error ">>>> 未处理的数据类型：${f.type}"
            }
        }

        if(orderBy){
            hql = "${hql} order by ${orderBy}"

            if(direction){
                hql = "${hql} ${direction}"
            }
        }


        if(max == null) {
            params["max"] = Integer.MAX_VALUE
        }else{
            params["max"] = max
        }
        if(offset == null){
            params["offset"] = 0
        }else{
            params["offset"] = offset
        }




        List result = []

        if(env != PRODUCTION) {
            log.info("query: ${hql}; ${params}")
        }

        clazz.withTransaction {
            result = clazz.executeQuery(hql,params)
        }

        return result
    }

    /**
     * 获取单一对象
     * @return
     */
    Object get(){
        if(validate("get")== false){
            return
        }
        Class clazz = clazzMap[domain]
        MetaDomain metaDomain = metaDomainMap[domain]

        def instance = clazz.newInstance()

        clazz.withTransaction {

            def obj = clazz.get(id)
            for(MetaField f in metaDomain.fields){
                instance[f.name] = obj[f.name]
            }
        }

        return instance
    }
}