package com.dhxl.web.controller.dxinterface.util;

import com.alibaba.fastjson.JSON;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by sunhongjie on 2018/12/10.
 */
public class RestFulClientUtil {

    public static RestTemplate reInitMessageConverter(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        HttpMessageConverter<?> converterTarget = null;
        int i = 0;
        for (int len = converterList.size(); i < len; i++) {
            if (converterList.get(i).getClass() == StringHttpMessageConverter.class) {
                converterTarget = converterList.get(i);
                break;
            }
        }

        if (converterTarget != null) {
            converterList.set(i, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        }
        return restTemplate;
    }


    public static String sendPostRest3(String url, String jsonData) {
        //-----此处是 解决乱码 start-----
        RestTemplate restTemplate = new RestTemplate();
        FormHttpMessageConverter fc = new FormHttpMessageConverter();
        StringHttpMessageConverter s = new StringHttpMessageConverter(StandardCharsets.UTF_16);
        List<HttpMessageConverter<?>> partConverters = new ArrayList<>();
        partConverters.add(s);
        partConverters.add(new ResourceHttpMessageConverter());
        fc.setPartConverters(partConverters);
        restTemplate.getMessageConverters().addAll(Arrays.asList(fc, new MappingJackson2HttpMessageConverter()));
        HttpHeaders headers = new HttpHeaders();
        //-----此处是  解决乱码 end----------
        headers.setContentType(MediaType.APPLICATION_JSON);
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Content-Type", "application/json; charset=UTF-8");
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
//        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(jsonData, headers);
        ResponseEntity<ResultVO> tt = restTemplate.postForEntity(url, request, ResultVO.class);
        ResultVO mResultVO = new ResultVO();
        if (tt != null && tt.getStatusCode().is2xxSuccessful()) {
            mResultVO = tt.getBody();
        }
        return JSON.toJSON(mResultVO).toString();
    }


    public static String sendPostRest2(String url, String jsonData) {
        StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> formEntity = new HttpEntity<String>(jsonData, headers);
        ResponseEntity<String> mResponseEntity = restTemplate.postForEntity(url, formEntity, String.class);
        String body = mResponseEntity.getBody();
        return body;
    }


    public static String sendGetRest(String url, Map<String, String> paramData) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, paramData);
        if (null != responseEntity) {
            return responseEntity.getBody();
        } else {
            return null;
        }
    }

}
