package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Unroll

class InvoiceConstructorSpockTest extends SpockRollbackTestAbstractClass {
	private static final String SELLER_NIF='123456789'
	private static final String BUYER_NIF='987654321'
	private static final String FOOD='FOOD'
	private static final int VALUE=16
	private static final int TAX=23
	@Shared private final LocalDate date=new LocalDate(2018,02,13)
	@Shared private Seller seller
	@Shared private Buyer buyer
	@Shared private ItemType itemType

	@Override
	def populate4Test() {
		IRS irs = IRS.getIRSInstance()

		seller = new Seller(irs,SELLER_NIF,'José Vendido','Somewhere')

		buyer = new Buyer(irs,BUYER_NIF,'Manuel Comprado','Anywhere')

		itemType = new ItemType(irs,FOOD,TAX)
	}

	def 'success'() {
		when:
		Invoice invoice = new Invoice(VALUE, date, itemType, seller, buyer)

		then:
		invoice.getReference() != null
		16.0 == invoice.getValue()
		invoice.getDate() == date
		invoice.getItemType() == itemType
		invoice.getSeller() == seller
		invoice.getBuyer() == buyer
		3.68 == invoice.getIva()
		!invoice.isCancelled()
		seller.getInvoiceByReference(invoice.getReference()) == invoice
		buyer.getInvoiceByReference(invoice.getReference()) == invoice
	}

	@Unroll('testing exceptions: #value, #dt, #it, #sel, #buy')
	def 'testing exceptions'() {
		when:
		new Invoice(value, dt, it, sel, buy)

		then:
		thrown(TaxException)

		where:
		value  | dt   | it       | sel    | buy
		VALUE  | date | itemType | null   | buyer
		VALUE  | date | itemType | seller | null
		VALUE  | date | null     | seller | buyer
		0      | date | null     | seller | buyer
		-23.6f | date | null     | seller | buyer
		VALUE  | null | itemType | seller | buyer
		VALUE  | new LocalDate(1969,12,31) | itemType | seller | buyer
	}
}
