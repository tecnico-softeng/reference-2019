package pt.ulisboa.tecnico.softeng.bank.domain

import spock.lang.Shared
import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class BankConstructorSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def BANK_CODE='BK01'
	@Shared def BANK_NAME='Money'

	@Override
	def populate4Test() { }

	def 'success'() {
		when:
		Bank bank = new Bank(BANK_NAME,BANK_CODE)

		then:
		bank.getName() == BANK_NAME
		bank.getCode() == BANK_CODE
		FenixFramework.getDomainRoot().getBankSet().size() == 1
		bank.getAccountSet().size() == 0
		bank.getClientSet().size() == 0
	}

	@Unroll('creating bank: #label')
	def 'exception'() {
		when: 'when creating an invalid bank'
		new Bank(null,BANK_CODE)

		then: 'throw an exception'
		thrown(BankException)

		where:
		name | code
		null  | BANK_CODE
		'   ' | BANK_CODE
		BANK_NAME | null
		BANK_NAME | '    '
		BANK_NAME | 'BK0'
		BANK_NAME | 'BK011'
	}

	def 'not unique code'() {
		given:
		new Bank(BANK_NAME,BANK_CODE)

		when:
		new Bank(BANK_NAME,BANK_CODE)

		then:
		def error = thrown(BankException)
		FenixFramework.getDomainRoot().getBankSet().size() == 1
	}

}
