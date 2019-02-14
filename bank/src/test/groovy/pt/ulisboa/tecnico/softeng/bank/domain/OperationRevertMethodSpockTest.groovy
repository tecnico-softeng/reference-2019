package pt.ulisboa.tecnico.softeng.bank.domain
import spock.lang.Specification

class OperationRevertMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def account

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		def client = new Client(bank,'António')
		account = new Account(bank,client)
	}

	def 'revert deposit'() {
		given: 'a deposit'
		def reference = account.deposit(100).getReference()
		def operation = bank.getOperation(reference)

		when: 'when reverting the deposit'
		def newReference = operation.revert()

		then: 'account should have have deposit as before'
		0 == account.getBalance()
		bank.getOperation(newReference) != null
		bank.getOperation(reference) != null
	}

	def 'revert withdraw'() {
		given: 'given a deposit'
		account.deposit(1000)

		when: 'when reverting the operation'
		def reference=this.account.withdraw(100).getReference()
		def operation=this.bank.getOperation(reference)
		def newReference=operation.revert()

		then: 'account should have have deposit as before'
		1000 == this.account.getBalance()
		this.bank.getOperation(newReference) != null
		this.bank.getOperation(reference) != null
	}

}
