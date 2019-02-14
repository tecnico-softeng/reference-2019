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
	}

	def 'rent a car has car available'() {
		given: 'given a car availble'
		car = new Car(PLATE_CAR,10,10,rentACar)

		when: 'when renting the car'
		def reference= RentACar.rent(Car, DRIVING_LICENSE, NIF, IBAN_BUYER, BEGIN, END, ADVENTURE_ID)

		then: 'than it should succeed: get a renting reference and car becomes not free'
		reference != null
		!car.isFree(BEGIN,END)
	}

	@Unroll('no car/motorcycle: #name')
	def 'exceptions'() {
		when: 'if the rent a car has no vehicles'
		RentACar.rent(type, DRIVING_LICENSE, NIF, IBAN_BUYER, BEGIN, END, ADVENTURE_ID)

		then: 'renting a vehicle should throw an exception'
		thrown(CarException)

		where:
		name         | type
		'car'        | Car.class
		'motorcycle' | Motorcycle.class
	}

	def 'no rent a cars'() {
		given: 'if there are no rent a cars'
		rentACar.delete()

		when: 'trying to rent a car'
		RentACar.rent(Car,DRIVING_LICENSE,NIF,IBAN_BUYER,BEGIN,END,ADVENTURE_ID)

		then: 'throws an exception'
		thrown(CarException)
	}
}
