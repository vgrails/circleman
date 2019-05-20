package com.circleman

import com.circleman.util.EnvConfig
import com.circleman.util.ParallelRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static com.circleman.util.EnvConfig.*

class EnvConfig单元测试{

    @BeforeEach
    void 初始化(){
        EnvConfig.LoadConfig()
    }

    @Test
    void 正常_基本功能(){
        assert env == DEVELOPMENT
        assert GetConfig("framework.app.name") == "demo"
    }

    @Test
    void 异常_重复初始化(){
        EnvConfig.LoadConfig()
        EnvConfig.LoadConfig()

        assert env == DEVELOPMENT
        assert GetConfig("framework.app.name") == "demo"
    }

    @Test
    void 异常_KEY空或不存在(){
        assert GetConfig(null) == null
        assert GetConfig("noexist.app.name") == null
    }

    @Test
    void 并发获取配置(){
        ParallelRunner runner=new ParallelRunner()
        runner.Run(100,100000, {int threadId, operationId->
            assert env == DEVELOPMENT
            assert GetConfig("framework.app.name") == "demo"
        })

        println runner.toString()
        assert runner.operationPerSecond > 100000
    }
}
