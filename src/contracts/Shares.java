package contracts;

import actions.Action;
import actions.RedeemShares;
import agents.Agent;
import agents.CanIssueShares;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This contract represents a bunch of shares of some Agent which can issue shares.
 */
public class Shares extends Contract {
    private Agent owner;
    private CanIssueShares issuer;
    private int nShares;
    private double previousValueOfShares;
    private double originalNAV;
    private int nSharesPendingToRedeem;

    public Shares(Agent owner, CanIssueShares issuer, int nShares, double originalNAV) {
        this.owner = owner;
        this.issuer = issuer;
        this.nShares = nShares;
        this.previousValueOfShares = getValue();
        this.originalNAV = originalNAV;
        this.nSharesPendingToRedeem = 0;

        assert(issuer instanceof Agent);
    }

    @Override
    public String getName(Agent me) {
        if (me==owner) {
            return "Shares of the firm: "+((Agent) issuer).getName();
        } else {
            return "Shares owned by our shareholder "+owner.getName();
        }
    }

    public void redeem(int numberToRedeem) {
        assert(numberToRedeem <= nShares);
        double nav = getNAV();
        ((Agent) issuer).payLiability(numberToRedeem * nav, this);
        owner.sellAssetForValue(this, numberToRedeem * nav);
        nShares -= numberToRedeem;
        nSharesPendingToRedeem -= numberToRedeem;
    }



    @Override
    public Agent getAssetParty() {
        return owner;
    }

    @Override
    public Agent getLiabilityParty() {
        return (Agent) issuer;
    }

    @Override
    public double getValue(Agent me) {
        return nShares * issuer.getNetAssetValue();
    }

    public double getNAV() { return issuer.getNetAssetValue(); }

    public int getnShares() {return nShares;}

    @Override
    public List<Action> getAvailableActions(Agent me) {
        if (!(me==owner) || !(nShares > 0)) return Collections.emptyList();

        ArrayList<Action> availableActions = new ArrayList<>();
        availableActions.add(new RedeemShares(this));
        return availableActions;
    }

    public void updateValue() {
        double valueChange = getValue() - previousValueOfShares;
        previousValueOfShares = getValue();

        if (valueChange > 0) {
            owner.appreciateAsset(this, valueChange);
            ((Agent) issuer).appreciateLiability(this, valueChange);
        } else if (valueChange < 0) {
            owner.devalueAsset(this, -1.0 * valueChange);
            ((Agent) issuer).devalueLiability(this, -1.0 * valueChange);
        }
    }

    public double getOriginalNAV() {
        return originalNAV;
    }

    public void addSharesPendingToRedeem(int number) {
        nSharesPendingToRedeem += number;
    }

    public int getnSharesPendingToRedeem() {
        return nSharesPendingToRedeem;
    }
}


