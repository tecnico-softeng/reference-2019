package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.car.exception.CarException

class RentingConflictSpockTest extends SpockRollbackTestAbstractClass {
	def PLATE_CAR = '22-33-HZ'
	def DRIVING_LICENSE = 'br112233'
	def date0 = LocalDate.parse('2018-01-05')
	def date1 = LocalDate.parse('2018-01-06')
	def date2 = LocalDate.parse('2018-01-07')
	def date3 = LocalDate.parse('2018-01-08')
	def date4 = LocalDate.parse('2018-01-09')
	def RENT_A_CAR_NAME ='Eartz'
	def NIF = 'NIF'
	def IBAN = 'IBAN'
	def IBAN_BUYER = 'IBAN'
	def car

	@Override
	def populate4Test() {
		RentACar rentACar = new RentACar(RENT_A_CAR_NAME,NIF,IBAN)
		car=new Car(PLATE_CAR,10,10,rentACar)
	}

	def 'reting is before dates'() {
		when:
        Renting renting=new Renting(DRIVING_LICENSE,date1,date2,car,NIF,IBAN_BUYER)

		then:
		!renting.conflict(date3,date4)
	}

	def 'reting is before dates same day interval'() {
		when:
        Renting renting=new Renting(DRIVING_LICENSE,date1,date2,car,NIF,IBAN_BUYER)

		then:
		!renting.conflict(date3,date3)
	}

	def 'renting ends on start date'() {
		when:
        Renting renting=new Renting(DRIVING_LICENSE,date1,date2,car,NIF,IBAN_BUYER)

		then:
		renting.conflict(date2,date3)
	}

	def 'renting starts on end date'() {
		when:
        Renting renting=new Renting(DRIVING_LICENSE,date1,date2,car,NIF,IBAN_BUYER)

		then:
		renting.conflict(date1,date1)
	}

	def 'renting starts during interval'() {
		when:
        Renting renting=new Renting(DRIVING_LICENSE,date1,date2,car,NIF,IBAN_BUYER)

		then:
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
