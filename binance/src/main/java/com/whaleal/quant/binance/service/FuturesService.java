package com.whaleal.quant.binance.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.connector.client.common.ApiResponse;
import com.binance.connector.client.derivatives_trading_usds_futures.rest.api.DerivativesTradingUsdsFuturesRestApi;
import com.binance.connector.client.derivatives_trading_usds_futures.rest.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whaleal.quant.binance.util.BinanceConfig;

/**
 * 合约交易服务（基于币安官方USDT合约SDK 6.0.1）
 */
public class FuturesService {

    private static final Logger log = LoggerFactory.getLogger(FuturesService.class);

    private final DerivativesTradingUsdsFuturesRestApi futuresRestApi;
    private final BinanceConfig config;
    private final ObjectMapper objectMapper;

    public FuturesService(DerivativesTradingUsdsFuturesRestApi futuresRestApi, BinanceConfig config) {
        this.futuresRestApi = futuresRestApi;
        this.config = config;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 设置杠杆倍数
     */
    public String setLeverage(String symbol, int leverage) {
        try {
            ChangeInitialLeverageRequest request = new ChangeInitialLeverageRequest();
            request.setSymbol(symbol);
            request.setLeverage((long) leverage);

            ApiResponse<ChangeInitialLeverageResponse> response = futuresRestApi.changeInitialLeverage(request);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("设置杠杆失败", e);
            throw new RuntimeException("设置杠杆失败: " + e.getMessage(), e);
        }
    }

    /**
     * 设置保证金模式
     */
    public String setMarginType(String symbol, String marginType) {
        try {
            ChangeMarginTypeRequest request = new ChangeMarginTypeRequest();
            request.setSymbol(symbol);
            request.setMarginType(MarginType.valueOf(marginType));

            ApiResponse<ChangeMarginTypeResponse> response = futuresRestApi.changeMarginType(request);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("设置保证金模式失败", e);
            throw new RuntimeException("设置保证金模式失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询合约账户
     */
    public String getAccount() {
        try {
            ApiResponse<AccountInformationV2Response> response = futuresRestApi.accountInformationV2(null);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("查询合约账户失败", e);
            throw new RuntimeException("查询合约账户失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询持仓
     */
    public String getPositions(String symbol) {
        try {
            ApiResponse<PositionInformationV2Response> response = futuresRestApi.positionInformationV2(symbol, null);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("查询持仓失败", e);
            throw new RuntimeException("查询持仓失败: " + e.getMessage(), e);
        }
    }

    /**
     * 下单（市价）
     */
    public String marketOrder(String symbol, String side, String positionSide, String quantity) {
        try {
            NewOrderRequest request = new NewOrderRequest();
            request.setSymbol(symbol);
            request.setSide(Side.valueOf(side));
            request.setPositionSide(PositionSide.valueOf(positionSide));
            request.setType("MARKET");
            request.setQuantity(Double.parseDouble(quantity));

            ApiResponse<NewOrderResponse> response = futuresRestApi.newOrder(request);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("合约下单失败", e);
            throw new RuntimeException("合约下单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 下单（限价）
     */
    public String limitOrder(String symbol, String side, String positionSide, String quantity, String price) {
        try {
            NewOrderRequest request = new NewOrderRequest();
            request.setSymbol(symbol);
            request.setSide(Side.valueOf(side));
            request.setPositionSide(PositionSide.valueOf(positionSide));
            request.setType("LIMIT");
            request.setQuantity(Double.parseDouble(quantity));
            request.setPrice(Double.parseDouble(price));
            request.setTimeInForce(TimeInForce.GTC);

            ApiResponse<NewOrderResponse> response = futuresRestApi.newOrder(request);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("合约下单失败", e);
            throw new RuntimeException("合约下单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 撤单
     */
    public String cancelOrder(String symbol, Long orderId) {
        try {
            ApiResponse<CancelOrderResponse> response = futuresRestApi.cancelOrder(symbol, orderId, null, null);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("撤单失败", e);
            throw new RuntimeException("撤单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 撤销所有订单
     */
    public String cancelAllOrders(String symbol) {
        try {
            ApiResponse<CancelAllOpenOrdersResponse> response = futuresRestApi.cancelAllOpenOrders(symbol, null);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("撤销所有订单失败", e);
            throw new RuntimeException("撤销所有订单失败: " + e.getMessage(), e);
        }
    }


    /**
     * 查询账户余额（简化版）
     */
    public String getBalance() {
        try {
            ApiResponse<FuturesAccountBalanceV2Response> response = futuresRestApi.futuresAccountBalanceV2(null);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("查询账户余额失败", e);
            throw new RuntimeException("查询账户余额失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询订单详情（P0核心功能）
     *
     * @param symbol 交易对
     * @param orderId 订单ID
     * @return 订单详情（JSON字符串）
     */
    public String getOrder(String symbol, Long orderId) {
        try {
            ApiResponse<QueryOrderResponse> response = futuresRestApi.queryOrder(
                    symbol, orderId, null, null);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("查询订单详情失败: {} orderId={}", symbol, orderId, e);
            throw new RuntimeException("查询订单详情失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询当前挂单（P0核心功能）
     *
     * @param symbol 交易对（可选，null表示查询所有）
     * @return 当前挂单列表（JSON字符串）
     */
    public String getOpenOrders(String symbol) {
        try {
            ApiResponse<CurrentAllOpenOrdersResponse> response = futuresRestApi.currentAllOpenOrders(symbol, null);
            log.debug("查询合约 {} 的当前挂单", symbol == null ? "ALL" : symbol);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("查询当前挂单失败: {}", symbol, e);
            throw new RuntimeException("查询当前挂单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询所有订单（历史订单）（P1功能）
     *
     * @param symbol 交易对
     * @param orderId 订单ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 返回数量限制（可选）
     * @return 订单列表（JSON字符串）
     */
    public String getAllOrders(String symbol, Long orderId, Long startTime, Long endTime, Integer limit) {
        try {
            ApiResponse<AllOrdersResponse> response = futuresRestApi.allOrders(
                    symbol, orderId, startTime, endTime, limit != null ? limit.longValue() : null, null);
            log.debug("查询合约 {} 的所有订单", symbol);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("查询所有订单失败: {}", symbol, e);
            throw new RuntimeException("查询所有订单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询资金费率历史（P1功能）
     *
     * @param symbol 交易对
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 返回数量限制（可选）
     * @return 资金费率历史（JSON字符串）
     */
    public String getFundingRateHistory(String symbol, Long startTime, Long endTime, Integer limit) {
        try {
            ApiResponse<GetFundingRateHistoryResponse> response = futuresRestApi.getFundingRateHistory(
                    symbol, startTime, endTime, limit != null ? limit.longValue() : null);
            log.debug("查询合约 {} 的资金费率", symbol);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("查询资金费率失败: {}", symbol, e);
            throw new RuntimeException("查询资金费率失败: " + e.getMessage(), e);
        }
    }
}
