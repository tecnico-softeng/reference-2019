package pt.ulisboa.tecnico.softeng.car.domain

import pt.ulisboa.tecnico.softeng.car.exception.CarException

class VehicleConstructorSpockTest extends SpockRollbackTestAbstractClass {
    def PLATE_CAR = '22-33-HZ'
    def PLATE_MOTORCYCLE = '44-33-HZ'
    def RENT_A_CAR_NAME = 'Eartz'
    def NIF = 'NIF'
    def IBAN = 'IBAN'
    def rentACar

    @Override
    def populate4Test() {
        rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN)
    }

    def 'success'() {
        given:
        def car = new Car(PLATE_CAR,10,10,this.rentACar)
        def motorcycle = new Motorcycle(PLATE_MOTORCYCLE,10,10,this.rentACar)

        expect:
        car.getPlate() == PLATE_CAR
        rentACar.hasVehicle(PLATE_CAR)
        motorcycle.getPlate() == PLATE_MOTORCYCLE
        rentACar.hasVehicle(PLATE_MOTORCYCLE)
        10.0 == car.getPrice()
    }

    def 'empty license plate'() {
        when:
        new Car('',10,10,this.rentACar)

        then:
        thrown(CarException)
    }

    def 'null license plate'() {
        when:
        new Car(null,10,10,this.rentACar)

        then:
        thrown(CarException)
    }

    def 'invalid license plate'() {
        when:
        new Car('AA-XX-a',10,10,this.rentACar)

        then:
        thrown(CarException)
    }

    def 'invalid license plate 2'() {
        when:
        new Car('AA-XX-aaa',10,10,this.rentACar)

        then:
        thrown(CarException)
    }

    def 'duplicated plate'() {
        when:
        new Car(PLATE_CAR,0,10,this.rentACar)

        new Car(PLATE_CAR,0,10,this.rentACar)

        then:
        thrown(CarException)
    }

    def 'duplicated plate different rent a car'() {
        when:
        new Car(PLATE_CAR,0,10,rentACar)

        RentACar rentACar2=new RentACar(RENT_A_CAR_NAME + '2',NIF,IBAN)

        new Car(PLATE_CAR,2,10,rentACar2)

        then:
        thrown(CarException)
    }

    def 'negative kilometers'() {
        when:
        new Car(PLATE_CAR,-1,10,this.rentACar)

        then:
        thrown(CarException)
    }

    def 'no rent a car'() {
        when:
        new Car(PLATE_CAR,0,10,null)

        then:
        thrown(CarException)
    }
}
