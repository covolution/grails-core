package grails.test.mixin

import spock.lang.Specification
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert


@TestMixin(GrailsUnitTestMixin)
class MetaClassCleanupSpec extends Specification {

    def "Test that meta classes are restored to prior state after test run"() {
        when:"A meta class is modified in the test"
            Author.metaClass.testMe = {-> "test"}
            Author.metaClass.testToo = {-> "second"}
            def a = new Author()
        then:"The methods are available"
            a.testMe() == "test"
            a.testToo() == "second"
    }

    def instance = new Author()
    def "Test that changes made to an instance are cleaned up - step 1"() {
        when:"a change is made to an instance"
            instance.metaClass.doWork = {->"done"}

        then:"The method is callable"
            instance.doWork() == "done"
    }

    def "Test that changes made to an instance are cleaned up - step 2"() {
        when:"when the method is called again"
            instance.doWork()

        then:"The method was cleaned by the registry cleaner"
            thrown MissingMethodException
    }    

    @AfterClass
    static void checkCleanup() {
        def a = new Author()

        try {
            a.testMe()
            Assert.fail("Should have cleaned up meta class changes")
        } catch (MissingMethodException) {
        }
        
        try {
            a.testToo()
            Assert.fail("Should have cleaned up meta class changes")
        } catch (MissingMethodException) {
        }
    }

}
class Author {
    String name
}

