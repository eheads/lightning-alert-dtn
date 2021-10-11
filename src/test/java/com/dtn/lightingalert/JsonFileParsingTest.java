package com.dtn.lightingalert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;

import com.dtn.lightningalert.model.Asset;
import com.dtn.lightningalert.model.LightningStrike;
import com.dtn.lightningalert.types.FlashType;
import com.dtn.lightningalert.util.JsonFileParsingUtil;

//Need to refactor to mock application-test.yml without loading the whole Application context
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.yml")
public class JsonFileParsingTest {
    private static final Logger log = LoggerFactory.getLogger(JsonFileParsingTest.class);
    private static final String assetFile = "classpath:assets.json";
    private static final String lightningstrikeFile = "classpath:lightning.json";
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    @Test
    void testAssetJsonFile() throws IOException {
        Resource assetsResources = resourceLoader.getResource(assetFile);
        
        //Get the file from the classpath
        Set<Asset> actual = JsonFileParsingUtil.parseAssetsJsonFile(assetsResources.getInputStream());

        Set<Asset> expected = new HashSet<Asset>();
        byte[] bdata = FileCopyUtils.copyToByteArray(assetsResources.getInputStream());
        String data = new String(bdata, StandardCharsets.UTF_8);
        log.debug("data: {}",data.length());
        Asset[] assetArray = (Asset[]) JsonFileParsingUtil.convertToObject(data, Asset[].class);
        for (Asset asset : assetArray) {
            expected.add(asset);
        }
        Assertions.assertEquals(expected.size(), actual.size());
    }

    @Test
    void testLightningJsonFile() throws IOException {
        //Get the file from the classpath
        Resource lsResources = resourceLoader.getResource(lightningstrikeFile);
        Set<LightningStrike> actual = JsonFileParsingUtil.parseLightningStrikesJsonFile(lsResources.getInputStream());
        
        //Parse and collect the raw JSON string 
        BufferedReader br = new BufferedReader(new InputStreamReader(lsResources.getInputStream()));
        Set<LightningStrike> expected = new HashSet<LightningStrike>();
        String line;
        while ((line = br.readLine()) != null) {
           //log.info("line: {}", line);
           LightningStrike ls = (LightningStrike) JsonFileParsingUtil.convertToObject(line, LightningStrike.class);
           // Exclude HEARTBEAT as it is not an actual lightning strike alert
           if (FlashType.HEARTBEAT.equals(ls.getFlashType())) {
               continue;
           }
           expected.add(ls);
        }
        
        log.info("Number of converted raw json string to set of LightningStrike objects: {}, Number of parsed Lightning Strikes from JsonFileParsingUtil: {}", actual.size(), actual.size());
        Assertions.assertEquals(expected.size(), actual.size());
    }
}