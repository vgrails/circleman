package com.circleman

import com.circleman.util.ParallelRunner
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.text.SimpleDateFormat

import static com.circleman.core.BaseApp.clazzMap
import static com.circleman.util.EnvironmentAwareConfig.getDEVELOPMENT
import static spark.Spark.stop


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
        assert  1  == 1
    }

    @Test
    void 并发_ORM操作(){
        ParallelRunner runner=new ParallelRunner()
        int THREAD_NUM = 4
        runner.Run(THREAD_NUM,5000, {int threadId, operationId->
            assert 1==1
        })

        println runner.toString()
        assert runner.operationPerSecond > 1000
    }
}
