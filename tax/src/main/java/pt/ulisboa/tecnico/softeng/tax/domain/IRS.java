package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

import java.util.Map;
import java.util.stream.Collectors;

public class IRS extends IRS_Base {
    public static final int SCALE = 1000;

    public static IRS getIRSInstance() {
        if (FenixFramework.getDomainRoot().getIrs() == null) {
            return createIrs();
        }
        return FenixFramework.getDomainRoot().getIrs();
    }

    @Atomic(mode = TxMode.WRITE)
    private static IRS createIrs() {
        return new IRS();
    }

    private IRS() {
        setRoot(FenixFramework.getDomainRoot());
    }

    public void delete() {
        setRoot(null);

        clearAll();

        deleteDomainObject();
    }

    public TaxPayer getTaxPayerByNif(String nif) {
        for (TaxPayer taxPayer : getTaxPayerSet()) {
            if (taxPayer.getNif().equals(nif)) {
                return taxPayer;
            }
        }
        return null;
    }

    public ItemType getItemTypeByName(String name) {
        for (ItemType itemType : getItemTypeSet()) {
            if (itemType.getName().equals(name)) {
                return itemType;
            }
        }
        return null;
    }

    private long taxes(int year) {
        if (year < 1970) {
            throw new TaxException();
        }

        long result = 0;
        for (Invoice invoice : getInvoiceSet()) {
            if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
                result = result + invoice.getIva();
            }
        }
        return result;
    }

    public Map<Integer, Long> getTaxesPerYear() {
        return getInvoiceSet().stream().map(i -> i.getDate().getYear()).distinct()
                .collect(Collectors.toMap(y -> y, this::taxes));
    }

    private long taxesReturn(int year) {
        return Math.round(taxes(year) * TaxPayer.PERCENTAGE / 100.0);
    }

    public Map<Integer, Long> getTaxesReturnPerYear() {
        return getInvoiceSet().stream().map(i -> i.getDate().getYear()).distinct()
                .collect(Collectors.toMap(y -> y, y -> taxesReturn(y)));
    }

    private void clearAll() {
        for (ItemType itemType : getItemTypeSet()) {
            itemType.delete();
        }

        for (TaxPayer taxPayer : getTaxPayerSet()) {
            taxPayer.delete();
        }

        for (Invoice invoice : getInvoiceSet()) {
            invoice.delete();
        }

    }

    @Override
    public int getCounter() {
        int counter = super.getCounter() + 1;
        setCounter(counter);
        return counter;
    }

}
