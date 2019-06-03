package com.circleman.util

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import groovy.util.logging.Slf4j
import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder
import spark.Filter
import spark.Request
import spark.Response
import spark.Route

import static com.circleman.Bootstrap.*

@Builder(builderStrategy = SimpleStrategy,prefix = "")
@Slf4j(category = "RestClient")
class RestClient {
    /** 服务端地址 */
    String server
    /** API路径 */
    String path
    /** 参数 */
    Map params


    String getServer(){
        if(server == null){
            server = "http://127.0.0.1:${getConfig("framework.port")}"
        }

        return server
    }


}


//HttpBuilder http = HttpBuilder.configure {
//    request.uri = "http://127.0.0.1:8080/organization/count"
//
//    response.success { FromServer from, byte[] body->
//        return new String(body, "UTF-8")
//    }
//}
//
//String message = http.get(String){}
//println message
//
//assert message == """{"code":200,"msg":"Hello World!"}"""

//import static groovyx.net.http.HttpBuilder.configure
//import groovy.json.JsonSlurper
//
//int count = configure {
//    request.uri = 'http://api.open-notify.org'
//}.get(Integer){
//    request.uri.path = '/astros.json'
//    response.parser('application/json'){ cc, fs->
//        new JsonSlurper().parse(fs.inputStream).number
//    }
//}
//
//println "There are $count astronauts in space"
//
