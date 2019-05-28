package com.circleman.core

import com.circleman.meta.MetaDomain
import com.circleman.util.EnvironmentAwareConfig
import com.google.gson.Gson
import grails.gorm.annotation.Entity
import groovy.util.logging.Slf4j
import org.grails.orm.hibernate.HibernateDatastore
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Route
import spark.template.velocity.VelocityTemplateEngine

import java.util.regex.Pattern

import static spark.Spark.port
import static spark.Spark.staticFiles

@Slf4j(category = "BaseApp")
class BaseApp extends EnvironmentAwareConfig{
    //ORM相关
    static Map<String, MetaDomain> metaDomainMap =[:]
    static Map<String, Object> clazzMap = [:]
    static private Set<Class> entities = []
    static private HibernateDatastore hibernateDatastore

    //静态工具
    private static Gson gson = new Gson()

    private static VelocityTemplateEngine engine = new VelocityTemplateEngine()



//    def before(final Closure closure){
//        spark.Spark.before(new Filter(){
//            void handle(Request request, Response response){
//                closure.delegate = this
//                closure(request, response)
//            }
//        })
//    }
//
//    def after(final Closure closure){
//        spark.Spark.after(new Filter(){
//            void handle(Request request, Response response){
//                closure.delegate = this
//                closure(request, response)
//            }
//        })
//    }
//
//    private Route createClosureBasedRouteForPath(String path, Closure ... closures) {
//        new Route(path) {
//            def handle(Request request, Response response) {
//                closures*.delegate = this
//                return closures*.call(request, response).findAll { it }.join()
//            }
//        }
//    }
//
//    def get(String path, Closure ... closures) {
//        spark.Spark.get(createClosureBasedRouteForPath(path, closures))
//    }

    static def GET(String path, Closure closure) {
        spark.Spark.get(path, new Route(){
            def handle(Request request, Response response) {

                response.type("text/json")

                closure.delegate = this
                Closure c = closure.rcurry(convertToParams(request))
                return c(request, response)
            }
        })
    }

    static def PUT(String path, Closure closure) {
        spark.Spark.get(path, new Route(){
            def handle(Request request, Response response) {
                closure.delegate = this
                Closure c = closure.rcurry(convertToParams(request))
                return c(request, response)
            }
        })
    }

    static def POST(String path, Closure closure) {
        spark.Spark.get(path, new Route(){
            def handle(Request request, Response response) {
                closure.delegate = this
                Closure c = closure.rcurry(convertToParams(request))
                return c(request, response)
            }
        })
    }

    static def DELETE(String path, Closure closure) {
        spark.Spark.get(path, new Route(){
            def handle(Request request, Response response) {
                closure.delegate = this
                Closure c = closure.rcurry(convertToParams(request))
                return c(request, response)
            }
        })
    }

    static json(Object obj) {
        return gson.toJson(obj)
    }

    static synchronized String template(String template, Map model) {
        return engine.render(new ModelAndView(model, "/templates/${template}"))
    }

    static Map convertToParams(Request request){
        Map<String, String> params = [:]

        Closure remoteStringQuote={ String str->
            if(str != null && str[0] in ["\"", "'"] && str[0]==str[-1]){
                str = str[1..-2]
            }

            return str
        }

        params.putAll(request.params())

        request.queryParams().each{ String key ->
            try {
                if (key.contains("[")) {
                    println "key: ${key} request.queryMap(key).value()"
                    key = key - "]"
                    String[] strings = key.split("\\[")
                    String group = strings[0]
                    String attr = strings[1]

                    if (params[group] == null) params[group] = [:]

                    Map map = params[group]
                    map[attr] = remoteStringQuote(request.queryMap().get(group, attr).value())
                } else {
                    params[key] = remoteStringQuote(request.queryMap(key).value())
                }
            }catch(Exception e){
            }
        }

        Map<String, String> output = [:]
        //将start转换成: offset
        try {
            output["offset"] = Integer.parseInt(params["start"].toString())
        }catch(Exception e){
            output["offset"] = 0
        }

        //将count转换成: max
        try {
            output["max"] = Integer.parseInt(params["count"].toString())
            //params.remove("count")
        }catch(Exception e){
            output["max"] = 10
        }

        //:xx -> xx sort_xx -> value => sort -> xx order -> value
        params.each{String key, Object value ->
            if(key.startsWith("sort_")){
                output["sort"] = key - "sort_"
                output["order"] = value.toString()

                //params.remove(key)
            }else if(key.startsWith(":")){
                String k = key - ":"
                output[k] = params[key]
                //params.remove(key)
            }
        }

        //将String id 转换成long id
        if(params['id']){
            try {
                output['id']=Long.parseLong(params['id'])
            }catch(Exception e){
            }
        }

        return output
    }

    static void initDatastore(){
        try {
            Map hibernateConfig = [:]

            //使用内存存储
            if(env == env){
                hibernateConfig['hibernate.hbm2ddl.auto'] = getConfig("database.hbm2ddl")
                hibernateConfig['dataSource.url'] = getConfig("database.url")
                hibernateConfig['dataSource.dialect'] = "org.hibernate.dialect.H2Dialect"
                hibernateConfig['dataSource.driverClassName'] = "org.h2.Driver"
            }else{
                hibernateConfig['dataSource.dialect'] = "org.hibernate.dialect.MySQL5InnoDBDialect"
                hibernateConfig['dataSource.driverClassName'] = "com.mysql.jdbc.Driver"
                hibernateConfig['dataSource.username'] = getConfig("database.username")
                hibernateConfig['dataSource.password'] = getConfig("database.password")
            }

            Reflections reflections = new Reflections(getConfig("framework.codegen.domainsPackage"))
            entities =reflections.getTypesAnnotatedWith(Entity)
            entities.each{ Class clazz ->
                try {
                    hibernateDatastore = new HibernateDatastore(hibernateConfig, clazz)
                    clazzMap[clazz.simpleName]=clazz
                    log.info "注册: ${clazz.canonicalName} (${clazz.simpleName})"
                }catch(Exception e){

                    log.error "注册: ${clazz.simpleName} 失败:${e.message}"
                }
            }
        }catch(Exception e){
            log.error "initDatastore 失败:${e.message}"
        }
    }

    static void initDomainDefaultAction() {

//        clazzMap.each { String simpleName, Class clazz ->
//            try {
//                String name = simpleName.uncapitalize()
//
//                GET "${name}", { Request request, Response response, Map params ->
//
//
//                    Map output = [:]
//
//                    Organization.withTransaction {
//                        output['total_count'] = Organization.count
//                        output['pos'] = params['offset']
//                        output['data'] = Organization.findAll([max:params['max'], offset: params['offset']])
//                    }
//
//                    return json(output)
//                }
//
//                GET "${name}/:id", { Request request, Response response, Map params ->
//
//                    println "${name} ${params}"
//
//                    def instance
//
//                    Organization.withTransaction {
//                        instance = Organization.get(params['id'] as Long)
//                    }
//
//                    return json(instance)
//                }
//
//            } catch (Exception e) {
//                log.error "注册默认行为异常:${e.message}"
//            }
//        }
    }


    /**
     * 初始化配置、模型和ORM
     * @return
     */
    static boolean initialization(){


        initConfig()

        staticFiles.location("/assets")
        port(getConfig("framework.port"))

        //加载模型
        loadResources(~/.*Model\.groovy/, "define")

        //校验所有模型配置正常
        if(env != PRODUCTION){
            boolean result = true

            metaDomainMap.each{String name, MetaDomain model->
                result = model.validate()

                if(result == false){
                    log.error "${name}的MetaDomain存在异常配置"
                }else{
                    log.info "${name}的MetaDomain校验通过"
                }
            }
        }

        initDatastore()
        initDomainDefaultAction()
        loadResources(~/.*Action\.groovy/, "define")
        loadResources(~/DataInit\.groovy/, "init")
    }

    synchronized static Set<Class> getEntityClasses(String pkg){
        Reflections reflections = new Reflections(pkg)
        return reflections.getTypesAnnotatedWith(Entity)
    }

    synchronized static private Set<String> getResourceScript(Pattern pattern){
        Reflections reflections = new Reflections(new ResourcesScanner())

        return reflections.getResources(pattern)
    }

    /**
     * 扫描资源目录，找到特定类，并调用特定类的方法
     * @param pattern 匹配模式
     * @param initMethod 初始化方法(无参数)
     * @return
     */
    synchronized static boolean loadResources(Pattern pattern, String initMethod){
        boolean output = true

        try {
            GroovyScriptEngine engine = new GroovyScriptEngine(".")

            getResourceScript(pattern).each{String name ->
                try {
                    Class clazz = engine.loadScriptByName("./src/main/resources/${name}")
                    output = clazz.newInstance()."${initMethod}"()

                    log.debug("${name} 加载: ${output}")
                }catch(Exception e){
                    log.debug("${name} 加载失败: ${e.message}")
                }
            }

        }catch(Exception e){
            log.error("loadResource ${pattern.toString()} 加载异常: ${e.message}")
        }

        if(env == DEVELOPMENT && getConfig("frameowork.debug")==true) {
            metaDomainMap.each { String name, MetaDomain domain ->
                log.info ">>>> name: ${name}"
                log.info domain.toString()
            }
        }

        return output
    }
}
