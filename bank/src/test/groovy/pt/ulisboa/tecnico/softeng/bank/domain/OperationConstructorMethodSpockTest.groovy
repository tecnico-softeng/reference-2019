package pt.ulisboa.tecnico.softeng.bank.domain

import spock.lang.Shared
import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class OperationConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def bank
	@Shared def account

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		def client = new Client(bank,'António')
		account = new Account(bank,client)
	}

	def 'success'() {
		when: 'when creating an operation'
		def operation = new Operation(Type.DEPOSIT, account,1000)

		then: 'the object should hold the proper values'
		with(operation) {
			getReference().startsWith(bank.getCode())
			getReference().length() > Bank.CODE_SIZE
			getType() == Type.DEPOSIT
			getAccount() == account
			1000 == getValue()
			getTime() != null
			bank.getOperation(getReference()) == operation
		}
	}


	@Unroll('operation: #type, #acc, #value')
	def 'exception'() {
		when: 'when creating an invalid operation'
		new Operation(type, acc, value)

		then: 'throw an exception'
		thrown(BankException)

		where:
		type          | acc     | value
		null          | account | 1000
		Type.WITHDRAW | null    | 1000
		Type.DEPOSIT  | account | 0
		Type.WITHDRAW | null    | -1000
	}

	def 'one amount'() {
		when:
		def operation = new Operation(Type.DEPOSIT, account,1)

		then:
		bank.getOperation(operation.getReference()) == operation
	}
}
