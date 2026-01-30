package com.whaleal.quant.binance.sync;

import com.whaleal.quant.base.model.Position;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionSyncService {
    private PositionCache positionCache;

    public PositionSyncService(PositionCache positionCache) {
        this.positionCache = positionCache;
    }

    @Scheduled(fixedRate = 30000) // 每30秒同步一次
    public void syncPositions() {
        List<Position> positions = fetchPositionsFromApi();
        positionCache.updatePositions(positions);
    }

    private List<Position> fetchPositionsFromApi() {
        // 实现从Binance API获取持仓的逻辑
        // 这里需要根据Binance API的实际接口进行实现
        return List.of();
    }

    public List<Position> getPositions() {
        return positionCache.getPositions();
    }

    public Position getPosition(String ticker) {
        return positionCache.getPosition(ticker);
    }
}
