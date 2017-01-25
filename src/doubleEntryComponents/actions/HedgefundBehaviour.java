package doubleEntryComponents.actions;

import doubleEntryComponents.Bank;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * This class implements a Hedgefund's behaviour, which consists in:
 *  Perform all actions proportionally to initial holdings
 */
public class HedgefundBehaviour extends Behaviour {

    public HedgefundBehaviour(Bank bank) {
        super(bank);
    }

    @Override
    public Action getNextAction(ArrayList<Action> availableActions) {
        return null;
    }

    @Override
    protected ArrayList<Action> chooseActions(ArrayList<Action> availableActions) {

        ArrayList<Action> chosenActions = new ArrayList<>();
        double totalInitialHoldings = bank.getGeneralLedger().getAssetValue();

        for (Action action : availableActions) {
            assert((action instanceof PayLoan) || (action instanceof SellAsset));
            action.setAmount(1.0*action.getMax()/totalInitialHoldings);
            chosenActions.add(action);
        }

        return chosenActions;

    }
}
