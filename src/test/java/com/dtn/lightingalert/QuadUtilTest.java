package com.dtn.lightingalert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtn.lightningalert.util.QuadKeyUtil;

public class QuadUtilTest {
    private static final Logger log = LoggerFactory.getLogger(QuadUtilTest.class);
    
    private static final int zoomLevel = 12;
    
    @Test
    void testQuadKey() {
        System.out.println("zoomLevel: "+zoomLevel);
        double latitude = 10.3846;
        double longtitude = 166.2902;
        //Calculating quadKey base on latitude and longtitude
        String quadKey = QuadKeyUtil.convertLocationToQuadKey(latitude, longtitude, zoomLevel); 
        log.info("quadKey: {}", quadKey);
        
        //Calculating latitude and longtitude base on quadKey
        double[] axis = QuadKeyUtil.convertQuadKeyToLocation(quadKey, zoomLevel);
        log.info("lat: {}",axis[0]);
        log.info("long: "+axis[1]);
        
        //the calculated latitude and longtitude are different from the original latitude and longtitude
        //need to dig deeper on the documentation why there are errors
        
        //But somehow, calculating the quadKey again based on the calculated latitude and longtitude
        //has the same quadKey value as the first calculated quadKey value (see line 15)
        String quadKey2 = QuadKeyUtil.convertLocationToQuadKey(axis[0], axis[1], zoomLevel); 
        log.info("quadKey2: {}",quadKey2);
        
        Assertions.assertEquals(quadKey, quadKey2);           
    }
}