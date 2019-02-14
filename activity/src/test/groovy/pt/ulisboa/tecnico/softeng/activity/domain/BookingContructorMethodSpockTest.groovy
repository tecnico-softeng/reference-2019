package pt.ulisboa.tecnico.softeng.activity.domain

import static org.junit.Assert.fail
import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException

class BookingContructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	def provider
	def offer
	def AMOUNT = 30
	def IBAN = 'IBAN'
	def NIF = '123456789'

	@Override
	def populate4Test() {
		provider = new ActivityProvider('XtremX','ExtremeAdventure','NIF',IBAN)

		def activity = new Activity(provider,'Bush Walking',18,80,3)

		def begin = new LocalDate(2016,12,19)
		def end = new LocalDate(2016,12,21)

		offer = new ActivityOffer(activity,begin,end,AMOUNT)
	}

	def 'success'() {
		when:
		def booking = new Booking(provider, offer, NIF, IBAN)

		expect:
		booking.getReference().startsWith(provider.getCode())
		booking.getReference().length() > ActivityProvider.CODE_SIZE
		offer.getNumberActiveOfBookings() == 1
		booking.getBuyerNif() == NIF
		booking.getIban() == IBAN
		0 == booking.getAmount()
	}

	def 'null provider'() {
		given:
		new Booking(null, offer, NIF, IBAN)

		expect:
		thrown(ActivityException)
	}

	def 'null offer'() {
		given:
		new Booking(provider,null, NIF, IBAN)

		expect:
		thrown(ActivityException)
	}

	def 'null nif'() {
		given:
		new Booking(null, offer,null, IBAN)

		expect:
		thrown(ActivityException)
	}

	def 'empty iban'() {
		given:
		new Booking(provider, null, NIF, '     ')

		expect:
		thrown(ActivityException)
	}

	def 'null iban'() {
		given:
		new Booking(null, offer, NIF, null)

		expect:
		thrown(ActivityException)
	}

	def 'empty nif'() {
		given:
		new Booking(this.provider,null,'     ',IBAN)

		expect:
		thrown(ActivityException)
	}

	def 'booking equal capacity'() {
		expect:
		new Booking(provider,offer,NIF,IBAN)
		new Booking(provider,offer,NIF,IBAN)
		new Booking(provider,offer,NIF,IBAN)

		try {
			new Booking(provider,offer,NIF,IBAN)
			fail()
		} catch(ActivityException ae) {
			this.offer.getNumberActiveOfBookings() == 3
		}

	}

	def 'booking equal capacity but has cancelled'() {
		given:
		new Booking(provider,offer,NIF,IBAN)
		new Booking(provider,offer,NIF,IBAN)

		def booking=new Booking(provider,offer,NIF,IBAN)

		booking.cancel()

		new Booking(provider,offer,NIF,IBAN)

		expect:
		this.offer.getNumberActiveOfBookings() == 3
	}

}
