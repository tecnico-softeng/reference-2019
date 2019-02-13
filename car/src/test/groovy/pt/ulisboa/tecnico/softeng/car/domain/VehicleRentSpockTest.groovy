package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.car.exception.CarException
import spock.lang.Unroll


class VehicleRentSpockTest extends SpockRollbackTestAbstractClass {
    static final String ADVENTURE_ID = 'AdventureId'
    static final String PLATE_CAR = '22-33-HZ'
    static final String RENT_A_CAR_NAME = 'Eartz'
    static final String DRIVING_LICENSE = 'lx1423'
    static final LocalDate date1 = LocalDate.parse('2018-01-06')
    static final LocalDate date2 = LocalDate.parse('2018-01-09')
    static final String NIF = 'NIF'
    static final String IBAN = 'IBAN'
    static final String IBAN_BUYER = 'IBAN'

    RentACar rentACar

    @Override
    def populate4Test() {
        rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN)
    }

    def 'double rent'() {
        given:
        def car = new Car(PLATE_CAR,10,10, rentACar)

        when:
        car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)
        car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)

        then:
        thrown(CarException)
    }

    @Unroll('#begin, #end')
    def 'exceptions'() {
        given:
        def car = new Car(PLATE_CAR,10,10,rentACar)

        when:
        car.rent(DRIVING_LICENSE, date1,null, NIF + "1", IBAN_BUYER, ADVENTURE_ID)

        then:
        thrown(CarException)

        where:
        begin | end
        date1 | null
        null  | date2
    }
}
