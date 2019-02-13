package pt.ulisboa.tecnico.softeng.hotel.domain

import static org.junit.Assert.*

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import spock.lang.Unroll

class HotelHasVacancyMethodSpockTest extends SpockRollbackTestAbstractClass {
	private static final String NIF_HOTEL = "123456700"
	private static final String NIF_BUYER = "123456789"
	private static final String IBAN_BUYER = "IBAN_BUYER"
	private static final LocalDate ARRIVAL = new LocalDate(2016, 12, 19)
	private static final LocalDate DEPARTURE = new LocalDate(2016, 12, 21)

	private Hotel hotel
	private Room room

	@Override
	def populate4Test() {
		this.hotel = new Hotel("XPTO123", "Paris", NIF_HOTEL, "IBAN", 20.0, 30.0)
		this.room = new Room(this.hotel, "01", Type.DOUBLE)
	}

	def "has vacancy"() {
		when: "it has vacancy"
		Room room = this.hotel.hasVacancy(Type.DOUBLE, ARRIVAL, DEPARTURE)

		then: "it returns a room"
		room != null
		room.getNumber().equals("01")
	}

	def "no vacancy"() {
		given: "a booking"
		this.room.reserve(Type.DOUBLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER);

		when: "looking for a vacancy in the same period"
		room = this.hotel.hasVacancy(Type.DOUBLE, ARRIVAL, DEPARTURE)

		then: "it does have an available room"
		room == null
	}

	def "no vacancy empty room set"() {
		given: "an hotel without rooms"
		Hotel otherHotel = new Hotel("XPTO124", "Paris Germain", "NIF2", "IBAN", 25.0, 35.0)

		when: "looking for a vancancy"
		room = otherHotel.hasVacancy(Type.DOUBLE, ARRIVAL, DEPARTURE)

		then: "it does have an available room"
		room == null
	}

	@Unroll('one of the following arguments is invalid: #type | #arrival | #departure')
	def "incorrect arguments"() {
		when: "looking for a vacancy"
		this.hotel.hasVacancy(type, arrival, departure)

		then: "an HotelException is thrown"
		def error = thrown(HotelException)

		where:
		type | arrival | departure
		null | ARRIVAL | DEPARTURE
		Type.DOUBLE | null | DEPARTURE
		Type.DOUBLE | ARRIVAL | null
	}
}
