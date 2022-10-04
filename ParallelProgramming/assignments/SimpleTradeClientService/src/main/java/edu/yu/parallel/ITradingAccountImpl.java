package edu.yu.parallel;

public class ITradingAccountImpl implements ITradingAccount {

    double cash = 1000000;
    double security = 0;

    @Override
    public synchronized double getCashBalance() {
        return cash;
    }

    @Override
    public synchronized double getPositionBalance() {
        return security;
    }

    @Override
    public synchronized void Buy(double amount) {
        double before = getPositionBalance();
        this.cash -= amount;
        this.security += amount;
        double after = getPositionBalance();
        //System.out.printf("PrevPos=%1.2f,BUY=%1.2f,NewPos=%1.2f", before, amount, after);

    }

    @Override
    public synchronized void Sell(double amount) {
        double before = getPositionBalance();
        this.cash += amount;
        this.security -= amount;
        double after = getPositionBalance();
        //System.out.printf("PrevPos=%1.2f,BUY=%1.2f,NewPos=%1.2f", before, amount, after);
    }

    @Override
    public synchronized String getBalanceString() {
        return String.format("Cash=%1.2f,Positions=%1.2f,Total=%1.2f", cash, security, cash + security);
    }
}
