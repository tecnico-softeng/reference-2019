package pt.ulisboa.tecnico.softeng.bank.domain

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
		given: 'a deposit operation'
		def reference = account.deposit(100).getReference()
		def operation = bank.getOperation(reference)

		when: 'when reverting the deposit'
		def newReference = operation.revert()

		then: 'account should have have balance as before'
		account.getBalance() == 0
		and: 'a new operation is added'
		bank.getOperation(newReference) != null
		and: 'the initial operation is not removed'
		bank.getOperation(reference) != null
	}

	def 'revert withdraw'() {
		given: 'given a deposit operation'
		account.deposit(1000)
		def reference = this.account.withdraw(100).getReference()
		def operation = this.bank.getOperation(reference)

		when: 'when reverting the operation'
		def newReference = operation.revert()

		then: 'account should have the balance as before'
		1000 == this.account.getBalance()
		and: 'a new operation is added'
		this.bank.getOperation(newReference) != null
		and: 'the initial operation is not removed'
		this.bank.getOperation(reference) != null
	}
}
