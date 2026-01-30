package com.whaleal.quant.binance;

import com.binance.connector.client.common.configuration.ClientConfiguration;
import com.binance.connector.client.common.configuration.SignatureConfiguration;
import com.binance.connector.client.derivatives_trading_usds_futures.rest.api.DerivativesTradingUsdsFuturesRestApi;
import com.binance.connector.client.margin_trading.rest.api.MarginTradingRestApi;
import com.binance.connector.client.spot.rest.api.SpotRestApi;
import com.whaleal.quant.binance.service.FuturesService;
import com.whaleal.quant.binance.service.KlineService;
import com.whaleal.quant.binance.service.MarginService;
import com.whaleal.quant.binance.service.MarketService;
import com.whaleal.quant.binance.service.TradeService;
import com.whaleal.quant.binance.util.BinanceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 币安SDK - 主入口类
 *
 * <p>提供Builder模式的API设计，支持现货交易、合约交易、杠杆交易、行情查询等功能。
 * 适配最新的币安官方SDK版本。
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * try (BinanceSDK sdk = BinanceSDK.builder()
 *         .apiKey("your_api_key")
 *         .secretKey("your_secret_key")
 *         .testnet(false)
 *         .build()) {
 *
 *     // 现货：获取行情
 *     String ticker = sdk.market().getTicker("BTCUSDT");
 *
 *     // 现货：下单
 *     String order = sdk.spot().executeOrder(
 *         "BTCUSDT", "BUY", "LIMIT", "0.001", "50000.00", "GTC"
 *     );
 *
 *     // 合约：下单
 *     String futuresOrder = sdk.futures().marketOrder(
 *         "BTCUSDT", "BUY", "BOTH", "0.01"
 *     );
 * }
 * }</pre>
 *
 * @author Binance SDK Team
 * @version 1.0.0
 */
public class BinanceSDK implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(BinanceSDK.class);

    private final BinanceConfig config;

    // 币安官方SDK客户端
    private final SpotRestApi spotRestApi;
    private final DerivativesTradingUsdsFuturesRestApi futuresRestApi;
    private final MarginTradingRestApi marginRestApi;

    // 服务封装层
    private final MarketService marketService;
    private final TradeService tradeService;
    private final FuturesService futuresService;
    private final MarginService marginService;
    private final KlineService klineService;

    private BinanceSDK(Builder builder) {
        log.debug("初始化币安SDK...");

        // 构建配置
        this.config = BinanceConfig.builder()
                .apiKey(builder.apiKey)
                .secretKey(builder.secretKey)
                .baseUrl(builder.testnet ? BinanceConfig.TESTNET_BASE_URL : BinanceConfig.PRODUCTION_BASE_URL)
                .testnet(builder.testnet)
                .build();

        // 初始化币安官方SDK客户端
        SignatureConfiguration signatureConfig = new SignatureConfiguration();
        signatureConfig.setApiKey(builder.apiKey);
        signatureConfig.setSecretKey(builder.secretKey);

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignatureConfiguration(signatureConfig);

        // 初始化最新版本的SDK客户端
        this.spotRestApi = new SpotRestApi(clientConfig);
        this.futuresRestApi = new DerivativesTradingUsdsFuturesRestApi(clientConfig);
        this.marginRestApi = new MarginTradingRestApi(clientConfig);

        // 初始化服务（使用官方SDK客户端）
        this.marketService = new MarketService(spotRestApi, config);
        this.tradeService = new TradeService(spotRestApi, config);
        this.futuresService = new FuturesService(futuresRestApi, config);
        this.marginService = new MarginService(marginRestApi, config);
        this.klineService = new KlineService(spotRestApi, config);

        log.info("币安SDK初始化成功 [环境: {}]", builder.testnet ? "测试网" : "生产环境");
        log.info("SDK版本: 适配最新官方SDK");
    }

    /**
     * 获取行情服务（现货行情）
     *
     * @return 行情服务实例
     */
    public MarketService market() {
        return marketService;
    }

    /**
     * 获取现货交易服务
     *
     * @return 现货交易服务实例
     */
    public TradeService spot() {
        return tradeService;
    }

    /**
     * 获取合约交易服务（USDT本位永续合约）
     *
     * @return 合约交易服务实例
     */
    public FuturesService futures() {
        return futuresService;
    }

    /**
     * 获取杠杆交易服务（全仓杠杆）
     *
     * @return 杠杆交易服务实例
     */
    public MarginService margin() {
        return marginService;
    }

    /**
     * 获取K线服务（返回统一 Candlestick 模型）
     *
     * @return K线服务实例
     */
    public KlineService kline() {
        return klineService;
    }

    /**
     * 获取配置信息（高级用户使用）
     *
     * @return 配置实例
     */
    public BinanceConfig getConfig() {
        return config;
    }

    /**
     * 获取SpotRestApi（高级用户使用）
     *
     * @return SpotRestApi实例
     */
    public SpotRestApi getSpotRestApi() {
        return spotRestApi;
    }

    /**
     * 获取FuturesRestApi（高级用户使用）
     *
     * @return DerivativesTradingUsdsFuturesRestApi实例
     */
    public DerivativesTradingUsdsFuturesRestApi getFuturesRestApi() {
        return futuresRestApi;
    }

    /**
     * 获取MarginRestApi（高级用户使用）
     *
     * @return MarginTradingRestApi实例
     */
    public MarginTradingRestApi getMarginRestApi() {
        return marginRestApi;
    }

    @Override
    public void close() {
        log.debug("关闭币安SDK...");
        // HttpClient不需要显式关闭
        log.debug("币安SDK已关闭");
    }

    /**
     * 创建Builder实例
     *
     * @return Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder类 - 用于构建BinanceSDK实例
     */
    public static class Builder {
        private String apiKey;
        private String secretKey;
        private boolean testnet = false; // 默认生产环境

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder testnet(boolean testnet) {
            this.testnet = testnet;
            return this;
        }

        public BinanceSDK build() {
            validate();
            return new BinanceSDK(this);
        }

        private void validate() {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("API Key 不能为空");
            }
            if (secretKey == null || secretKey.trim().isEmpty()) {
                throw new IllegalArgumentException("Secret Key 不能为空");
            }
        }
    }
}
