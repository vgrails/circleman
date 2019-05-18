environment = "development"

framework {

    //运行端口
    port = 8080

    //反射扫描位置
    reflectionScan = "com.circleman"    //reflections的扫描起点

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
}

//database {
//    url = ""
//    username = ""
//    password = ""
//    hbm2ddl = ""
//}

codegen {
    //是否生成
    generateDomains = true
    //生成类的包名
    domainsPackage = "com.circleman.domains"   //codegen模型的位置
    //是否覆盖
    overridden = false
}



//environments {
//
//    //生产
//    production {
//        database {
//            url = ""
//            username = ""
//            password = ""
//            hbm2ddl = "production"
//        }
//    }
//}