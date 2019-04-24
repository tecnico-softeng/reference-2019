package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class DepositOperation extends DepositOperation_Base {
    @Override
    public void delete() {
        TransferOperation transferOperation = getTransferOperationAsDeposit();
        if (transferOperation != null) {
            setTransferOperationAsDeposit(null);
            transferOperation.delete();
        }

        super.delete();
    }

    @Override
    public Operation.Type getType() {
        return Type.DEPOSIT;
    }

    @Override
    protected String doRevert() {
        if (!canRevert()) {
            throw new BankException();
        }
        return getAccount().withdraw(getValue()).getReference();
    }

    @Override
    public boolean isSubOperation() {
        return getTransferOperationAsDeposit() != null;
    }

}
