package pt.ulisboa.tecnico.softeng.hotel.domain

import static org.junit.Assert.*

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import spock.lang.Unroll

class BookingConflictMethodSpockTest extends SpockRollbackTestAbstractClass {
	private Room room
	private final LocalDate arrival = new LocalDate(2016, 12, 19)
	private final LocalDate departure = new LocalDate(2016, 12, 24)
	private Booking booking
	private final String NIF_HOTEL = "123456700"
	private final String NIF_BUYER = "123456789"
	private final String IBAN_BUYER = "IBAN_BUYER"

	@Override
	def populate4Test() {
		Hotel hotel = new Hotel("XPTO123", "Londres", this.NIF_HOTEL, "IBAN", 20.0, 30.0)
		room = new Room(hotel, "01", Room.Type.SINGLE)
	}

	@Unroll('from #arrival to #departure should not overlap with period from 2016, 12, 19 to 2016, 12, 24')
	def 'dates do not overlap' (LocalDate arrival, LocalDate departure, boolean result) {
		given: "a booking"
		this.booking = new Booking(this.room, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER)

		expect: "it does not conflic with non overlapping dates"
		this.booking.conflict(arrival, departure) == result

		where:
		arrival | departure || result
		new LocalDate(2016, 12, 9) | new LocalDate(2016, 12, 15) || false
		new LocalDate(2016, 12, 9) | new LocalDate(2016, 12, 19) || false
		new LocalDate(2016, 12, 26) | new LocalDate(2016, 12, 30) || false
		new LocalDate(2016, 12, 24) | new LocalDate(2016, 12, 30) || false
	}

	def 'no conflict because it is cancelled' () {
		given: "a booking"
		this.booking = new Booking(this.room, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER)

		and: "the booking is cancelled"
		this.booking.cancel()

		expect: "it does not conflict with conflicting dates"
		this.booking.conflict(this.booking.getArrival(), this.booking.getDeparture()) == false
	}

	def 'arguments are inconsistent' () {
		given: "a booking"
		this.booking = new Booking(this.room, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER)

		when: "start date is later than end date"
		this.booking.conflict(new LocalDate(2016, 12, 15), new LocalDate(2016, 12, 9))

		then: "an Hotel Exception is thrown"
		thrown(HotelException)
	}

	def 'begin equals end day' () {
		given: "a booking"
		this.booking = new Booking(this.room, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER)

		expect: "it conflicts same day not overlapping"
		this.booking.conflict(new LocalDate(2016, 12, 9), new LocalDate(2016, 12, 9))
	}

	@Unroll('from #arrival to #departure should not overlap with period from 2016, 12, 19 to 2016, 12, 24')
	def 'dates do overlap' (LocalDate arrival, LocalDate departure, boolean result) {
		given: "a booking"
		this.booking = new Booking(this.room, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER)

		expect: "it does conflic with overlapping dates"
		this.booking.conflict(arrival, departure) == result

		where:
		arrival | departure || result
		new LocalDate(2016, 12, 9) | new LocalDate(2016, 12, 30) || true
		new LocalDate(2016, 12, 19) | new LocalDate(2016, 12, 29) || true
		new LocalDate(2016, 12, 7) | new LocalDate(2016, 12, 24) || true
		new LocalDate(2016, 12, 8) | new LocalDate(2016, 12, 21) || true
		new LocalDate(2016, 12, 21) | new LocalDate(2016, 12, 30) || true
		new LocalDate(2016, 12, 20) | new LocalDate(2016, 12, 22) || true
	}
}
