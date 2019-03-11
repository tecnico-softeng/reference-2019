package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.*

class UndoStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
    def activityInterface
    def hotelInterface
    def carInterface
    def bankInterface
    def taxInterface
    def broker
    def client
    def adventure

    def populate4Test() {
        activityInterface = Mock(ActivityInterface)
        hotelInterface = Mock(HotelInterface)
        carInterface = Mock(CarInterface)
        bankInterface = Mock(BankInterface)
        taxInterface = Mock(TaxInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,
                activityInterface, hotelInterface, carInterface, bankInterface, taxInterface)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

        adventure.setState(Adventure.State.UNDO)
    }

    def 'success revert payment'() {
        given: 'a bank cancel payment succeeds'
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION
        and: 'the adventure has a payment confirmation'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure is cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'the adventure has a payment cancellation'
        adventure.getPaymentCancellation() == PAYMENT_CANCELLATION
    }

    def 'fail revert payment bank exception'() {
        given: 'a bank cancel payment throws a bank exception'
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> { throw new BankException() }
        and: 'the adventure has a payment confirmation'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change state'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the adventure payment cancellation is null'
        adventure.getPaymentCancellation() == null
    }

    def 'fail revert payment remote access exception'() {
        given: 'a bank cancel payment throws a remote access exception'
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> { throw new RemoteAccessException() }
        and: 'the adventure has a payment confirmation'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the adventure payment cancellation is null'
        adventure.getPaymentCancellation() == null
    }

    def 'success revert activity'() {
        given: 'an activity cancel reservation succeeds'
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has an activity confirmation'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure is cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'the adventure has a activity cancellation token'
        adventure.getActivityCancellation() == ACTIVITY_CANCELLATION
    }

    def 'fail revert activity due to activity exception'() {
        given: 'an activity cancel reservation throws an activity exception'
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> { throw new ActivityException() }
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has an activity confirmation'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the adventure activity cancellation token is null'
        adventure.getActivityCancellation() == null
    }

    def 'fail revert activity remote exception'() {
        given: 'an activity cancel reservation throws a remote access exception'
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> { throw new RemoteAccessException() }
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has an activity confirmation'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the adventure activity cancellation token is null'
        adventure.getActivityCancellation() == null
    }

    def 'success revert room booking'() {
        given: 'a hotel cancel booking succeeds'
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has a room confirmation'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state changes to cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'the adventure has a room cancellation token'
        adventure.getRoomCancellation() == ROOM_CANCELLATION
    }

    def 'success revert room booking hotel exception'() {
        given: 'a hotel cancel booking throws a hotel exception'
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> { throw new HotelException() }
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has a room confirmation'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the adventure room cancellation token is null'
        adventure.getRoomCancellation() == null
    }

    def 'success revert room booking remote exception'() {
        given: 'a hotel cancel booking throws a remote access exception'
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> { throw new RemoteAccessException() }
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has a room confirmation'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the adventure room cancellation token is null'
        adventure.getRoomCancellation() == null
    }

    def 'success revert rent car'() {
        given: 'a car cancel renting succeeds'
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has cancelled room'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        and: 'has a car renting confirmation'
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state changes to cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'the adventure has a renting cancellation token'
        adventure.getRentingCancellation() == RENTING_CANCELLATION
    }

    def 'fail revert rent car car exception'() {
        given: 'a car cancel renting throws a car exception'
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> { throw new CarException() }
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has cancelled room'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        and: 'has a car renting confirmation'
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the adventure renting cancellation token is null'
        adventure.getRentingCancellation() == null
    }

    def 'fail revert rent car remote exception'() {
        given: 'a car cancel renting throws a remote access exception'
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> { throw new RemoteAccessException() }
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has cancelled room'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        and: 'has a car renting confirmation'
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the adventure renting cancellation token is null'
        adventure.getRentingCancellation() == null
    }

    def 'success cancel invoice'() {
        given: 'an invoice cancel succeeds'
        taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has cancelled room'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        and: 'has cancelled car renting'
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CANCELLATION)
        and: 'has a invoice reference'
        adventure.setInvoiceReference(INVOICE_REFERENCE)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state is cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'the invoice is cancelled'
        adventure.getInvoiceCancelled()
    }

    def 'fail cancel invoice tax exception'() {
        given: 'an invoice cancel throws a tax exception'
        taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw new TaxException() }
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has cancelled room'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        and: 'has cancelled car renting'
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CANCELLATION)
        and: 'has a invoice reference'
        adventure.setInvoiceReference(INVOICE_REFERENCE)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the invoice is not cancelled'
        !adventure.getInvoiceCancelled()
    }

    def 'fail cancel invoice remote access exception'() {
        given: 'an invoice cancel throws a remote accesss exception'
        taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw new RemoteAccessException() }
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has cancelled room'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        and: 'has cancelled car renting'
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CANCELLATION)
        and: 'has a invoice reference'
        adventure.setInvoiceReference(INVOICE_REFERENCE)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the invoice is not cancelled'
        !adventure.getInvoiceCancelled()
    }

}
