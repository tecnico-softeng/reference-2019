package pt.ulisboa.tecnico.softeng.car.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.car.domain.RentACar;

public class RentACarData {
    private String code;
    private String name;
    private String nif;
    private String iban;
    private Integer numVehicles;
    private int numPending;

    public RentACarData() {
    }

    public RentACarData(RentACar rentACar) {
        this.code = rentACar.getCode();
        this.name = rentACar.getName();
        this.nif = rentACar.getNif();
        this.iban = rentACar.getIban();
        this.numVehicles = rentACar.getVehicleSet().size();
        this.numPending = rentACar.getProcessor().getRentingSet().size();
    }

    public Integer getNumVehicles() {
        return this.numVehicles;
    }

    public void setNumVehicles(int numVehicles) {
        this.numVehicles = numVehicles;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNif() {
        return this.nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getIban() {
        return this.iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public int getNumPending() {
        return this.numPending;
    }

    public void setNumPending(int numPending) {
        this.numPending = numPending;
    }
}
