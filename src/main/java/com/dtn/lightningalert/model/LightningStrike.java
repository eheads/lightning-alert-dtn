/**
 * 
 */
package com.dtn.lightningalert.model;

import com.dtn.lightningalert.types.FlashType;

import lombok.Data;

/**
 * A class to model the lightning alert information
 *
 */

@Data
public class LightningStrike {
    
    private FlashType flashType;
    private long strikeTime;
    private double latitude;
    private double longitude;
    private int peakAmps;
    private int reserved;
    private int icHeight;
    private long receivedTime;
    private int numberOfSensors;
    private int multiplicity;
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("flashType: "+getFlashType().getType());
        str.append(", strikeTime: "+getStrikeTime());
        str.append(", latitude: "+getLatitude());
        str.append(", longitude: "+getLongitude());
        
        return str.toString();
    }
}