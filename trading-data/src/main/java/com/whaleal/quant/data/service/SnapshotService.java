package com.whaleal.quant.data.service;

import com.whaleal.quant.model.Ticker;
import com.whaleal.quant.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 快照服务类
 * 实现行情快照的存储和回放能力
 * 支持冷热数据分离
 *
 * @author whaleal
 * @version 1.0.0
 */
@Slf4j
@Service
public class SnapshotService {

    private static final String HOT_STORAGE_PATH = "/data/quant/hot";
    private static final String COLD_STORAGE_PATH = "/data/quant/cold";
    private static final String SNAPSHOT_EXTENSION = ".snapshot.gz";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CacheService cacheService;

    /**
     * 保存行情快照
     * @param traceContext 追踪上下文
     * @param tickers 行情数据列表
     * @param timestamp 时间戳
     * @return 快照ID
     */
    public String saveSnapshot(TraceContext traceContext, List<Ticker> tickers, Instant timestamp) {
        String snapshotId = generateSnapshotId(timestamp);
        Path snapshotPath = getHotStoragePath(snapshotId);

        try {
            // 确保存储目录存在
            Files.createDirectories(snapshotPath.getParent());

            // 序列化并压缩快照数据
            try (FileOutputStream fos = new FileOutputStream(snapshotPath.toFile());
                 GZIPOutputStream gzos = new GZIPOutputStream(fos);
                 ObjectOutputStream oos = new ObjectOutputStream(gzos)) {

                // 写入元数据
                oos.writeObject(traceContext);
                oos.writeObject(timestamp);
                oos.writeInt(tickers.size());

                // 写入行情数据
                for (Ticker ticker : tickers) {
                    oos.writeObject(ticker);
                }

                log.info("[{}] 保存行情快照成功: {}，包含 {} 条数据",
                        traceContext.getTraceId(), snapshotId, tickers.size());
            }

            return snapshotId;
        } catch (Exception e) {
            log.error("[{}] 保存行情快照失败: {}", traceContext.getTraceId(), e.getMessage(), e);
            throw new RuntimeException("保存行情快照失败", e);
        }
    }

    /**
     * 加载行情快照
     * @param snapshotId 快照ID
     * @return 快照数据
     */
    public Snapshot loadSnapshot(String snapshotId) {
        Path snapshotPath = getSnapshotPath(snapshotId);

        try {
            // 解压缩并反序列化快照数据
            try (FileInputStream fis = new FileInputStream(snapshotPath.toFile());
                 GZIPInputStream gzis = new GZIPInputStream(fis);
                 ObjectInputStream ois = new ObjectInputStream(gzis)) {

                // 读取元数据
                TraceContext traceContext = (TraceContext) ois.readObject();
                Instant timestamp = (Instant) ois.readObject();
                int size = ois.readInt();

                // 读取行情数据
                List<Ticker> tickers = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    tickers.add((Ticker) ois.readObject());
                }

                log.info("加载行情快照成功: {}，包含 {} 条数据", snapshotId, tickers.size());
                return new Snapshot(snapshotId, traceContext, timestamp, tickers);
            }
        } catch (Exception e) {
            log.error("加载行情快照失败: {}", e.getMessage(), e);
            throw new RuntimeException("加载行情快照失败", e);
        }
    }

    /**
     * 回放行情快照
     * @param snapshotId 快照ID
     * @param playbackListener 回放监听器
     */
    public void playbackSnapshot(String snapshotId, PlaybackListener playbackListener) {
        Snapshot snapshot = loadSnapshot(snapshotId);
        playbackListener.onStart(snapshot);

        // 按时间顺序回放行情数据
        for (Ticker ticker : snapshot.getTickers()) {
            playbackListener.onTicker(ticker);
        }

        playbackListener.onComplete(snapshot);
    }

    /**
     * 将快照从热存储迁移到冷存储
     * @param snapshotId 快照ID
     */
    public void migrateToColdStorage(String snapshotId) {
        Path hotPath = getHotStoragePath(snapshotId);
        Path coldPath = getColdStoragePath(snapshotId);

        try {
            // 确保冷存储目录存在
            Files.createDirectories(coldPath.getParent());

            // 移动文件
            Files.move(hotPath, coldPath);
            log.info("将快照迁移到冷存储: {}", snapshotId);
        } catch (Exception e) {
            log.error("迁移快照到冷存储失败: {}", e.getMessage(), e);
            throw new RuntimeException("迁移快照到冷存储失败", e);
        }
    }

    /**
     * 生成快照ID
     * @param timestamp 时间戳
     * @return 快照ID
     */
    private String generateSnapshotId(Instant timestamp) {
        return "snapshot_" + timestamp.toEpochMilli();
    }

    /**
     * 获取快照路径
     * @param snapshotId 快照ID
     * @return 快照路径
     */
    private Path getSnapshotPath(String snapshotId) {
        Path hotPath = getHotStoragePath(snapshotId);
        if (Files.exists(hotPath)) {
            return hotPath;
        }
        return getColdStoragePath(snapshotId);
    }

    /**
     * 获取热存储路径
     * @param snapshotId 快照ID
     * @return 热存储路径
     */
    private Path getHotStoragePath(String snapshotId) {
        return Paths.get(HOT_STORAGE_PATH, snapshotId + SNAPSHOT_EXTENSION);
    }

    /**
     * 获取冷存储路径
     * @param snapshotId 快照ID
     * @return 冷存储路径
     */
    private Path getColdStoragePath(String snapshotId) {
        return Paths.get(COLD_STORAGE_PATH, snapshotId + SNAPSHOT_EXTENSION);
    }

    /**
     * 快照类
     */
    public static class Snapshot {
        private final String snapshotId;
        private final TraceContext traceContext;
        private final Instant timestamp;
        private final List<Ticker> tickers;

        public Snapshot(String snapshotId, TraceContext traceContext, Instant timestamp, List<Ticker> tickers) {
            this.snapshotId = snapshotId;
            this.traceContext = traceContext;
            this.timestamp = timestamp;
            this.tickers = tickers;
        }

        public String getSnapshotId() {
            return snapshotId;
        }

        public TraceContext getTraceContext() {
            return traceContext;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public List<Ticker> getTickers() {
            return tickers;
        }
    }

    /**
     * 回放监听器接口
     */
    public interface PlaybackListener {
        /**
         * 回放开始
         * @param snapshot 快照
         */
        void onStart(Snapshot snapshot);

        /**
         * 回放单个行情数据
         * @param ticker 行情数据
         */
        void onTicker(Ticker ticker);

        /**
         * 回放完成
         * @param snapshot 快照
         */
        void onComplete(Snapshot snapshot);
    }
}
