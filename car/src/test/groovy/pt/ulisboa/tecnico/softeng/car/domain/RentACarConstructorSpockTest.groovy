package pt.ulisboa.tecnico.softeng.car.domain

import pt.ulisboa.tecnico.softeng.car.exception.CarException
import spock.lang.Specification


class RentACarConstructorSpockTest extends SpockRollbackTestAbstractClass {
    def NAME = 'eartz'
    def NIF = 'NIF'
    def IBAN = 'IBAN'

    @Override
    def populate4Test() { }

    def 'success'() {
        given:
        RentACar rentACar=new RentACar(NAME,NIF,IBAN)

        expect:
        rentACar.getName() == NAME
    }

    def 'null name'() {
        when:
        new RentACar(null,NIF,IBAN)

        then:
        thrown(CarException)
    }

    def 'empty name'() {
        when:
        new RentACar('',NIF,IBAN)

        then:
        thrown(CarException)
    }
}
