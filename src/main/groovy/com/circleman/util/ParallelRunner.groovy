package com.circleman.util

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import groovy.transform.CompileStatic

/**
 * 并行任务执行工具
 */
class ParallelRunner {

    int elapseInseconds
    BigInteger operationPerSecond

    void Run(int threadNum, int operationNum, Closure closure){
        List<Thread> threads = []

        Date start = new Date()
        for (int i = 0; i < threadNum; i++) {
            threads.add(Thread.start {
                for (int j = 0; j < operationNum; j++) {
                    closure.call(i, j)
                }
            })
        }

        threads*.join()
        Date stop = new Date()
        TimeDuration duration = TimeCategory.minus(stop, start)

        elapseInseconds=duration.seconds
        operationPerSecond = threadNum * operationNum / (duration.toMilliseconds() / 1000)
    }

    String toString(){
        return "耗时:${elapseInseconds}s, 操作/秒:${operationPerSecond}"
    }
}
