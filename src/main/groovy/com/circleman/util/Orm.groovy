package com.circleman.util

import com.circleman.core.BaseApp
import com.circleman.meta.MetaDomain
import com.circleman.meta.MetaField
import grails.gorm.transactions.Transactional
import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import groovy.util.logging.Slf4j
import static com.circleman.Bootstrap.*
import com.circleman.domains.TestNumberic


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
    String orderField
    /** 排序方向 */
    String orderDirection


    /** 过滤字段 */
    String filterField
    /** 过滤操作符 */
    String filterOp
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
        count: ["domain", "filterField", "filterOp", "p1", "p2"],
        query: ["domain", "filterField", "filterOp", "p1", "p2", "max", "offset", "orderField", "orderDirection"],
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

        if(!type && allowedAttributes.keySet().contains(type)==false){
            log.error "非法的操作类型:${type}"
            output = false
        }

        if(!domain && metaDomainMap[domain]==null){
            log.error "非法的模型名称:${domain}"
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
        if(orderField && domainAttrs.contains(orderField) == false){
            log.error "非法排序字段:${orderField}"
            output = false
        }

        //排序方向检查
        if(orderDirection && ["asc", "desc"].contains(orderDirection)== false){
            log.error "排序方向值:${orderDirection}异常"
            output = false
        }

        //过滤条件校验
        if(filterField) {
            if (domainAttrs.contains(filterField) == false) {
                log.error "非法过滤字段:${filterField}"
                output = false
            }

            //过滤OP校验
            if(opMap.keySet().contains(filterOp) == false && opMap.values().contains(filterOp)== false){
                log.error "非法过滤OP:${filterOp}"
                output = false
            }


            if(p1 == null || (filterOp == "between" && p2 == null)) {
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

    void delete(){
        if(validate("delete")== false){
            return
        }
        Class clazz = clazzMap[domain]

        String hql="delete from ${domain} where"

        hql = "${hql} where id = ${id}"

        clazz.withTransaction {
            clazz.executeUpdate(hql)
        }
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

        String hql="select count(*) from ${domain}"

        if(filterField){
            hql = "${hql} where ${filterField} ${opMap[filterOp]?:filterOp} "

            MetaField f = metaDomain.getMetaField(filterField)

            if(f.numberic == true || f.type in ['Boolean', "boolean"]){
                hql = "${hql} ${p1}"
            }else if(f.type in ["String", "char"]){

                String value = p1

                if(f.type == "String" && filterOp == "like") {
                    value = "%${value}%"
                }

                hql = "${hql} \"${value}\""
            }
        }

        List result = []

        clazz.withTransaction {
            result = clazz.executeQuery(hql)
        }

        return result[0]
    }

    /**
     * 查询支持分页
     * @return
     */
    List query(){
        if(validate("query")== false){
            return
        }
        Class clazz = clazzMap[domain]
        MetaDomain metaDomain = metaDomainMap[domain]

        String hql="from ${domain}"
        if(filterField) {
            hql = "${hql} where ${filterField} ${opMap[filterOp]?:filterOp} "

            MetaField f = metaDomain.getMetaField(filterField)
            if(f.numberic == true || f.type in ['Boolean', "boolean"]){
                hql = "${hql} ${p1}"
            }else if(f.type in ["String", "char"]){

                String value = p1

                if(f.type == "String" && filterOp == "like") {
                    value = "%${value}%"
                }

                hql = "${hql} \"${value}\""
            }
        }

        if(orderField){
            hql = "${hql} order by ${orderField}"

            if(orderDirection){
                hql = "${hql} ${orderDirection}"
            }
        }

        println ">>>>>>>>>>>>>>>"
        println hql

        List result = []

        clazz.withTransaction {
            result = clazz.executeQuery(hql,[max: max, offset: offset])
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
        clazz.withTransaction {
            return clazz.get(id)
        }
    }
}