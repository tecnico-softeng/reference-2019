package pt.ulisboa.tecnico.softeng.car.domain

import org.joda.time.LocalDate
import org.junit.Rule
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule

import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.car.services.remote.dataobjects.RestInvoiceData
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.BankException
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.RemoteAccessException
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.TaxException

import static org.powermock.api.mockito.PowerMockito.*

@PrepareForTest([TaxInterface.class, BankInterface.class])
class InvoiceProcessorSubmitRentingMethodSpockTest extends SpockRollbackTestAbstractClass {
    private static final String ADVENTURE_ID = "AdventureId"
    def CANCEL_PAYMENT_REFERENCE = 'CancelPaymentReference'
	def INVOICE_REFERENCE = 'InvoiceReference'
	def PAYMENT_REFERENCE = 'PaymentReference'
	def PLATE_CAR = '22-33-HZ'
	def DRIVING_LICENSE = 'br112233'
	def date0 = LocalDate.parse('2018-01-05')
	def date1 = LocalDate.parse('2018-01-06')
	def date2 = LocalDate.parse('2018-01-07')
	def date3 = LocalDate.parse('2018-01-08')
	def date4 = LocalDate.parse('2018-01-09')
	def RENT_A_CAR_NAME = 'Eartz'
	def NIF = 'NIF'
	def NIF_CUSTOMER = 'NIF1'
	def IBAN = 'IBAN'
	def IBAN_CUSTOMER = 'IBAN'
	private Car car
	private RentACar rentACar

	@Rule PowerMockRule rule = new PowerMockRule()

	@Override
	def populate4Test() {
		rentACar = new RentACar(RENT_A_CAR_NAME,NIF,IBAN)
		car = new Car(PLATE_CAR,10,10,rentACar)
	}

	def 'success'() {
        given: "setting things up"
        mockStatic(TaxInterface.class)
		when(TaxInterface.submitInvoice(Mockito.any(RestInvoiceData.class))).thenReturn(INVOICE_REFERENCE)

		mockStatic(BankInterface.class)
		when(BankInterface.processPayment(Mockito.any(RestBankOperationData.class))).thenReturn(PAYMENT_REFERENCE)

        when: "successfully renting a car"
        Renting renting = car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER, ADVENTURE_ID)

        then: "checking if renting was successful"
		renting.paymentReference == PAYMENT_REFERENCE
		renting.invoiceReference == INVOICE_REFERENCE
	}

	def 'one tax failure on submit invoice'() {
		given:
		mockStatic(BankInterface.class)
		when(BankInterface.processPayment(Mockito.any(RestInvoiceData.class)))
				.thenReturn(PAYMENT_REFERENCE)

		mockStatic(TaxInterface.class)
		when(TaxInterface.submitInvoice(Mockito.any(RestInvoiceData.class)))
				.thenThrow(new TaxException())
				.thenReturn(INVOICE_REFERENCE)

        when:
		car.rent(DRIVING_LICENSE,date0,date1,NIF_CUSTOMER,IBAN_CUSTOMER,ADVENTURE_ID)
		car.rent(DRIVING_LICENSE,date2,date3,NIF_CUSTOMER,IBAN_CUSTOMER,ADVENTURE_ID)

		and:
        verifyStatic(TaxInterface.class, Mockito.times(3))
		TaxInterface.submitInvoice(Mockito.any(RestInvoiceData.class))

        then: "if verifications go well, test passes"
		true
	}

	def 'one remote failure on submit invoice'() {
		given:
		mockStatic(BankInterface.class)
		when(BankInterface.processPayment(Mockito.any(RestBankOperationData.class)))
				.thenReturn(PAYMENT_REFERENCE)

		mockStatic(TaxInterface.class)
		when(TaxInterface.submitInvoice(Mockito.any(RestInvoiceData.class)))
				.thenThrow(new RemoteAccessException())
				.thenReturn(INVOICE_REFERENCE)

		when:
		car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER,ADVENTURE_ID)
		car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER,ADVENTURE_ID)

		and:
		verifyStatic(TaxInterface.class, Mockito.times(3))
		TaxInterface.submitInvoice(Mockito.any(RestInvoiceData.class))

		then: "if verifications go well, test passes"
		true
	}

	def 'one bank failure on process payment'() {
		given:
		mockStatic(BankInterface.class)
		when(BankInterface.processPayment(Mockito.any(RestBankOperationData.class)))
				.thenThrow(new BankException())
				.thenReturn(PAYMENT_REFERENCE)

		mockStatic(TaxInterface.class)
		when(TaxInterface.submitInvoice(Mockito.any(RestInvoiceData.class)))
				.thenReturn(INVOICE_REFERENCE)

		when:
		car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER, ADVENTURE_ID)
		car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER, ADVENTURE_ID)

		and:
        verifyStatic(TaxInterface.class, Mockito.times(3))
		BankInterface.processPayment(Mockito.any(RestBankOperationData.class))

		then: "if verifications go well, test passes"
		true
	}

	def 'one remote failure on process payment'() {
		given:
		mockStatic(BankInterface.class)
		when(BankInterface.processPayment(Mockito.any(RestBankOperationData.class)))
				.thenThrow(new RemoteAccessException())
				.thenReturn(PAYMENT_REFERENCE)

		mockStatic(TaxInterface.class)
		when(TaxInterface.submitInvoice(Mockito.any(RestInvoiceData.class)))
				.thenReturn(INVOICE_REFERENCE)

		when:
		car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER, ADVENTURE_ID)
		car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER, ADVENTURE_ID)

		and:
        verifyStatic(BankInterface.class, Mockito.times(3))
		BankInterface.processPayment(Mockito.any(RestBankOperationData.class))

		then: "if verifications go well, test passes"
		true
	}

	def 'successful cancel'() {
		given:
		mockStatic(BankInterface.class)
		when(BankInterface.processPayment(Mockito.any(RestBankOperationData.class)))
				.thenReturn(PAYMENT_REFERENCE)

		when(BankInterface.cancelPayment(Mockito.any(String.class)))
				.thenReturn(CANCEL_PAYMENT_REFERENCE)

		mockStatic(TaxInterface.class)
		when(TaxInterface.submitInvoice(Mockito.any(RestInvoiceData.class)))
				.thenReturn(INVOICE_REFERENCE)

		doNothing().when(TaxInterface, "cancelInvoice", Mockito.any(String.class))

		Renting renting = this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER, ADVENTURE_ID)

		when:
		renting.cancel()

		then: "if verifications go well, test passes"
		renting.isCancelled()
	}

	def 'one bank exception on cancel payment'() {
		given:
		mockStatic(TaxInterface.class)
		when(TaxInterface.submitInvoice(Mockito.any(RestInvoiceData.class)))
			.thenReturn(INVOICE_REFERENCE)

		doNothing().when(TaxInterface, "cancelInvoice", Mockito.any(String.class))

		mockStatic(BankInterface.class)
		when(BankInterface.processPayment(Mockito.any(RestBankOperationData.class)))
				.thenReturn(PAYMENT_REFERENCE)

		when(BankInterface.cancelPayment(Mockito.any(String.class)))
				.thenThrow(new BankException())
				.thenReturn(CANCEL_PAYMENT_REFERENCE)

		Renting renting = this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER, ADVENTURE_ID)

		when:
		renting.cancel()
		car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER, ADVENTURE_ID)

		and:
        verifyStatic(BankInterface.class, Mockito.times(2))
		BankInterface.cancelPayment(Mockito.any(String.class))

		then: "if verifications go well, test passes"
		renting.isCancelled()
	}

	def 'one remote exception on cancel payment'() {
		given:
		mockStatic(TaxInterface.class)
		when(TaxInterface.submitInvoice(Mockito.any(RestInvoiceData.class)))
				.thenReturn(INVOICE_REFERENCE)

		doNothing().when(TaxInterface, "cancelInvoice", Mockito.any(String.class))

		mockStatic(BankInterface.class)
		when(BankInterface.processPayment(Mockito.any(RestBankOperationData.class)))
				.thenReturn(PAYMENT_REFERENCE)

		when(BankInterface.cancelPayment(Mockito.any(String.class)))
				.thenThrow(new RemoteAccessException())
				.thenReturn(CANCEL_PAYMENT_REFERENCE)

		Renting renting = this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER, ADVENTURE_ID)

		when:
		renting.cancel()
		car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER, ADVENTURE_ID)

		and:
        verifyStatic(BankInterface.class, Mockito.times(2))
		BankInterface.cancelPayment(Mockito.any(String.class))

		then: "if verifications go well, test passes"
		renting.isCancelled()
	}

	def 'one remote/tax exception on cancel invoice'(Throwable exp) {
		given:
		mockStatic(TaxInterface.class)
		when(TaxInterface.submitInvoice(Mockito.any(RestInvoiceData.class)))
				.thenReturn(INVOICE_REFERENCE)

		when(TaxInterface.cancelInvoice(Mockito.any(String.class))).thenAnswer(new Answer<Void>() {
			def i = 0

			@Override
			Void answer(InvocationOnMock invocation) throws Throwable {
				if (i < 1) {
					i++
					throw exp
				}
			}
		})

		mockStatic(BankInterface.class)
		when(BankInterface.processPayment(Mockito.any(RestBankOperationData.class)))
				.thenReturn(PAYMENT_REFERENCE)

		when(BankInterface.cancelPayment(Mockito.any(String.class)))
				.thenReturn(CANCEL_PAYMENT_REFERENCE)

		Renting renting = this.car.rent(DRIVING_LICENSE, date0, date1, NIF_CUSTOMER, IBAN_CUSTOMER, ADVENTURE_ID)

		when:
		renting.cancel()
		car.rent(DRIVING_LICENSE, date2, date3, NIF_CUSTOMER, IBAN_CUSTOMER, ADVENTURE_ID)

		and:
        verifyStatic(TaxInterface.class, Mockito.times(2))
		TaxInterface.cancelInvoice(Mockito.any(String.class))

		then: "if verifications go well, test passes"
		renting.isCancelled()


		where:
		exp << [new TaxException(), new RemoteAccessException()]

	}
}
