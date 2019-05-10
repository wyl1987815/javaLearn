package com.wyl.treasury.util;

import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * Created on 2019年04月18日 下午2:06:15
 * <p>Title:       [apache http工具类]/p>
 * <p>Description: [pache http工具类，包括get/post请求，json报文、文件上传等]</p>
 * <p>Company:     羚羊极速</p>
 * @author [wangyunliang]
 * @version 1.0
 */
@Slf4j
public class ApacheHttpUtil {
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 30000;

    public static final String LY_OSS_OBJECT_DOWNLOAD_URL = "/files?access_token={token}&obj_id={obj_id}";

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(500);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        // configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     *
     * @param url
     * @return
     */
    public static String get(String url) {
        return get(url, Collections.emptyMap(), Collections.emptyMap());
    }

    public static String get(String url, Map<String, String> headers) {
        return get(url, Collections.emptyMap(), headers);
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     *
     * @param url
     * @param params
     * @return
     */
    public static String get(String url, Map<String, Object> params, Map<String, String> headers) {
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        int i = 0;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (i == 0) {
                param.append("?");
            } else {
                param.append("&");
            }
            param.append(entry.getKey()).append("=").append(entry.getValue());
            i++;
        }
        apiUrl += param;
        String result = null;
        CloseableHttpClient httpclient = HttpClients.createMinimal(connMgr);
        try {
            HttpGet httpGet = new HttpGet(apiUrl);
            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            CloseableHttpResponse response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                // EntityUtils.consume(entity);
                result = EntityUtils.toString(entity, "UTF-8");
            } else {
                log.error("访问URL错误", response.getStatusLine());
                return String.valueOf(statusCode);
            }
            response.close();
        } catch (IOException e) {
            log.error("", e);
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），不带输入数据
     *
     * @param apiUrl
     * @return
     */
    public static String post(String apiUrl) {
        return post(apiUrl, Collections.emptyMap());
    }


    public static String post(String apiUrl, HashMap<String, Object> params) {
        return post(apiUrl, params, Collections.emptyMap());
    }

    public static String post(String apiUrl, Map<String, String> headers) {
        return post(apiUrl, Collections.emptyMap(), headers);
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String post(String apiUrl, Map<String, Object> params, Map<String, String> headers) {
        CloseableHttpClient httpClient = HttpClients.createMinimal(connMgr);
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }

            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            log.error("", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 上传图片到羚羊云对象存储
     * @param apiUrl
     * @param file
     * @param fileName
     * @return
     */
    public static String uploadFileToLy(String apiUrl, byte[] file, String fileName,String token) {
        String urlStr = apiUrl + "&size=" + file.length;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            // 把一个普通参数和文件上传给下面这个地址 是一个servlet
            HttpPost httpPost = new HttpPost(urlStr);
            // 把文件转换成流对象FileBody
            HttpEntity reqEntity = MultipartEntityBuilder.create().
                    addBinaryBody("file", file, ContentType.DEFAULT_BINARY, fileName).build();
            //log.info("uploadFileToLy url="+URL_STR);
            httpPost.setEntity(reqEntity);
            // 发起请求 并返回请求的响应
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.error("uploadFileToLy url="+urlStr+ " error status code :" + statusCode);
                return null;
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                JSONObject object = JSON.parseObject(result);
                String objId = object.getString("obj_id");
                result = LY_OSS_OBJECT_DOWNLOAD_URL.replace("{token}",token).replace("{obj_id}",objId);
                log.info("uploadFileToLy url="+urlStr+ ",result=" + result);
                return result;
            }

        } catch (Exception e) {
            log.error("请求/upload2异常", e);
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String filePath = "d:/4.png";
        String accessToken = "2147500035_3356491776_1546271999_25d08183632312b0cb0aa941896abe27";
        String url = "https://jxsr-oss1.antelopecloud.cn/upload2?access_token="+accessToken+"&expiretype=0";
        File file = new File(filePath);
        String imgBase64 = ImgBase64.getImageBase64Str(filePath);
        final byte[] cuted = Base64Util.decode(imgBase64);
        String result = uploadFileToLy(url,cuted,file.getName(),accessToken);
        System.out.println(result);
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param apiUrl
     * @param json   json对象
     * @return
     */
    public static String postJson(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.createMinimal(connMgr);
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");// 解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage());

        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String postSSL(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
                .setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式
     *
     * @param apiUrl API接口URL
     * @param json   JSON对象
     * @return
     */
    public static String postSSLJson(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
                .setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");// 解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            // stringEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 未扩展完成 创建SSL安全连接
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new File("my.keystore"),
                    "nopassword".toCharArray(), new TrustSelfSignedStrategy()).build();
            // Allow TLSv1 protocol only
            sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        } catch (Exception e) {
            log.error("", e);
        }
        return sslsf;
    }

}