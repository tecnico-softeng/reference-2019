package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.car.exception.CarException

class RentingConflictSpockTest extends SpockRollbackTestAbstractClass {
	private static final String PLATE_CAR='22-33-HZ'
	private static final String DRIVING_LICENSE='br112233'
	private static final LocalDate date0= LocalDate.parse('2018-01-05')
	private static final LocalDate date1= LocalDate.parse('2018-01-06')
	private static final LocalDate date2= LocalDate.parse('2018-01-07')
	private static final LocalDate date3= LocalDate.parse('2018-01-08')
	private static final LocalDate date4= LocalDate.parse('2018-01-09')
	private static final String RENT_A_CAR_NAME='Eartz'
	private static final String NIF='NIF'
	private static final String IBAN='IBAN'
	private static final String IBAN_BUYER='IBAN'
	private Car car

	@Override
	def populate4Test() {
		RentACar rentACar = new RentACar(RENT_A_CAR_NAME,NIF,IBAN)
		car=new Car(PLATE_CAR,10,10,rentACar)
	}

	def 'reting is before dates'() {
		given:
        Renting renting=new Renting(DRIVING_LICENSE,date1,date2,car,NIF,IBAN_BUYER)

		expect:
		!renting.conflict(date3,date4)
	}

	def 'reting is before dates same day interval'() {
		given:
        Renting renting=new Renting(DRIVING_LICENSE,date1,date2,car,NIF,IBAN_BUYER)

		expect:
		!renting.conflict(date3,date3)
	}

	def 'renting ends on start date'() {
		given:
        Renting renting=new Renting(DRIVING_LICENSE,date1,date2,car,NIF,IBAN_BUYER)

		expect:
		renting.conflict(date2,date3)
	}

	def 'renting starts on end date'() {
		given:
        Renting renting=new Renting(DRIVING_LICENSE,date1,date2,car,NIF,IBAN_BUYER)

		expect:
		renting.conflict(date1,date1)
	}

	def 'renting starts during interval'() {
		given:
        Renting renting=new Renting(DRIVING_LICENSE,date1,date2,car,NIF,IBAN_BUYER)

		expect:
		renting.conflict(date0,date3)
	}

	def 'end before begin'() {
		given:
        Renting renting=new Renting(DRIVING_LICENSE,date1,date2,car,NIF,IBAN_BUYER)

		when:
		renting.conflict(date2,date1)

		then:
		thrown(CarException)
	}
}
