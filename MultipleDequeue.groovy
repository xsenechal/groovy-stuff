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
                    Thread.sleep(200); println "worker: $it"
                }
            }
        }
        //Not needed if using @scheduled from Spring
        if(askForReschedule){
            println "Reschedule done to poolSie:$poolSize"
            runProcess()
        }
        isStopped = true
    }

    def askForStop(){askForStop = true    }
    def askForReschedule(int poolSize){this.poolSize = poolSize; askForReschedule = true    }
}

Consummer c = new Consummer()

//Simulate a ask for reschedule
Thread.start {
    Thread.sleep(1000)
    println "asking for reschedule"
    c.askForReschedule(3)
}

//Simulate a ask for stop
Thread.start {
    Thread.sleep(5000)
    println "asking for stop"
    c.askForStop()
}
c.runProcess()