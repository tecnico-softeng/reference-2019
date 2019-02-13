package pt.ulisboa.tecnico.softeng.hotel.domain

import static org.junit.Assert.*

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import spock.lang.Unroll

class HotelSetPriceMethodSpockTest extends SpockRollbackTestAbstractClass {
	private final double PRICE = 25.0

	private Hotel hotel

	@Override
	def populate4Test() {
		this.hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", PRICE + 5.0, PRICE + 10.0)
	}

	def "success single"() {
		when:
		this.hotel.setPrice(Room.Type.SINGLE, PRICE);

		then:
		this.hotel.getPrice(Room.Type.SINGLE) == this.PRICE
		this.hotel.getPrice(Room.Type.DOUBLE) == this.PRICE + 10.0
	}

	def "success double"() {
		when:
		this.hotel.setPrice(Room.Type.DOUBLE, PRICE);

		then:
		this.hotel.getPrice(Room.Type.DOUBLE) == this.PRICE
		this.hotel.getPrice(Room.Type.SINGLE) == this.PRICE + 5.0
	}

	@Unroll('one of the following argument is invalid: #type | #price')
	def "incorret arguments"() {
		when:
		this.hotel.setPrice(type, price);

		then:
		def error = thrown(HotelException)

		where:
		type | price
		Room.Type.SINGLE | -1.0
		Room.Type.DOUBLE | -1.0
	}
}
