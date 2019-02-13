package pt.ulisboa.tecnico.softeng.hotel.domain

import static junit.framework.TestCase.assertTrue
import static org.junit.Assert.*

import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class HotelReserveRoomMethodSpockTest extends SpockRollbackTestAbstractClass {
	private static final String NIF_HOTEL = "123456789"
	private static final String NIF_BUYER = "123456700"
	private static final String IBAN_BUYER = "IBAN_CUSTOMER"
	private static final String IBAN_HOTEL = "IBAN_HOTEL"
	private final LocalDate ARRIVAL = new LocalDate(2016, 12, 19)
	private final LocalDate DEPARTURE = new LocalDate(2016, 12, 24)

	private Room room
	private Hotel hotel

	@Override
	def populate4Test() {
		this.hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, IBAN_HOTEL, 20.0, 30.0)
		this.room = new Room(this.hotel, "01", Room.Type.SINGLE)
	}

	def "success"() {
		when: "a reservation is done"
		def ref = Hotel.reserveRoom(Room.Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

		then: "a correct reference is returned"
		assertTrue(ref != null)
		assertTrue(ref.startsWith("XPTO123"))
	}

	def "no vancancy"() {
		given: "the sigle room is booked"
		Hotel.reserveRoom(Room.Type.SINGLE, ARRIVAL, new LocalDate(2016, 12, 25), NIF_BUYER, IBAN_BUYER)

		when: "booking during the same period"
		Hotel.reserveRoom(Room.Type.SINGLE, ARRIVAL, new LocalDate(2016, 12, 25), NIF_BUYER, IBAN_BUYER)

		then: "throws an HotelException"
		def error = thrown(HotelException)
	}

	def "no hotels"() {
		given: "there is no hotels"
		for (Hotel hotel: FenixFramework.getDomainRoot().getHotelSet()) {
			hotel.delete();
		}

		when: "reserve a room"
		Hotel.reserveRoom(Room.Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER);

		then: "throws an HotelException"
		def error = thrown(HotelException)
	}

	def "no rooms"() {
		given: "there is no rooms"
		for (Room room: this.hotel.getRoomSet()) {
			room.delete();
		}

		when: "reserve a room"
		Hotel.reserveRoom(Room.Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER);

		then: "throws an HotelException"
		def error = thrown(HotelException)
	}
}
