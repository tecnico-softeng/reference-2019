package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException

class BulkRoomBookingProcessBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
    def hotelInterface
    def broker
    def bulk

    @Override
    def populate4Test() {
        hotelInterface = Mock(HotelInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, hotelInterface)
        bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, IBAN_BUYER)
    }

    def 'success'() {
        given: 'the hotel interface returns two references for booking'
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                bulk.getId()) >> { new HashSet<>(Arrays.asList("ref1", "ref2")) }

        when: 'the bulk booking is processed'
        bulk.processBooking()

        then: 'the two references are stored'
        bulk.getReferences().size() == 2
    }

    def 'success twice'() {
        given: 'the hotel interface returns two references for booking'
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                bulk.getId()) >> { new HashSet<>(Arrays.asList("ref1", "ref2")) }

        when: 'the bulk booking is processed'
        bulk.processBooking()

        then: 'the two references are stored'
        bulk.getReferences().size() == 2

        when: 'the bulk booking is processed again'
        bulk.processBooking()

        then: 'it does not request more bookings'
        0 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                bulk.getId())
        bulk.getReferences().size() == 2

    }

    def 'one hotel exception'() {
        when: 'the bulk booking is processed twice'
        bulk.processBooking()
        bulk.processBooking()

        then: 'the first invocation returns an exception'
        1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                bulk.getId()) >> { throw new HotelException() }
        and: 'the second invocation returns data'
        1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                bulk.getId()) >> { new HashSet<>(Arrays.asList("ref1", "ref2")) }
        and: 'the bulk booking is not cancelled'
        !bulk.getCancelled()
        and: 'the references are stored'
        bulk.getReferences().size() == 2
    }

//    @Test
//    public void maxHotelException(@Mocked final HotelInterface hotelInterface) {
//        new Expectations() {
//            {
//                hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
//                        bulk.getId())
//                result = new HotelException()
//            }
//        }
//
//        bulk.processBooking()
//        bulk.processBooking()
//        bulk.processBooking()
//        bulk.processBooking()
//
//        assertEquals(0, bulk.getReferences().size())
//    }
//
//    @Test
//    public void maxMinusOneHotelException(@Mocked final HotelInterface hotelInterface) {
//        new Expectations() {
//            {
//                hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
//                        bulk.getId())
//                result = new Delegate() {
//                    int i = 0
//
//                    Set<String> delegate() {
//                        i++
//                        if (i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
//                            throw new HotelException()
//                        } else {
//                            return new HashSet<>(Arrays.asList("ref1", "ref2"))
//                        }
//                    }
//                }
//            }
//        }
//
//        bulk.processBooking()
//        bulk.processBooking()
//        bulk.processBooking()
//
//        assertEquals(2, bulk.getReferences().size())
//    }
//
//    @Test
//    public void hotelExceptionValueIsResetBySuccess(@Mocked final HotelInterface hotelInterface) {
//        new Expectations() {
//            {
//                hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
//                        bulk.getId())
//                result = new Delegate() {
//                    int i = 0
//
//                    Set<String> delegate() {
//                        i++
//                        if (i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
//                            throw new HotelException()
//                        } else if (i == BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
//                            return new HashSet<>(Arrays.asList("ref1", "ref2"))
//                        } else if (i < 2 * BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
//                            throw new HotelException()
//                        } else {
//                            return new HashSet<>(Arrays.asList("ref3", "ref4"))
//                        }
//                    }
//                }
//            }
//        }
//
//        bulk.processBooking()
//        bulk.processBooking()
//        bulk.processBooking()
//        bulk.processBooking()
//        bulk.processBooking()
//        bulk.processBooking()
//
//        assertEquals(2, bulk.getReferences().size())
//    }
//
//    @Test
//    public void hotelExceptionValueIsResetByRemoteException(@Mocked final HotelInterface hotelInterface) {
//        new Expectations() {
//            {
//                hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
//                        bulk.getId())
//                result = new Delegate() {
//                    int i = 0
//
//                    Set<String> delegate() {
//                        i++
//                        if (i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
//                            throw new HotelException()
//                        } else if (i == BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
//                            throw new RemoteAccessException()
//                        } else if (i < 2 * BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
//                            throw new HotelException()
//                        } else {
//                            return new HashSet<>(Arrays.asList("ref1", "ref2"))
//                        }
//                    }
//                }
//            }
//        }
//
//        bulk.processBooking()
//        bulk.processBooking()
//        bulk.processBooking()
//        bulk.processBooking()
//        bulk.processBooking()
//        bulk.processBooking()
//
//        assertEquals(2, bulk.getReferences().size())
//    }
//
//    @Test
//    public void oneRemoteException(@Mocked final HotelInterface hotelInterface) {
//        new Expectations() {
//            {
//                hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
//                        bulk.getId())
//                result = new RemoteAccessException()
//                result = new HashSet<>(Arrays.asList("ref1", "ref2"))
//            }
//        }
//
//        bulk.processBooking()
//        bulk.processBooking()
//
//        assertEquals(2, bulk.getReferences().size())
//    }
//
//    @Test
//    public void maxRemoteException(@Mocked final HotelInterface hotelInterface) {
//        new Expectations() {
//            {
//                hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
//                        bulk.getId())
//                result = new RemoteAccessException()
//            }
//        }
//
//        for (int i = 0 i < BulkRoomBooking.MAX_REMOTE_ERRORS i++) {
//            bulk.processBooking()
//        }
//        bulk.processBooking()
//
//        assertEquals(0, bulk.getReferences().size())
//    }
//
//    @Test
//    public void maxMinusOneRemoteException(@Mocked final HotelInterface hotelInterface) {
//        new Expectations() {
//            {
//                hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
//                        bulk.getId())
//                result = new Delegate() {
//                    int i = 0
//
//                    Set<String> delegate() {
//                        i++
//                        if (i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
//                            throw new RemoteAccessException()
//                        } else {
//                            return new HashSet<>(Arrays.asList("ref1", "ref2"))
//                        }
//                    }
//                }
//                result = new RemoteAccessException()
//                times = BulkRoomBooking.MAX_REMOTE_ERRORS - 1
//
//                hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
//                        bulk.getId())
//                result = new HashSet<>(Arrays.asList("ref1", "ref2"))
//            }
//        }
//
//        for (int i = 0 i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1 i++) {
//            bulk.processBooking()
//        }
//        bulk.processBooking()
//
//        assertEquals(2, bulk.getReferences().size())
//    }
//
//    @Test
//    public void remoteExceptionValueIsResetBySuccess(@Mocked final HotelInterface hotelInterface) {
//        new Expectations() {
//            {
//                hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
//                        bulk.getId())
//                result = new Delegate() {
//                    int i = 0
//
//                    Set<String> delegate() {
//                        i++
//                        if (i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
//                            throw new RemoteAccessException()
//                        } else if (i == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
//                            return new HashSet<>(Arrays.asList("ref1", "ref2"))
//                        } else if (i < 2 * BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
//                            throw new RemoteAccessException()
//                        } else {
//                            return new HashSet<>(Arrays.asList("ref3", "ref4"))
//                        }
//                    }
//                }
//            }
//        }
//
//        for (int i = 0 i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1 i++) {
//            bulk.processBooking()
//        }
//        bulk.processBooking()
//        for (int i = 0 i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1 i++) {
//            bulk.processBooking()
//        }
//        bulk.processBooking()
//
//        assertEquals(2, bulk.getReferences().size())
//    }
//
//    @Test
//    public void remoteExceptionValueIsResetByHotelException(@Mocked final HotelInterface hotelInterface) {
//        new Expectations() {
//            {
//                hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
//                        bulk.getId())
//                result = new Delegate() {
//                    int i = 0
//
//                    Set<String> delegate() {
//                        i++
//                        if (i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
//                            throw new RemoteAccessException()
//                        } else if (i == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
//                            throw new HotelException()
//                        } else if (i < 2 * BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
//                            throw new RemoteAccessException()
//                        } else {
//                            return new HashSet<>(Arrays.asList("ref1", "ref2"))
//                        }
//                    }
//                }
//            }
//        }
//
//        for (int i = 0 i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1 i++) {
//            bulk.processBooking()
//        }
//        bulk.processBooking()
//        for (int i = 0 i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1 i++) {
//            bulk.processBooking()
//        }
//        bulk.processBooking()
//
//        assertEquals(2, bulk.getReferences().size())
//    }

}
