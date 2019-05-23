package com.circleman.meta

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import groovy.util.logging.Slf4j

/**
 * 模型元数据
 */
@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(includeNames = true)
@Slf4j(category = "MetaDomain")
class MetaDomain{
    /** 名称 */
    String name
    /** 包名 */
    String pkg
    /** 翻译 */
    String locale
    /** 属性 */
    List<MetaField> fields = [new MetaField().type("long").name("id").locale("编号").flex(1)]
    /** 排序 */
    Set<String> sortable = []
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
    synchronized MetaField getMetaField(String name){
        if(fieldsMap.size()==0){
            for(MetaField f in fields){
                fieldsMap[f.name]=f
            }
        }

        return fieldsMap[name]
    }

    synchronized boolean validate(){
        boolean output = true

        if(name == null || name.size()==0){
            log.error "模型属性:name未设置"
            output = false
        }

        if(pkg == null || pkg.size()==0){
            log.error "模型${name}属性:pkg未设置"
            output = false
        }

        //默认使用属性名
        if(!locale) {
            log.warn "模型${name}属性:locale:未设置"
            locale = name
            output = false
        }

        //属性检查
        Set<String> fieldNames = fields*.name
        if(fieldNames.size() < fields.size()){
            log.error "模型属性名称出现重名"
            output = false
        }

        if(fieldNames.contains('id') == false){
            log.error "模型未包含id属性"
            output = false
        }

        for(MetaField f in fields){
            output = f.validate()
        }

        return output
    }

    synchronized String toDomain(){
        String attributesDefinition = ""
        String constraintsDefinition = ""
        String imports = ""

        for(MetaField f in fields){
            if(f.name == 'id') continue
            attributesDefinition +=f.toField() + "\n"
            constraintsDefinition +=f.toConstraint() + "\n"
        }

        String output="""
package ${pkg}
${imports}

/**
${locale}
*/
@Entity
class ${name} implements GormEntity<${name}>{
${attributesDefinition}
    static constraints = {
${constraintsDefinition}
    }
}
    """
    return output
    }
}