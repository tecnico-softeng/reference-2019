package pt.ulisboa.tecnico.softeng.car.domain

import pt.ulisboa.tecnico.softeng.car.exception.CarException
import spock.lang.Shared
import spock.lang.Unroll

class VehicleConstructorSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def PLATE_CAR = '22-33-HZ'
    def PLATE_MOTORCYCLE = '44-33-HZ'
    def RENT_A_CAR_NAME = 'Eartz'
    def NIF = 'NIF'
    def IBAN = 'IBAN'
    @Shared def rentACar

    @Override
    def populate4Test() {
        rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN)
    }

    def 'success'() {
        when:
        def car = new Car(PLATE_CAR, 10, 10, rentACar)
        def motorcycle = new Motorcycle(PLATE_MOTORCYCLE, 10, 10, rentACar)

        then:
        with(car) {
            getPlate() == PLATE_CAR
            getPrice() == 10.0
        }

        with(rentACar) {
            rentACar.hasVehicle(PLATE_CAR)
            rentACar.hasVehicle(PLATE_MOTORCYCLE)
        }

        motorcycle.getPlate() == PLATE_MOTORCYCLE
    }

    @Unroll('RentACar: #plate, #km, #price, #rac')
    def 'exceptions'() {
        when: 'creating a car with wrong parameters'
        new Car(plate, km, price, rac)

        then: 'throws an exception'
        thrown(CarException)

        where:
        plate       | km | price | rac
        PLATE_CAR   | 0  | 10    | null
        PLATE_CAR   | -1 | 10    | rentACar
        'AA-XX-aaa' | 10 | 10    | rentACar
        'AA-XX-a'   | 10 | 10    | rentACar
        null        | 10 | 10    | rentACar
        ''          | 10 | 10    | rentACar
    }

    def 'duplicated plate'() {
        when: 'creating 2 cars with the same plate'
        new Car(PLATE_CAR, 0, 10, rentACar)
        new Car(PLATE_CAR, 0, 10, rentACar)

        then: 'throws an exception'
        thrown(CarException)
    }

    def 'duplicated plate different rent a car'() {
        given: 'create a car in rentacar'
        new Car(PLATE_CAR, 0, 10, rentACar)

        when: 'creating 2 cars with the same plate, even in two different rentacars'
        def rentACar2=new RentACar(RENT_A_CAR_NAME + '2', NIF, IBAN)
        new Car(PLATE_CAR, 2, 10, rentACar2)

        then: 'throws an exception'
        thrown(CarException)
    }

}
