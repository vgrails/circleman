package com.circleman

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import static spark.Spark.*
import static com.circleman.Bootstrap.*
class IntegrationTests {

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
    void 随便测试1(){


    }

    @Test
    void test2(){

    }
}
