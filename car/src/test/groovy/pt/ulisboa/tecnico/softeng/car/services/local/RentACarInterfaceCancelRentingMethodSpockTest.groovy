package pt.ulisboa.tecnico.softeng.car.services.local

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.car.domain.Car
import pt.ulisboa.tecnico.softeng.car.domain.RentACar
import pt.ulisboa.tecnico.softeng.car.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.car.exception.CarException
import spock.lang.Unroll

class RentACarInterfaceCancelRentingMethodSpockTest extends SpockRollbackTestAbstractClass {
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
	def renting


	@Override
	def populate4Test() {
		rentACar = new RentACar(RENT_A_CAR_NAME,NIF,IBAN)

		car = new Car(PLATE_CAR,10,10,rentACar)

		renting = car.rent(DRIVING_LICENSE, BEGIN, END, NIF, IBAN_BUYER, ADVENTURE_ID)
	}

	def 'success'() {
		when: 'when cancelling a renting'
		String cancel = RentACarInterface.cancelRenting(renting.getReference())

		then: 'the renting becomes cancelled, and the cancellation reference stored'
		renting.isCancelled()
		renting.getCancellationReference() == cancel
	}

	@Unroll('#label')
	def 'exceptions'() {
		when: 'canceling a wrong ref'
		RentACarInterface.cancelRenting(ref)

		then: 'throws an exception'
		thrown(CarException)

		where:
		label | ref
		'missing ref' | 'MISSING_REFERENCE'
		'null ref'    | null
		'empty ref'   | ''
	}


	def 'does not exist reference'() {
		when:
		RentACarInterface.cancelRenting('MISSING_REFERENCE')

		then:
		thrown(CarException)
	}
}
