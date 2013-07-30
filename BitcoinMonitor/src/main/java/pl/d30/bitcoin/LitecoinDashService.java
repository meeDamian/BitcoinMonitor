package pl.d30.bitcoin;

public class LitecoinDashService extends VirtualCoinDashService {

    @Override
    protected void onInitialize(boolean isReconnect) {
        setItem(ITEM_LTC);
        super.onInitialize(isReconnect);
    }

    @Override
    protected void onUpdateData(int reason) {
        super.onUpdateData(reason);
    }
}
