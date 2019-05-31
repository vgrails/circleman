package com.circleman.util

import groovy.util.logging.Slf4j
import spark.Request
import spark.Response
import spark.Route
import spark.Filter

@Slf4j(category = "RouteUtil")
class RouteUtil {
    /** 保存已注册的Url映射 */
    static Set<String> registeredUrls = []

    /**
     * 移除字符类型包裹的 ""或 ''
     */
    static private String remoteStringQuote( String str){
        if(str != null && str[0] in ["\"", "'"] && str[0]==str[-1]){
            str = str[1..-2]
        }

        return str
    }

    /**
     * 将request转换成params
     */
    static private Map convertToParams(Request request){
        Map<String, String> params = [:]

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

    /**
     * 注册GET
     */
    synchronized static def GET(String path, Closure closure) {

        route("get", path, closure)
    }

    /**
     * 注册PUT
     */
    synchronized static def PUT(String path, Closure closure) {

        route("put", path, closure)
    }

    /**
     * 注册POST
     */
    synchronized static def POST(String path, Closure closure) {

        route("post", path, closure)
    }

    /**
     * 注册DELETE
     */
    synchronized static def DELETE(String path, Closure closure) {

        route("delete", path, closure)
    }

    /**
     * 注册BEFORE
     */
    synchronized static def BEFORE(Closure closure) {

        route("before", null, closure)
    }

    /**
     * 注册AFTER
     */
    synchronized static def AFTER(Closure closure) {

        route("after", null, closure)
    }

    /**
     * 路由处理
     */
    synchronized static def route(String verb, String path, Closure closure) {
        String key = "${verb}:${path}"

        if(registeredUrls.contains(key)==false) {
            registeredUrls.add(key)

            if(path != null) {
                spark.Spark."${verb}"(path, new Route() {
                    def handle(Request request, Response response) {

                        response.type("text/json")

                        closure.delegate = this
                        Closure c = closure.rcurry(convertToParams(request))
                        return c(request, response)
                    }
                })
            }else if(verb == "before" || verb == "after") {
                spark.Spark."${verb}"(new Filter(){
                    void handle(Request request, Response response){
                        closure.delegate = this
                        closure(request, response)
                    }
                })
            }
        }else{
            log.error "请勿重复添加路由:${key}"
        }
    }
}
