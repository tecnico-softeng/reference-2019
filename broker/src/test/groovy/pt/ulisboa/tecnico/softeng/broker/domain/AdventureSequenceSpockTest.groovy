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
        and: 'a room booing'
        hotelInterface.reserveRoom(_) >> bookingRoomData
        and: 'a car renting'
        carInterface.rentCar(*_) >> rentingData
        and: 'a bank payment'
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        and: 'a tax payment'
        taxInterface.submitInvoice(_) >> INVOICE_DATA
        and: 'the correct return of the date associate with each reservation and payment'
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
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        hotelInterface.reserveRoom(_ as RestRoomBookingData) >> bookingRoomData
        bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_DATA

        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData
        hotelInterface.getRoomBookingData(ROOM_CONFIRMATION) >> bookingRoomData
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)

        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)

        when:
        1.upto(5) { adventure.process() }

        then:
        State.CONFIRMED == adventure.getState().getValue()
    }

    def 'success sequence no hotel'() {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        carInterface.rentCar(_ as CarInterface.Type, _ as String, _ as String, _ as String,
                _ as LocalDate, _ as LocalDate, _ as String) >> rentingData
        bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_DATA

        bankInterface.getOperationData(PAYMENT_CONFIRMATION)

        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData
        carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData

        def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN, true)

        when:
        1.upto(5) { adventure.process() }

        then:
        State.CONFIRMED == adventure.getState().getValue()
    }

    def 'success sequence no hotel no car'() {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_DATA

        bankInterface.getOperationData(PAYMENT_CONFIRMATION)

        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData

        Adventure adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN)

        when:
        1.upto(4) { adventure.process() }

        then:
        State.CONFIRMED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail activity'() {
        given: 'an adventure'
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)
        and: 'an activity exception'
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> { throw new ActivityException() }

        when: 'the life cycle of the adventure'
        1.upto(3) { adventure.process() }

        then: 'the final state is cancelled'
        State.CANCELLED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail hotel'() {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        hotelInterface.reserveRoom(_ as RestRoomBookingData) >> { throw new HotelException() }
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

        Adventure adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)

        when:
        1.upto(4) { adventure.process() }

        then:
        State.CANCELLED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail car'() {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        carInterface.rentCar(_ as CarInterface.Type, _ as String, _ as String, _ as String,
                _ as LocalDate, _ as LocalDate, _ as String) >> { throw new CarException() }
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

        def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN, true)

        when:
        1.upto(4) { adventure.process() }

        then:
        State.CANCELLED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail payment'() {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        hotelInterface.reserveRoom(_ as RestRoomBookingData) >> bookingRoomData
        carInterface.rentCar(_ as CarInterface.Type, _ as String, _ as String, _ as String,
                _ as LocalDate, _ as LocalDate, _ as String) >> rentingData
        bankInterface.processPayment(_ as RestBankOperationData) >> { throw new BankException() }
        activityInterface.cancelReservation(_ as String) >> ACTIVITY_CANCELLATION
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION

        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)

        when:
        1.upto(6) { adventure.process() }

        then:
        State.CANCELLED == adventure.getState().getValue()
    }

    def 'unsuccess sequence fail tax'() {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        hotelInterface.reserveRoom(_ as RestRoomBookingData) >> bookingRoomData
        carInterface.rentCar(CarInterface.Type.CAR, _ as String, _ as String, _ as String,
                _ as LocalDate, _ as LocalDate, _ as String) >> rentingData
        bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_ as RestInvoiceData) >> { throw new TaxException() }
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION

        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)

        when:
        1.upto(6) { adventure.process() }

        then:
        State.CANCELLED == adventure.getState().getValue()
    }
}
