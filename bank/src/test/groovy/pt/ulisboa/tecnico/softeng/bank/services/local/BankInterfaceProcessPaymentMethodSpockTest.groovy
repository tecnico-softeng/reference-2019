package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import spock.lang.Shared
import spock.lang.Unroll

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.Operation
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData

class BankInterfaceProcessPaymentMethodSpockTest extends SpockRollbackTestAbstractClass {
	def TRANSACTION_SOURCE='ADVENTURE'
	def TRANSACTION_REFERENCE='REFERENCE'
	def bank
	def account
	@Shared def iban

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		def client = new Client(bank,'António')
		account = new Account(bank, client)
		iban = account.getIBAN()
		account.deposit(500)
	}

	def 'success'() {
		given:
		account.getIBAN()

		when:
		String newReference=BankInterface.processPayment(new BankOperationData(iban,100,TRANSACTION_SOURCE,TRANSACTION_REFERENCE))

		then:
		newReference != null
		newReference.startsWith('BK01')
		bank.getOperation(newReference) != null
		bank.getOperation(newReference).getType() == Operation.Type.WITHDRAW
	}

	def 'success two banks'() {
		given:
		Bank otherBank = new Bank('Money','BK02')
		Client otherClient=new Client(otherBank,'Manuel')
		Account otherAccount=new Account(otherBank,otherClient)
		String otherIban = otherAccount.getIBAN()
		otherAccount.deposit(1000)

		when:
		BankInterface.processPayment(new BankOperationData(otherIban,100,TRANSACTION_SOURCE,TRANSACTION_REFERENCE))

		then:
		900.0 == otherAccount.getBalance()
		BankInterface.processPayment(new BankOperationData(iban,100,TRANSACTION_SOURCE,TRANSACTION_REFERENCE + 'PLUS'))
		400 == this.account.getBalance()
	}

	def 'redo an already payed'() {
		given:
		this.account.getIBAN()

		when:
		String firstReference=BankInterface.processPayment(new BankOperationData(iban,100,TRANSACTION_SOURCE,TRANSACTION_REFERENCE))
		String secondReference=BankInterface.processPayment(new BankOperationData(iban,100,TRANSACTION_SOURCE,TRANSACTION_REFERENCE))

		then:
		secondReference == firstReference
		400.0 == account.getBalance()
	}

	def 'one amount'() {
		when:
		BankInterface.processPayment(new BankOperationData(this.iban, 1, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then:
		499.0 == account.getBalance()
	}


	@Unroll('bank operation data, process payment: #ibn, #val')
	def 'problem process payment'() {
		when:
		BankInterface.processPayment(
				new BankOperationData(ibn, val, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then:
		thrown(BankException)

		where:
		ibn     | val
		null    | 100
		'  '    | 100
 		iban    | 0
		'other' | 0
	}



	@Unroll('process payment: #ibn, #val')
	def 'problem process payment in bank'() {
		when:
		Bank.processPayment(ibn,val)


		then:
		thrown(BankException)

		where:
		ibn     | val
		null    | 10
		''      | 10
		'XPTO'  | 10
		'other' | 0
	}

	def 'no banks'() {
		given:
		FenixFramework.getDomainRoot().getBankSet().clear()

		when:
		Bank.processPayment(this.account.getIBAN(),10)

		then:
		thrown(BankException)
	}

}
