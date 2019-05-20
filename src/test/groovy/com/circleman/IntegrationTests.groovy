package com.circleman

import com.circleman.core.MetaType
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import static spark.Spark.*

class IntegrationTests {

    @BeforeAll
    static void beforeAll(){
        Bootstrap.main()
    }

    @AfterAll
    static void afterAll(){
        stop()
    }


    @Test
    void 随便测试1(){

        MetaType metaType = "STRING" as MetaType
        assert metaType.toString() == "STRING"
        assert metaType.value == 3

        assert metaType.next().toString() == "DATE"
        assert metaType.previous().toString() == "CHAR"

        metaType = "FLOAT"

        assert  metaType.toString() == "FLOAT"

        assert  metaType.isNumberic() == true

        metaType = "ONE2ONE"
        assert metaType.isRelation() == true
        assert metaType.isNumberic() == false
    }

    @Test
    void test2(){
        MetaType metaType = "STRING" as MetaType
        assert metaType.toString() == "STRING"
        assert metaType.value == 3

        assert metaType.next().toString() == "DATE"
        assert metaType.previous().toString() == "CHAR"

        metaType = "FLOAT"

        assert  metaType.toString() == "FLOAT"

        assert  metaType.isNumberic() == true

        metaType = "ONE2ONE"
        assert metaType.isRelation() == true
        assert metaType.isNumberic() == false
    }
}
