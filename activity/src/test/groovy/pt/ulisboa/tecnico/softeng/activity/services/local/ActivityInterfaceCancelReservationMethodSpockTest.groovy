package pt.ulisboa.tecnico.softeng.activity.services.local

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.domain.*
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface

class ActivityInterfaceCancelReservationMethodSpockTest extends SpockRollbackTestAbstractClass {
    def IBAN = "IBAN"
    def NIF = "123456789"
    def AGE = 28
    def provider
    def offer

    def bankInterface
    def taxInterface

    def activityInterface = new ActivityInterface()

    @Override
    def populate4Test() {
        bankInterface = Mock(BankInterface)
        taxInterface = Mock(TaxInterface)
        def processor = new Processor(bankInterface, taxInterface)

        provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN, processor)
        def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

        def begin = new LocalDate(2016, 12, 19)
        def end = new LocalDate(2016, 12, 21)
        offer = new ActivityOffer(activity, begin, end, 30)
    }

    def 'success'() {
        given: 'a booking'
        def booking = new Booking(provider, offer, AGE, NIF, IBAN)
        and: 'and a booking that was submitted'
        provider.getProcessor().submitBooking(booking)

        when: 'when cancelling a reservation'
        def cancel = activityInterface.cancelReservation(booking.getReference())

        then: 'booking is cancelled'
        booking.isCancelled()
        cancel == booking.getCancel()
        and:
        1 * bankInterface.cancelPayment(_)
        1 * taxInterface.cancelInvoice(_)
    }

    def 'booking does not exist'() {
        given:
        provider.getProcessor().submitBooking(new Booking(provider, offer, AGE, NIF, IBAN))

        when:
        activityInterface.cancelReservation("XPTO")

        then:
        thrown(ActivityException)
        and:
        and:
        0 * bankInterface.cancelPayment(_)
        0 * taxInterface.cancelInvoice(_)
    }

}
