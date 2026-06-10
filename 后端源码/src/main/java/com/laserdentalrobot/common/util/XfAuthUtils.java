package com.laserdentalrobot.common.util;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 讯飞虚拟人签名工具 照着官方文档写的
 */
public class XfAuthUtils {

    private static final String XF_HOST = "avatar.cn-huadong-1.xf-yun.com";
    private static final String XF_PATH = "/v1/interact";

    /**
     * 生成讯飞虚拟人带签名的wss地址
     */
    public static String getXfAvatarSignedWsUrl(String appId, String apiKey, String apiSecret) {
        try {
            // rfc1123格式时间
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = sdf.format(new Date());

            // 拼签名原文
            String signatureOrigin = "host: " + XF_HOST + "\ndate: " + date + "\nGET " + XF_PATH + " HTTP/1.1";

            // hmac-sha256加密再base64
            
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(spec);
            byte[] digest = mac.doFinal(signatureOrigin.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.encodeBase64String(digest);

            // 拼authorization再base64
            String authorization = String.format(
                    "api_key=\"%s\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"%s\"",
                    apiKey, signature
            );
            String encodedAuth = Base64.encodeBase64String(authorization.getBytes(StandardCharsets.UTF_8));

            // 最终wss地址
            return String.format(
                    "wss://%s%s?authorization=%s&date=%s&host=%s",
                    XF_HOST, XF_PATH, encodedAuth, date, XF_HOST
            );
        } catch (Exception e) {
            throw new RuntimeException("讯飞虚拟人签名生成失败", e);
        }
    }
}
