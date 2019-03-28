package pt.ulisboa.tecnico.softeng.bank.services.remote.dataobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.joda.time.DateTime;
import pt.ulisboa.tecnico.softeng.bank.domain.Bank;
import pt.ulisboa.tecnico.softeng.bank.domain.TransferOperation;

public class RestBankOperationData {
    private String reference;
    private String type;
    private String sourceIban;
    private String targetIban;
    private Double value;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private DateTime time;
    private String transactionSource;
    private String transactionReference;

    public RestBankOperationData() {
    }

    public RestBankOperationData(TransferOperation operation) {
        this.reference = operation.getReference();
        this.type = operation.getType().name();
        this.sourceIban = operation.getWithdrawOperation().getAccount().getIban();
        this.sourceIban = operation.getDepositOperation().getAccount().getIban();
        this.value = new Double(operation.getWithdrawOperation().getValue()) / Bank.SCALE;
        this.time = operation.getTime();
        this.transactionSource = operation.getTransactionSource();
        this.transactionReference = operation.getTransactionReference();
    }

    public RestBankOperationData(String sourceIban, String targetIban, long value, String transactionSource, String transactionReference) {
        this.sourceIban = sourceIban;
        this.targetIban = targetIban;
        this.value = new Double(value) / Bank.SCALE;
        this.transactionSource = transactionSource;
        this.transactionReference = transactionReference;
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetIban() {
        return this.targetIban;
    }

    public void setTargetIban(String targetIban) {
        this.targetIban = targetIban;
    }

    public long getValue() {
        return Math.round(this.value * Bank.SCALE);
    }

    public void setValue(long value) {
        this.value = Long.valueOf(value).doubleValue() * Bank.SCALE;
    }

    public DateTime getTime() {
        return this.time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public String getTransactionSource() {
        return this.transactionSource;
    }

    public void setTransactionSource(String transactionSource) {
        this.transactionSource = transactionSource;
    }

    public String getTransactionReference() {
        return this.transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

}
