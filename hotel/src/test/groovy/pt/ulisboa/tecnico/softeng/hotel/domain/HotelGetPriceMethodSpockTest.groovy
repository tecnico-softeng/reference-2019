package pt.ulisboa.tecnico.softeng.hotel.domain

import static org.junit.Assert.*

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class HotelGetPriceMethodSpockTest extends SpockRollbackTestAbstractClass {
	private final double PRICE_SINGLE = 20.0
	private final double PRICE_DOUBLE = 30.0
	private Hotel hotel

	@Override
	def populate4Test() {
	}

	def "price single"() {
		given: "a hotel"
		this.hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", PRICE_SINGLE, PRICE_DOUBLE)

		expect:
		this.hotel.getPrice(Room.Type.SINGLE) == PRICE_SINGLE
	}

	def "price double"() {
		given: "a hotel"
		this.hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", PRICE_SINGLE, PRICE_DOUBLE)

		expect:
		this.hotel.getPrice(Room.Type.DOUBLE) == PRICE_DOUBLE
	}

	def "incorrect input"() {
		given: "a hotel"
		this.hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", PRICE_SINGLE, PRICE_DOUBLE)

		when:
		this.hotel.getPrice(null)

		then: "a HotelException"
		def error = thrown(HotelException)
	}
}
