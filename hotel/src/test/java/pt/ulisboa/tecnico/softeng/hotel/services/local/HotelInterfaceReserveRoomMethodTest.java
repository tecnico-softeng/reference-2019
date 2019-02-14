package pt.ulisboa.tecnico.softeng.hotel.services.local;

import static junit.framework.TestCase.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.RollbackTestAbstractClass;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData;

public class HotelInterfaceReserveRoomMethodTest extends RollbackTestAbstractClass {
	private final LocalDate arrival = new LocalDate(2016, 12, 19);
	private final LocalDate departure = new LocalDate(2016, 12, 24);
	private Hotel hotel;
	private static final String NIF_HOTEL = "123456789";
	private static final String NIF_BUYER = "123456700";
	private static final String IBAN_BUYER = "IBAN_CUSTOMER";
	private static final String IBAN_HOTEL = "IBAN_HOTEL";
	private static final String ADVENTURE_ID = "ADVENTURE_ID";

	@Override
	public void populate4Test() {
		this.hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, IBAN_HOTEL, 20.0, 30.0);
		new Room(this.hotel, "01", Room.Type.SINGLE);
	}

	@Test
	public void success() {
		RestRoomBookingData bookingData = new RestRoomBookingData("SINGLE", this.arrival, this.departure, NIF_BUYER,
				IBAN_BUYER, ADVENTURE_ID);

		bookingData = HotelInterface.reserveRoom(bookingData);

		assertTrue(bookingData.getReference() != null);
		assertTrue(bookingData.getReference().startsWith("XPTO123"));
	}

	@Test(expected = HotelException.class)
	public void noHotels() {
		FenixFramework.getDomainRoot().getHotelSet().stream().forEach(h -> h.delete());

		RestRoomBookingData bookingData = new RestRoomBookingData("SINGLE", this.arrival, this.departure, NIF_BUYER,
				IBAN_BUYER, ADVENTURE_ID);

		HotelInterface.reserveRoom(bookingData);
	}

	@Test(expected = HotelException.class)
	public void noVacancy() {
		RestRoomBookingData bookingData = new RestRoomBookingData("SINGLE", this.arrival, new LocalDate(2016, 12, 25),
				NIF_BUYER, IBAN_BUYER, ADVENTURE_ID);

		HotelInterface.reserveRoom(bookingData);

		bookingData = new RestRoomBookingData("SINGLE", this.arrival, new LocalDate(2016, 12, 25), NIF_BUYER,
				IBAN_BUYER, ADVENTURE_ID + "1");

		HotelInterface.reserveRoom(bookingData);
	}

	@Test(expected = HotelException.class)
	public void noRooms() {
		this.hotel.getRoomSet().stream().forEach(r -> r.delete());

		RestRoomBookingData bookingData = new RestRoomBookingData("SINGLE", this.arrival, new LocalDate(2016, 12, 25),
				NIF_BUYER, IBAN_BUYER, ADVENTURE_ID);

		HotelInterface.reserveRoom(bookingData);
	}

}