package com.huotu.agento2o.service.service.product.impl;

import com.huotu.agento2o.service.config.ServiceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * shedule Test
 * Created by elvis on 2016/5/16.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = {ServiceConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SheduleWaringTest {
    @Test
    public void testSheduleWarning() throws InterruptedException {
        while(true){
            Thread.sleep(10);
        }
    }
}
