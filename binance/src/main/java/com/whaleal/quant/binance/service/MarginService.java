package com.whaleal.quant.binance.service;

import com.binance.connector.client.common.ApiResponse;
import com.binance.connector.client.margin_trading.rest.api.MarginTradingRestApi;
import com.binance.connector.client.margin_trading.rest.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whaleal.quant.binance.util.BinanceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 杠杆交易服务（基于币安官方杠杆SDK 4.0.0）
 */
public class MarginService {

    private static final Logger log = LoggerFactory.getLogger(MarginService.class);

    private final MarginTradingRestApi marginRestApi;
    private final BinanceConfig config;
    private final ObjectMapper objectMapper;

    public MarginService(MarginTradingRestApi marginRestApi, BinanceConfig config) {
        this.marginRestApi = marginRestApi;
        this.config = config;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 借币
     */
    public String borrow(String asset, String amount) {
        try {
            MarginAccountBorrowRepayRequest request = new MarginAccountBorrowRepayRequest();
            request.setAsset(asset);
            request.setAmount(amount);
            request.setType("BORROW");

            ApiResponse<MarginAccountBorrowRepayResponse> response = marginRestApi.marginAccountBorrowRepay(request);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("借币失败", e);
            throw new RuntimeException("借币失败: " + e.getMessage(), e);
        }
    }

    /**
     * 还币
     */
    public String repay(String asset, String amount) {
        try {
            MarginAccountBorrowRepayRequest request = new MarginAccountBorrowRepayRequest();
            request.setAsset(asset);
            request.setAmount(amount);
            request.setType("REPAY");

            ApiResponse<MarginAccountBorrowRepayResponse> response = marginRestApi.marginAccountBorrowRepay(request);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("还币失败", e);
            throw new RuntimeException("还币失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询杠杆账户
     */
    public String getAccount() {
        try {
            ApiResponse<QueryCrossMarginAccountDetailsResponse> response = marginRestApi.queryCrossMarginAccountDetails(null);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("查询杠杆账户失败", e);
            throw new RuntimeException("查询杠杆账户失败: " + e.getMessage(), e);
        }
    }

    /**
     * 下单（市价）
     */
    public String marketOrder(String symbol, String side, String quantity) {
        try {
            MarginAccountNewOrderRequest request = new MarginAccountNewOrderRequest();
            request.setSymbol(symbol);
            request.setSide(Side.valueOf(side));
            request.setType("MARKET");
            request.setQuantity(Double.parseDouble(quantity));

            ApiResponse<MarginAccountNewOrderResponse> response = marginRestApi.marginAccountNewOrder(request);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("杠杆下单失败", e);
            throw new RuntimeException("杠杆下单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 下单（限价）
     */
    public String limitOrder(String symbol, String side, String quantity, String price) {
        try {
            MarginAccountNewOrderRequest request = new MarginAccountNewOrderRequest();
            request.setSymbol(symbol);
            request.setSide(Side.valueOf(side));
            request.setType("LIMIT");
            request.setQuantity(Double.parseDouble(quantity));
            request.setPrice(Double.parseDouble(price));
            request.setTimeInForce(TimeInForce.GTC);

            ApiResponse<MarginAccountNewOrderResponse> response = marginRestApi.marginAccountNewOrder(request);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("杠杆下单失败", e);
            throw new RuntimeException("杠杆下单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 撤单
     */
    public String cancelOrder(String symbol, Long orderId) {
        try {
            ApiResponse<MarginAccountCancelOrderResponse> response = marginRestApi.marginAccountCancelOrder(
                    symbol, null, orderId, null, null, null);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("撤单失败", e);
            throw new RuntimeException("撤单失败: " + e.getMessage(), e);
        }
    }


    /**
     * 取消所有挂单
     */
    public String cancelAllOrders(String symbol) {
        try {
            ApiResponse<MarginAccountCancelAllOpenOrdersOnASymbolResponse> response =
                    marginRestApi.marginAccountCancelAllOpenOrdersOnASymbol(symbol, null, null);
            log.info("已取消杠杆交易对 {} 的所有挂单", symbol);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("取消所有挂单失败: {}", symbol, e);
            throw new RuntimeException("取消所有挂单失败: " + e.getMessage(), e);
        }
    }

    // Note: 杠杆账户查询订单功能暂时移除，因为官方SDK 4.0.0版本API签名发生变化
    // 用户可以通过 getMarginRestApi() 获取原始API进行高级操作

}
