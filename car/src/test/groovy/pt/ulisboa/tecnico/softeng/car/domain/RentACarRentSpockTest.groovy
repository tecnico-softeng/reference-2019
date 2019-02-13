package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.car.exception.CarException
import spock.lang.Unroll

class RentACarRentSpockTest extends SpockRollbackTestAbstractClass {
    def ADVENTURE_ID = "AdventureId"
    def PLATE_CAR='22-33-HZ'
    def RENT_A_CAR_NAME='Eartz'
    def DRIVING_LICENSE='lx1423'
    def BEGIN= LocalDate.parse('2018-01-06')
    def END= LocalDate.parse('2018-01-09')
    def NIF='NIF'
    def IBAN='IBAN'
    def IBAN_BUYER='IBAN'
    def rentACar
    def car

    @Override
    def populate4Test() {
        rentACar = new RentACar(RENT_A_CAR_NAME,NIF,IBAN)
        car = new Car(PLATE_CAR,10,10,rentACar)
    }

    def 'rent a car has car available'() {
        when:
        String reference= RentACar.rent(Car,DRIVING_LICENSE,NIF,IBAN_BUYER,BEGIN,END,ADVENTURE_ID)

        then:
        reference != null
        !car.isFree(BEGIN,END)
    }

    @Unroll('no car/motorcycle: #name')
    def 'exceptions'() {
        when:
        RentACar.rent(Motorcycle,DRIVING_LICENSE,NIF,IBAN_BUYER,BEGIN,END,ADVENTURE_ID)

        then:
        thrown(CarException)

        where:
        name         | type
        'car'        | Car.class
        'motorcycle' | Motorcycle.class
    }

    def 'no rent a cars'() {
        given:
        rentACar.delete()

        when:
        RentACar.rent(Car,DRIVING_LICENSE,NIF,IBAN_BUYER,BEGIN,END,ADVENTURE_ID)

        then:
        thrown(CarException)
    }
}
