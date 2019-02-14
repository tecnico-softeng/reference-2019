package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate

class RentACarGetRentingSpockTest extends SpockRollbackTestAbstractClass {
    def ADVENTURE_ID = "AdventureId"
    def NAME1 = 'eartz'
    def PLATE_CAR1 = 'aa-00-11'
    def DRIVING_LICENSE = 'br123'
    def date1 = LocalDate.parse('2018-01-06')
    def date2 = LocalDate.parse('2018-01-07')
    def date3 = LocalDate.parse('2018-01-08')
    def date4 = LocalDate.parse('2018-01-09')
    def NIF = 'NIF'
    def IBAN = 'IBAN'
    def IBAN_BUYER = 'IBAN'
    def renting

    @Override
    def populate4Test() {
        def rentACar1 = new RentACar(NAME1,NIF,IBAN)
        def car1 = new Car(PLATE_CAR1,10,10,rentACar1)

        renting = car1.rent(DRIVING_LICENSE,date1,date2,NIF,IBAN_BUYER,ADVENTURE_ID)

        car1.rent(DRIVING_LICENSE,date3,date4,NIF,IBAN_BUYER,ADVENTURE_ID)
    }

    def 'get renting'() {
        expect:
        RentACar.getRenting(renting.getReference()) == renting
    }

    def 'non existing'() {
        expect:
        null == RentACar.getRenting("a")
    }
}
