package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.car.exception.CarException

class RentACarRentSpockTest extends SpockRollbackTestAbstractClass {
    private static final String ADVENTURE_ID = "AdventureId"
    private static final String PLATE_CAR='22-33-HZ'
    private static final String RENT_A_CAR_NAME='Eartz'
    private static final String DRIVING_LICENSE='lx1423'
    private static final LocalDate BEGIN= LocalDate.parse('2018-01-06')
    private static final LocalDate END= LocalDate.parse('2018-01-09')
    private static final String NIF='NIF'
    private static final String IBAN='IBAN'
    private static final String IBAN_BUYER='IBAN'
    private RentACar rentACar
    private Car car

    @Override
    def populate4Test() {
        rentACar = new RentACar(RENT_A_CAR_NAME,NIF,IBAN)
        car = new Car(PLATE_CAR,10,10,rentACar)
    }

    def 'rent a car has car available'() {
        given:
        String reference= RentACar.rent(Car,DRIVING_LICENSE,NIF,IBAN_BUYER,BEGIN,END,ADVENTURE_ID)

        expect:
        reference != null
        !car.isFree(BEGIN,END)
    }

    def 'rent a car has no cars available'() {
        given:
        RentACar.rent(Car,DRIVING_LICENSE,NIF,IBAN_BUYER,BEGIN,END,ADVENTURE_ID)

        when:
        RentACar.rent(Car,DRIVING_LICENSE,NIF,IBAN_BUYER,BEGIN.plusDays(1),END.plusDays(1),ADVENTURE_ID)

        then:
        thrown(CarException)
    }

    def 'no rent a cars'() {
        given:
        rentACar.delete()

        when:
        RentACar.rent(Car,DRIVING_LICENSE,NIF,IBAN_BUYER,BEGIN,END,ADVENTURE_ID)

        then:
        thrown(CarException)
    }

    def 'no motorcycles'() {
        when:
        RentACar.rent(Motorcycle,DRIVING_LICENSE,NIF,IBAN_BUYER,BEGIN,END,ADVENTURE_ID)

        then:
        thrown(CarException)
    }
}
