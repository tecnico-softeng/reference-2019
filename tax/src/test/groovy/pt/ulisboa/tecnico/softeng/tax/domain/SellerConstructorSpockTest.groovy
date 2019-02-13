package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Unroll

import static org.junit.Assert.fail
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class SellerConstructorSpockTest extends SpockRollbackTestAbstractClass {
	private static final String ADDRESS = 'Somewhere'
	private static final String NAME = 'José Vendido'
	private static final String NIF = '123456789'
	IRS irs

	@Override
	def populate4Test() {
		this.irs=IRS.getIRSInstance()
	}

	def 'success'() {
		when:
		Seller seller=new Seller(this.irs,NIF,NAME,ADDRESS)

		then:
		seller.getNif() == NIF
		seller.getName() == NAME
		seller.getAddress() == ADDRESS
		IRS.getIRSInstance().getTaxPayerByNIF(NIF) == seller
	}

	def 'unique nif'() {
		given: "a seller"
		Seller seller=new Seller(this.irs,NIF,NAME,ADDRESS)

		when: "another buyer with the same info"
		new Seller(this.irs,NIF,NAME,ADDRESS)

		then: "an exception is thrown"
		def error = thrown(TaxException)
		IRS.getIRSInstance().getTaxPayerByNIF(NIF) == seller
	}


	@Unroll('testing exceptions: #nif, #name, #address')
	def 'testing exceptions'() {
		when:
		new Seller(this.irs,nif, name, address)

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
