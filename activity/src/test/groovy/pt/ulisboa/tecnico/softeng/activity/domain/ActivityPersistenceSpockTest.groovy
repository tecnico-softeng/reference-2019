package pt.ulisboa.tecnico.softeng.activity.domain
import spock.lang.Specification
import org.joda.time.LocalDate
import pt.ist.fenixframework.Atomic
import pt.ist.fenixframework.Atomic.TxMode
import pt.ist.fenixframework.FenixFramework

class ActivityPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
	def ADVENTURE_ID = 'AdventureId'
	def ACTIVITY_NAME = 'Activity_Name'
	def PROVIDER_NAME = 'Wicket'
	def PROVIDER_CODE = 'A12345'
	def IBAN = 'IBAN'
	def NIF = 'NIF'
	def BUYER_IBAN = 'IBAN2'
	def BUYER_NIF = 'NIF2'
	def CAPACITY = 25
	def AMOUNT = 30.0
	def begin = new LocalDate(2017,04,01)
	def end = new LocalDate(2017,04,15)

	@Override
	def whenCreateInDatabase() {
		def activityProvider = new ActivityProvider(PROVIDER_CODE,PROVIDER_NAME,NIF,IBAN)

		def activity = new Activity(activityProvider,ACTIVITY_NAME,18,65,CAPACITY)

		def offer = new ActivityOffer(activity,this.begin,this.end,AMOUNT)
		offer.book(activityProvider,offer,54,BUYER_NIF,BUYER_IBAN,ADVENTURE_ID)
	}

	@Override
	def thenAssert() {
		assert 1 == FenixFramework.getDomainRoot().getActivityProviderSet().size()

		def providers = new ArrayList<>(FenixFramework.getDomainRoot().getActivityProviderSet())
		def provider = providers.get(0)

		verifyAll {
			PROVIDER_CODE == provider.getCode()
			PROVIDER_NAME == provider.getName()
			1 == provider.getActivitySet().size()
			NIF == provider.getNif()
			IBAN == provider.getIban()
		}

		Processor processor=provider.getProcessor()
		assert processor != null
		assert 1 == processor.getBookingSet().size()

		def activities = new ArrayList<>(provider.getActivitySet())
		def activity = activities.get(0)
		verifyAll {
			ACTIVITY_NAME == activity.getName()
			activity.getCode().startsWith(PROVIDER_CODE)
			18 == activity.getMinAge()
			65 == activity.getMaxAge()
			CAPACITY == activity.getCapacity()
			1 == activity.getActivityOfferSet().size()
		}

		def offers = new ArrayList<>(activity.getActivityOfferSet())
		def offer = offers.get(0)
		verifyAll {
			begin == offer.getBegin()
			end == offer.getEnd()
			CAPACITY == offer.getCapacity()
			1 == offer.getBookingSet().size()
			AMOUNT == offer.getPrice()
		}

		def bookings = new ArrayList<>(offer.getBookingSet())
		def booking = bookings.get(0)
		verifyAll {
			booking.getReference() != null
			booking.getCancel() == null
			booking.getCancellationDate() == null
			booking.getPaymentReference() == null
			booking.getInvoiceReference() == null
			!booking.getCancelledInvoice()
			booking.getCancelledPaymentReference() == null
			'SPORT' == booking.getType()
			BUYER_NIF == booking.getBuyerNif()
			BUYER_IBAN == booking.getIban()
			NIF == booking.getProviderNif()
			AMOUNT == booking.getAmount()
			ADVENTURE_ID == booking.getAdventureId()
			begin == booking.getDate()
			booking.getTime() != null
			booking.getProcessor() != null
		}
	}

	@Override
	def deleteFromDatabase() {
		for (def activityProvider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			activityProvider.delete()
		}
	}

}
