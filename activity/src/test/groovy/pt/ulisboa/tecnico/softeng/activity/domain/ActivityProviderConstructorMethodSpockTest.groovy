package pt.ulisboa.tecnico.softeng.activity.domain

import spock.lang.Shared
import spock.lang.Unroll

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException

class ActivityProviderConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def PROVIDER_CODE = 'XtremX'
	@Shared def PROVIDER_NAME = 'Adventure++'
	@Shared def IBAN = 'IBAN'
	@Shared def NIF = 'NIF'

	@Override
	def populate4Test() { }

	def 'success'() {
		when:
		def provider = new ActivityProvider(PROVIDER_CODE,PROVIDER_NAME,NIF,IBAN)

		then:
		provider.getName() == PROVIDER_NAME
		provider.getCode().length() == ActivityProvider.CODE_SIZE
		FenixFramework.getDomainRoot().getActivityProviderSet().size() == 1
		provider.getActivitySet().size() == 0
	}

	@Unroll('exceptions: #code, #prov, #nif, #iban')
	def 'exceptions'() {
		when:
		new ActivityProvider(code, prov, nif, iban)

		then:
		thrown(ActivityException)

		where:
		code          | prov          | nif  | iban
		null          | PROVIDER_NAME | NIF  | IBAN
		'  '          | PROVIDER_NAME | NIF  | IBAN
		PROVIDER_CODE | null          | NIF  | IBAN
		PROVIDER_CODE | '  '          | NIF  | IBAN
		'12345'       | '  '          | NIF  | IBAN
		'1234567'     | '  '          | NIF  | IBAN
		PROVIDER_CODE | PROVIDER_NAME | null | IBAN
		PROVIDER_CODE | PROVIDER_NAME | '  ' | IBAN
	}

	@Unroll('uniques: #cd1, #cd2, #n1, #n2, #nif1, #nif2')
	def 'uniques'() {
		given:
		new ActivityProvider(cd1, n1, nif1, IBAN)

		when:
			new ActivityProvider(cd2, n2, nif2, IBAN)

		then:
		def error = thrown(ActivityException)
		FenixFramework.getDomainRoot().getActivityProviderSet().size() == 1

		where:
		cd1           | cd2           | n1            | n2            | nif1 | nif2
		PROVIDER_CODE | PROVIDER_CODE | PROVIDER_NAME | 'Hello'       | NIF  | NIF + 2
		'123456'      | PROVIDER_CODE | PROVIDER_NAME | PROVIDER_NAME | NIF  | NIF + 2
		PROVIDER_CODE | '123456'      | PROVIDER_NAME | 'jdgdsk'      | NIF  | NIF

	}
}
