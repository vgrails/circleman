package com.circleman

import com.circleman.util.ParallelRunner
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.text.SimpleDateFormat

import static com.circleman.core.BaseApp.clazzMap
import static com.circleman.util.EnvironmentAwareConfig.getDEVELOPMENT
import static spark.Spark.stop
import groovyx.net.http.*

//
//
//

class IntegrationTest {

    @BeforeAll
    static void beforeAll(){
        Bootstrap.main()
        Bootstrap.env = DEVELOPMENT
    }

    @AfterAll
    static void afterAll(){
        stop()
    }

    @Test
    void 正常_DateBooleanChar() {
        HttpBuilder http = HttpBuilder.configure {
            request.uri = "http://127.0.0.1:8080/organization/count"

            response.success { FromServer from, byte[] body->
                return new String(body, "UTF-8")
            }
        }

        String message = http.get(String){}
        println message

        assert message == """{"code":200,"msg":"Hello World!"}"""
    }

//    @Test
//    void 并发_ORM操作(){
//        ParallelRunner runner=new ParallelRunner()
//        int THREAD_NUM = 4
//        runner.Run(THREAD_NUM,5000, {int threadId, operationId->
//            assert 1==1
//        })
//
//        println runner.toString()
//        assert runner.operationPerSecond > 1000
//    }
}
