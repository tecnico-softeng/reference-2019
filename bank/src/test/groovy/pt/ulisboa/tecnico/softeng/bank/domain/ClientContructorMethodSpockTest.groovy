package pt.ulisboa.tecnico.softeng.bank.domain

import spock.lang.Shared
import spock.lang.Specification
import org.junit.Assert
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class ClientContructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def CLIENT_NAME='António'
	@Shared def bank

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
	}

	def 'success'() {
		when:
		Client client = new Client(bank, CLIENT_NAME)

		then:
		client.getName() == CLIENT_NAME
		client.getID().length() >= 1
		bank.getClientSet().contains(client)
	}

	@Unroll('creating client: #label')
	def 'exception'() {
		when: 'when creating an invalid client'
		new Client(bnk, name)

		then: 'throw an exception'
		thrown(BankException)

		where:
		bnk   | name
		null  | CLIENT_NAME
		bank  | null
		bank  | '   '
		bank  | ''
	}
}
