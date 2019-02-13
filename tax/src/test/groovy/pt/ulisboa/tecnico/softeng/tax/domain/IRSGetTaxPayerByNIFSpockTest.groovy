package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Unroll

class IRSGetTaxPayerByNIFSpockTest extends SpockRollbackTestAbstractClass {
	private static final String SELLER_NIF='123456789'
	private static final String BUYER_NIF='987654321'
	private IRS irs

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()

		new Seller(irs, SELLER_NIF,'José Vendido','Somewhere')

		new Buyer(irs, BUYER_NIF,'Manuel Comprado','Anywhere')
	}

	@Unroll('success #label')
	def 'success: '() {
		when:
		TaxPayer taxPayer = irs.getTaxPayerByNIF(nif)

		then:
		taxPayer != null
		taxPayer.getNif() == nif

		where:
		label                 | nif
		'buyer nif'           | BUYER_NIF
		'seller nif'          | SELLER_NIF
	}

	@Unroll('#label')
	def 'test: '() {
		when:
		TaxPayer taxPayer=irs.getTaxPayerByNIF('122456789')

		then:
		taxPayer == null

		where:
		label                | nif
		'null nif'           | null
		'empty nif'          | ''
		'nif does not exist' | '122456789'
	}

}
