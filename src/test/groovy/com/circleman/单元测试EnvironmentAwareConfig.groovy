package com.circleman

import com.circleman.util.EnvironmentAwareConfig
import com.circleman.util.ParallelRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static com.circleman.util.EnvironmentAwareConfig.*

class 单元测试EnvironmentAwareConfig {

    @BeforeEach
    void Init(){
        EnvironmentAwareConfig.initConfig()
    }

    @Test
    void 正常_基础功能(){

        Map <String, Object> resultMap =[
            "framework.port": 8080,
            "framework.reflectionScan": "com.circleman",
            "framework.app.name": "demo",
            "framework.app.fullName": "Circleman演示系统",
            "framework.app.version": "1.0.0",
            "framework.threadPool.min": 10,
            "framework.threadPool.max": 100,
            "framework.threadPool.timeout": 10,
            "framework.codegen.generateDomains": true,
            "framework.codegen.domainsPackage": "com.circleman.domains",
            "framework.codegen.overridden": false,
            "database.hbm2ddl": "create-drop",
            "database.url": "jdbc:h2:mem:demo",
            "codegen.generateDomains": true,
            "codegen.domainsPackage": "com.circleman.domains",
            "codegen.overridden": false
        ]

        assert env == DEVELOPMENT

        resultMap.each{ key, value->
            assert getConfig(key) == value
        }
    }

    @Test
    void 异常_二次加载(){
        EnvironmentAwareConfig.initConfig()
        EnvironmentAwareConfig.initConfig()

        assert env == DEVELOPMENT
        assert getConfig("framework.app.name") == "demo"
    }

    @Test
    void 异常_获取不存在(){
        assert getConfig(null) == null
        assert getConfig("noexist.app.name") == null
    }

    @Test
    void 并发_读取配置(){
        ParallelRunner runner=new ParallelRunner()
        runner.Run(100,100000, {int threadId, operationId->
            assert env == DEVELOPMENT
            assert getConfig("framework.app.name") == "demo"
        })

        println runner.toString()
        assert runner.operationPerSecond > 100000
    }
}
