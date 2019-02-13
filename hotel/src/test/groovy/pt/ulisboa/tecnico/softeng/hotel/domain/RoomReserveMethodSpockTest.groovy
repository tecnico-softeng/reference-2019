package pt.ulisboa.tecnico.softeng.hotel.domain

import static org.junit.Assert.*

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import spock.lang.Unroll

class RoomReserveMethodSpockTest extends SpockRollbackTestAbstractClass {
	private static final String NIF_HOTEL = "123456700"
	private static final String NIF_BUYER = "123456789"
	private static final String IBAN_BUYER = "IBAN_BUYER"
	private static final LocalDate ARRIVAL = new LocalDate(2016, 12, 19)
	private static final LocalDate DEPARTURE = new LocalDate(2016, 12, 24)
	private Room room;

	@Override
	def populate4Test() {
		Hotel hotel = new Hotel("XPTO123", "Lisboa", this.NIF_HOTEL, "IBAN", 20.0, 30.0)
		this.room = new Room(hotel, "01", Type.SINGLE)
	}

	def "success"() {
		when: "a booking for an available room occurs"
		Booking booking = this.room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

		then:
		this.room.getBookingSet().size() == 1
		booking.getReference().length() > 0
		booking.getArrival() == ARRIVAL
		booking.getDeparture() == DEPARTURE
	}

	def "no double"() {
		when: "a booking for an unavailable room occurs"
		this.room.reserve(Type.DOUBLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

		then: "a HotelException is thrown"
		def error = thrown(HotelException)
	}

	def "room is already reserved"() {
		given: "a booking for a room"
		this.room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

		when: "when a booking is done for the same period"
		this.room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

		then: "an HotelException is thrown"
		def error = thrown(HotelException)
		this.room.getBookingSet().size() == 1
	}

	@Unroll('one of the arguments is invalid: #type | #arrival | #departure | #buyerNIF | #buyerIban')
	def "incorrect arguments"() {
		when: "a reserve is done with an incorrect argument"
		this.room.reserve(type, arrival, departure, buyerNIF, buyerIban)

		then: "an HotelException is thrown"
		def error = thrown(HotelException)

		where:
		type | arrival | departure | buyerNIF | buyerIban
		null | ARRIVAL | DEPARTURE | NIF_BUYER | IBAN_BUYER
		Type.SINGLE | null | DEPARTURE | NIF_BUYER | IBAN_BUYER
		Type.SINGLE | ARRIVAL | null | NIF_BUYER | IBAN_BUYER
		Type.SINGLE | ARRIVAL | DEPARTURE | null | IBAN_BUYER
		Type.SINGLE | ARRIVAL | DEPARTURE | NIF_BUYER | null
	}
}
