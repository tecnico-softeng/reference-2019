package pt.ulisboa.tecnico.softeng.activity.domain

import org.junit.Assert

class ActivityMatchAgeMethodSpockTest extends SpockRollbackTestAbstractClass {
	private static final int MIN_AGE=25
	private static final int MAX_AGE=80
	private static final int CAPACITY=30
	private Activity activity

	@Override
	def populate4Test() {
		def provider = new ActivityProvider('XtremX','ExtremeAdventure','NIF','IBAN')

		activity = new Activity(provider,'Bush Walking',MIN_AGE,MAX_AGE,CAPACITY)
	}

	def 'success'() {
		expect:
		activity.matchAge(arg)

		where:
		arg << [(MAX_AGE - MIN_AGE) / 2, MIN_AGE]
	}


	void lessThanMinAge() {
		Assert.assertFalse(this.activity.matchAge(MIN_AGE - 1))
	}

	void successEqualMaxAge() {
		Assert.assertFalse(this.activity.matchAge(MAX_AGE))
	}

	void greaterThanMaxAge() {
		Assert.assertFalse(this.activity.matchAge(MAX_AGE + 1))
	}

}
