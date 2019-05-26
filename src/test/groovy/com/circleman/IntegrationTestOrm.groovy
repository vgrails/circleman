package com.circleman

import com.circleman.domains.Organization
import com.circleman.domains.TestDateBooleanChar
import com.circleman.domains.TestNumberic
import com.circleman.util.Orm
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.text.SimpleDateFormat

import static com.circleman.core.BaseApp.clazzMap
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
    void 正常_DateBooleanChar() {
        long beforeCount = new Orm().domain("TestDateBooleanChar").count()

        assert beforeCount + 1 == new Orm().domain("TestDateBooleanChar").attributes(
            boolean1: true,
            char1: 'e' as char,
            date: new SimpleDateFormat("yyyy-MM-dd").parse("2009-10-22")
        ).create()

        long afterCount = new Orm().domain("TestDateBooleanChar").count()
        assert afterCount == beforeCount + 1
        new Orm().domain("TestDateBooleanChar").attributes(
            boolean1: false,
            char1: 'f' as char,
            date: new SimpleDateFormat("yyyy-MM-dd").parse("2009-10-23")
        ).id(beforeCount + 1).update()

        clazzMap["TestDateBooleanChar"].withTransaction {
            println new Orm().domain("TestDateBooleanChar").id(afterCount).get()
        }

        def list

        list = new Orm().domain("TestDateBooleanChar").query()

        for(TestDateBooleanChar t in list){
            println "${t.id} date:${t.date} boolean:${t.boolean1} char:${t.char1}"
        }

        for(int i =0;i<20;i++){
            new Orm().domain("TestDateBooleanChar").attributes(
                boolean1: (i%2==0),
                char1: ('g'+i) as char,
                date: new SimpleDateFormat("yyyy-MM-dd").parse("2009-10-23")
            ).create()
        }

        list = new Orm().domain("TestDateBooleanChar").query()

        for(TestDateBooleanChar t in list){
            println "${t.id} date:${t.date} boolean:${t.boolean1} char:${t.char1}"
        }

        list = new Orm().domain("TestDateBooleanChar").filter("boolean1").op("=").p1(true).query()

        for(TestDateBooleanChar t in list){
            println "${t.id} date:${t.date} boolean:${t.boolean1} char:${t.char1}"
        }

        list = new Orm().domain("TestDateBooleanChar").filter("char1").op("=").p1('g' as char).query()

        for(TestDateBooleanChar t in list){
            println "${t.id} date:${t.date} boolean:${t.boolean1} char:${t.char1}"
        }

        list = new Orm().domain("TestDateBooleanChar").filter("date").op("=").p1(new SimpleDateFormat("yyyy-MM-dd").parse("2009-10-23")).query()

        assert list.size() == 21

        for(TestDateBooleanChar t in list){
            println "${t.id} date:${t.date} boolean:${t.boolean1} char:${t.char1}"
        }

        list = new Orm().domain("TestDateBooleanChar").filter("date").op("between").p1(new SimpleDateFormat("yyyy-MM-dd").parse("2009-10-22")).p2(new SimpleDateFormat("yyyy-MM-dd").parse("2009-10-24")).query()

        21 == new Orm().domain("TestDateBooleanChar").filter("date").op("between").p1(new SimpleDateFormat("yyyy-MM-dd").parse("2009-10-22")).p2(new SimpleDateFormat("yyyy-MM-dd").parse("2009-10-24")).count()

        assert list.size() == 21

        for(TestDateBooleanChar t in list){
            println "${t.id} date:${t.date} boolean:${t.boolean1} char:${t.char1}"
        }
    }

    @Test
    void 正常_NUMBERIC_CRUD_COUNT_GET() {

        long beforeCount = new Orm().domain("TestNumberic").count()

        assert beforeCount + 1 == new Orm().domain("TestNumberic").attributes(
            byte1: 1 ,
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

        long afterCount = new Orm().domain("TestNumberic").count()
        assert afterCount == beforeCount + 1
        assert afterCount == new Orm().domain("TestNumberic").attributes(
            byte1: 2,
            short1: 2,
            int1: 2,
            int2: 2,
            long1: 2,
            long2: 2,
            float1: 2.00,
            float2: 2.00,
            double1: 2.0000,
            double2: 2.0000
        ).id(beforeCount + 1).update()

        clazzMap["TestNumberic"].withTransaction {
            println new Orm().domain("TestNumberic").id(afterCount).get()
        }

        TestNumberic.withTransaction {
            println beforeCount + 1
            println TestNumberic.get(beforeCount + 1)

        }

        for(int i=10;i<100;i++) {
            new Orm().domain("TestNumberic").attributes(
                byte1: 1+i,
                short1: 1+i,
                int1: 1+i,
                int2: 1+i,
                long1: 1+i,
                long2: 1+i,
                float1: 1.00+i,
                float2: 1.00+i,
                double1: 1.0000+i,
                double2: 1.0000+i
            ).create()
        }

        def list= new Orm().domain("TestNumberic").max(10).offset(20).query()

        for(TestNumberic o in list){
            println "${o.byte1} ${o.double1}"
        }

        list= new Orm().domain("TestNumberic").max(10).filter("byte1").op("le").p1(15 as byte).orderBy("int1").direction("desc").query()

        println ">>>> begin output"
        for(TestNumberic o in list){
            println "${o.byte1} ${o.float1}"
        }
        println "<<<< end output"

        assert list.size() == 6

        //删除所有
        list= new Orm().domain("TestNumberic").query()

        for(TestNumberic o in list){
            assert new Orm().domain("TestNumberic").id(o.id).delete() == true
        }

        assert new Orm().domain("TestNumberic").count() == 0
    }


    @Test
    void 正常_STRING_CRUD_COUNT_GET(){

        long beforeCount = new Orm().domain("Organization").count()
        assert beforeCount + 1 == new Orm().domain("Organization").attributes([name: "1工程", description:"1工程汉子多如牛"]).create()

        long afterCount = new Orm().domain("Organization").count()
        assert afterCount == beforeCount + 1
        new Orm().domain("Organization").attributes([name: "工程01", description:"工程汉子多如牛01"]).id(beforeCount+1).update()

        afterCount = new Orm().domain("Organization").count()
        assert afterCount == beforeCount + 1

        Organization org = new Orm().domain("Organization").id(afterCount).get()

        assert org.id == beforeCount + 1
        assert org.name == "工程01"
        assert org.description == "工程汉子多如牛01"

        assert beforeCount + 2 == new Orm().domain("Organization").attributes([name: "工程02", description:"工程汉子多如牛02"]).create()

        afterCount = new Orm().domain("Organization").count()
        assert afterCount == beforeCount + 2

        List<Organization> organizations= new Orm().domain("Organization").query()

        assert organizations.size() == afterCount

        assert organizations[-1].name == "工程02"
        assert organizations[-1].description == "工程汉子多如牛02"

        Organization.withTransaction {
            for(int i=0;i<100;i++) {
                new Organization(name: "研发${i}", description: "码农${i}集散地").save()
            }
            new Organization(name: "行政", description: "美女集散地").save()
        }


        organizations= new Orm().domain("Organization").max(10).offset(20).query()

        for(Organization o in organizations){
            println "${o.name} ${o.description}"
        }

        organizations= new Orm().domain("Organization").max(10).offset(20).query()

        for(Organization o in organizations){
            println "${o.name} ${o.description}"
        }

        Organization.withTransaction {
            Organization.findAll()*.delete()
        }

    }

    @Test
    void 正常_更新(){
        Organization.withTransaction {
            new Organization(name: "销售", description: "忽悠集散地").save()
        }

        Organization.withTransaction {
            Organization.findAll().each{ Organization o ->
                println "${o.name} ${o.description}"
            }
        }

        long beforeCount = new Orm().domain("Organization").count()
        new Orm().domain("Organization").attributes([name: "工程", description:"工程汉子多如牛"]).id(1).update()



        Organization.withTransaction {
            Organization.findAll().each{ Organization o ->
                println "${o.name} ${o.description}"
            }
        }

        long afterCount = new Orm().domain("Organization").count()
        assert afterCount == beforeCount
    }
}
