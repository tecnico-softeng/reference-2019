package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ist.fenixframework.FenixFramework

class BankPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
	def BANK_NAME = 'Money'
	def BANK_CODE = 'BK01'
	def CLIENT_NAME = 'João dos Anzóis'

	@Override
	def whenCreateInDatabase() {
		Bank bank = new Bank(BANK_NAME,BANK_CODE)
		Client client = new Client(bank,CLIENT_NAME)
		Account account = new Account(bank,client)
		account.deposit(100)
	}

	@Override
	def thenAssert() {
		assert 1 == FenixFramework.getDomainRoot().getBankSet().size()

		List<Bank> banks=new ArrayList<>(FenixFramework.getDomainRoot().getBankSet())
		Bank bank=banks.get(0)

		assert BANK_NAME == bank.getName()
		assert BANK_CODE == bank.getCode()
		assert 1 == bank.getClientSet().size()
		assert 1 == bank.getAccountSet().size()
		assert 1 == bank.getOperationSet().size()

		List<Client> clients=new ArrayList<>(bank.getClientSet())
		Client client=clients.get(0)

		assert CLIENT_NAME == client.getName()

		List<Account> accounts=new ArrayList<>(bank.getAccountSet())
		Account account=accounts.get(0)

		assert client == account.getClient()
		assert null != account.getIBAN()
		assert 100 == account.getBalance()

		List<Operation> operations=new ArrayList<>(bank.getOperationSet())
		Operation operation=operations.get(0)

		assert Operation.Type.DEPOSIT == operation.getType()
		assert 100 == operation.getValue()
		assert null != operation.getReference()
		assert null != operation.getTime()
	}

	@Override
	def deleteFromDatabase() {
		for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
			bank.delete()
		}
	}
}
