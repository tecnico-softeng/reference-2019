package pt.ulisboa.tecnico.softeng.hotel.services.local

import static org.junit.Assert.*

import java.util.stream.Collectors

import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import spock.lang.Unroll

class HotelInterfaceBulkBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
	private static final String NIF_BUYER = "123456789"
	private static final String IBAN_BUYER = "IBAN_BUYER"
	private static final String BULK_ID = "BULK_ID"
	private static final LocalDate ARRIVAL = new LocalDate(2016, 12, 19)
	private static final LocalDate DEPARTURE = new LocalDate(2016, 12, 21)

	private Hotel hotel;

	@Override
	def populate4Test() {
		this.hotel = new Hotel("XPTO123", "Paris", "NIF", "IBAN", 20.0, 30.0)
		new Room(this.hotel, "01", Type.DOUBLE)
		new Room(this.hotel, "02", Type.SINGLE)
		new Room(this.hotel, "03", Type.DOUBLE)
		new Room(this.hotel, "04", Type.SINGLE)

		this.hotel = new Hotel("XPTO124", "Paris", "NIF2", "IBAN2", 25.0, 35.0)
		new Room(this.hotel, "01", Type.DOUBLE)
		new Room(this.hotel, "02", Type.SINGLE)
		new Room(this.hotel, "03", Type.DOUBLE)
		new Room(this.hotel, "04", Type.SINGLE)
	}

	@Unroll('bulkbooking #number rooms and the reference size is #refSize')
	def "success"() {
		when: "bulkbooking rooms"
		Set<String> references = HotelInterface.bulkBooking(number, ARRIVAL, DEPARTURE, NIF_BUYER,
				IBAN_BUYER, BULK_ID)

		then: "references are returned"
		references.size() == refSize

		where:
		number | refSize
		2 || 2
		1 || 1
		8 || 8
	}

	def "unsuccess"() {
		when: "bulkbooking rooms"
		Set<String> references = HotelInterface.bulkBooking(9, ARRIVAL, DEPARTURE, NIF_BUYER,
				IBAN_BUYER, BULK_ID)

		then: "references are returned"
		thrown(HotelException)
		and: "no rooms are booked"
		HotelInterface.getAvailableRooms(8, ARRIVAL, DEPARTURE).size() == 8
	}

	def "no rooms"() {
		given: "there is a single hotel and has no rooms"
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			hotel.delete()
		}
		this.hotel = new Hotel("XPTO124", "Paris", "NIF", "IBAN", 27.0, 37.0)

		when: "a bulkbooking is done"
		HotelInterface.bulkBooking(3, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER, BULK_ID)

		then: "a HotelException is thrown"
		thrown(HotelException)
	}

	@Unroll('invalid arguments: #number | #arrival | #departure | #nif | #iban')
	def "invalid arguments"() {
		when: "a bulkbooking is done with an invalid argument"
		HotelInterface.bulkBooking(number, arrival, departure, nif, iban, BULK_ID)

		then: "a HotelException is thrown"
		thrown(HotelException)

		where:
		number | arrival | departure | nif | iban
		-1 | ARRIVAL | DEPARTURE | NIF_BUYER | IBAN_BUYER
		0 | ARRIVAL | DEPARTURE | NIF_BUYER | IBAN_BUYER
		2 | null | DEPARTURE | NIF_BUYER | IBAN_BUYER
		2 | ARRIVAL | null | NIF_BUYER  | IBAN_BUYER
		2 | ARRIVAL | DEPARTURE | null  | IBAN_BUYER
		2 | ARRIVAL | DEPARTURE | "  "  | IBAN_BUYER
		2 | ARRIVAL | DEPARTURE | NIF_BUYER | null
		2 | ARRIVAL | DEPARTURE | NIF_BUYER | "   "
	}

	def "idempotent bulk booking"() {
		given: "a bulkboooking of 4 rooms"
		Set<String> references = HotelInterface.bulkBooking(4, ARRIVAL, DEPARTURE, NIF_BUYER,
				IBAN_BUYER, BULK_ID)

		when: "do a bulkboooking with the same id"
		Set<String> equalReferences = HotelInterface.bulkBooking(4, ARRIVAL, DEPARTURE, NIF_BUYER,
				IBAN_BUYER, BULK_ID)

		then: "returns the same references"
		HotelInterface.getAvailableRooms(4, ARRIVAL, DEPARTURE).size() == 4
		references.stream().sorted().collect(Collectors.toList()).equals(
				equalReferences.stream().sorted().collect(Collectors.toList()))
	}
}
