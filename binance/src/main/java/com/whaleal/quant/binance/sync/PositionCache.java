package com.whaleal.quant.binance.sync;

import com.whaleal.quant.base.model.Position;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PositionCache {
    private final Map<String, Position> positions = new ConcurrentHashMap<>();

    public void updatePositions(List<Position> newPositions) {
        positions.clear();
        for (Position position : newPositions) {
            positions.put(position.getSymbol(), position);
        }
    }

    public List<Position> getPositions() {
        return List.copyOf(positions.values());
    }

    public Position getPosition(String ticker) {
        return positions.get(ticker);
    }

    public void addPosition(Position position) {
        positions.put(position.getSymbol(), position);
    }

    public void removePosition(String ticker) {
        positions.remove(ticker);
    }

    public boolean containsPosition(String ticker) {
        return positions.containsKey(ticker);
    }

    public int size() {
        return positions.size();
    }

    public void clear() {
        positions.clear();
    }
}
