package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.car.exception.CarException
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface
import spock.lang.Unroll

class RentACarCancelRentingMethodSpockTest extends SpockRollbackTestAbstractClass {
	private static final String ADVENTURE_ID = "AdventureId"
	private static final String PLATE_CAR='22-33-HZ'
	private static final String RENT_A_CAR_NAME='Eartz'
	private static final String DRIVING_LICENSE='lx1423'
	private static final LocalDate BEGIN= LocalDate.parse('2018-01-06')
	private static final LocalDate END= LocalDate.parse('2018-01-09')
	private static final String NIF='NIF'
	private static final String IBAN='IBAN'
	private static final String IBAN_BUYER='IBAN'
	private RentACar rentACar
	private Car car
	private Renting renting


	@Override
	def populate4Test() {
		rentACar = new RentACar(RENT_A_CAR_NAME,NIF,IBAN)

		car = new Car(PLATE_CAR,10,10,rentACar)

		renting = RentACar.rent(Car,DRIVING_LICENSE,NIF,IBAN_BUYER,BEGIN,END,ADVENTURE_ID)
	}

	def 'success'() {
		given:
		String cancel = RentACar.cancelRenting(renting.getReference())

		expect:
		renting.isCancelled()
		renting.getCancellationReference() == cancel
	}

	@Unroll('#label')
	def 'exceptions'() {
		when:
		RentACar.cancelRenting(ref)

		then:
		thrown(CarException)

		where:
		label | ref
		'missing ref' | 'MISSING_REFERENCE'
		'null ref'    | null
		'empty ref'   | ''
	}


	def 'success integration'() {
		given:
		GroovySpy(TaxInterface, global: true)

		when:
		String cancel= RentACar.cancelRenting(renting.getReference())

		then:
		this.renting.isCancelled()
		this.renting.getCancellationReference() == cancel
	}

	def 'does not exist integration'() {
		given:
		GroovySpy(TaxInterface, global: true)

		when:
        RentACar.cancelRenting('MISSING_REFERENCE')

		then:
		thrown(CarException)

		0 * TaxInterface.cancelInvoice(_)
	}
}
