package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import spock.lang.Shared

class VehicleIsFreeSpockTest extends SpockRollbackTestAbstractClass {
    def ADVENTURE_ID = "AdventureId"
    def PLATE_CAR='22-33-HZ'
    def RENT_A_CAR_NAME='Eartz'
    def DRIVING_LICENSE='lx1423'
    def NIF='NIF'
    def IBAN='IBAN'
    def IBAN_BUYER='IBAN'
    @Shared def date1= LocalDate.parse('2018-01-06')
    @Shared def date2= LocalDate.parse('2018-01-07')
    @Shared def date3= LocalDate.parse('2018-01-08')
    @Shared def date4= LocalDate.parse('2018-01-09')

    RentACar rentACar
    Car car

    @Override
    def populate4Test() {
        rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN)
        car = new Car(PLATE_CAR,10,10,rentACar)
    }

    def 'no booking was made'() {
        expect:
        car.isFree(begin,end)
        
	    where:
        begin | end
        date1 | date2
        date1 | date3
        date3 | date4
        date4 | date4
    }

    def 'bookings were made'() {
        given:
        car.rent(DRIVING_LICENSE, date2, date2, NIF, IBAN_BUYER, ADVENTURE_ID)

        when:
        car.rent(DRIVING_LICENSE, date3, date4, NIF, IBAN_BUYER, ADVENTURE_ID)

        then:
        car.isFree(date1,date1)
        !car.isFree(begin,end)

        where:
        begin | end
        date1 | date2
        date1 | date3
        date3 | date4
        date4 | date4
    }

}

