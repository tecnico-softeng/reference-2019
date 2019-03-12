package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import spock.lang.Unroll

class ActivityOfferHasVacancyMethodSpockTest extends SpockRollbackTestAbstractClass {
	def IBAN = "IBAN"
	def NIF = "123456789"
	def provider
	def offer

	def bankInterface = Mock(BankInterface)
	def taxInterface = Mock(TaxInterface)

	@Override
	def populate4Test() {
		provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN)
		def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

		def begin = new LocalDate(2016, 12, 19)
		def end = new LocalDate(2016, 12, 21)

		offer = new ActivityOffer(activity, begin, end, 30)
	}


	@Unroll('success: #label')
	def 'success'() {
		when:
		for(int i = 0; i < n; i++) {
			new Booking(provider, offer, NIF, IBAN)
		}

		then:
		offer.hasVacancy() == res

		where:
		n | label                     || res
		1 | 'one booking'             || true
		3 | 'booking is full'         || false
		2 | 'booking is full minus 1' || true
	}

	def 'has cancelled bookings'() {
		given:
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		def booking = new Booking(provider, offer, NIF, IBAN)
		provider.getProcessor().submitBooking(booking)

		when:
		booking.cancel()

		then:
		offer.hasVacancy()
	}

	def 'has cancelled bookings but full'() {
		given:
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		def booking = new Booking(provider, offer, NIF, IBAN)
		provider.getProcessor().submitBooking(booking)

		when:
		booking.cancel()

		and:
		def booking2 = new Booking(provider, offer, NIF, IBAN)
		provider.getProcessor().submitBooking(booking2)

		then:
		!offer.hasVacancy()
	}
}
