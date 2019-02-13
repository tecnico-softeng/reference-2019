package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Unroll

import static org.junit.Assert.fail
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class ItemTypeConstructorSpockTest extends SpockRollbackTestAbstractClass {
	private static final String CAR = 'CAR'
	private static final int TAX = 23
	IRS irs

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()
	}

	def 'success'() {
		given:
		IRS irs = IRS.getIRSInstance()

		when:
		ItemType itemType = new ItemType(irs,CAR,TAX)

		then:
		itemType.getName() == CAR
		itemType.getTax() == TAX
		IRS.getIRSInstance().getItemTypeByName(CAR) != null
		irs.getItemTypeByName(CAR) == itemType
	}

	def 'unique name'() {
		given: "an item type"
		ItemType itemType = new ItemType(irs,CAR,TAX)

		when: "another item type is created with the same name"
		itemType = new ItemType(irs,CAR,TAX)

		then: "an exception is thrown"
		def error = thrown(TaxException)
		IRS.getIRSInstance().getItemTypeByName(CAR) == itemType
	}

	@Unroll('#label')
	def 'test: '() {
		when:
		new ItemType(irs, name, tax)

		then:
		thrown(TaxException)

		where:
		label                | name | tax
		'null item type'     | null | TAX
		'empty item type'    | ''   | TAX
		'negative item type' | CAR  | -34
	}

}
