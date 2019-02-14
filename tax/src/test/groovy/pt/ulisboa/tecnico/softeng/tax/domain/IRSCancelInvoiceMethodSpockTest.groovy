package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Unroll

class IRSCancelInvoiceMethodSpockTest extends SpockRollbackTestAbstractClass {
	def SELLER_NIF = '123456789'
	def BUYER_NIF = '987654321'
	def FOOD = 'FOOD'
	def VALUE = 16
	def date = new LocalDate(2018,02,13)
	def irs
	def reference
	def invoice

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()

		def seller = new Seller(irs,SELLER_NIF,'José Vendido','Somewhere')
		def buyer = new Buyer(irs,BUYER_NIF,'Manuel Comprado','Anywhere')
		def itemType = new ItemType(irs,FOOD,VALUE)

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
