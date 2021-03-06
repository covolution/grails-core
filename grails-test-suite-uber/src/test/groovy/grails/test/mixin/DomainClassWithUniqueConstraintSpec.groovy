package grails.test.mixin

import spock.lang.Specification
import grails.persistence.Entity

/**
 * Tests the usage of unique contstraint in unit tests
 */
@TestFor(Group)
class DomainClassWithUniqueConstraintSpec extends Specification{


    void "Test that unique constraint is enforced"() {
        given:"An existing persisted instance"
            new Group(name:"foo").save(flush:true)

        when:"We try to persist another instance"
            def g = new Group(name:"foo")
            g.save()

        then:"a validation error occurs"
            g.hasErrors()
            Group.count() == 1
    }
}
@Entity
class Group {
    String name
    static constraints = {
        name unique:true
    }
}
