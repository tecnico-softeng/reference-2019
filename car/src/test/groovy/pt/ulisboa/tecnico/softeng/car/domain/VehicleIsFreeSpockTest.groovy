package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate

class VehicleIsFreeSpockTest extends SpockRollbackTestAbstractClass {
    def ADVENTURE_ID = "AdventureId"
    def PLATE_CAR='22-33-HZ'
    def RENT_A_CAR_NAME='Eartz'
    def DRIVING_LICENSE='lx1423'
    def date1= LocalDate.parse('2018-01-06')
    def date2= LocalDate.parse('2018-01-07')
    def date3= LocalDate.parse('2018-01-08')
    def date4= LocalDate.parse('2018-01-09')
    def NIF='NIF'
    def IBAN='IBAN'
    def IBAN_BUYER='IBAN'

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

