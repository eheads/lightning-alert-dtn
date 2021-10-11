package com.dtn.lightningalert.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import com.dtn.lightningalert.model.Asset;
import com.dtn.lightningalert.model.LightningStrike;
import com.dtn.lightningalert.types.FlashType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFileParsingUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonFileParsingUtil.class);

    private JsonFileParsingUtil() {}

    /**
     * Convert a json string to the given class
     * 
     * @param <T>
     * @param object
     * @param clazz
     * @return <T> Object
     * @throws IOException
     */
    public static <T> Object convertToObject(String object, Class<T> clazz) throws IOException {
        return new ObjectMapper().readValue(object, clazz);
    }

    /**
     * Convert the content of a file to a Set of Assets data
     * 
     * @param file
     * @return Set<Asset>
     */
    public static Set<Asset> parseAssetsJsonFile(InputStream fileInpuStream) {
        Set<Asset> assets = new HashSet<Asset>();
        try {
            byte[] bdata = FileCopyUtils.copyToByteArray(fileInpuStream);
            String data = new String(bdata, StandardCharsets.UTF_8);
            log.debug("data: {}",data.length());
            Asset[] assetArray = (Asset[]) convertToObject(data, Asset[].class);
            for (Asset asset : assetArray) {
                assets.add(asset);
            }
        } catch (IOException e) {
            log.error("Something went wrong while reading the json files: {}", e);
        } catch (Exception e) {
            log.error("Something went wrong while reading the json files: {}", e);
        }

        log.info("assets size: {}", assets.size());
        return assets;
    }

    /**
     * Convert the content of a file to a Set of Lightning strikes data and exclude heartbeats.
     * 
     * @param file
     * @return Set<LightningStrike>
     */
    public static Set<LightningStrike> parseLightningStrikesJsonFile(InputStream fileInpuStream) {
        Set<LightningStrike> lightningStrikes = new HashSet<LightningStrike>();

        //This is working on IDE but not when running the jar file
        //        Set<String> parsedLightningAlerts;
        //        try {
        //            parsedLightningAlerts = Files.lines(file.toPath()).collect(Collectors.toSet());
        //            for (String str : parsedLightningAlerts) {
        //                LightningStrike la = (LightningStrike) convertToObject(str, LightningStrike.class);
        //                // Exclude HEARTBEAT as it is not an actual lightning strike alert
        //                if (FlashType.HEARTBEAT.equals(la.getFlashType())) {
        //                    continue;
        //                }
        //                lightningStrikes.add(la);
        //            }
        //        } catch (IOException e) {
        //            log.error("Something went wrong while reading the json files: {}", e);
        //        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(fileInpuStream));
            String line;
            while ((line = br.readLine()) != null) {
                LightningStrike ls = (LightningStrike) convertToObject(line, LightningStrike.class);
                // Exclude HEARTBEAT as it is not an actual lightning strike alert
                if (FlashType.HEARTBEAT.equals(ls.getFlashType())) {
                    continue;
                }
                lightningStrikes.add(ls);
            }
        }catch (Exception e) {
            log.error("Something went wrong while reading the json files: {}", e);
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                log.error("Something went wrong while reading the json files: {}", e);
            }
        }
        log.info("lightning strike alerts size: {}", lightningStrikes.size());
        return lightningStrikes;
    }
}