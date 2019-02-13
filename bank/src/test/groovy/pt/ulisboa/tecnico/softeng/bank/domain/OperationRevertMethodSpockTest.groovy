package pt.ulisboa.tecnico.softeng.bank.domain
import spock.lang.Specification

class OperationRevertMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def account

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		Client client = new Client(bank,'António')
		account = new Account(bank,client)
	}

	def 'revert deposit'() {
		when:
		String reference = account.deposit(100).getReference()
		Operation operation = bank.getOperation(reference)
		String newReference=operation.revert()

		then:
		0 == this.account.getBalance()
		this.bank.getOperation(newReference) != null
		this.bank.getOperation(reference) != null
	}

	def 'revert withdraw'() {
		when:
		account.deposit(1000)
		String reference=this.account.withdraw(100).getReference()
		Operation operation=this.bank.getOperation(reference)
		String newReference=operation.revert()

		then:
		1000 == this.account.getBalance()
		this.bank.getOperation(newReference) != null
		this.bank.getOperation(reference) != null
	}

}
