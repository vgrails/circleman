package com.circleman.core


import com.google.gson.Gson
import grails.gorm.annotation.Entity
import groovy.json.JsonOutput
import org.grails.orm.hibernate.HibernateDatastore
import org.reflections.Reflections
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Filter
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Route
import spark.template.velocity.VelocityTemplateEngine

import static spark.Spark.get

class BaseApp {
    //静态工具相关
    private static Gson gson = new Gson()
    static HibernateDatastore datastore

    private static VelocityTemplateEngine engine = new VelocityTemplateEngine()
    //配置相关
    private static Map config= null


    //环境相关
    private static String env = null
    private static String DEVELOP = "DEVELOP"
    private static String TEST = "TEST"
    private static String PRODUCT = "PRODUCT"

    //ORM相关
    static Map<String, MetaDomain> metaDomainMap =[:]
    private static Set entities = []
    static Map<String, Object> clazzMap = [:]

    //日志相关
    static Logger log = LoggerFactory.getLogger(BaseApp)

    def before(final Closure closure){
        spark.Spark.before(new Filter(){
            void handle(Request request, Response response){
                closure.delegate = this
                closure(request, response)
            }
        })
    }

    def after(final Closure closure){
        spark.Spark.after(new Filter(){
            void handle(Request request, Response response){
                closure.delegate = this
                closure(request, response)
            }
        })
    }

    private Route createClosureBasedRouteForPath(String path, Closure ... closures) {
        new Route(path) {
            def handle(Request request, Response response) {
                closures*.delegate = this
                return closures*.call(request, response).findAll { it }.join()
            }
        }
    }

    def get(String path, Closure ... closures) {
        spark.Spark.get(createClosureBasedRouteForPath(path, closures))
    }

    static def GET(String path, Closure closure) {


        spark.Spark.get(path, new Route(){
            def handle(Request request, Response response) {
                closure.delegate = this

                Closure c = closure.rcurry(convertToParams(request))

                return c(request, response)
            }
        })
    }

    def post(String path, Closure ... closures) {
        spark.Spark.post(createClosureBasedRouteForPath(path, closures))
    }

    def put(String path, Closure ... closures) {
        spark.Spark.put(createClosureBasedRouteForPath(path, closures))
    }

    def delete(String path, Closure ... closures) {
        spark.Spark.delete(createClosureBasedRouteForPath(path, closures))
    }

    def head(String path, Closure ... closures) {
        spark.Spark.head(createClosureBasedRouteForPath(path, closures))
    }

    def trace(String path, Closure ... closures) {
        spark.Spark.trace(createClosureBasedRouteForPath(path, closures))
    }

    def connect(String path, Closure ... closures) {
        spark.Spark.connect(createClosureBasedRouteForPath(path, closures))
    }

    def options(String path, Closure ... closures) {
        spark.Spark.options(createClosureBasedRouteForPath(path, closures))
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

        return params
    }

    static void initDatastore(){

        Map memdbConfig = [
            'hibernate.hbm2ddl.auto':'create-drop',
            'dataSource.url':'jdbc:h2:mem:myDB'
        ]

        Map mysqlConfig = [
            'hibernate.hbm2ddl.auto':'create-drop',
            'dataSource.dialect':'org.hibernate.dialect.MySQL5InnoDBDialect',
            'dataSource.url':'jdbc:mysql://localhost:3306/test?useSSL=false',
            'dataSource.driverClassName':'com.mysql.jdbc.Driver',
            'dataSource.username':'user',
            'dataSource.password':'123'
        ]

        try {

            Reflections reflections = new Reflections("com.circleman")

            entities = reflections.getTypesAnnotatedWith(Entity)

            entities.each{ Class clazz ->
                try {
                    datastore = new HibernateDatastore(memdbConfig, clazz)
                    clazzMap[clazz.simpleName]=clazz
                    log.info "Registered: ${clazz.canonicalName} (${clazz.simpleName})"
                }catch(Exception e){
                }
            }
        }catch(Exception e){
            println "initDatastore failed!"
        }
    }

    static void initDomainDefaultAction(){

        try {

            clazzMap.each{ String simpleName, Class clazz ->
                try {
                    log.info "Registered Action: ${simpleName}"

//                    try {
//                        def O = this.class.forName("com.circleman.domains.Organization")
//                        println O.count
//                        println O.findByName("研发")
//                    }catch(Exception e){
//                        println e.message
//                    }


                    get "${simpleName.uncapitalize()}/list", { Request request, Response response ->
                        clazz.withTransaction {

                            Map params = convertToParams(request)
                            println params

                            List output = [
                                [count: clazz.count, dollars: "密", color: "#ff0000"],
                                [count: new Random().nextInt(100) + 10, dollars: "码", color: "#00ff00"],
                                [count: new Random().nextInt(100) + 10, dollars: "青", color: "#0000ff"],
                                [count: new Random().nextInt(100) + 10, dollars: "清", color: "#ffff00"],
                                [count: new Random().nextInt(100) + 10, dollars: "菁", color: "#ffff00"]
                            ]

                            log.debug OrmService.list(simpleName, params)

//                        Map queryMap = [:]
//
//
//                        params.keySet().each { String key->
//                            if(key.startsWith("sort_")){
//                                queryMap["sort"] = key - "sort_"
//                                queryMap["order"] = params[key]
//                            }
//                        }
//
//                        List<Map<String, String>> datas = []
//
//                        long start = 0
//                        long count = 10
//
//                        try{
//                            start = Integer.parseInt(params.start)
//                            count = Integer.parseInt(params.count)
//                        }catch(Exception e){}
//
//                        queryMap["max"]=count
//                        queryMap["offset"]=start
//
//
//                        List<Region> regions=Region.findAll(queryMap)
//
//                        Map output = [
//                            data: regions,
//                            pos: start,
//                            total_count: Region.count
//                        ]
//
//                        String out = (output as JSON)
//
//                        println "list: ${out}"
//                        render out


                            json(output)
                        }
                    }

                    log.debug  "==> generate ${simpleName}/chart"
                }catch(Exception e){
                    println e.message
                }
            }
        }catch(Exception e){
            println "initDomainDefaultAction failed!${e.message}"
        }
    }

    static Object getConfig(String key){
        return config.get(key)
    }

    //TODO: 重复代码和LoadConfig
    static synchronized String getEnv(){

        if(env == null){
            env = new ConfigSlurper().parse(new File("./src/main/resources/config/config.groovy").toURI().toURL()).get("environment") ?: DEVELOP
        }

        return env
    }

    static void loadConfig(){

        if(config == null) {
            try {
                println "loadConfig()"
                config = new ConfigSlurper(getEnv()).parse(new File("./src/main/resources/config/config.groovy").toURI().toURL()).flatten()
                println config

                log.debug("Config:\n ${JsonOutput.prettyPrint(gson.toJson(config))}")

                println JsonOutput.prettyPrint(gson.toJson(config))
            } catch (Exception e) {
                log.error("loadConfig error: ${e.message}")
            }
        }
    }


    static List<Object> query(OrmQuery query){
        List<Object> results=[]
        Class clazz = clazzMap[query.domain]

        clazz.withTransaction {
            results = clazz.executeQuery(query.toHql(),[max: query.max, offset: query.offset])
            return results
        }
    }

    static long count(OrmQuery query){
        def results
        Class clazz = clazzMap[query.domain]

        clazz.withTransaction {
            String hql = query.toCountHql()
            results = clazz.executeQuery(hql)
            return results[0]
        }
    }

    static boolean delete(OrmDelete delete){
        Class clazz = clazzMap[delete.domain]

        clazz.withTransaction {
            String hql = delete.toHql()
            clazz.executeUpdate(hql)
            return true
        }
    }

    static long create(OrmCreate create){
        long id
        Class clazz = clazzMap[create.domain]

        clazz.withTransaction {
            def instance = clazz.newInstance(create.attributes)
            instance.save(flush:true)

            id = instance.id
        }
        return id
    }

    static long update(OrmUpdate update){
        long id
        Class clazz = clazzMap[update.domain]

        clazz.withTransaction {
            id = clazz.executeUpdate(update.toHql(), update.attributes)
        }
        return id
    }

    static String delete(String domain, Map<String, Object> params){
        log.info domain

        return "Hello OrmService"
    }

    static String list(String domain, OrmQuery params=null){
        log.info domain

        return "Hello OrmService"
    }

    static String tree(String domain, Map<String, Object> params){
        log.info domain

        return "Hello OrmService"
    }
}
