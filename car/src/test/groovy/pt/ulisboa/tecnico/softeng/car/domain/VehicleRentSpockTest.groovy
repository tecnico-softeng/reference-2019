package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.car.exception.CarException


class VehicleRentSpockTest extends SpockRollbackTestAbstractClass {
    private static final String ADVENTURE_ID = "AdventureId";
    private static final String PLATE_CAR='22-33-HZ'
    private static final String RENT_A_CAR_NAME='Eartz'
    private static final String DRIVING_LICENSE='lx1423'
    private static final LocalDate date1=LocalDate.parse('2018-01-06')
    private static final LocalDate date2=LocalDate.parse('2018-01-09')
    private static final String NIF='NIF'
    private static final String IBAN='IBAN'
    private static final String IBAN_BUYER='IBAN'
    private RentACar rentACar
    private Car car

    @Override
    def populate4Test() {
        rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN);
    }

    def 'double rent'() {
        given:
        car = new Car(PLATE_CAR,10,10,rentACar)

        when:
        car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID);
        car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID);



        then:
        thrown(CarException)
    }

    def 'begin is null'() {
        given:
        def car = new Car(PLATE_CAR,10,10,rentACar)

        when:
        car.rent(DRIVING_LICENSE,null,date2,NIF + "1",IBAN_BUYER)

        then:
        thrown(CarException)
    }

    def 'end is null'() {
        given:
        def car = new Car(PLATE_CAR,10,10,rentACar)

        when:
        car.rent(DRIVING_LICENSE, date1,null, NIF + "1", IBAN_BUYER)

        then:
        thrown(CarException)
    }

}
