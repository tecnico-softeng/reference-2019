package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;

public class Adventure extends Adventure_Base {
    public enum RoomType {
        SINGLE, DOUBLE
    }

    public enum VehicleType {
        CAR, MOTORCYCLE
    }

    public enum State {
        PROCESS_PAYMENT, RESERVE_ACTIVITY, BOOK_ROOM, RENT_VEHICLE, UNDO, CONFIRMED, CANCELLED, TAX_PAYMENT
    }

    public Adventure(Broker broker, LocalDate begin, LocalDate end, Client client, double margin, RoomType roomType, VehicleType vehicleType) {
        checkArguments(broker, begin, end, client, margin, roomType, vehicleType);

        setID(broker.getCode() + broker.getCounter());
        setBegin(begin);
        setEnd(end);
        setMargin(margin);
        setClient(client);
        setRoomType(roomType);
        setVehicleType(vehicleType);
        broker.addAdventure(this);
        setBroker(broker);

        setCurrentAmount(0.0);
        setTime(DateTime.now());

        setState(State.RESERVE_ACTIVITY);
    }

    void delete() {
        setBroker(null);
        setClient(null);

        getState().delete();

        deleteDomainObject();
    }

    private void checkArguments(Broker broker, LocalDate begin, LocalDate end, Client client, double margin, RoomType roomType, VehicleType vehicleType) {
        if (client == null || broker == null || begin == null || end == null) {
            throw new BrokerException();
        }

        if (end.isBefore(begin)) {
            throw new BrokerException();
        }

        if (client.getAge() < 18 || client.getAge() > 100) {
            throw new BrokerException();
        }

        if (margin <= 0 || margin > 1) {
            throw new BrokerException();
        }

        if (roomType != null && begin.equals(end)) {
            throw new BrokerException();
        }
    }

    public int getAge() {
        return getClient().getAge();
    }

    public String getIban() {
        return getClient().getIban();
    }

    void incAmountToPay(double toPay) {
        setCurrentAmount(getCurrentAmount() + toPay);
    }

    double getAmount() {
        return getCurrentAmount() * (1 + getMargin());
    }

    public void setState(State state) {
        if (getState() != null) {
            getState().delete();
        }

        switch (state) {
            case RESERVE_ACTIVITY:
                setState(new ReserveActivityState());
                break;
            case BOOK_ROOM:
                setState(new BookRoomState());
                break;
            case RENT_VEHICLE:
                setState(new RentVehicleState());
                break;
            case PROCESS_PAYMENT:
                setState(new ProcessPaymentState());
                break;
            case TAX_PAYMENT:
                setState(new TaxPaymentState());
                break;
            case UNDO:
                setState(new UndoState());
                break;
            case CONFIRMED:
                setState(new ConfirmedState());
                break;
            case CANCELLED:
                setState(new CancelledState());
                break;
            default:
                new BrokerException();
                break;
        }
    }

    public void process() {
        // logger.debug("process ID:{}, state:{} ", this.ID, getState().name());
        getState().process();
    }

    boolean shouldCancelRoom() {
        return getRoomConfirmation() != null && getRoomCancellation() == null;
    }

    boolean roomIsCancelled() {
        return !shouldCancelRoom();
    }

    boolean shouldCancelActivity() {
        return getActivityConfirmation() != null && getActivityCancellation() == null;
    }

    boolean activityIsCancelled() {
        return !shouldCancelActivity();
    }

    boolean shouldCancelPayment() {
        return getPaymentConfirmation() != null && getPaymentCancellation() == null;
    }

    boolean paymentIsCancelled() {
        return !shouldCancelPayment();
    }

    boolean shouldCancelVehicleRenting() {
        return getRentingConfirmation() != null && getRentingCancellation() == null;
    }

    boolean rentingIsCancelled() {
        return !shouldCancelVehicleRenting();
    }

    boolean shouldCancelInvoice() {
        return getInvoiceReference() != null && !getInvoiceCancelled();
    }

    boolean invoiceIsCancelled() {
        return !shouldCancelInvoice();
    }

}
