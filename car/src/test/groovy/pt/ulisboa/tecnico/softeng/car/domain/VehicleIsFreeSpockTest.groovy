package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate

class VehicleIsFreeSpockTest extends SpockRollbackTestAbstractClass {
    static final String ADVENTURE_ID = "AdventureId"
    static final String PLATE_CAR='22-33-HZ'
    static final String RENT_A_CAR_NAME='Eartz'
    static final String DRIVING_LICENSE='lx1423'
    static final LocalDate date1= LocalDate.parse('2018-01-06')
    static final LocalDate date2= LocalDate.parse('2018-01-07')
    static final LocalDate date3= LocalDate.parse('2018-01-08')
    static final LocalDate date4= LocalDate.parse('2018-01-09')
    static final String NIF='NIF'
    static final String IBAN='IBAN'
    static final String IBAN_BUYER='IBAN'

    RentACar rentACar

    @Override
    def populate4Test() {
        rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN)
    }

    def 'no booking was made'() {
        when:
        Car car = new Car(PLATE_CAR,10,10,rentACar)

        then:
        car.isFree(date1,date2)
        car.isFree(date1,date3)
        car.isFree(date3,date4)
        car.isFree(date4,date4)
    }

    def 'bookings were made'() {
        given:
        Car car = new Car(PLATE_CAR,10,10,rentACar)
        car.rent(DRIVING_LICENSE, date2, date2, NIF, IBAN_BUYER, ADVENTURE_ID)

        when:
        car.rent(DRIVING_LICENSE, date3, date4, NIF, IBAN_BUYER, ADVENTURE_ID)

        then:
        !car.isFree(date1,date2)
        !car.isFree(date1,date3)
        !car.isFree(date3,date4)
        !car.isFree(date4,date4)
        car.isFree(date1,date1)
    }

}

