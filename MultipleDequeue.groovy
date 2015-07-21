import java.util.concurrent.ArrayBlockingQueue

import static groovyx.gpars.GParsPool.withPool

class Consummer{

    int poolSize = 10
    def askForStop = false
    def askForReschedule = false
    def isStopped = false

    def runProcess(){
        isStopped = false; askForStop = false; askForReschedule = false

        if(poolSize < 1){return }

        withPool(poolSize) {
            (1..poolSize).eachParallel {
                while (!askForStop && !askForReschedule) {

                    //Get new task and Process it
                    def t = getNext()
                    if(t){
                        Thread.sleep(300); println "worker: $it for task ${t} / ${queue.size()}"
                    }
                }
            }
        }
        //Not needed if using @scheduled from Spring
        if(askForReschedule){
            println "///////////////Reschedule done to poolSize:$poolSize/////////////////"
            runProcess()
        }
        isStopped = true
    }

    def askForStop(){askForStop = true    }
    def askForReschedule(int poolSize){this.poolSize = poolSize; askForReschedule = true    }


    def queue = new ArrayBlockingQueue(100)
    def getNext(){    queue.poll()   }
    def enqueue(o){   queue.put(o)   }
}

Consummer c = new Consummer()

//Simulate a producer
i = 0; t = 0
Thread.start {
    while(true){
        Thread.sleep(1000);
        if(t++ == 5) return;
        if(c.queue.size() < 50){
            50.times {c.enqueue(i++)}
        }
    }
}
//A break for the producer.. and again
Thread.start {
    Thread.sleep(9000);
    while(true){
        if(c.queue.size() < 50){
            50.times {c.enqueue(i++)}
        }
    }
}

//Simulate a ask for reschedule
Thread.start {
    Thread.sleep(10000)
    println "////////////////asking for reschedule////////////////"
    c.askForReschedule(3)
}

//Simulate a ask for stop
Thread.start {
    Thread.sleep(15000)
    println "////////////////asking for stop////////////////"
    c.askForStop()
}
c.runProcess()