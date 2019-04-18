package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class WithdrawOperation extends WithdrawOperation_Base {
    @Override
    public void delete() {
        if (getTransferOperationAsWithdraw() != null) {
            TransferOperation transferOperation = getTransferOperationAsWithdraw();
            setTransferOperationAsWithdraw(null);
            transferOperation.delete();
        }

        super.delete();
    }

    @Override
    public Operation.Type getType() {
        return Type.WITHDRAW;
    }

    @Override
    protected String doRevert() {
        if (!canRevert()) {
            throw new BankException();
        }
        return getAccount().deposit(getValue()).getReference();
    }

    @Override
    public boolean isSubOperation() {
        return getTransferOperationAsWithdraw() != null;
    }
}
