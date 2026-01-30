package com.whaleal.quant.binance.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP客户端工具类
 *
 * <p>封装OkHttp，提供GET/POST/DELETE等HTTP方法。
 * 自动处理币安API的签名认证。
 *
 * @author Binance SDK Team
 */
@Slf4j
public class HttpClient {

    private final BinanceConfig config;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    public HttpClient(BinanceConfig config) {
        this.config = config;

        // 构建OkHttpClient
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(config.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .build();

        // 构建ObjectMapper
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * GET请求（无需签名）
     *
     * @param path 请求路径（例如：/api/v3/ticker/price）
     * @param params 查询参数
     * @return JSON响应字符串
     * @throws IOException 如果请求失败
     */
    public String get(String path, Map<String, String> params) throws IOException {
        String url = buildUrl(path, params);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return execute(request);
    }

    /**
     * GET请求（需要签名）
     *
     * @param path 请求路径
     * @param params 查询参数
     * @return JSON响应字符串
     * @throws IOException 如果请求失败
     */
    public String getWithSignature(String path, Map<String, String> params) throws IOException {
        // 添加时间戳
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));

        // 生成签名
        String queryString = buildQueryString(params);
        String signature = generateSignature(queryString);
        params.put("signature", signature);

        String url = buildUrl(path, params);

        Request request = new Request.Builder()
                .url(url)
                .header("X-MBX-APIKEY", config.getApiKey())
                .get()
                .build();

        return execute(request);
    }

    /**
     * POST请求（需要签名）
     *
     * @param path 请求路径
     * @param params 请求参数
     * @return JSON响应字符串
     * @throws IOException 如果请求失败
     */
    public String post(String path, Map<String, String> params) throws IOException {
        // 添加时间戳
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));

        // 生成签名
        String queryString = buildQueryString(params);
        String signature = generateSignature(queryString);
        params.put("signature", signature);

        String url = buildUrl(path, params);

        RequestBody body = RequestBody.create("", JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(url)
                .header("X-MBX-APIKEY", config.getApiKey())
                .post(body)
                .build();

        return execute(request);
    }

    /**
     * DELETE请求（需要签名）
     *
     * @param path 请求路径
     * @param params 请求参数
     * @return JSON响应字符串
     * @throws IOException 如果请求失败
     */
    public String delete(String path, Map<String, String> params) throws IOException {
        // 添加时间戳
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));

        // 生成签名
        String queryString = buildQueryString(params);
        String signature = generateSignature(queryString);
        params.put("signature", signature);

        String url = buildUrl(path, params);

        Request request = new Request.Builder()
                .url(url)
                .header("X-MBX-APIKEY", config.getApiKey())
                .delete()
                .build();

        return execute(request);
    }

    /**
     * 执行HTTP请求
     *
     * @param request OkHttp请求对象
     * @return 响应字符串
     * @throws IOException 如果请求失败
     */
    private String execute(Request request) throws IOException {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无响应体";
                throw new IOException(String.format("请求失败 [%d]: %s", response.code(), errorBody));
            }

            if (response.body() == null) {
                throw new IOException("响应体为空");
            }

            return response.body().string();
        }
    }

    /**
     * 构建完整URL
     *
     * @param path 路径
     * @param params 参数
     * @return 完整URL
     */
    private String buildUrl(String path, Map<String, String> params) {
        StringBuilder url = new StringBuilder(config.getBaseUrl()).append(path);

        if (params != null && !params.isEmpty()) {
            url.append("?").append(buildQueryString(params));
        }

        return url.toString();
    }

    /**
     * 构建查询字符串
     *
     * @param params 参数Map
     * @return 查询字符串（例如：symbol=BTCUSDT&side=BUY）
     */
    private String buildQueryString(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            queryString.append(entry.getKey()).append("=").append(entry.getValue());
        }

        return queryString.toString();
    }

    /**
     * 生成HMAC SHA256签名
     *
     * @param data 待签名数据
     * @return 签名字符串（十六进制）
     */
    private String generateSignature(String data) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    config.getSecretKey().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            hmacSha256.init(secretKeySpec);

            byte[] hash = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("生成签名失败", e);
        }
    }

    /**
     * 获取ObjectMapper
     *
     * @return ObjectMapper实例
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * 关闭HTTP客户端
     */
    public void close() {
        if (okHttpClient != null) {
            okHttpClient.dispatcher().executorService().shutdown();
            okHttpClient.connectionPool().evictAll();
        }
    }
}

