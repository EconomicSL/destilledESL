package contracts;

import demos.BoEDemo;
import demos.Parameters;

import java.util.*;

public class AssetMarket {
    private HashMap<Asset.AssetType, Double> prices;
    private HashMap<Asset.AssetType, Double> oldPrices;
    private HashMap<Asset.AssetType, Double> priceImpacts;
    private HashMap<Asset.AssetType, Double> amountsSold;
    private HashMap<Asset.AssetType, Double> haircuts;
    private HashMap<Asset.AssetType, Double> totalAmountsSold;
    private HashSet<Order> orderbook;

    public AssetMarket() {
        prices = new HashMap<>();
        priceImpacts = new HashMap<>();
        amountsSold = new HashMap<>();
        haircuts = new HashMap<>();
        totalAmountsSold = new HashMap<>();
        orderbook = new HashSet<>();

        init();

    }

    private void init() {
        setPrice(Asset.AssetType.CORPORATE_BONDS, 1.0);
        setPrice(Asset.AssetType.EQUITIES, 1.0);
        setPrice(Asset.AssetType.EXTERNAL1, 1.0);
        setPrice(Asset.AssetType.EXTERNAL2, 1.0);
        setPrice(Asset.AssetType.EXTERNAL3, 1.0);
        setPrice(Asset.AssetType.MBS, 1.0);

        priceImpacts.put(Asset.AssetType.MBS, Parameters.PRICE_IMPACT_MBS);
        priceImpacts.put(Asset.AssetType.EQUITIES, Parameters.PRICE_IMPACT_EQUITIES);
        priceImpacts.put(Asset.AssetType.CORPORATE_BONDS, Parameters.PRICE_IMPACT_CORPORATE_BONDS);

        haircuts.put(Asset.AssetType.MBS, Parameters.getInitialHaircut(Asset.AssetType.MBS));
        haircuts.put(Asset.AssetType.EQUITIES, Parameters.getInitialHaircut(Asset.AssetType.EQUITIES));
        haircuts.put(Asset.AssetType.CORPORATE_BONDS, Parameters.getInitialHaircut(Asset.AssetType.CORPORATE_BONDS));

        totalAmountsSold.put(Asset.AssetType.MBS, 0.0);
        totalAmountsSold.put(Asset.AssetType.EQUITIES, 0.0);
        totalAmountsSold.put(Asset.AssetType.CORPORATE_BONDS, 0.0);


    }

    public void putForSale(Asset asset, double amount) {
        orderbook.add(new Order(asset, amount));
        Asset.AssetType type = asset.getAssetType();

        System.out.println("Putting for sale: "+asset.getAssetType()+", an amount "+amount);

        if (!amountsSold.containsKey(type)) {
            amountsSold.put(type, amount);
        } else {
            amountsSold.put(type, amountsSold.get(type) + amount);
        }

    }


    public void clearTheMarket() {
        System.out.println("\nMARKET CLEARING\n");
        for (Map.Entry<Asset.AssetType, Double> entry : amountsSold.entrySet()) {
            if (Parameters.FIRESALE_CONTAGION) {
                oldPrices = new HashMap<>(prices);
                computePriceImpact(entry.getKey(), entry.getValue());

                prices.forEach((assetType,newPrice) -> {
                    if (oldPrices.get(assetType) > newPrice) {
                        BoEDemo.devalueCommonAsset(assetType, oldPrices.get(assetType) - newPrice);
                    }
                });
            }

            if (Parameters.HAIRCUT_CONTAGION) computeHaircut(entry.getKey(), entry.getValue());

            if (!totalAmountsSold.containsKey(entry.getKey())) {
                totalAmountsSold.put(entry.getKey(), entry.getValue());
            } else {
                totalAmountsSold.put(entry.getKey(), totalAmountsSold.get(entry.getKey()) + entry.getValue());
            }
        }

        amountsSold.clear();

        for (Order order : orderbook) {
            order.settle();
        }

        orderbook.clear();
    }


    private void computePriceImpact(Asset.AssetType assetType, double amountSold) {
        double newPrice = prices.get(assetType) * (1.0 - amountSold * priceImpacts.get(assetType));
        setPrice(assetType, newPrice);
    }

    private void computeHaircut(Asset.AssetType assetType, double amountSold) {
        if (!haircuts.containsKey(assetType)) return;

        double h0 = Parameters.getInitialHaircut(assetType);
        double p0 = 1.0;

        double newHaircut = h0 * 1.0 + Parameters.HAIRCUT_SLOPE * ( (p0 - getPrice(assetType)) / p0 - Parameters.HAIRCUT_PRICE_FALL_THRESHOLD);

        if(newHaircut < h0) newHaircut = h0;
        if(newHaircut > 1) newHaircut = 1.0;

        haircuts.put(assetType, newHaircut);

    }

    public double getPrice(Asset.AssetType assetType) {
        return prices.get(assetType);
    }

    public double getHaircut(Asset.AssetType assetType) {
        return haircuts.containsKey(assetType) ? haircuts.get(assetType) : 0.0;
    }

    public void setPrice(Asset.AssetType assetType, double newPrice) {
        prices.put(assetType, newPrice);
    }
    //todo: should not be public

    private class Order {
        private Asset asset;
        private double quantity;

        private Order(Asset asset, double quantity) {
            this.asset = asset;
            this.quantity = quantity;
        }

        private void settle() {
            asset.clearSale(quantity);
        }

    }

    public ArrayList<Asset.AssetType> getAssetTypes() {
        ArrayList<Asset.AssetType> assetTypesArray = new ArrayList<>();

        Set<Asset.AssetType> assetTypes = prices.keySet();
        for (Asset.AssetType type : assetTypes) {
            assetTypesArray.add(type);
        }

        return assetTypesArray;
    }

    public double getTotalAmountSold(Asset.AssetType assetType) {
        return totalAmountsSold.containsKey(assetType) ? totalAmountsSold.get(assetType) : 0.0;
    }
}
