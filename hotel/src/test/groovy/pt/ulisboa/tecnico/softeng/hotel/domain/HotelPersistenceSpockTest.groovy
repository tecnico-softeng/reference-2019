package pt.ulisboa.tecnico.softeng.hotel.domain

import static org.junit.Assert.*

import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type

class HotelPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
	private static final String HOTEL_NIF = "123456789"
	private static final String HOTEL_IBAN = "IBAN"
	private static final String HOTEL_NAME = "Berlin Plaza"
	private final static String HOTEL_CODE = "H123456"
	private static final String ROOM_NUMBER = "01"
	private static final String CLIENT_NIF = "123458789"
	private static final String CLIENT_IBAN = "IBANC"

	private final LocalDate arrival = new LocalDate(2017, 12, 15)
	private final LocalDate departure = new LocalDate(2017, 12, 19)

	@Override
	def whenCreateInDatabase() {
		Hotel hotel = new Hotel(HOTEL_CODE, HOTEL_NAME, HOTEL_NIF, HOTEL_IBAN, 10.0, 20.0)
		new Room(hotel, ROOM_NUMBER, Type.DOUBLE)
		Hotel.reserveRoom(Type.DOUBLE, this.arrival, this.departure, CLIENT_NIF, CLIENT_IBAN)
	}

	@Override
	def thenAssert() {
		assert FenixFramework.getDomainRoot().getHotelSet().size() == 1

		List<Hotel> hotels = new ArrayList<>(FenixFramework.getDomainRoot().getHotelSet())
		Hotel hotel = hotels.get(0)

		assert hotel.getName().equals(HOTEL_NAME)
		assert hotel.getCode().equals(HOTEL_CODE)
		assert hotel.getIban().equals(HOTEL_IBAN)
		assert hotel.getNif().equals(HOTEL_NIF)
		assert hotel.getPriceSingle() == 10.0
		assert hotel.getPriceDouble() == 20.0
		assert hotel.getRoomSet().size() == 1
		Processor processor = hotel.getProcessor();
		assert processor != null
		assert processor.getBookingSet().size() == 1

		List<Room> rooms = new ArrayList<>(hotel.getRoomSet());
		Room room = rooms.get(0);

		assert room.getNumber().equals(ROOM_NUMBER)
		assert room.getType() == Type.DOUBLE
		assert room.getBookingSet().size() == 1

		List<Booking> bookings = new ArrayList<>(room.getBookingSet());
		Booking booking = bookings.get(0);

		assert booking.getReference() != null
		assert booking.getArrival() == arrival
		assert booking.getDeparture() == departure
		assert booking.getBuyerIban().equals(CLIENT_IBAN)
		assert booking.getBuyerNif().equals(CLIENT_NIF)
		assert booking.getProviderNif().equals(HOTEL_NIF)
		assert booking.getPrice() == 80.0
		assert booking.getRoom() == room
		assert booking.getTime() != null
		assert booking.getProcessor() != null
	}

	@Override
	def deleteFromDatabase() {
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			hotel.delete()
		}
	}
}
