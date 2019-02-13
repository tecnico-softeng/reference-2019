package pt.ulisboa.tecnico.softeng.hotel.domain

import static org.junit.Assert.*

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type

class RoomGetBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
	private final String NIF_BUYER = "123456789";
	private final String IBAN_BUYER = "IBAN_BUYER";
	private final LocalDate ARRIVAL = new LocalDate(2016, 12, 19)
	private final LocalDate DEPARTURE = new LocalDate(2016, 12, 24)

	private Hotel hotel
	private Room room
	private Booking booking

	@Override
	def populate4Test() {
		this.hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", 20.0, 30.0)
		this.room = new Room(this.hotel, "01", Type.SINGLE)
	}

	def "success"() {
		given: "a booking"
		this.booking = this.room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

		expect: "get booking using cancellation reference"
		this.room.getBooking(this.booking.getReference()) == this.booking
	}

	def "success cancelled"() {
		given: "booking is cancelled"
		this.booking = this.room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)
		this.booking.cancel()

		expect: "get booking using cancellation reference"
		this.room.getBooking(this.booking.getCancellation()) == this.booking
	}

	def "does not exist"() {
		expect: "a null from a non existing reference"
		this.room.getBooking("XPTO") == null
	}
}
