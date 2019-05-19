package com.circleman

import com.circleman.util.EnvConfig
import com.circleman.util.ParallelRunner

import static com.circleman.util.EnvConfig.*

class EnvConfigUnitTest extends GroovyTestCase{

    //正常
    void testEnvConfigNormal(){
        EnvConfig.LoadConfig()

        assert env == DEVELOPMENT
        assert getConfig("framework.app.name") == "demo"
    }

    //异常
    void testEnvConfigAbnormal(){
        EnvConfig.LoadConfig()
        EnvConfig.LoadConfig()

        assert env == DEVELOPMENT
        assert getConfig("framework.app.name") == "demo"


        assert getConfig("noexist.app.name") == null
    }

    //并发
    void testEnvConfigCocurrent(){

        ParallelRunner runner=new ParallelRunner()

        runner.Run(100,10000, {int threadId, operationId->
            EnvConfig.LoadConfig()

            assert env == DEVELOPMENT
            assert getConfig("framework.app.name") == "demo"
        })

        println runner.toString()

        assert runner.operationPerSecond > 100000
    }
}
