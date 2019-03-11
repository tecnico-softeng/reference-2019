package pt.ulisboa.tecnico.softeng.broker.domain;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.*;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.*;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.*;

import static org.junit.Assert.assertEquals;

@RunWith(JMockit.class)
public class AdventureSequenceTest extends RollbackTestAbstractClass {
    private RestActivityBookingData bookingActivityData;
    private RestRoomBookingData bookingRoomData;
    private RestRentingData rentingData;

    @Override
    public void populate4Test() {
        this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                new ActivityInterface(), new HotelInterface(), new CarInterface(), new BankInterface(), new TaxInterface());
        this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);

        this.bookingActivityData = new RestActivityBookingData();
        this.bookingActivityData.setReference(ACTIVITY_CONFIRMATION);
        this.bookingActivityData.setPrice(70.0);
        this.bookingActivityData.setPaymentReference(PAYMENT_CONFIRMATION);
        this.bookingActivityData.setInvoiceReference(INVOICE_REFERENCE);

        this.bookingRoomData = new RestRoomBookingData();
        this.bookingRoomData.setReference(ROOM_CONFIRMATION);
        this.bookingRoomData.setPrice(80.0);
        this.bookingRoomData.setPaymentReference(PAYMENT_CONFIRMATION);
        this.bookingRoomData.setInvoiceReference(INVOICE_REFERENCE);

        this.rentingData = new RestRentingData();
        this.rentingData.setReference(RENTING_CONFIRMATION);
        this.rentingData.setPrice(60.0);
        this.rentingData.setPaymentReference(PAYMENT_CONFIRMATION);
        this.rentingData.setInvoiceReference(INVOICE_REFERENCE);
    }

    @Test
    public void successSequence(@Mocked final TaxInterface taxInterface, @Mocked final BankInterface bankInterface,
                                @Mocked final ActivityInterface activityInterface, @Mocked final HotelInterface hotelInterface,
                                @Mocked final CarInterface carInterface) {
        // Testing: book activity, hotel, car, pay, tax, confirm
        new Expectations() {
            {
                activityInterface.reserveActivity((RestActivityBookingData) this.any);
                this.result = AdventureSequenceTest.this.bookingActivityData;

                hotelInterface.reserveRoom((RestRoomBookingData) this.any);
                this.result = AdventureSequenceTest.this.bookingRoomData;

                carInterface.rentCar((CarInterface.Type) this.any, this.anyString, this.anyString, this.anyString,
                        (LocalDate) this.any, (LocalDate) this.any, this.anyString);
                this.result = AdventureSequenceTest.this.rentingData;

                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = PAYMENT_CONFIRMATION;

                taxInterface.submitInvoice((RestInvoiceData) this.any);
                this.result = INVOICE_DATA;

                bankInterface.getOperationData(PAYMENT_CONFIRMATION);

                activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);
                this.result = AdventureSequenceTest.this.bookingActivityData;

                carInterface.getRentingData(RENTING_CONFIRMATION);
                this.result = AdventureSequenceTest.this.rentingData;

                hotelInterface.getRoomBookingData(ROOM_CONFIRMATION);
                this.result = AdventureSequenceTest.this.bookingRoomData;
            }
        };

        Adventure adventure = new Adventure(this.broker, ARRIVAL, DEPARTURE, this.client, MARGIN, true);

        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();

        assertEquals(State.CONFIRMED, adventure.getState().getValue());
    }

    @Test
    public void successSequenceOneNoCar(@Mocked final TaxInterface taxInterface,
                                        @Mocked final BankInterface bankInterface, @Mocked final ActivityInterface activityInterface,
                                        @Mocked final HotelInterface hotelInterface, @Mocked CarInterface carInterface) {
        // Testing: book activity, hotel, pay, tax, confirm
        new Expectations() {
            {
                activityInterface.reserveActivity((RestActivityBookingData) this.any);
                this.result = AdventureSequenceTest.this.bookingActivityData;

                hotelInterface.reserveRoom((RestRoomBookingData) this.any);
                this.result = AdventureSequenceTest.this.bookingRoomData;

                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = PAYMENT_CONFIRMATION;

                taxInterface.submitInvoice((RestInvoiceData) this.any);
                this.result = INVOICE_DATA;

                bankInterface.getOperationData(PAYMENT_CONFIRMATION);

                activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);
                this.result = AdventureSequenceTest.this.bookingActivityData;

                hotelInterface.getRoomBookingData(ROOM_CONFIRMATION);
                this.result = AdventureSequenceTest.this.bookingRoomData;
            }
        };

        Adventure adventure = new Adventure(this.broker, ARRIVAL, DEPARTURE, this.client, MARGIN);

        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();

        Assert.assertEquals(State.CONFIRMED, adventure.getState().getValue());
    }

    @Test
    public void successSequenceNoHotel(@Mocked final TaxInterface taxInterface,
                                       @Mocked final BankInterface bankInterface, @Mocked final ActivityInterface activityInterface,
                                       @Mocked final HotelInterface hotelInterface, @Mocked CarInterface carInterface) {

        // Testing: book activity, car, pay, tax, confirm
        new Expectations() {
            {
                activityInterface.reserveActivity((RestActivityBookingData) this.any);
                this.result = AdventureSequenceTest.this.bookingActivityData;

                carInterface.rentCar((CarInterface.Type) this.any, this.anyString, this.anyString, this.anyString,
                        (LocalDate) this.any, (LocalDate) this.any, this.anyString);
                this.result = AdventureSequenceTest.this.rentingData;

                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = PAYMENT_CONFIRMATION;

                taxInterface.submitInvoice((RestInvoiceData) this.any);
                this.result = INVOICE_DATA;

                bankInterface.getOperationData(PAYMENT_CONFIRMATION);

                activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);
                this.result = AdventureSequenceTest.this.bookingActivityData;

                carInterface.getRentingData(RENTING_CONFIRMATION);
                this.result = AdventureSequenceTest.this.rentingData;
            }
        };

        Adventure adventure = new Adventure(this.broker, ARRIVAL, ARRIVAL, this.client, MARGIN, true);

        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();

        Assert.assertEquals(State.CONFIRMED, adventure.getState().getValue());
    }

    @Test
    public void successSequenceNoHotelNoCar(@Mocked final TaxInterface taxInterface,
                                            @Mocked final BankInterface bankInterface, @Mocked final ActivityInterface activityInterface,
                                            @Mocked final HotelInterface hotelInterface) {
        // Testing: book activity, pay, tax, confirm
        new Expectations() {
            {
                activityInterface.reserveActivity((RestActivityBookingData) this.any);
                this.result = AdventureSequenceTest.this.bookingActivityData;

                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = PAYMENT_CONFIRMATION;

                taxInterface.submitInvoice((RestInvoiceData) this.any);
                this.result = INVOICE_DATA;

                bankInterface.getOperationData(PAYMENT_CONFIRMATION);

                activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);
                this.result = AdventureSequenceTest.this.bookingActivityData;
            }
        };

        Adventure adventure = new Adventure(this.broker, ARRIVAL, ARRIVAL, this.client, MARGIN);

        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();

        Assert.assertEquals(State.CONFIRMED, adventure.getState().getValue());
    }

    @Test
    public void unsuccessSequenceFailActivity(@Mocked final TaxInterface taxInterface,
                                              @Mocked final BankInterface bankInterface, @Mocked final ActivityInterface activityInterface,
                                              @Mocked final HotelInterface hotelInterface) {
        // Testing: fail activity, undo, cancelled
        new Expectations() {
            {
                activityInterface.reserveActivity((RestActivityBookingData) this.any);
                this.result = new ActivityException();
            }
        };

        Adventure adventure = new Adventure(this.broker, ARRIVAL, DEPARTURE, this.client, MARGIN);

        adventure.process();
        adventure.process();

        Assert.assertEquals(State.CANCELLED, adventure.getState().getValue());
    }

    @Test
    public void unsuccessSequenceFailHotel(@Mocked TaxInterface taxInterface, @Mocked final BankInterface bankInterface,
                                           @Mocked final ActivityInterface activityInterface, @Mocked final HotelInterface hotelInterface) {
        // Testing: activity, fail hotel, undo, cancelled
        new Expectations() {
            {
                activityInterface.reserveActivity((RestActivityBookingData) this.any);
                this.result = AdventureSequenceTest.this.bookingActivityData;

                hotelInterface.reserveRoom((RestRoomBookingData) this.any);
                this.result = new HotelException();

                activityInterface.cancelReservation(ACTIVITY_CONFIRMATION);
                this.result = ACTIVITY_CANCELLATION;
            }
        };

        Adventure adventure = new Adventure(this.broker, ARRIVAL, DEPARTURE, this.client, MARGIN);

        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();

        Assert.assertEquals(State.CANCELLED, adventure.getState().getValue());
    }

    @Test
    public void unsuccessSequenceFailCar(@Mocked final TaxInterface taxInterface,
                                         @Mocked final BankInterface bankInterface, @Mocked final ActivityInterface activityInterface,
                                         @Mocked final HotelInterface hotelInterface, @Mocked final CarInterface carInterface) {
        // Testing: activity, fail car, undo, cancelled
        new Expectations() {
            {
                activityInterface.reserveActivity((RestActivityBookingData) this.any);
                this.result = AdventureSequenceTest.this.bookingActivityData;

                carInterface.rentCar((CarInterface.Type) this.any, this.anyString, this.anyString, this.anyString,
                        (LocalDate) this.any, (LocalDate) this.any, this.anyString);
                this.result = new CarException();

                activityInterface.cancelReservation(ACTIVITY_CONFIRMATION);
                this.result = ACTIVITY_CANCELLATION;
            }
        };

        Adventure adventure = new Adventure(this.broker, ARRIVAL, ARRIVAL, this.client, MARGIN, true);

        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();

        Assert.assertEquals(State.CANCELLED, adventure.getState().getValue());
    }

    @Test
    public void unsuccessSequenceFailPayment(@Mocked TaxInterface taxInterface,
                                             @Mocked final BankInterface bankInterface, @Mocked final ActivityInterface activityInterface,
                                             @Mocked final HotelInterface hotelInterface, @Mocked final CarInterface carInterface) {

        // Testing: activity, room, car, fail payment, undo, cancelled
        new Expectations() {
            {
                activityInterface.reserveActivity((RestActivityBookingData) this.any);
                this.result = AdventureSequenceTest.this.bookingActivityData;

                hotelInterface.reserveRoom((RestRoomBookingData) this.any);
                this.result = AdventureSequenceTest.this.bookingRoomData;

                carInterface.rentCar((CarInterface.Type) this.any, this.anyString, this.anyString, this.anyString,
                        (LocalDate) this.any, (LocalDate) this.any, this.anyString);
                this.result = AdventureSequenceTest.this.rentingData;

                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = new BankException();

                activityInterface.cancelReservation(this.anyString);
                this.result = ACTIVITY_CANCELLATION;

                hotelInterface.cancelBooking(ROOM_CONFIRMATION);
                this.result = ROOM_CANCELLATION;

                carInterface.cancelRenting(RENTING_CONFIRMATION);
                this.result = RENTING_CANCELLATION;
            }
        };

        Adventure adventure = new Adventure(this.broker, ARRIVAL, DEPARTURE, this.client, MARGIN, true);

        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();

        Assert.assertEquals(State.CANCELLED, adventure.getState().getValue());
    }

    @Test
    public void unsuccessSequenceFailTax(@Mocked TaxInterface taxInterface, @Mocked final BankInterface bankInterface,
                                         @Mocked final ActivityInterface activityInterface, @Mocked final HotelInterface hotelInterface,
                                         @Mocked final CarInterface carInterface) {
        // Testing: activity, room, car, payment, fail tax, undo, cancelled
        new Expectations() {
            {
                activityInterface.reserveActivity((RestActivityBookingData) this.any);
                this.result = AdventureSequenceTest.this.bookingActivityData;

                hotelInterface.reserveRoom((RestRoomBookingData) this.any);
                this.result = AdventureSequenceTest.this.bookingRoomData;

                carInterface.rentCar(CarInterface.Type.CAR, this.anyString, this.anyString, this.anyString,
                        (LocalDate) this.any, (LocalDate) this.any, this.anyString);
                this.result = AdventureSequenceTest.this.rentingData;

                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = PAYMENT_CONFIRMATION;

                taxInterface.submitInvoice((RestInvoiceData) this.any);
                this.result = new TaxException();

                activityInterface.cancelReservation(ACTIVITY_CONFIRMATION);
                this.result = ACTIVITY_CANCELLATION;

                hotelInterface.cancelBooking(ROOM_CONFIRMATION);
                this.result = ROOM_CANCELLATION;

                carInterface.cancelRenting(RENTING_CONFIRMATION);
                this.result = RENTING_CANCELLATION;

                bankInterface.cancelPayment(PAYMENT_CONFIRMATION);
                this.result = PAYMENT_CANCELLATION;
            }
        };

        Adventure adventure = new Adventure(this.broker, ARRIVAL, DEPARTURE, this.client, MARGIN, true);

        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();
        adventure.process();

        Assert.assertEquals(State.CANCELLED, adventure.getState().getValue());
    }

}