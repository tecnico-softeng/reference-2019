package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

class TaxPayerGetTaxesPerYearMethodsSpockTest extends SpockRollbackTestAbstractClass {
	private static final String SELLER_NIF = '123456788'
	private static final String BUYER_NIF = '987654311'
	private static final String FOOD = 'FOOD'
	private static final int TAX = 10
	private final LocalDate date = new LocalDate(2018, 02, 13)
	private Seller seller
	private Buyer buyer
	private ItemType itemType

	@Override
	def populate4Test() {
		IRS irs=IRS.getIRSInstance()

		seller = new Seller(irs, SELLER_NIF, 'José Vendido', 'Somewhere')

		buyer = new Buyer(irs, BUYER_NIF, 'Manuel Comprado', 'Anywhere')

		itemType = new ItemType(irs, FOOD, TAX)
	}

	def 'success'() {
		given:
		new Invoice(100, new LocalDate(2017, 12, 12), itemType, seller, buyer)
		new Invoice(100, date, itemType, seller, buyer)
		new Invoice(100, date, itemType, seller, buyer)
		new Invoice(50, date, itemType, seller, buyer)

		when:
		Map<Integer, Double> toPay=seller.getToPayPerYear()

		then:
		toPay.keySet().size() == 2
		10.0 == toPay.get(2017)
		25.0 == toPay.get(2018)
		Map<Integer, Double> taxReturn=buyer.getTaxReturnPerYear()

		taxReturn.keySet().size() == 2
		0.5 == taxReturn.get(2017)
		1.25 == taxReturn.get(2018)
	}

	def 'success empty'() {
		when:
		Map<Integer, Double> toPay=seller.getToPayPerYear()

		then:
		toPay.keySet().size() == 0
		Map<Integer, Double> taxReturn=buyer.getTaxReturnPerYear()
		taxReturn.keySet().size() == 0
	}

}
