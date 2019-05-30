package com.circleman.util

import spark.Request
import spark.Response
import spark.Route

@Singleton
class RouteUtil {

    static private Map<String, Boolean> registeredUrlMap = [:]



    static private Map convertToParams(Request request){
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

    static def GET(String path, Closure closure) {



        registeredUrlMap[path]=true

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

        spark.Spark.put(path, new Route(){
            def handle(Request request, Response response) {
                closure.delegate = this
                Closure c = closure.rcurry(convertToParams(request))
                return c(request, response)
            }
        })
    }

    static def POST(String path, Closure closure) {
        spark.Spark.POST(path, new Route(){
            def handle(Request request, Response response) {
                closure.delegate = this
                Closure c = closure.rcurry(convertToParams(request))
                return c(request, response)
            }
        })
    }

    static def DELETE(String path, Closure closure) {
        spark.Spark.delete(path, new Route(){
            def handle(Request request, Response response) {
                closure.delegate = this
                Closure c = closure.rcurry(convertToParams(request))
                return c(request, response)
            }
        })
    }

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

}
