package pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import pt.ulisboa.tecnico.softeng.tax.domain.IRS;
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class InvoiceData {
    public enum Type {SELL, BUY}

    private Type type;
    private String otherNif;
    private String reference;
    private String sellerNif;
    private String buyerNif;
    private String itemType;
    private Double value;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Double iva;
    private DateTime time;

    public InvoiceData() {
    }

    public InvoiceData(String reference, String sellerNif, String buyerNif, String itemType,
                       long value, LocalDate date, DateTime time) {
        if (reference == null) {
            throw new TaxException();
        }
        this.reference = reference;
        this.sellerNif = sellerNif;
        this.buyerNif = buyerNif;
        this.itemType = itemType;
        this.value = new Double(value) / IRS.SCALE;
        this.date = date;
        this.time = time;
    }

    public InvoiceData(Invoice invoice) {
        this.reference = invoice.getReference();
        this.sellerNif = invoice.getSeller().getNif();
        this.buyerNif = invoice.getBuyer().getNif();
        this.itemType = invoice.getItemType().getName();
        this.value = new Double(invoice.getValue()) / IRS.SCALE;
        this.date = invoice.getDate();
        this.iva = new Double(invoice.getIva()) / IRS.SCALE;
        this.time = invoice.getTime();
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getOtherNif() {
        return this.otherNif;
    }

    public void setOtherNif(String otherNif) {
        this.otherNif = otherNif;
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSellerNif() {
        return this.sellerNif;
    }

    public void setSellerNif(String sellerNif) {
        this.sellerNif = sellerNif;
    }

    public String getBuyerNif() {
        return this.buyerNif;
    }

    public void setBuyerNif(String buyerNif) {
        this.buyerNif = buyerNif;
    }

    public String getItemType() {
        return this.itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Double getValue() {
        return this.value;
    }

    public long getValueLong() {
        return Math.round(getValue() * IRS.SCALE);
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getIva() {
        return this.iva;
    }

    public long getIvaLong() {
        return Math.round(getIva() * IRS.SCALE);
    }

    public void setIva(Double iva) {
        this.iva = iva;
    }

    public DateTime getTime() {
        return this.time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

}
