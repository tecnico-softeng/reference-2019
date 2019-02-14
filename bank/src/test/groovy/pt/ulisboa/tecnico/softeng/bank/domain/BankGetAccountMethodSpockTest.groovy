package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class BankGetAccountMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def client

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		client = new Client(bank,'António')
	}

	def 'success'() {
		given:
		def account = new Account(bank, client)

		expect:
		bank.getAccount(account.getIBAN()) == account
	}

	@Unroll('getting account: #label')
	def 'exception'() {
		when: 'when getting an invalid account'
		bank.getAccount(acc)

		then: 'throw an exception'
		thrown(BankException)

		where:
		acc | label
		null  | 'null iban'
		''    | 'empty iban'
		'   ' | 'blank iban'
	}

	def 'empty set of accounts'() {
		expect:
		bank.getAccount('XPTO') == null
	}

	def 'several accounts but do no match'() {
		given:
		new Account(bank, client)
		and:
		new Account(bank, client)

		expect:
		bank.getAccount('XPTO') == null
	}
}
