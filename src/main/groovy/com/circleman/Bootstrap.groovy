package com.circleman

import com.circleman.core.BaseApp
import com.circleman.core.MetaLayout
import com.circleman.core.MetaLayoutBuilder
import com.circleman.domains.Employee
import com.circleman.domains.Organization
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response

import static spark.Spark.*;

class Bootstrap extends BaseApp{
    static Logger log = LoggerFactory.getLogger(Bootstrap)

    static void main(String[] args){

        initialization()

        log.info getConfig("framework.port").toString()

        log.info System.getProperty("user.dir")


        MetaLayout l= new MetaLayoutBuilder().hbox(id: "left-right"){
            tree(id: "leftTree")
            vbox(id: "rightvbox"){
                toolbar(id: "toolbar")
                grid(id: "grid")
            }
        }


//        println "==========================="
//        println l.toString()
//        println "==========================="
//
//
//        println JsonOutput.prettyPrint(JsonOutput.toJson(l))


        //ORM Testing
//        OrmQuery q = new OrmQuery().domain("Organization").max(20).offset(0)
//        q.expr(new Expr().op("eq").orderBy("name").param1("研发1"))
//        println q.toHql()
//        q.expr(new Expr().op("eq").orderBy("id").param1(2))
//        println q.toHql()
//
//
//        OrmUpdate u = new OrmUpdate().domain("Organization").id(1).attributes([name: "天上人间", description: "女神节日吧"])
//        println u.toHql()
//        println update(u)
//
//        List<Organization> results=query(new OrmQuery().domain("Organization").max(20).offset(0))
//
//        results.each{ Organization o ->
//            log.info "${o.name} ${o.description}"
//        }
//
//        log.info "create"
//        (create(new OrmCreate().domain("Organization").attributes([name: "天人", description: "节日"])).toString())
//
//        get "/chart", { Request request, Response response ->
//
//            List output = []
//            for(int i=0;i<5;i++){
//                int value = new Random().nextInt(100)
//                output.add([count: value, dollars: value, color: "#00FF00"])
//            }
//
//            println "result: ${output.size()}"
//            println output
//
//            json(output)
//        }
//
//        log.info count(new OrmQuery().domain("Organization")).toString()
//
//        for(long i=1;i<10;i++){
//            log.info delete(new OrmDelete().domain("Organization").id(i)).toString()
//        }
//
//        log.info count(new OrmQuery().domain("Organization")).toString()












        //println GetMetaDomain('organization')

//        get "/", { Request request, Response response ->
//            println convertToParams(request)
//
//            response.type('text/html')
//            println response.type()
//            return "<html><title>html</title><body><h2>Hello World!</h2></body><html>"
//        }

//        def cls = Organization
//
//        println Organization.getGormPersistentEntity().getPropertyByName('name').properties
//        println Organization.getGormPersistentEntity().getPropertyByName('description')
//
//
//
//        println "----------------------------"
//
//        for(int i=0;i<cls.getDeclaredFields().size();i++){
//            String name = cls.getDeclaredFields()[i].name
//
//            if(name in ['version', 'metaClass', 'constraints', 'transients'] || name.startsWith("org_")|| name.startsWith("\$")|| name.startsWith("__")|| name.startsWith("org_")){
//                continue
//            }
//
//            println "${cls.getDeclaredFields()[i].name} ${cls.getDeclaredFields()[i].type.simpleName}"
//        }
//
//        println "----------------------------"

//        get "/:controllers/:action", {Request request, Response response ->
//
//            println convertToParams(request)
//
//            Map model = ["message":"妧妧"]
//
//            template("index.vm", model)
//        }


        get "/", {Request request, Response response ->

            println convertToParams(request)

            Map model = [name:"little 我问问"]

            response.type("text/html")

            //response.header('charset', 'utf-8')
            println model
            template("index.vm", model)
        }


        GET "/employee/detail", { Request request, Response response, Map params ->

            Map model = [code:0]

            long id = Long.parseLong(params.id)


            Employee e

            Employee.withTransaction {
                e = Employee.findById(id)
            }

            if(e == null || e.id != id){
                model = [code: -1, message: "对象不存在"]
            }else{
                model = [code: -1, data: e]
            }

            json(model)
        }
//
//        get "api/:controllers/:action", {Request request, Response response ->
//
//            println convertToParams(request)
//            log.info(convertToParams(request).toString())
//            Map model = ["message":"妧妧"]
//
//            json(model)
//        }
    }
}
