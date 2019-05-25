package com.circleman

import com.circleman.domains.TestNumberic
import com.circleman.util.Orm
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import static com.circleman.util.EnvironmentAwareConfig.getDEVELOPMENT
import static spark.Spark.stop


class IntegrationTestOrm {

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
    void 正常_NUMBERIC_CRUD_COUNT_GET(){

        long beforeCount = new Orm().domain("TestNumberic").type("count").count()

        assert beforeCount + 1 == new Orm().domain("TestNumberic").type("create").attributes(
            byte1: 1,
            short1: 1,
            int1: 1,
            int2: 1,
            long1: 1,
            long2: 1,
            float1: 1.00,
            float2: 1.00,
            double1: 1.0000,
            double2: 1.0000
        ).create()

        long afterCount = new Orm().domain("TestNumberic").type("count").count()
        assert afterCount == beforeCount + 1
//        new Orm().domain("Organization").type("update").attributes([name: "工程01", description:"工程汉子多如牛01"]).id(beforeCount+1).update()
//
//        afterCount = new Orm().domain("Organization").type("count").count()
//        assert afterCount == beforeCount + 1
//
//        TestNumberic org = new Orm().domain("Organization").type("get").id(beforeCount+1).get()
//
//        assert org.id == beforeCount + 1
//        assert org.name == "工程01"
//        assert org.description == "工程汉子多如牛01"
//
//        assert beforeCount + 2 == new Orm().domain("Organization").type("create").attributes([name: "工程02", description:"工程汉子多如牛02"]).create()
//
//        afterCount = new Orm().domain("Organization").type("count").count()
//        assert afterCount == beforeCount + 2
//
//        List<TestNumberic> organizations= new Orm().domain("Organization").type("query").query()
//
//        assert organizations.size() == afterCount
//
//        assert organizations[-1].name == "工程02"
//        assert organizations[-1].description == "工程汉子多如牛02"
//
//        TestNumberic.withTransaction {
//            for(int i=0;i<100;i++) {
//                new TestNumberic(name: "研发${i}", description: "码农${i}集散地").save()
//            }
//            new TestNumberic(name: "行政", description: "美女集散地").save()
//        }
//
//
//        organizations= new Orm().domain("Organization").type("query").max(10).offset(20).query()
//
//        for(TestNumberic o in organizations){
//            println "${o.name} ${o.description}"
//        }
//
//        organizations= new Orm().domain("Organization").type("query").max(10).offset(20).query()
//
//        for(TestNumberic o in organizations){
//            println "${o.name} ${o.description}"
//        }
//
//        TestNumberic.withTransaction {
//            TestNumberic.findAll()*.delete()
//        }

    }


    @Test
    void 正常_STRING_CRUD_COUNT_GET(){

//        Organization.withTransaction {
//            for(int i=0;i<100;i++) {
//                new Organization(name: "研发${i}", description: "码农${i}集散地").save()
//            }
//            new Organization(name: "行政", description: "美女集散地").save()
//        }

        long beforeCount = new Orm().domain("Organization").type("count").count()
        assert beforeCount + 1 == new Orm().domain("Organization").type("create").attributes([name: "1工程", description:"1工程汉子多如牛"]).create()

        long afterCount = new Orm().domain("Organization").type("count").count()
        assert afterCount == beforeCount + 1
        new Orm().domain("Organization").type("update").attributes([name: "工程01", description:"工程汉子多如牛01"]).id(beforeCount+1).update()

        afterCount = new Orm().domain("Organization").type("count").count()
        assert afterCount == beforeCount + 1

        TestNumberic org = new Orm().domain("Organization").type("get").id(beforeCount+1).get()

        assert org.id == beforeCount + 1
        assert org.name == "工程01"
        assert org.description == "工程汉子多如牛01"

        assert beforeCount + 2 == new Orm().domain("Organization").type("create").attributes([name: "工程02", description:"工程汉子多如牛02"]).create()

        afterCount = new Orm().domain("Organization").type("count").count()
        assert afterCount == beforeCount + 2

        List<TestNumberic> organizations= new Orm().domain("Organization").type("query").query()

        assert organizations.size() == afterCount

        assert organizations[-1].name == "工程02"
        assert organizations[-1].description == "工程汉子多如牛02"

        TestNumberic.withTransaction {
            for(int i=0;i<100;i++) {
                new TestNumberic(name: "研发${i}", description: "码农${i}集散地").save()
            }
            new TestNumberic(name: "行政", description: "美女集散地").save()
        }


        organizations= new Orm().domain("Organization").type("query").max(10).offset(20).query()

        for(TestNumberic o in organizations){
            println "${o.name} ${o.description}"
        }

        organizations= new Orm().domain("Organization").type("query").max(10).offset(20).query()

        for(TestNumberic o in organizations){
            println "${o.name} ${o.description}"
        }

        TestNumberic.withTransaction {
            TestNumberic.findAll()*.delete()
        }

    }

    @Test
    void 正常_更新(){
        TestNumberic.withTransaction {
            new TestNumberic(name: "销售", description: "忽悠集散地").save()
        }

        TestNumberic.withTransaction {
            TestNumberic.findAll().each{ TestNumberic o ->
                println "${o.name} ${o.description}"
            }
        }

        long beforeCount = new Orm().domain("Organization").type("count").count()
        new Orm().domain("Organization").type("update").attributes([name: "工程", description:"工程汉子多如牛"]).id(1).update()



        TestNumberic.withTransaction {
            TestNumberic.findAll().each{ TestNumberic o ->
                println "${o.name} ${o.description}"
            }
        }

        long afterCount = new Orm().domain("Organization").type("count").count()
        assert afterCount == beforeCount
    }
}
