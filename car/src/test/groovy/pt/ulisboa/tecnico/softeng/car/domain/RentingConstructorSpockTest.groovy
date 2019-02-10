package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.car.exception.CarException

class RentingConstructorSpockTest extends SpockRollbackTestAbstractClass {

    private static final String RENT_A_CAR_NAME = 'Eartz'
    private static final String PLATE_CAR = '22-33-HZ'
    private static final String DRIVING_LICENSE = 'br112233'
    private static final LocalDate date1 = LocalDate.parse('2018-01-06')
    private static final LocalDate date2 = LocalDate.parse('2018-01-07')
    private static final String NIF = 'NIF'
    private static final String IBAN = 'IBAN'
    private static final String IBAN_BUYER = 'IBAN'
    private Car car

    @Override
    def populate4Test() {
        RentACar rentACar = new RentACar(RENT_A_CAR_NAME,NIF,IBAN)
        car=new Car(PLATE_CAR,10,10,rentACar)
    }

    def 'success'() {
        given:
        Renting renting=new Renting(DRIVING_LICENSE,date1,date2,this.car,NIF,IBAN_BUYER)

        expect:
        renting.getDrivingLicense() == DRIVING_LICENSE
        this.car.getPrice() * (date2.getDayOfYear() - date1.getDayOfYear() + 1) == renting.getPrice()
    }

    def 'null driving license'() {
        when:
        new Renting(null,date1,date2,this.car,NIF,IBAN_BUYER)

        then:
        thrown(CarException)
    }

    def 'empty driving license'() {
        when:
        new Renting('',date1,date2,this.car,NIF,IBAN_BUYER)

        then:
        thrown(CarException)
    }

    def 'invalid driving license'() {
        when:
        new Renting('12',date1,date2,this.car,NIF,IBAN_BUYER)

        then:
        thrown(CarException)
    }

    def 'null begin'() {
        when:
        new Renting(DRIVING_LICENSE,null,date2,this.car,NIF,IBAN_BUYER)

        then:
        thrown(CarException)
    }

    def 'null end'() {
        when:
        new Renting(DRIVING_LICENSE,date1,null,this.car,NIF,IBAN_BUYER)

        then:
        thrown(CarException)
    }

    def 'end before begin'() {
        when:
        new Renting(DRIVING_LICENSE,date2,date1,this.car,NIF,IBAN_BUYER)

        then:
        thrown(CarException)
    }

    def 'null car'() {
        when:
        new Renting(DRIVING_LICENSE,date1,date2,null,NIF,IBAN_BUYER)

        then:
        thrown(CarException)
    }
}
