package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.car.exception.CarException
import spock.lang.Shared
import spock.lang.Unroll

import java.sql.Driver

class RentingConstructorSpockTest extends SpockRollbackTestAbstractClass {

     static final String RENT_A_CAR_NAME = 'Eartz'
     static final String PLATE_CAR = '22-33-HZ'
     static final String DRIVING_LICENSE = 'br112233'
     static final LocalDate date1 = LocalDate.parse('2018-01-06')
     static final LocalDate date2 = LocalDate.parse('2018-01-07')
     static final String NIF = 'NIF'
     static final String IBAN = 'IBAN'
     static final String IBAN_BUYER = 'IBAN'
     @Shared Car car

    @Override
    def populate4Test() {
        RentACar rentACar = new RentACar(RENT_A_CAR_NAME, NIF,IBAN)
        car = new Car(PLATE_CAR, 10, 10, rentACar)
    }

    def 'success'() {
        when:
        Renting renting = new Renting(DRIVING_LICENSE, date1, date2, car, NIF, IBAN_BUYER)

        then:
        renting.getDrivingLicense() == DRIVING_LICENSE
        car.getPrice() * (date2.getDayOfYear() - date1.getDayOfYear() + 1) == renting.getPrice()
    }

    @Unroll('RentACar: exceptions')
    def 'exceptions'() {
        when:
        new Renting(dl, d1, d2, veh, nif, iban)

        then:
        thrown(CarException)

        where:
        dl   | d1 | d2 | veh | nif | iban
        null | date1 | date2 | car | NIF | IBAN_BUYER
        ''   | date1 | date2 | car | NIF | IBAN_BUYER
        '12' | date1 | date2 | car | NIF | IBAN_BUYER
        DRIVING_LICENSE | null  | date2 | car  | NIF | IBAN_BUYER
        DRIVING_LICENSE | date1 | null  | car  | NIF | IBAN_BUYER
        DRIVING_LICENSE | date2 | date1 | car  | NIF | IBAN_BUYER
        DRIVING_LICENSE | date1 | date2 | null | NIF | IBAN_BUYER
    }
}
