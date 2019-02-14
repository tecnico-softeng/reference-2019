package pt.ulisboa.tecnico.softeng.car.domain

import pt.ulisboa.tecnico.softeng.car.exception.CarException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll


class RentACarConstructorSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def NAME = 'eartz'
    @Shared def NIF = 'NIF'
    @Shared def IBAN = 'IBAN'

    @Override
    def populate4Test() { }

    def 'success'() {
        given: 'creating a new rent a car'
        RentACar rentACar=new RentACar(NAME,NIF,IBAN)

        expect: 'should succeed'
        rentACar.getName() == NAME
    }


    @Unroll('RentACar: #name')
    def 'exceptions'() {
        when: 'creating a RentACar with invalid arguments'
        new RentACar(name, nif, iban)

        then: 'throws an exception'
        thrown(CarException)

        where:
        name | nif | iban
        null | NIF | IBAN
        ''   | NIF | IBAN
    }
}
