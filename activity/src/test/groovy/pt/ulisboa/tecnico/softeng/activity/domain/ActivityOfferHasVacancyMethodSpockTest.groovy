package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import org.junit.Assert
import mockit.Expectations
import mockit.Mocked
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData

class ActivityOfferHasVacancyMethodSpockTest extends SpockRollbackTestAbstractClass {
	def IBAN = 'IBAN'
	def NIF = '123456789'
	def provider
	def offer

	@Override
	def populate4Test() {
		provider = new ActivityProvider('XtremX','ExtremeAdventure','NIF',IBAN)

		def activity=new Activity(provider,'Bush Walking',18,80,3)
		def begin = new LocalDate(2016,12,19)
		def end = new LocalDate(2016,12,21)

		offer =n ew ActivityOffer(activity,begin,end,30)
	}

	def 'success'() {
		when:
		new Booking(provider, offer,NIF,IBAN)

		then:
		offer.hasVacancy()
	}

	def 'booking is full'() {
		when:
		new Booking(provider, offer, NIF, IBAN)
		new Booking(provider, offer, NIF, IBAN)
		new Booking(provider, offer, NIF, IBAN)

		then:
		!offer.hasVacancy()
	}

	def 'booking is full minus one'() {
		when:
		new Booking(provider, offer, NIF, IBAN)
		new Booking(provider, offer, NIF, IBAN)

		then:
		offer.hasVacancy()
	}

	def 'has cancelled bookings'(@Mocked final TaxInterface taxInterface, @Mocked final BankInterface bankInterface) {
		given:
		new Expectations(){
{
    BankInterface.processPayment((RestBankOperationData)this.any);
    TaxInterface.submitInvoice((RestInvoiceData)this.any);
  }
}


		this.provider.getProcessor().submitBooking(new Booking(this.provider,this.offer,NIF,IBAN))

		this.provider.getProcessor().submitBooking(new Booking(this.provider,this.offer,NIF,IBAN))

		Booking booking=new Booking(this.provider,this.offer,NIF,IBAN)

		this.provider.getProcessor().submitBooking(booking)

		when:
		booking.cancel()

		then:
		this.offer.hasVacancy()
	}

	void hasCancelledBookingsButFull(@Mocked final TaxInterface taxInterface, @Mocked final BankInterface bankInterface) {
		new Expectations(){
{
    BankInterface.processPayment((RestBankOperationData)this.any);
    TaxInterface.submitInvoice((RestInvoiceData)this.any);
  }
}


		this.provider.getProcessor().submitBooking(new Booking(this.provider,this.offer,NIF,IBAN))

		this.provider.getProcessor().submitBooking(new Booking(this.provider,this.offer,NIF,IBAN))

		Booking booking=new Booking(this.provider,this.offer,NIF,IBAN)

		this.provider.getProcessor().submitBooking(booking)

		booking.cancel()

		booking=new Booking(this.provider,this.offer,NIF,IBAN)

		this.provider.getProcessor().submitBooking(booking)

		Assert.assertFalse(this.offer.hasVacancy())
	}

}
