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
    static private String removeStringQuote(String str){
        if(str != null && str[0] in ["\"", "'"] && str[0]==str[-1]){
            str = str[1..-2]
        }

        return str
    }

    /**
     * 将request转换成params
     */
    static private Map convertRequestToParams(Request request){
        Map<String, String> params = [:]

        request.params().each{
            println "${it.key} ${it.value}"
        }


        request.queryParams().each{ String key->
            params[key] = removeStringQuote(request.queryParamsValues(key)[0])
        }

        Map<String, String> output = [:]

        //:xx -> xx sort_xx -> value => sort -> xx order -> value
        params.each{String key, Object value ->

            if(key == "start"){
                try {
                    output["offset"] = Integer.parseInt(params["start"].toString())
                }catch(Exception e){
                    output["offset"] = 0
                }
            }else if(key == "count"){
                try {
                    output["max"] = Integer.parseInt(params["count"].toString())
                }catch(Exception e){
                    output["max"] = 60
                }
            }else if(key == 'id'){
                try {
                    output['id']=Long.parseLong(params['id'])
                }catch(Exception e){
                }
            }else if(key.startsWith("sort_")){
                output["sort"] = key - "sort_"
                output["order"] = value.toString()

                //params.remove(key)
            }else if(key.startsWith(":")){
                String k = key - ":"
                output[k] = params[key]
                //params.remove(key)
            }else{
                output[key] = params[key]
            }
        }

        //设置默认的OFFSET和分页大小
        if(output['offset'] == null){
            output["offset"] = 0
        }

        if(output['max'] == null){
            output["max"] = 60
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
                        Closure c = closure.rcurry(convertRequestToParams(request))
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
