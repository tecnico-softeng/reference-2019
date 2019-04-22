package pt.ulisboa.tecnico.softeng.car.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.car.domain.Processor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessorData {
    private RentACarData rentacar;
    private List<RentingData> pendingRentings;

    public ProcessorData(Processor processor) {
        this.rentacar = new RentACarData(processor.getRentACar());
        this.pendingRentings = processor.getRentingSet().stream().map(renting -> new RentingData(renting))
                .sorted(Comparator.comparing(RentingData::getBegin)).collect(Collectors.toList());
    }


    public RentACarData getRentacar() {
        return this.rentacar;
    }

    public void setRentACar(RentACarData rentACar) {
        this.rentacar = rentACar;
    }

    public List<RentingData> getPendingRentings() {
        return this.pendingRentings;
    }

    public void setPendingRentings(List<RentingData> pendingRentings) {
        this.pendingRentings = pendingRentings;
    }
}
