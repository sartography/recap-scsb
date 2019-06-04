package org.recap.controller.swagger;



import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.config.SwaggerInterceptor;
import org.recap.controller.BaseControllerUT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertTrue;


/**
 * Created by hemalathas on 25/1/17.
 */
public class SwaggerInterceptorUT extends BaseControllerUT {

    @MockBean
    SwaggerInterceptor swaggerInterceptor;

    @MockBean
    HttpServletRequest httpServletRequest;

    @MockBean
    private HttpServletResponse httpServletResponse;

    @Test
    public void testPreHandle() throws Exception {
        httpServletRequest.setAttribute("api_key","recap");
        boolean continueExport = swaggerInterceptor.preHandle(httpServletRequest,httpServletResponse,new Object());
        assertTrue(!continueExport);
    }

    @Test
    public void testPostHandle() throws Exception {
        swaggerInterceptor.postHandle(httpServletRequest,httpServletResponse,new Object(),new ModelAndView());
    }

    @Test
    public void testAfterCompletion() throws Exception {
        swaggerInterceptor.afterCompletion(httpServletRequest,httpServletResponse,new Object(),new Exception());
    }

}