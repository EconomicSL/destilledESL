package test;

import doubleEntryComponents.Bank;
import doubleEntryComponents.actions.LCR_Constraint;
import doubleEntryComponents.behaviours.BankBehaviour1;
import doubleEntryComponents.actions.LeverageConstraint;
import doubleEntryComponents.contracts.Asset;
import doubleEntryComponents.contracts.AssetMarket;
import doubleEntryComponents.contracts.Loan;

public class ShortSimulationDemo {

    public static AssetMarket assetMarket = new AssetMarket();

    void init() {
    }

    public static void main(String[] args) {
        Bank bank1 = new Bank("Bank 1");
        Bank bank2 = new Bank("Bank 2");
        Bank hedgeFund = new Bank("HedgeFund 1");

        initBank1(bank1);
        initBank2(bank2);
        initHedgefund(hedgeFund);
        initLoans(bank1, bank2, hedgeFund);

        initBehaviours(bank1, bank2, hedgeFund);

        runSchedule(bank1, bank2, hedgeFund);

    }

    private static void runSchedule(Bank bank1, Bank bank2, Bank hedgefund) {
        System.out.println("Time t=0.");
        bank1.printBalanceSheet();
        bank2.printBalanceSheet();
        hedgefund.printBalanceSheet();

        System.out.println("Shock arrives!");
        shockExternalAsset(1.0*(17-15)/17);
        updateAssetPrices(bank1, bank2, hedgefund);
        bank1.act();
        bank1.printBalanceSheet();

        updateAssetPrices(bank1, bank2, hedgefund);
        System.out.println("price of A1 :"+assetMarket.getPrice(Asset.AssetType.A1));

//        bank1.act();
//
        hedgefund.printBalanceSheet();
        hedgefund.act();
        hedgefund.printBalanceSheet();

        bank2.act();
        bank2.printBalanceSheet();
    }

    private static void initBehaviours(Bank bank1, Bank bank2, Bank hedgefund) {
        bank1.setBehaviour(new BankBehaviour1(bank1));
        bank2.setBehaviour(new BankBehaviour1(bank2));
        hedgefund.setBehaviour(new BankBehaviour1(hedgefund));

    }
    private static void initBank1(Bank bank) {
        bank.addCash(20);
        bank.add(new Asset(bank, Asset.AssetType.E, assetMarket, 17.0));
        bank.add(new Asset(bank, Asset.AssetType.A1, assetMarket, 40.0));
        bank.setLeverageConstraint(new LeverageConstraint(bank, 5.0/100, 4.0/100, 3.0/100));
        bank.setLCR_constraint(new LCR_Constraint(bank, 1.0, 1.0, 1.0, 20.0));
    }

    private static void initBank2(Bank bank) {
        bank.addCash(20);
        bank.add(new Asset(bank, Asset.AssetType.A2, assetMarket, 40.0));
        bank.add(new Asset(bank, Asset.AssetType.A3, assetMarket, 17.0));
        bank.setLeverageConstraint(new LeverageConstraint(bank, 5.0/100, 4.0/100, 3.0/100));
        bank.setLCR_constraint(new LCR_Constraint(bank, 1.0, 1.0, 1.0, 20.0));

    }

    private static void initHedgefund(Bank hedgefund) {
        hedgefund.addCash(100.0); //9
        hedgefund.add(new Asset(hedgefund, Asset.AssetType.A1, assetMarket, 20.0));
        hedgefund.add(new Asset(hedgefund, Asset.AssetType.A2, assetMarket, 20.0));
        hedgefund.setLeverageConstraint(new LeverageConstraint(hedgefund, 4.0/100, 3.0/100, 2.0/100));

    }

    private static void initLoans(Bank bank1, Bank bank2, Bank hedgefund) {
        Loan loan1H = new Loan(bank1,hedgefund,23.0);
        bank1.add(loan1H);
        hedgefund.add(loan1H);

        Loan loan2H = new Loan(bank2, hedgefund, 23.0);
        bank2.add(loan2H);
        hedgefund.add(loan2H);

        bank1.add(new Loan(null, bank1, 95.0));
        bank2.add(new Loan(null, bank2, 95.0));
    }

    private static void shockExternalAsset(double percentage) {
        assetMarket.setPriceE(assetMarket.getPrice(Asset.AssetType.E)*(1-percentage));
    }

    private static void updateAssetPrices(Bank bank1, Bank bank2, Bank hedgefund) {
        bank1.updateAssetPrices();
        bank2.updateAssetPrices();
        hedgefund.updateAssetPrices();
    }
}


