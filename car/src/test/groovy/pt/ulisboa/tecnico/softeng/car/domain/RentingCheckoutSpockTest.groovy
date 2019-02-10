package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.car.exception.CarException

class RentingCheckoutSpockTest extends SpockRollbackTestAbstractClass {
	private static final String ADVENTURE_ID = "AdventureId";
	private static final String NAME1='eartz'
	private static final String PLATE_CAR1='aa-00-11'
	private static final String DRIVING_LICENSE='br123'
	private static final LocalDate date1= LocalDate.parse('2018-01-06')
	private static final LocalDate date2= LocalDate.parse('2018-01-07')
	private static final String NIF='NIF'
	private static final String IBAN='IBAN'
	private static final String IBAN_BUYER='IBAN'
	private Car car

	@Override
	def populate4Test() {
		RentACar rentACar = new RentACar(NAME1,NIF,IBAN)
		car=new Car(PLATE_CAR1,10,10,rentACar)
	}

	def 'checkout'() {
		given:
        Renting renting=car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)

		when:
		renting.checkout(100)

		then:
		car.getKilometers() == 110
	}

	def 'fail checkout'() {
		given:
        Renting renting=car.rent(DRIVING_LICENSE, date1, date2, NIF, IBAN_BUYER, ADVENTURE_ID)

		when:
		renting.checkout(-10)

		then:
		thrown(CarException)
	}
}
