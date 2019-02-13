package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Unroll

class IRSCancelInvoiceMethodSpockTest extends SpockRollbackTestAbstractClass {
	private static final String SELLER_NIF='123456789'
	private static final String BUYER_NIF='987654321'
	private static final String FOOD='FOOD'
	private static final int VALUE=16
	private final LocalDate date=new LocalDate(2018,02,13)
	private IRS irs
	private String reference
	Invoice invoice

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()

		Seller seller = new Seller(irs,SELLER_NIF,'José Vendido','Somewhere')

		Buyer buyer = new Buyer(irs,BUYER_NIF,'Manuel Comprado','Anywhere')

		ItemType itemType = new ItemType(irs,FOOD,VALUE)

		invoice = new Invoice(30.0, date, itemType, seller, buyer)

		reference = invoice.getReference()
	}

	def 'success'() {
		when:
		IRS.cancelInvoice(reference)

		then:
		invoice.isCancelled()
	}

	@Unroll('#label')
	def 'test: '() {
		when:
		IRS.cancelInvoice(ref)

		then:
		thrown(TaxException)

		where:
		label                      | ref
		'null reference'           | null
		'empty reference'          | ' '
		'reference does not exist' | 'XXXXXXXX'
	}

}
