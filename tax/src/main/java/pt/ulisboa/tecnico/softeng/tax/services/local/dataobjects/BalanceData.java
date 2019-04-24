package pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.tax.domain.IRS;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BalanceData {
    private List<Integer> years;
    private Map<Integer, Double> taxes;
    private Map<Integer, Double> returns;

    public BalanceData() {
        this.years = IRS.getIRSInstance().getTaxesPerYear().entrySet().stream().map(Map.Entry::getKey).sorted().collect(Collectors.toList());
        this.taxes = IRS.getIRSInstance().getTaxesPerYear().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new Double(e.getValue()) / IRS.SCALE));
        this.returns = IRS.getIRSInstance().getTaxesReturnPerYear().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new Double(e.getValue()) / IRS.SCALE));
    }

    public Map<Integer, Double> getTaxes() {
        return this.taxes;
    }

    public void setTaxes(Map<Integer, Double> taxes) {
        this.taxes = taxes;
    }

    public Map<Integer, Double> getReturns() {
        return this.returns;
    }

    public void setReturns(Map<Integer, Double> returns) {
        this.returns = returns;
    }

    public List<Integer> getYears() {
        return this.years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }
}
