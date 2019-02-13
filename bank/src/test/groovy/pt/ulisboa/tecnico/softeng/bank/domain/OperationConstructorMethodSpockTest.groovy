package pt.ulisboa.tecnico.softeng.bank.domain

import spock.lang.Shared
import spock.lang.Specification
import org.junit.Assert
import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class OperationConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def bank
	@Shared def account

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		Client client = new Client(bank,'António')
		account = new Account(bank,client)
	}

	def 'success'() {
		when:
		Operation operation=new Operation(Type.DEPOSIT, account,1000)

		then:
		operation.getReference().startsWith(bank.getCode())
		operation.getReference().length() > Bank.CODE_SIZE
		operation.getType() == Type.DEPOSIT
		operation.getAccount() == account
		1000 == operation.getValue()
		operation.getTime() != null
		bank.getOperation(operation.getReference()) == operation
	}


	@Unroll('operation: #type, #acc, #value')
	def 'exception'() {
		when: 'when creating an invalid operation'
		new Operation(null,this.account,1000)

		then: 'throw an exception'
		thrown(BankException)

		where:
		type  | acc | value
		null          | account | 1000
		Type.WITHDRAW | null    | 1000
		Type.DEPOSIT  | account | 0
		Type.WITHDRAW | null    | -1000
	}

	def 'one amount'() {
		given:
		Operation operation=new Operation(Type.DEPOSIT, account,1)

		expect:
		bank.getOperation(operation.getReference()) == operation
	}
}
