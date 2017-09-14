package com.best.oasis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import com.best.oasis.util.JSONUtils;

/**
 * Created by yiwei on 2017/3/8.
 */
public class BaseController {
    public static final Logger log = LoggerFactory.getLogger("Controller");

    public Map<String, Object> resultMap(String result, int count, Object data, String msg) {
        Map<String, Object> map = new HashMap();
        map.put("result", result);
        map.put("count", count);
        map.put("data", data);
        map.put("msg", msg);
        return map;
    }

    public String resultJson(String result, int count, Object data, String msg) {
        Map<String, Object> map = new HashMap();
        map.put("result", result);
        map.put("count", count);
        map.put("data", data);
        map.put("msg", msg);
        String json = "";
        try {
            json = JSONUtils.obj2json(map);
        }catch (Exception e){
            log.error("Resolve object to json error", e);
        }
        return json;
    }
}
