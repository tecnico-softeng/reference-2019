package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException
import spock.lang.Shared
import spock.lang.Unroll

class AdventureConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def broker
	@Shared def client17
	@Shared def client18
	@Shared def client20
	@Shared def client100

	@Override
	def populate4Test() {
		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
		client17 = new Client(broker, CLIENT_IBAN, CLIENT_NIF + 17, DRIVING_LICENSE + 17, 17)
		client20 = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
	}

	@Unroll('success: #begin, #end, #age, #margin')
	def 'success 18 20 and 100'() {
		when: 'an adventure is created'
		def client = new Client(broker, iban, nif, dl, age)
		def adventure = new Adventure(broker, begin, end, client, margin)

		then: 'all its attributes are correctly set'
		adventure.getBroker() == broker
		with(adventure) {
			getBroker() == broker
			getBegin() == begin
			getEnd() == end
			getClient() == client
			getMargin() == margin
			getAge() == age
			getIban().equals(iban)

			getPaymentConfirmation() == null
			getActivityConfirmation() == null
			getRoomConfirmation() == null
		}
		broker.getAdventureSet().contains(adventure)

		where:
		begin | end   | margin | iban            | nif             | dl                   | age
		BEGIN | END   | MARGIN | CLIENT_IBAN + 1 | CLIENT_NIF + 10 | DRIVING_LICENSE + 10 | AGE
		BEGIN | END   | MARGIN | CLIENT_IBAN + 2 | CLIENT_NIF + 11 | DRIVING_LICENSE + 11 | 18
		BEGIN | END   | MARGIN | CLIENT_IBAN + 3 | CLIENT_NIF + 13 | DRIVING_LICENSE + 13 | 100
	}

	@Unroll('#label')
	def 'invalid arguments'() {
		when: 'an adventure is created with invalid arguments'
		new Adventure(broker, begin, end, client, margin)

		then: 'an exception is thrown'
		thrown(BrokerException)

		where:
		broker | begin | end                | client   | margin | label
		broker | BEGIN | END                | client17 | MARGIN | 'client is 17 years old'
		broker | BEGIN | END                | client20 | 0      | 'margin is zero'
		broker | BEGIN | END                | client20 | -100   | 'margin is negative'
		broker | BEGIN | END                | null     | MARGIN | 'client is null'
	}

}
