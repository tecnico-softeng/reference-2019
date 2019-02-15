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
		client20 = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
		client100 = new Client(broker, CLIENT_IBAN + "3", OTHER_NIF + "3", DRIVING_LICENSE + "3", 100)
	}

	@Unroll('success: #begin, #end, #age, #margin')
	def 'success 18 20 and 100'() {
		when: 'an adventure is created'
		def adventure = new Adventure(broker, begin, end, client100, margin)

		then: 'all its attributes are correctly set'
		adventure.getBroker() == broker
		with(adventure) {
			getBroker() == broker
			getBegin() == begin
			getEnd() == end
			getClient() == client
			getMargin() == margin
			getAge() == age
			getIban().equals(CLIENT_IBAN)

			getPaymentConfirmation() == null
			getActivityConfirmation() == null
			getRoomConfirmation() == null
		}
		broker.getAdventureSet().contains(adventure)

		where:
		cli       | begin | end   | margin | age
		client18  | BEGIN | END   | MARGIN | 18
		client20  | BEGIN | END   | MARGIN | 20
		client100 | BEGIN | END   | MARGIN | 100
		client20  | BEGIN | BEGIN | MARGIN | 20
		client20  | BEGIN | END   | 1      | 20
	}

	@Unroll('#label')
	def 'invalid arguments'() {
		when: 'an adventure is created with invalid arguments'
		new Adventure(broker, begin, end, client, margin)

		then: 'an exception is thrown'
		thrown(BrokerException)

		where:
		broker | begin | end                | client   | margin | label
		null   | BEGIN | END                | client20 | MARGIN | 'broker is null'
		broker | null  | END                | client20 | MARGIN | 'begin date is null'
		broker | BEGIN | null               | client20 | MARGIN | 'end date is null'
		broker | BEGIN | BEGIN.minusDays(1) | client20 | MARGIN | 'end date before begin date'
		broker | BEGIN | END                | null     | MARGIN | 'client is null'
		broker | BEGIN | END                | client17 | MARGIN | 'client is 17 years old'
		broker | BEGIN | END                | client20 | 0      | 'margin is zero'
		broker | BEGIN | END                | client20 | -100   | 'margin is negative'
	}
}
