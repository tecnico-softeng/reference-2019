package pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.joda.time.LocalDate;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;


public class RoomBookingData {
    private String reference;
    private String cancellation;
    private String hotelName;
    private String hotelCode;
    private String roomNumber;
    private String bookRoom;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate arrival;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate departure;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate cancellationDate;
    private double price;

    public RoomBookingData() {
    }

    public RoomBookingData(RestRoomBookingData restRoomBookingData) {
        this.reference = restRoomBookingData.getReference();
        this.hotelCode = restRoomBookingData.getHotelCode();
        this.hotelName = restRoomBookingData.getHotelName();
        this.roomNumber = restRoomBookingData.getRoomNumber();
        this.bookRoom = restRoomBookingData.getBookRoom();
        this.arrival = restRoomBookingData.getArrival();
        this.departure = restRoomBookingData.getDeparture();
        this.price = new Double(restRoomBookingData.getPrice()) / Adventure.SCALE;
        this.cancellation = restRoomBookingData.getCancellation();
        this.cancellationDate = restRoomBookingData.getCancellationDate();
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCancellation() {
        return this.cancellation;
    }

    public void setCancellation(String cancellation) {
        this.cancellation = cancellation;
    }

    public String getHotelName() {
        return this.hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getHotelCode() {
        return this.hotelCode;
    }

    public void setHotelCode(String hotelCode) {
        this.hotelCode = hotelCode;
    }

    public String getRoomNumber() {
        return this.roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBookRoom() {
        return this.bookRoom;
    }

    public void setBookRoom(String bookRoom) {
        this.bookRoom = bookRoom;
    }

    public LocalDate getArrival() {
        return this.arrival;
    }

    public void setArrival(LocalDate arrival) {
        this.arrival = arrival;
    }

    public LocalDate getDeparture() {
        return this.departure;
    }

    public void setDeparture(LocalDate departure) {
        this.departure = departure;
    }

    public LocalDate getCancellationDate() {
        return this.cancellationDate;
    }

    public void setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
