package org.recap.config;

import org.apache.commons.lang3.StringUtils;
import org.recap.util.HelperUtil;
import org.recap.util.MD5EncoderUtil;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by hemalathas on 7/9/16.
 */
@Component
public class SwaggerInterceptor implements HandlerInterceptor {

    MD5EncoderUtil md5EncoderUtil;

    private String scsbApiKey;

    public MD5EncoderUtil getMd5EncoderUtil() {
        if(null == md5EncoderUtil) {
            md5EncoderUtil = new MD5EncoderUtil();
        }
        return md5EncoderUtil;
    }

    public String getScsbApiKey() {
        if(StringUtils.isBlank(scsbApiKey)) {
            Environment environment = HelperUtil.getBean(Environment.class);
            scsbApiKey = environment.getProperty("scsb.api.key");
        }
        return scsbApiKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        boolean continueExport = false;
        String date = new Date().toString();
        String key = request.getHeader("api_key");
        if (key != null && matchingWithInstitutionKeys(key)) {
            continueExport = true;
        } else {
            continueExport = false;
            response.setStatus(401);
            response.setHeader("Date" , date);
            response.getWriter().println("Authentication Failed");
        }
        return continueExport;
    }

    private boolean matchingWithInstitutionKeys(String key) {
        String[] keys = StringUtils.split(getScsbApiKey(), ",");
        for(String existingKey : keys) {
            if(getMd5EncoderUtil().matching(existingKey, key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    	
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    	
    }
}




