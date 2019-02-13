package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class SellerToPaySpockTest extends SpockRollbackTestAbstractClass {
	private static final String SELLER_NIF = '123456789'
	private static final String BUYER_NIF = '987654321'
	private static final String FOOD = 'FOOD'
	private static final int TAX = 10
	private final LocalDate date = new LocalDate(2018, 02, 13)
	private Seller seller
	private Buyer buyer
	private ItemType itemType

	@Override
	def populate4Test() {
		IRS irs = IRS.getIRSInstance()

		seller = new Seller(irs, SELLER_NIF, 'José Vendido', 'Somewhere')

		buyer = new Buyer(irs, BUYER_NIF, 'Manuel Comprado', 'Anywhere')

		itemType = new ItemType(irs, FOOD, TAX)
	}

	def 'success'() {
		given:
		new Invoice(100, date, itemType, seller, buyer)
		new Invoice(100, date, itemType, seller, buyer)
		new Invoice(50, date, itemType, seller, buyer)

		when:
		def value = seller.toPay(year)

		then:
		toPay  ==  value

		where:
		year | toPay
		2018 | 25.0
		2015 | 0

	}

	def 'no invoices'() {
		expect:
		def value  =  seller.toPay(2018)
		0.00f  ==  value
	}

	def 'before 1970'() {
		when:
		new Invoice(100, new LocalDate(1969, 02, 13), itemType, seller, buyer)
		new Invoice(50, new LocalDate(1969, 02, 13), itemType, seller, buyer)

		seller.toPay(1969)

		then:
		thrown(TaxException)
	}

	def 'equal 1970'() {
		when:
		new Invoice(100, new LocalDate(1970, 02, 13), itemType, seller, buyer)
		new Invoice(50, new LocalDate(1970, 02, 13), itemType, seller, buyer)

		def value = seller.toPay(1970)

		then:
		15.0  ==  value
	}

	def 'ignore cancelled'() {
		when:
		new Invoice(100, date, itemType, seller, buyer)
		Invoice invoice = new Invoice(100, date, itemType, seller, buyer)

		new Invoice(50, date, itemType, seller, buyer)
		invoice.cancel()

		def value = seller.toPay(2018)

		then:
		15.0  ==  value
	}

}
