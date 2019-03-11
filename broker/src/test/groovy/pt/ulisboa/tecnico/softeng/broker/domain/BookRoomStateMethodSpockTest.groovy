package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class BookRoomStateMethodSpockTest extends SpockRollbackTestAbstractClass {
    def broker
    def hotelInterface
    def client
    def adventure
    def bookingData

    @Override
    def populate4Test() {
        hotelInterface = Mock(HotelInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,
                new ActivityInterface(), hotelInterface, new CarInterface(), new BankInterface(), new TaxInterface())
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

        bookingData = new RestRoomBookingData()
        bookingData.setReference(ROOM_CONFIRMATION)
        bookingData.setPrice(80.0)

        adventure.setState(Adventure.State.BOOK_ROOM)
    }

    def 'success book room move to payment'() {
        given: 'the hotel reservation is successful'
        hotelInterface.reserveRoom(_) >> bookingData

        when: 'a next step in the adventure is processed'
        adventure.process()

        then: 'the adventure state progresses to process payment'
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
        and: 'the room is confirmed'
        adventure.getRoomConfirmation() == ROOM_CONFIRMATION
    }

    def 'success book room move to renting'() {
        given: 'an adventure wich includes renting'
        def adv = new Adventure(broker, BEGIN, END, client, MARGIN, true)
        and: 'in book room state'
        adv.setState(Adventure.State.BOOK_ROOM)
        and: 'a successful room booking'
        hotelInterface.reserveRoom(_) >> bookingData

        when: 'a next step in the adventure is processed'
        adv.process()

        then: 'the adventure state progresses to rent vehicle'
        adv.getState().getValue() == Adventure.State.RENT_VEHICLE
        and: 'the room is confirmed'
        adv.getRoomConfirmation() == ROOM_CONFIRMATION
    }

    def 'hotel exception'() {
        given: 'the hotel reservation throws a hotel exception'
        hotelInterface.reserveRoom(_) >> { throw new HotelException() }

        when: 'a next step in the adventure is processed'
        adventure.process()

        then: 'the adventure state progresses to undo'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the room confirmation is null'
        adventure.getRoomConfirmation() == null
    }

    def 'one remote access exception'() {
        given: 'the hotel reservation throws a remote access exception'
        hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }

        when: 'a next step in the adventure is processed'
        adventure.process()

        then: 'the adventure state do not state'
        adventure.getState().getValue() == Adventure.State.BOOK_ROOM
        and: 'the number of errors is 1'
        adventure.getState().getNumOfRemoteErrors() == 1
    }

    def 'max remote access exception'() {
        given: 'the hotel reservation throws a remote access exception'
        hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }

        when: 'the adventure is processed max remote errors'
        for (int i = 0; i < BookRoomState.MAX_REMOTE_ERRORS; i++) {
            adventure.process()
        }

        then: 'the adventure state change to undo'
        adventure.getState().getValue() == Adventure.State.UNDO
    }

    def 'max -1 remote access exception'() {
        given: 'the hotel reservation throws a remote access exception'
        hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }

        when: 'the adventure is processed max -1 remote errors'
        for (int i = 0; i < BookRoomState.MAX_REMOTE_ERRORS - 1; i++) {
            adventure.process()
        }

        then: 'the adventure state is book room'
        adventure.getState().getValue() == Adventure.State.BOOK_ROOM
        and: 'the number of errors is max remote errors - 1'
        adventure.getState().getNumOfRemoteErrors() == BookRoomState.MAX_REMOTE_ERRORS - 1
    }

    def 'five remote access exception one success'() {
        given: 'the hotel reservation throws a remote access exception'
        hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }

        when: 'the adventure is processed 5 times'
        for (int i = 0; i < 5; i++) {
            adventure.process()
        }

        then: 'the adventure state is book room'
        adventure.getState().getValue() == Adventure.State.BOOK_ROOM
        and: 'the number of errors is 5'
        adventure.getState().getNumOfRemoteErrors() == 5

        when: 'the adventure is processed again'
        adventure.process()

        then: 'the hotel reservation is successful'
        hotelInterface.reserveRoom(_) >> bookingData
        and: 'the adventure state progresses to process payment'
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
        and: 'the room is confirmed'
        adventure.getRoomConfirmation() == ROOM_CONFIRMATION
    }

    def 'one remote access exception and one hotel exception'() {
        given: 'the hotel reservation throws a remote access exception'
        hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }

        when: 'the adventure is processed 5 times'
        adventure.process()

        then: 'the adventure state is book room'
        adventure.getState().getValue() == Adventure.State.BOOK_ROOM
        and: 'the number of errors is 1'
        adventure.getState().getNumOfRemoteErrors() == 1

        when: 'the adventure is processed again'
        adventure.process()

        then: 'the hotel reservation throws and hotel exception'
        hotelInterface.reserveRoom(_) >> { throw new HotelException() }
        and: 'the adventure state progresses to undo'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the room is confirmation is null'
        adventure.getRoomConfirmation() == null
    }

}
