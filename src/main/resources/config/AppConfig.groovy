environment = "development"

framework {
    //运行端口
    port = 8080

    debug = false
    //反射扫描位置
    reflectionScan = "com.circleman"

    app {
        //应用简称(EN)
        name = "demo"
        //应用全称(中文)
        fullName = "Circleman演示系统"
        //应用版本
        version = "1.0.0"
    }

    threadPool{
        min = 10
        max = 100
        timeout = 10
    }

    codegen {
        //是否生成
        generateDomains = true
        //生成类的包名
        domainsPackage = "com.circleman.domains"

        apiPrefix = "api"
        //是否覆盖
        overridden = false
    }
}

database {
    hbm2ddl="create-drop"
    url = "jdbc:h2:mem:demo"
}

//environments {
//    //生产
//    production {
//        database {
//            hbm2ddl = "update"
//            dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
//            url = 'jdbc:mysql://localhost:3306/test?useSSL=false'
//            driverClassName = 'com.mysql.jdbc.Driver'
//            username: "test"
//            password: "123456"
//        }
//    }
//}