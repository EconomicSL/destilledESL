package contracts;

import actions.Action;
import agents.Agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Other extends Contract {
    private Agent assetParty;
    private Agent liabilityParty;
    private double amount;

    public Other(Agent assetParty, Agent liabilityParty, double amount) {
        this.assetParty = assetParty;
        this.liabilityParty = liabilityParty;
        this.amount = amount;
    }

    @Override
    public Agent getAssetParty() {
        return assetParty;
    }

    @Override
    public Agent getLiabilityParty() {
        return liabilityParty;
    }

    public double getValue() {
        return amount;
    }

    @Override
    public List<Action> getAvailableActions(Agent me) {
        return Collections.emptyList();
    }
}
