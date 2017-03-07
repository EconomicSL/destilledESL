package actions;

import agents.Bank;
import contracts.Loan;

import static java.lang.Math.min;

/**
 * the Payloan action represents the chance to pay back a loan that the agent has on its liability side.
 *
 * Its perform function takes one parameter: amount.
 */
public class PayLoan extends Action {

    private Loan loan;

    public PayLoan(Loan loan) {
        this.loan = loan;
        setAmount(0.0);
    }

    @Override
    public void perform() {
        Bank borrower = (Bank) loan.getLiabilityParty();

        // changes the accounts
        // TODO: Important! This bit here can cause the borrower to raise liquidity immediately.
        // Todo: we probably need an exception to guard against defaults.
        borrower.getMainLedger().payLiability(getAmount(), loan);


        if (loan.getAssetParty()!= null) {
            Bank lender = (Bank) loan.getAssetParty();
            lender.getMainLedger().pullFunding(getAmount(), loan);
        }

        // changes the contract
        loan.reducePrincipal(getAmount());

    }

    public double getMax() {
        return loan.getValue();
    }

    @Override
    public void print() {
        System.out.println("PayLoan action by "+loan.getLiabilityParty().getName()+" -> amount: "+String.format( "%.2f", getAmount()));
    }

    public String getName() {
        return "PayLoan";
    }

    public Loan getLoan() {
        return loan;
    }
}