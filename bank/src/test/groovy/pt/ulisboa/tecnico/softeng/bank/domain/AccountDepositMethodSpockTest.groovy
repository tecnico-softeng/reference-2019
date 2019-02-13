package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class AccountDepositMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def account

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		Client client = new Client(bank,'António')

		account = new Account(bank, client)
	}

	@Unroll('success deposit, #label: #amnt, #balance')
	def 'success'() {
		when:
		String reference = account.deposit(50).getReference()

		then:
		50 == account.getBalance()
		Operation operation = bank.getOperation(reference)
		operation != null
		operation.getType() == Operation.Type.DEPOSIT
		operation.getAccount() == account
		50 == operation.getValue()

		where:
		label              | amnt | balance
		'fifty'            | 50   | 50
		'one amount'       | 1    | 1
	}

	@Unroll('Deposit: #label')
	def 'throwing exception'() {
		when: 'when deposit an invalid amount'
		account.deposit(amnt)

		then: 'throw an exception'
		thrown(BankException)

		where:
		amnt | label
		0    | 'zero amount'
		-100 | 'negative amount'
	}


	def 'one amount'() {
		expect:
		account.deposit(1)
	}
}
