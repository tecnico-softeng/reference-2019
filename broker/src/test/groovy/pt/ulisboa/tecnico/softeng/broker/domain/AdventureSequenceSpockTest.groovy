package pt.ulisboa.tecnico.softeng.broker.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.*

public class AdventureSequenceSpockTest extends SpockRollbackTestAbstractClass {
    def taxInterface
    def bankInterface
    def activityInterface
    def hotelInterface
    def carInterface

    def bookingActivityData
    def bookingRoomData
    def rentingData
    def broker
    def client

    @Override
    def populate4Test() {
        taxInterface = Mock(TaxInterface)
        bankInterface = Mock(BankInterface)
        activityInterface = Mock(ActivityInterface)
        hotelInterface = Mock(HotelInterface)
        carInterface = Mock(CarInterface)

        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                activityInterface, hotelInterface, carInterface, bankInterface, taxInterface)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        bookingActivityData = new RestActivityBookingData()
        bookingActivityData.setReference(ACTIVITY_CONFIRMATION)
        bookingActivityData.setPrice(70.0)
        bookingActivityData.setPaymentReference(PAYMENT_CONFIRMATION)
        bookingActivityData.setInvoiceReference(INVOICE_REFERENCE)

        bookingRoomData = new RestRoomBookingData()
        bookingRoomData.setReference(ROOM_CONFIRMATION)
        bookingRoomData.setPrice(80.0)
        bookingRoomData.setPaymentReference(PAYMENT_CONFIRMATION)
        bookingRoomData.setInvoiceReference(INVOICE_REFERENCE)

        rentingData = new RestRentingData()
        rentingData.setReference(RENTING_CONFIRMATION)
        rentingData.setPrice(60.0)
        rentingData.setPaymentReference(PAYMENT_CONFIRMATION)
        rentingData.setInvoiceReference(INVOICE_REFERENCE)
    }

    def 'success sequence'() {
        given: 'an adventure'
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)
        and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData
        and: 'a room booking'
        hotelInterface.reserveRoom(_) >> bookingRoomData
        and: 'a car renting'
        carInterface.rentCar(*_) >> rentingData
        and: 'a bank payment'
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        and: 'a tax payment'
        taxInterface.submitInvoice(_) >> INVOICE_DATA
        and: 'the correct return of the data associated with each reservation and payment'
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData
        carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData
        hotelInterface.getRoomBookingData(ROOM_CONFIRMATION) >> bookingRoomData
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)

        when: 'the life cycle of the adventure'
        1.upto(6) { adventure.process() }

        then: 'the final state is confirmed'
        adventure.getState().getValue() == State.CONFIRMED
    }


    def 'success sequence one no car'() {
        given:
        given: 'an adventure'
		def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)
        and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData
        and: 'a room booking'
        hotelInterface.reserveRoom(_) >> bookingRoomData
        and: 'a bank payment'
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        and: 'a tax payment'
        taxInterface.submitInvoice(_) >> INVOICE_DATA
        and: 'the correct return of the data associated with each reservation and payment'
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData
        hotelInterface.getRoomBookingData(ROOM_CONFIRMATION) >> bookingRoomData
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)

		when: 'the life cycle of the adventure'
        1.upto(5) { adventure.process() }

        then: 'the final state is confirmed'
        State.CONFIRMED == adventure.getState().getValue()
    }

    def 'success sequence no hotel'() {
        given: 'an adventure with rent vehicle'
        def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN, true)
        and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData
        and: 'a car renting'
        carInterface.rentCar(*_) >> rentingData
        and: 'a bank payment'
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        and: 'a tax payment'
        taxInterface.submitInvoice(_) >> INVOICE_DATA
		and: 'the correct return of the data associated with each reservation and payment'
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData
        carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData

        when: 'the life cycle of the adventure'
        1.upto(5) { adventure.process() }
		
		then: 'the final state is confirmed'
        State.CONFIRMED == adventure.getState().getValue()
    }

    def 'success sequence no hotel no car'() {
        given: 'an adventure'
        def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN)
        and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData
        and: 'a bank payment'
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        and: 'a tax payment'
        taxInterface.submitInvoice(_) >> INVOICE_DATA
		and: 'the correct return of the data associated with each reservation and payment'
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData

        when: 'the life cycle of the adventure'
        1.upto(4) { adventure.process() }

        then: 'the final state is confirmed'
        State.CONFIRMED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail activity'() {
        given: 'an adventure'
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)
        and: 'an activity exception'
        activityInterface.reserveActivity(_) >> { throw new ActivityException() }

        when: 'the life cycle of the adventure'
        1.upto(2) { adventure.process() }

        then: 'the final state is cancelled'
        State.CANCELLED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail hotel'() {
        given: 'an adventure'
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)
        and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData
        and: 'an hotel exception'
        hotelInterface.reserveRoom(_) >> { throw new HotelException() }
		and: 'an activity reservation cancelation'
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

        when: 'the life cycle of the adventure'
        1.upto(4) { adventure.process() }

        then: 'the final state is cancelled'
        State.CANCELLED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail car'() {
        given: 'an adventure with rent vehicle'
        def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN, true) 
		and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData
		and: 'a car exception'
        carInterface.rentCar(*_) >> { throw new CarException() }
		and: 'an activity reservation cancelation'
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

        when: 'the life cycle of the adventure'
        1.upto(4) { adventure.process() }

        then: 'the final state is cancelled'
        State.CANCELLED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail payment'() {
        given: 'an adventure with rent vehicle'
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)
		and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData
        and: 'a room booking'
        hotelInterface.reserveRoom(_) >> bookingRoomData
		and: 'a car rental'
        carInterface.rentCar(*_) >> rentingData
		and: 'a bank exception'
        bankInterface.processPayment(_) >> { throw new BankException() }
		and: 'the correct return of the data associated with each cancelation'
        activityInterface.cancelReservation(_) >> ACTIVITY_CANCELLATION
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION

        when: 'the life cycle of the adventure'
        1.upto(6) { adventure.process() }

        then: 'the final state is cancelled'
        State.CANCELLED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail tax'() {
        given: 'an adventure with rent vehicle'
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)
		and: 'an activity reservation'
        activityInterface.reserveActivity(_) >> bookingActivityData
		and: 'a room booking'
        hotelInterface.reserveRoom(_) >> bookingRoomData
		and: 'a car rental'
        carInterface.rentCar(CarInterface.Type.CAR, *_) >> rentingData
		and: 'a bank payment'
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
		and: 'a tax exception'
        taxInterface.submitInvoice(_) >> { throw new TaxException() }
		and: 'the correct return of the data associated with each cancelation'
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION

        when: 'the life cycle of the adventure'
        1.upto(6) { adventure.process() }

        then: 'the final state is cancelled'
        State.CANCELLED == adventure.getState().getValue()
    }
}
