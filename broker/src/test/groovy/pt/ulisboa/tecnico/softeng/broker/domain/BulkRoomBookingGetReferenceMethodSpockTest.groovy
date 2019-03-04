package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class BulkRoomBookingGetRoomBookingData4TypeMethodSpockTest extends SpockRollbackTestAbstractClass {
    def broker
    def hotelInterface
    def bulk

    @Override
    def populate4Test() {
        hotelInterface = Mock(HotelInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, hotelInterface)
        bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, CLIENT_IBAN)
        new Reference(bulk, REF_ONE)
        new Reference(bulk, REF_TWO)
    }

    def 'success SINGLE room'() {
        def roomBookingData = new RestRoomBookingData()
        roomBookingData.setRoomType(SINGLE)

        given: 'that the hotel interface returns a booking data for a single room'
        hotelInterface.getRoomBookingData(_) >> roomBookingData

        when: 'it is requested a SINGLE room from the set of bulked booked rooms'
        def bookingData = bulk.getRoomBookingData4Type(SINGLE)

        then: 'a the booking of a single room is returned'
        bookingData.getRoomType() == SINGLE
        and: 'the number of references is decremented'
        bulk.getReferences().size() == 1
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()
    }

    def 'success DOUBLE room'() {
        def roomBookingData = new RestRoomBookingData()
        roomBookingData.setRoomType(DOUBLE)

        given: 'that the hotel interface returns a booking data for a double room'
        hotelInterface.getRoomBookingData(_) >> roomBookingData

        when: 'it is requested a double room from the set of bulked booked rooms'
        def bookingData = bulk.getRoomBookingData4Type(DOUBLE)

        then: 'a the booking of a double room is returned'
        bookingData.getRoomType() == DOUBLE
        and: 'the number of references is decremented'
        bulk.getReferences().size() == 1
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()
    }

    def 'hotel exception'() {
        given: 'that the hotel interface returns a hotel exception'
        hotelInterface.getRoomBookingData(_) >> { throw new HotelException() }

        when: 'it is requested a double room from the set of bulked booked rooms'
        def bookingData = bulk.getRoomBookingData4Type(DOUBLE)

        then: 'no booking is returned'
        bookingData == null
        and: 'the number of references is not changed'
        bulk.getReferences().size() == 2
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()
    }

    def 'remote access exception'() {
        given: 'that the hotel interface returns a remote access exception'
        hotelInterface.getRoomBookingData(_) >> { throw new RemoteAccessException() }

        when: 'it is requested a double room from the set of bulked booked rooms'
        def bookingData = bulk.getRoomBookingData4Type(DOUBLE)

        then: 'no booking is returned'
        bookingData == null
        and: 'the number of references is not changed'
        bulk.getReferences().size() == 2
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()
    }

    def 'max remote exception'() {
        given: 'that the hotel interface returns remote access exceptions'
        hotelInterface.getRoomBookingData(_) >> { throw new RemoteAccessException() }

        when: 'the request is done the max number of exceptions'
        for (int i = 0; i < (BulkRoomBooking.MAX_REMOTE_ERRORS / 2).intValue(); i++) {
            bulk.getRoomBookingData4Type(DOUBLE)
        }

        then: 'the set of references is not changed'
        bulk.getReferences().size() == 2
        and: 'the bulk is cancelled'
        bulk.getCancelled()
    }

    def 'max minus one remote exception'() {
        def roomBookingData = new RestRoomBookingData()
        roomBookingData.setRoomType(DOUBLE)

        given: 'that the hotel interface returns remote access exceptions'
        hotelInterface.getRoomBookingData(_) >> { throw new RemoteAccessException() }

        when: 'the request is done max number of exceptions - 1'
        for (int i = 0; i < (BulkRoomBooking.MAX_REMOTE_ERRORS / 2).intValue() - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE)
        }

        then: 'the set of references is not changed'
        bulk.getReferences().size() == 2
        and: 'the the bulk is not cancelled'
        !bulk.getCancelled()

        when: 'another request is done'
        bulk.getRoomBookingData4Type(DOUBLE)

        then: 'the interface returns booking data'
        hotelInterface.getRoomBookingData(_) >> roomBookingData
        and: 'the references are decremented'
        bulk.getReferences().size() == 1
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()
    }

    def 'remote exception value is reset by success'() {
        def roomBookingData = new RestRoomBookingData()
        roomBookingData.setRoomType(DOUBLE)

        given: 'that the hotel interface returns remote access exceptions'
        hotelInterface.getRoomBookingData(_) >> { throw new RemoteAccessException() }

        when: 'the request is done max number of exceptions - 1'
        for (int i = 0; i < (BulkRoomBooking.MAX_REMOTE_ERRORS / 2).intValue() - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE)
        }

        then: 'the set of references is not changed'
        bulk.getReferences().size() == 2
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()

        when: 'another request is done'
        bulk.getRoomBookingData4Type(DOUBLE)

        then: 'the interface returns booking data'
        hotelInterface.getRoomBookingData(_) >> roomBookingData
        and: 'the references are decremented'
        bulk.getReferences().size() == 1
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()

        when: 'the request is done max number of exceptions - 1'
        for (int i = 0; i < (BulkRoomBooking.MAX_REMOTE_ERRORS / 2).intValue() - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE)
        }

        then: 'the hotel interface returns remote access exceptions'
        hotelInterface.getRoomBookingData(_) >> { throw new RemoteAccessException() }
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()
    }

    def 'remote exception value is reset by hotel exception'() {
        given: 'that the hotel interface returns remote access exceptions'
        hotelInterface.getRoomBookingData(_) >> { throw new RemoteAccessException() }

        when: 'the request is done max number of exceptions - 1'
        for (int i = 0; i < (BulkRoomBooking.MAX_REMOTE_ERRORS / 2).intValue() - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE)
        }

        then: 'the set of references is not changed'
        bulk.getReferences().size() == 2
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()

        when: 'another request is done'
        bulk.getRoomBookingData4Type(DOUBLE)

        then: 'the interface throws an hotel exception'
        hotelInterface.getRoomBookingData(_) >> { throw new HotelException() }
        and: 'the references are not changed'
        bulk.getReferences().size() == 2
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()

        when: 'the request is done max number of exceptions - 1'
        for (int i = 0; i < (BulkRoomBooking.MAX_REMOTE_ERRORS / 2).intValue() - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE)
        }

        then: 'the hotel interface returns remote access exceptions'
        hotelInterface.getRoomBookingData(_) >> { throw new RemoteAccessException() }
        and: 'the bulk is not cancelled'
        !bulk.getCancelled()
    }

}
