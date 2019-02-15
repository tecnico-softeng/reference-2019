package pt.ulisboa.tecnico.softeng.activity.services.local

import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.domain.Activity
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider
import pt.ulisboa.tecnico.softeng.activity.domain.Booking
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData
import spock.lang.Unroll

class ActivityInterfaceGetActivityReservationDataMethodSpockTest extends SpockRollbackTestAbstractClass {
	private static final String NAME='ExtremeAdventure'
	private static final String CODE='XtremX'
	private final LocalDate begin=new LocalDate(2016,12,19)
	private final LocalDate end=new LocalDate(2016,12,21)
	private ActivityProvider provider
	private ActivityOffer offer
	private Booking booking

	@Override
	def populate4Test() {
		provider = new ActivityProvider(CODE,NAME,'NIF','IBAN')
		def activity = new Activity(provider,'Bush Walking',18,80,3)
		offer = new ActivityOffer(activity,begin,end,30)
	}

	def 'success'() {
		when:
		booking = new Booking(provider,offer,'123456789','IBAN')
		RestActivityBookingData data=ActivityInterface.getActivityReservationData(booking.getReference())

		then:
		data.getReference() == booking.getReference()
		data.getCancellation() == null
		data.getName() == NAME
		data.getCode() == CODE
		data.getBegin() == begin
		data.getEnd() == end
		data.getCancellationDate() == null
	}

	def 'success cancelled'() {
		given:
		booking = new Booking(provider,offer,'123456789','IBAN')
		provider.getProcessor().submitBooking(booking)

		when:
		booking.cancel()
		RestActivityBookingData data=ActivityInterface.getActivityReservationData(booking.getCancel())

		then:
		data.getReference() == booking.getReference()
		data.getCancellation() == booking.getCancel()
		data.getName() == NAME
		data.getCode() == CODE
		data.getBegin() == begin
		data.getEnd() == end
		data.getCancellationDate() != null
	}

	@Unroll('exceptions: #label')
	def 'exceptions'() {
		when:
		ActivityInterface.getActivityReservationData(ref)

		then:
		thrown(ActivityException)

		where:
		ref    | label
		null   | 'null reference'
		''     | 'empty reference'
		'XPTO' | 'not exists reference'
	}
}
