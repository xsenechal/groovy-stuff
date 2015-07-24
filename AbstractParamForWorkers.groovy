/**
 * Simulate multiple worker sharing both
 * - some code in AbstractWorker
 * - params in AbstractParams
 *
 * BUT keeping the ability to have worker specific code + params (with typing)
 */
abstract class AbstractParams{String sharePropertyUsedInAbstractWorker = 'hello from abstract param' }
class MyParams1 extends AbstractParams{ String toto = 'hello params1 for worker1'}
class MyParams2 extends AbstractParams{ String tata = 'hello params2 for worker2'}


//http://stackoverflow.com/questions/19312641/passing-generic-subtype-class-information-to-superclass-in-java
abstract class AbstractWorker<T extends AbstractParams>{

    protected T p;

    AbstractWorker(Class<? extends T> paramClass) {
        p = paramClass.newInstance() //or other way to retrieve param according to the subClass
    }
    void doThingsInAbstract(){println p.sharePropertyUsedInAbstractWorker}
    abstract void run()
}
class MyWorker1 extends AbstractWorker<MyParams1>{

    MyWorker1() {     super(MyParams1.class);    }

    void doThingsInSubclass() {println p.toto}

    @Override
    void run() {
        doThingsInAbstract()
        doThingsInSubclass()
    }
}
class MyWorker2 extends AbstractWorker<MyParams2>{

    MyWorker2() {     super(MyParams2.class);    }

    void doThingsInSubclass() {println p.tata}

    @Override
    void run() {
        doThingsInAbstract()
        doThingsInSubclass()
    }
}
new MyWorker1().run()
new MyWorker2().run()