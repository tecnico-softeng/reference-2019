package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.car.exception.CarException

class RentingCheckoutSpockTest extends SpockRollbackTestAbstractClass {
	def ADVENTURE_ID = "AdventureId"
	def NAME1='eartz'
	def PLATE_CAR1='aa-00-11'
	def DRIVING_LICENSE='br123'
	def date1= LocalDate.parse('2018-01-06')
	def date2= LocalDate.parse('2018-01-07')
	def NIF='NIF'
	def IBAN='IBAN'
	def IBAN_BUYER='IBAN'
	def car

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
