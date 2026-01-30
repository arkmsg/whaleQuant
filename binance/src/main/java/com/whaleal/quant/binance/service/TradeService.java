package com.whaleal.quant.binance.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.connector.client.common.ApiResponse;
import com.binance.connector.client.spot.rest.api.SpotRestApi;
import com.binance.connector.client.spot.rest.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whaleal.quant.binance.model.AccountResp;
import com.whaleal.quant.binance.model.OrderResp;
import com.whaleal.quant.binance.util.BinanceConfig;

/**
 * 交易服务（基于币安官方Spot SDK 7.0.1）
 */
public class TradeService {

    private static final Logger log = LoggerFactory.getLogger(TradeService.class);

    private final SpotRestApi spotRestApi;
    private final BinanceConfig config;
    private final ObjectMapper objectMapper;

    public TradeService(SpotRestApi spotRestApi, BinanceConfig config) {
        this.spotRestApi = spotRestApi;
        this.config = config;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 下单
     */
    public OrderResp executeOrder(String symbol, String side, String type, String quantity,
                                   String price, String timeInForce) {
        try {
            NewOrderRequest request = new NewOrderRequest();
            request.setSymbol(symbol);
            request.setSide(Side.valueOf(side));
            request.setType(OrderType.valueOf(type));
            request.setQuantity(Double.parseDouble(quantity));

            if (price != null && !price.isEmpty()) {
                request.setPrice(Double.parseDouble(price));
            }
            if (timeInForce != null && !timeInForce.isEmpty()) {
                request.setTimeInForce(TimeInForce.valueOf(timeInForce));
            }

            ApiResponse<NewOrderResponse> response = spotRestApi.newOrder(request);
            return objectMapper.readValue(objectMapper.writeValueAsString(response.getData()), OrderResp.class);
        } catch (Exception e) {
            log.error("下单失败: {} {} {} {}", symbol, side, type, quantity, e);
            throw new RuntimeException("下单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 取消订单
     */
    public OrderResp cancelOrder(String symbol, Long orderId) {
        try {
            ApiResponse<DeleteOrderResponse> response = spotRestApi.deleteOrder(
                    symbol, orderId, null, null, null, null);
            return objectMapper.readValue(objectMapper.writeValueAsString(response.getData()), OrderResp.class);
        } catch (Exception e) {
            log.error("撤单失败: {} orderId={}", symbol, orderId, e);
            throw new RuntimeException("撤单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询订单
     */
    public OrderResp queryOrder(String symbol, Long orderId) {
        try {
            ApiResponse<GetOrderResponse> response = spotRestApi.getOrder(symbol, orderId, null, null);
            return objectMapper.readValue(objectMapper.writeValueAsString(response.getData()), OrderResp.class);
        } catch (Exception e) {
            log.error("查询订单失败: {} orderId={}", symbol, orderId, e);
            throw new RuntimeException("查询订单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询账户信息
     */
    public AccountResp getAccount() {
        try {
            ApiResponse<GetAccountResponse> response = spotRestApi.getAccount(null, null);
            return objectMapper.readValue(objectMapper.writeValueAsString(response.getData()), AccountResp.class);
        } catch (Exception e) {
            log.error("查询账户信息失败", e);
            throw new RuntimeException("查询账户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 取消所有挂单
     */
    public String cancelAllOrders(String symbol) {
        try {
            ApiResponse<DeleteOpenOrdersResponse> response = spotRestApi.deleteOpenOrders(symbol, null);
            log.info("已取消交易对 {} 的所有挂单", symbol);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("取消所有挂单失败: {}", symbol, e);
            throw new RuntimeException("取消所有挂单失败: " + e.getMessage(), e);
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
            ApiResponse<GetOpenOrdersResponse> response = spotRestApi.getOpenOrders(symbol, null);
            log.debug("查询交易对 {} 的当前挂单", symbol == null ? "ALL" : symbol);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("查询当前挂单失败: {}", symbol, e);
            throw new RuntimeException("查询当前挂单失败: " + e.getMessage(), e);
        }
    }

}
