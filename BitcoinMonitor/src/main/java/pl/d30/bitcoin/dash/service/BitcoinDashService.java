package pl.d30.bitcoin.dash.service;

public class BitcoinDashService extends VirtualCoinDashService {

    @Override
    protected void onUpdateData(int reason) {
        if( !experimental ) new DownloadFilesTask().execute( getBtcUrl() );
        else new DownloadFilesTask().execute( "http://bitcoin.30d.pl/api/v3?s=" + getSourceName(source) + "&c=" + currency );

    }

    private String getBtcUrl() {
        switch( source ) {
            case SOURCE_BTCE: return "https://btc-e.com/api/2/btc_" + currency + "/ticker"; // USD and EUR only
            case SOURCE_BITSTAMP: return "https://www.bitstamp.net/api/ticker/";
            default:
            case SOURCE_MTGOX: return "https://data.mtgox.com/api/1/BTC" + currency + "/ticker";
        }
    }
}
