package com.circleman.util

import com.circleman.core.BaseApp
import com.circleman.meta.MetaDomain
import com.circleman.meta.MetaField
import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import groovy.util.logging.Slf4j
import static com.circleman.Bootstrap.*

@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(includeNames = true)
@Slf4j(category = "Orm")
/**
 * 支持单一条件操作的ORM工具
 */
class Orm {

    /** 操作类型 */
    String type
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
        count: ["type","domain", "expr"],
        query: ["type","domain", "expr", "max", "offset", "orderField", "orderDirection"],
        create: ["type","domain", "attributes"],
        update: ["type","domain", "attributes", "id"],
        delete: ["type","domain", "id"],
        get:["type", "domain", "id"]
    ]

    static Set<String> allAttributes = []

    /**
     * 参数校验
     * @return
     */
    synchronized boolean validate(){
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
            if(this[attr] != null){
                log.error "当前操作不支持对${attr}属性赋值:${this[attr]}"
                output = false
            }
        }

        //属性必须是domain的字段
        List<String> domainAttrs = metaDomainMap[domain].fields*.name
        for(String attr in attributes){
            if(domainAttrs.contains(attr) == false){
                log.error "当前模型不包含属性:${attr}"
                output = false
            }
        }

        //排序字段是domain的字段
        if(domainAttrs.contains(orderField) == false){
            log.error "非法排序字段:${orderField}"
            output = false
        }

        //排序方向检查
        if(orderDirection != null && ["asc", "desc"].contains(orderDirection)== false){
            log.error "排序方向值:${orderDirection}异常"
            output = false
        }

        //过滤条件校验
        if(filterField) {
            if (domainAttrs.contains(filterField) == false) {
                log.error "非法过滤字段:${filterField}"
                output = false
            }
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

        return output
    }

    /**
     * 创建
     * @return
     */
    long create(){
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
        Class clazz = clazzMap[domain]

        String hql="update from ${domain} "

        if(attributes) {
            hql = "${hql} set "

            attributes.eachWithIndex { String field, Object value, int index ->
                hql = "${hql} ${field} = :${field}"

                if (index < attributes.size() - 1) hql = "${hql},"
            }
        }

        if(id != null) {
            hql = "${hql} where id = ${id}"
        }else{
            log.error "更新操作非法ID"
            return -1
        }
        clazz.withTransaction {
            id = clazz.executeUpdate(hql, attributes)
        }

        return id
    }

    void delete(){
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

        Class clazz = clazzMap[domain]
        MetaDomain metaDomain = metaDomainMap[domain]

        String hql="select count(*) from ${domain}"

        if(filterField){
            hql = "${hql} where ${expr.field} ${Expr.opMap[expr.op]} "

            MetaField f = metaDomain.getMetaField(expr.field)

            if(f.numberic == true || f.type in ['Boolean', "boolean"]){
                hql = "${hql} ${expr.param1}"
            }else if(f.type in ["String", "char"]){

                String value = expr.param1

                if(f.type == "String" && expr.op == "like") {
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
        Class clazz = clazzMap[domain]
        MetaDomain metaDomain = metaDomainMap[domain]

        String hql="from ${domain}"
        if(filterField) {
            hql = "${hql} where ${expr.field} ${Expr.opMap[expr.op]} "

            MetaField f = metaDomain.getMetaField(expr.field)
            if(f.numberic == true || f.type in ['Boolean', "boolean"]){
                hql = "${hql} ${expr.param1}"
            }else if(f.type in ["String", "char"]){

                String value = expr.param1

                if(f.type == "String" && expr.op == "like") {
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

        Class clazz = clazzMap[domain]

        def result

        clazz.withTransaction {
            result = clazz.get(id)
        }

        return result
    }
}