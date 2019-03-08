package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.*

class UndoStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
    def hotelInterface
    def bankInterface
    def taxInterface
    def broker
    def client
    def adventure

    def populate4Test() {
        hotelInterface = Mock(HotelInterface)
        bankInterface = Mock(BankInterface)
        taxInterface = Mock(TaxInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,
                new ActivityInterface(), hotelInterface, new CarInterface(), bankInterface, new TaxInterface())
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

//    @Test
//    public void failRevertPaymentBankException(@Mocked final BankInterface bankInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        new Expectations() {
//            {
//                BankInterface.cancelPayment(PAYMENT_CONFIRMATION)
//                result = new BankException()
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.UNDO, adventure.getState().getValue())
//    }
//
//    @Test
//    public void failRevertPaymentRemoteAccessException(@Mocked final BankInterface bankInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        new Expectations() {
//            {
//                BankInterface.cancelPayment(PAYMENT_CONFIRMATION)
//                result = new RemoteAccessException()
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.UNDO, adventure.getState().getValue())
//    }
//
//    @Test
//    public void successRevertActivity(@Mocked final BankInterface bankInterface,
//                                      @Mocked final ActivityInterface activityInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
//        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
//        new Expectations() {
//            {
//                ActivityInterface.cancelReservation(ACTIVITY_CONFIRMATION)
//                result = ACTIVITY_CANCELLATION
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.CANCELLED, adventure.getState().getValue())
//    }
//
//    @Test
//    public void failRevertActivityActivityException(@Mocked final BankInterface bankInterface,
//                                                    @Mocked final ActivityInterface activityInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
//        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
//        new Expectations() {
//            {
//                ActivityInterface.cancelReservation(ACTIVITY_CONFIRMATION)
//                result = new ActivityException()
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.UNDO, adventure.getState().getValue())
//    }
//
//    @Test
//    public void failRevertActivityRemoteException(@Mocked final BankInterface bankInterface,
//                                                  @Mocked final ActivityInterface activityInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
//        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
//        new Expectations() {
//            {
//                ActivityInterface.cancelReservation(ACTIVITY_CONFIRMATION)
//                result = new RemoteAccessException()
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.UNDO, adventure.getState().getValue())
//    }
//
//    @Test
//    public void successRevertRoomBooking(@Mocked final BankInterface bankInterface,
//                                         @Mocked final HotelInterface hotelInterface, @Mocked final CarInterface carInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
//        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
//        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
//        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
//        new Expectations() {
//            {
//                hotelInterface.cancelBooking(ROOM_CONFIRMATION)
//                result = ROOM_CANCELLATION
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.CANCELLED, adventure.getState().getValue())
//    }
//
//    @Test
//    public void successRevertRoomBookingHotelException(@Mocked final BankInterface bankInterface,
//                                                       @Mocked final HotelInterface hotelInterface, @Mocked final CarInterface carInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
//        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
//        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
//        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
//        new Expectations() {
//            {
//                hotelInterface.cancelBooking(ROOM_CONFIRMATION)
//                result = new HotelException()
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.UNDO, adventure.getState().getValue())
//    }
//
//    @Test
//    public void successRevertRoomBookingRemoteException(@Mocked final BankInterface bankInterface,
//                                                        @Mocked final HotelInterface hotelInterface, @Mocked final CarInterface carInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
//        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
//        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
//        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
//        new Expectations() {
//            {
//                hotelInterface.cancelBooking(ROOM_CONFIRMATION)
//                result = new RemoteAccessException()
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.UNDO, adventure.getState().getValue())
//    }
//
//    @Test
//    public void successRevertRentCar(@Mocked final BankInterface bankInterface,
//                                     @Mocked final HotelInterface roomInterface, @Mocked final CarInterface carInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
//        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
//        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
//        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
//        adventure.setRoomCancellation(ROOM_CANCELLATION)
//        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
//        new Expectations() {
//            {
//                CarInterface.cancelRenting(RENTING_CONFIRMATION)
//                result = RENTING_CANCELLATION
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.CANCELLED, adventure.getState().getValue())
//    }
//
//    @Test
//    public void failRevertRentCarCarException(@Mocked final BankInterface bankInterface,
//                                              @Mocked final HotelInterface roomInterface, @Mocked final CarInterface carInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
//        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
//        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
//        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
//        adventure.setRoomCancellation(ROOM_CANCELLATION)
//        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
//        new Expectations() {
//            {
//                CarInterface.cancelRenting(RENTING_CONFIRMATION)
//                result = new CarException()
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.UNDO, adventure.getState().getValue())
//    }
//
//    @Test
//    public void failRevertRentCarRemoteException(@Mocked final BankInterface bankInterface,
//                                                 @Mocked final HotelInterface roomInterface, @Mocked final CarInterface carInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
//        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
//        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
//        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
//        adventure.setRoomCancellation(ROOM_CANCELLATION)
//        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
//        new Expectations() {
//            {
//                CarInterface.cancelRenting(RENTING_CONFIRMATION)
//                result = new RemoteAccessException()
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.UNDO, adventure.getState().getValue())
//    }
//
//    @Test
//    public void successCancelInvoice(@Mocked final BankInterface bankInterface,
//                                     @Mocked final HotelInterface roomInterface, @Mocked final CarInterface carInterface,
//                                     @Mocked final TaxInterface taxInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
//        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
//        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
//        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
//        adventure.setRoomCancellation(ROOM_CANCELLATION)
//        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
//        adventure.setRentingCancellation(RENTING_CONFIRMATION)
//        adventure.setInvoiceReference(INVOICE_REFERENCE)
//        new Expectations() {
//            {
//                TaxInterface.cancelInvoice(INVOICE_REFERENCE)
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.CANCELLED, adventure.getState().getValue())
//    }
//
//    @Test
//    public void failCancelInvoiceTaxException(@Mocked final BankInterface bankInterface,
//                                              @Mocked final HotelInterface roomInterface, @Mocked final CarInterface carInterface,
//                                              @Mocked final TaxInterface taxInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
//        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
//        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
//        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
//        adventure.setRoomCancellation(ROOM_CANCELLATION)
//        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
//        adventure.setRentingCancellation(RENTING_CONFIRMATION)
//        adventure.setInvoiceReference(INVOICE_REFERENCE)
//        new Expectations() {
//            {
//                TaxInterface.cancelInvoice(INVOICE_REFERENCE)
//                result = new TaxException()
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.UNDO, adventure.getState().getValue())
//    }
//
//    @Test
//    public void failCancelInvoiceRemoteException(@Mocked final BankInterface bankInterface,
//                                                 @Mocked final HotelInterface roomInterface, @Mocked final CarInterface carInterface,
//                                                 @Mocked final TaxInterface taxInterface) {
//        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
//        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
//        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
//        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
//        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
//        adventure.setRoomCancellation(ROOM_CANCELLATION)
//        adventure.setRentingConfirmation(RENTING_CANCELLATION)
//        adventure.setRentingCancellation(RENTING_CANCELLATION)
//        adventure.setInvoiceReference(INVOICE_REFERENCE)
//        new Expectations() {
//            {
//                TaxInterface.cancelInvoice(INVOICE_REFERENCE)
//                result = new RemoteAccessException()
//            }
//        }
//
//        adventure.process()
//
//        Assert.assertEquals(Adventure.State.UNDO, adventure.getState().getValue())
//    }

}
