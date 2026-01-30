package com.whaleal.quant.longport.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.longport.quote.PrePostQuote;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * 实时报价响应
 *
 * <p>封装股票实时行情信息，支持24小时交易时段的价格。
 *
 * @author Longport SDK Team
 */
@Data
@Builder
public class SecurityQuoteResp {

    /**
     * 股票代码
     */
    private String symbol;

    /**
     * 当前价格（自动根据时段选择）
     */
    private BigDecimal price;

    /**
     * 最新成交价
     */
    private BigDecimal lastDone;

    /**
     * 昨收价
     */
    private BigDecimal prevClose;

    /**
     * 今开价
     */
    private BigDecimal open;

    /**
     * 最高价
     */
    private BigDecimal high;

    /**
     * 最低价
     */
    private BigDecimal low;

    /**
     * 成交量
     */
    private long volume;

    /**
     * 成交额
     */
    private BigDecimal turnover;

    /**
     * 时间戳
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime timestamp;

    /**
     * 交易时段
     */
    private MarketTimeSlot timeSlot;

    /**
     * 盘前报价
     */
    private PrePostQuote preMarketQuote;

    /**
     * 盘后报价
     */
    private PrePostQuote postMarketQuote;

    /**
     * 夜盘报价
     */
    private PrePostQuote overnightQuote;

    /**
     * 获取涨跌额
     */
    public BigDecimal getChange() {
        if (price != null && prevClose != null && prevClose.compareTo(BigDecimal.ZERO) > 0) {
            return price.subtract(prevClose);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取涨跌幅（%）
     */
    public BigDecimal getChangePercent() {
        if (price != null && prevClose != null && prevClose.compareTo(BigDecimal.ZERO) > 0) {
            return price.subtract(prevClose)
                    .divide(prevClose, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    /**
     * 是否处于盘前交易
     */
    public boolean isPreMarket() {
        return MarketTimeSlot.PRE_MARKET == timeSlot;
    }

    /**
     * 是否处于正常交易
     */
    public boolean isRegularMarket() {
        return MarketTimeSlot.REGULAR == timeSlot;
    }

    /**
     * 是否处于盘后交易
     */
    public boolean isPostMarket() {
        return MarketTimeSlot.POST_MARKET == timeSlot;
    }

    /**
     * 是否处于夜盘交易
     */
    public boolean isOvernight() {
        return MarketTimeSlot.OVERNIGHT == timeSlot;
    }
}

