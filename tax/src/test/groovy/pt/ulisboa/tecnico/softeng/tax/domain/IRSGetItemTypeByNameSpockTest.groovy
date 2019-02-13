package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Unroll

class IRSGetItemTypeByNameSpockTest extends SpockRollbackTestAbstractClass {
	private static final String FOOD = 'FOOD'
	private static final int VALUE = 16
	private IRS irs

	@Override
	def populate4Test() {
		irs=IRS.getIRSInstance()

		new ItemType(irs,FOOD,VALUE)
	}

	@Unroll('#label')
	def 'test: '() {
		when:
		ItemType itemType=irs.getItemTypeByName(name)

		then:
		itemType == null

		where:
		label                 | name
		'null name'           | null
		'empty name'          | ' '
		'does not exist name' | 'CAR'
	}
}
