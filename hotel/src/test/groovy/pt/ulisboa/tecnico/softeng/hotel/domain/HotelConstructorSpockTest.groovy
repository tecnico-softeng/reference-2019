package pt.ulisboa.tecnico.softeng.hotel.domain

import static org.junit.Assert.*

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import spock.lang.Unroll

class HotelConstructorSpockTest extends SpockRollbackTestAbstractClass {
	private static final String IBAN = "IBAN"
	private static final String NIF = "NIF"

	private static final String HOTEL_NAME = "Londres"
	private static final String HOTEL_CODE = "XPTO123"

	private static final double PRICE_SINGLE = 20.0
	private static final double PRICE_DOUBLE = 30.0

	@Override
	def populate4Test() {
	}

	def "success"() {
		when: "an hotel is created"
		Hotel hotel = new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

		then: "all information is correctly set"
		hotel.getName().equals(HOTEL_NAME)
		hotel.getCode().length() == Hotel.CODE_SIZE
		hotel.getRoomSet().size() == 0
		FenixFramework.getDomainRoot().getHotelSet().size() == 1
		hotel.getPrice(Room.Type.SINGLE) == PRICE_SINGLE
		hotel.getPrice(Room.Type.DOUBLE) == PRICE_DOUBLE
	}

	@Unroll('one of the following arguments is not allowed: #code | #name | #nif | #iban | #priceSingle| #priceDouble')
	def "incorrect input parameters"() {
		when: "an hotel is created with an incorrect input parameter"
		new Hotel(code, name, nif, iban, priceSingle, priceDouble)

		then: "an HotelException is thrown"
		def error = thrown(HotelException)

		where:
		code | name | nif | iban | priceSingle| priceDouble
		null | HOTEL_NAME | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		"      " | HOTEL_NAME | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		"" | HOTEL_NAME | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		HOTEL_CODE | null | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		HOTEL_CODE | "  " | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		HOTEL_CODE | "" | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		"123456" | HOTEL_NAME | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		"12345678" | HOTEL_NAME | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		HOTEL_CODE | HOTEL_NAME | NIF | IBAN | -1.0 | PRICE_DOUBLE
		HOTEL_CODE | HOTEL_NAME | NIF | IBAN | PRICE_SINGLE | -1.0
	}

	def "code not unique"() {
		given: "an hotel"
		new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

		when: "another hotel is created with the same code"
		new Hotel(HOTEL_CODE, HOTEL_NAME + " City", NIF+ "1", IBAN, PRICE_SINGLE, PRICE_DOUBLE)

		then: "an HotelException is thrown"
		def error = thrown(HotelException)
		FenixFramework.getDomainRoot().getHotelSet().size() == 1
	}

	def "nif not unique"() {
		given: "an hotel"
		new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

		when: "another hotel is created with the same nif"
		new Hotel("XPTO124", HOTEL_NAME + "_New", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

		then: "an HotelException is thrown"
		def error = thrown(HotelException)
		FenixFramework.getDomainRoot().getHotelSet().size() == 1
	}
}
