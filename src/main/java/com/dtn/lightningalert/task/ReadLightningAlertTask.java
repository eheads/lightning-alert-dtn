package com.dtn.lightningalert.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dtn.lightningalert.model.Asset;
import com.dtn.lightningalert.model.LightningStrike;
import com.dtn.lightningalert.util.JsonFileParsingUtil;
import com.dtn.lightningalert.util.QuadKeyUtil;

@Component
public class ReadLightningAlertTask {
    private static final Logger log = LoggerFactory.getLogger(ReadLightningAlertTask.class);
    
    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${map.zoomLevel}")
    private int zoomLevel;
    
    @Value("${jsonfile.asset}")
    private String assetFile;
    
    @Value("${jsonfile.lightningstrike}")
    private String lightningstrikeFile;

    @Scheduled(fixedDelayString = "${schedule.fixedDelay}")
    public void readLightningAlert() {
        log.info("[Start reading lightning alert data...]");
        try {
            Map<String, Asset> map = new HashMap<String, Asset>();
            
            Resource assetsResources = resourceLoader.getResource(assetFile);
            Resource lsResources = resourceLoader.getResource(lightningstrikeFile);
            Set<Asset> assets = JsonFileParsingUtil.parseAssetsJsonFile(assetsResources.getInputStream());
            Set<LightningStrike> lightningStrikes = JsonFileParsingUtil.parseLightningStrikesJsonFile(lsResources.getInputStream());
            
            if(!assets.isEmpty() && !lightningStrikes.isEmpty())
            for(Asset asset : assets) {
                for(LightningStrike ls : lightningStrikes) {
                    String quadKey = QuadKeyUtil.convertLocationToQuadKey(ls.getLatitude(), ls.getLongitude(), zoomLevel);
                    if(asset.getQuadKey().equals(quadKey) && !map.containsKey(quadKey)) {
                        map.put(quadKey, asset);
                        log.info("lightning alert for {}:{}", asset.getAssetOwner(), asset.getAssetName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Something went wrong while reading the json files: {}", e);
        }
        log.info("[End reading lightning alert data]");
    }
}