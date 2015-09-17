/**
 * Created by xs on 17/09/15.
 */
def rCit = new File("CIT.txt").newReader()
def rPub = new File("PUB.txt").newReader()

def lCit = rCit.readLine()
def lPub = rPub.readLine()

while (lCit){
    //Define matching keys
    def kCit = lCit[0].toInteger()
    def kPub = lPub[0].toInteger()

    if (kPub < kCit){
        lPub = rPub.readLine()
        continue
    }
    if (kPub > kCit){
        lCit = rCit.readLine()
        continue
    }

    println "PUB[$lPub] - CIT[$lCit]"
    //read next CIT as we have N cit per PUB
    lCit = rCit.readLine()
}
