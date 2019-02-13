package pt.ulisboa.tecnico.softeng.bank.domain
import spock.lang.Specification
import org.junit.Assert
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountWithdrawMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def account

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		Client client = new Client(bank,'António')

		account = new Account(bank,client)

		account.deposit(100)
	}

	def 'success'() {
		when:
		String reference=this.account.withdraw(40).getReference()

		then:
		60 == this.account.getBalance()
		Operation operation=this.bank.getOperation(reference)
		operation != null
		operation.getType() == Operation.Type.WITHDRAW
		operation.getAccount() == this.account
		40 == operation.getValue()
	}

	@Unroll('Withdraw: #label')
	def 'throwing exception'() {
		when: 'when withdrawing an invalid amount'
		account.withdraw(amnt)

		then: 'throw an exception'
		thrown(BankException)

		where:
		amnt | label
		0    | 'zero amount'
		-20  | 'negative amount'
		101  | 'equal to balance plus one'
		150  | 'more than balance'
	}

	@Unroll('Withdraw: #label')
	def 'all good'() {
		when: 'when withdrawing an invalid amount'
		account.deposit(amnt)

		then: 'success'
		true

		where:
		amnt | label
		1    | 'one amount'
		100  | 'equal to balance'
	}
}
