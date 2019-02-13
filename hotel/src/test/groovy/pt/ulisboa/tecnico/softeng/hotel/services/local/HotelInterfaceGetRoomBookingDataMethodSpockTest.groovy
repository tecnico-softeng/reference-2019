package pt.ulisboa.tecnico.softeng.hotel.services.local

import static org.junit.Assert.*

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Booking
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.local.HotelInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData
import spock.lang.Unroll

class HotelInterfaceGetRoomBookingDataMethodSpockTest extends SpockRollbackTestAbstractClass {
	private static final LocalDate ARRIVAL = new LocalDate(2016, 12, 19)
	private static final LocalDate DEPARTURE = new LocalDate(2016, 12, 24)
	private static final String NIF_HOTEL = "123456700"
	private static final String NIF_BUYER = "123456789"
	private static final String IBAN_BUYER = "IBAN_BUYER"
	private Hotel hotel
	private Room room
	private Booking booking

	@Override
	def populate4Test() {
		this.hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, "IBAN", 20.0, 30.0)
		this.room = new Room(this.hotel, "01", Type.SINGLE)
		this.booking = this.room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)
	}

	def "success"() {
		when: "get the booking data from a booking"
		RestRoomBookingData data = HotelInterface.getRoomBookingData(this.booking.getReference())

		then: "it contains the correct information"
		this.booking.getReference().equals(data.getReference())
		data.getCancellation() == null
		data.getCancellationDate() == null
		data.getHotelName().equals(this.hotel.getName())
		data.getHotelCode().equals(this.hotel.getCode())
		data.getRoomNumber() == this.room.getNumber()
		data.getRoomType().equals(this.room.getType().name())
		data.getArrival() == this.booking.getArrival()
		data.getDeparture() == this.booking.getDeparture()
		data.getPrice() == this.booking.getPrice()
	}

	def "success cancellation"() {
		given: "a cancelled booking"
		this.booking.cancel();

		when: "the get booking data"
		RestRoomBookingData data = HotelInterface.getRoomBookingData(this.booking.getCancellation());

		then: "it contains the correct information"
		this.booking.getReference().equals(data.getReference())
		data.getCancellation() == this.booking.getCancellation()
		data.getCancellationDate() == this.booking.getCancellationDate()
		data.getHotelName().equals(this.hotel.getName())
		data.getHotelCode().equals(this.hotel.getCode())
		data.getRoomNumber() == this.room.getNumber()
		data.getRoomType().equals(this.room.getType().name())
		data.getArrival() == this.booking.getArrival()
		data.getDeparture() == this.booking.getDeparture()
		data.getPrice() == this.booking.getPrice()
	}

	@Unroll('invalid #reference')
	def "invalid arguments"() {
		when:
		HotelInterface.getRoomBookingData(reference)

		then:
		thrown(HotelException)

		where:
		reference << [null, "", "XPTO"]
	}
}
