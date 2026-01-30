package com.whaleal.quant.core.time;

public class MarketTimeInfo {
    
    private final String timeZone;
    private final int[] morningStart;
    private final int[] morningEnd;
    private final int[] afternoonStart;
    private final int[] afternoonEnd;
    private final int[] preMarketStart;
    private final int[] preMarketEnd;
    private final int[] afterHoursStart;
    private final int[] afterHoursEnd;
    
    public MarketTimeInfo(String timeZone, int[] morningStart, int[] morningEnd,
                         int[] afternoonStart, int[] afternoonEnd,
                         int[] preMarketStart, int[] preMarketEnd,
                         int[] afterHoursStart, int[] afterHoursEnd) {
        this.timeZone = timeZone;
        this.morningStart = morningStart;
        this.morningEnd = morningEnd;
        this.afternoonStart = afternoonStart;
        this.afternoonEnd = afternoonEnd;
        this.preMarketStart = preMarketStart;
        this.preMarketEnd = preMarketEnd;
        this.afterHoursStart = afterHoursStart;
        this.afterHoursEnd = afterHoursEnd;
    }
    
    public MarketTimeInfo(String timeZone, int[] morningStart, int[] morningEnd,
                         int[] afternoonStart, int[] afternoonEnd,
                         int[] preMarketStart, int[] preMarketEnd) {
        this(timeZone, morningStart, morningEnd, afternoonStart, afternoonEnd,
             preMarketStart, preMarketEnd, null, null);
    }
    
    public MarketTimeInfo(String timeZone, int[] morningStart, int[] morningEnd,
                         int[] afternoonStart, int[] afternoonEnd) {
        this(timeZone, morningStart, morningEnd, afternoonStart, afternoonEnd,
             null, null, null, null);
    }
    
    public MarketTimeInfo(String timeZone, int[] morningStart, int[] morningEnd) {
        this(timeZone, morningStart, morningEnd, null, null, null, null, null, null);
    }
    
    public String getTimeZone() {
        return timeZone;
    }
    
    public int[] getMorningStart() {
        return morningStart;
    }
    
    public int[] getMorningEnd() {
        return morningEnd;
    }
    
    public int[] getAfternoonStart() {
        return afternoonStart;
    }
    
    public int[] getAfternoonEnd() {
        return afternoonEnd;
    }
    
    public int[] getPreMarketStart() {
        return preMarketStart;
    }
    
    public int[] getPreMarketEnd() {
        return preMarketEnd;
    }
    
    public int[] getAfterHoursStart() {
        return afterHoursStart;
    }
    
    public int[] getAfterHoursEnd() {
        return afterHoursEnd;
    }
}
