package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Unroll

import static org.junit.Assert.fail
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class BuyerConstructorSpockTest extends SpockRollbackTestAbstractClass {
	private static final String ADDRESS = 'Somewhere'
	private static final String NAME = 'José Vendido'
	private static final String NIF = '123456789'
	IRS irs

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()
	}

	def 'success'() {
		when:
		Buyer buyer = new Buyer(irs, NIF, NAME, ADDRESS)

		then:
		buyer.getNif() == NIF
		buyer.getName() == NAME
		buyer.getAddress() == ADDRESS
		IRS.getIRSInstance().getTaxPayerByNIF(NIF) == buyer
	}

	def 'unique nif'() {
		given: "a buyer"
		Buyer seller = new Buyer(irs, NIF, NAME, ADDRESS)

		when: "another buyer with the same info"
		new Buyer(irs, NIF, NAME, ADDRESS)

		then: "an exception is thrown"
		def error = thrown(TaxException)
		IRS.getIRSInstance().getTaxPayerByNIF(NIF) == seller
	}

	@Unroll('testing exceptions: #nif, #name, #address')
	def 'testing exceptions'() {
		when:
		new Buyer(irs, nif, name, address)

		then:
		thrown(TaxException)

		where:
		nif        | name | address
		null       | NAME | ADDRESS
		''         | NAME | ADDRESS
		'12345678' | NAME | ADDRESS
		NIF        | null | ADDRESS
		NIF        | ''   | ADDRESS
		NIF        | NAME | null
		NIF        | NAME | ''
	}
}
