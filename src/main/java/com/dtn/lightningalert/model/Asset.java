package com.dtn.lightningalert.model;

import lombok.Data;

/**
 * A class to model the asset information
 *
 */

@Data
public class Asset {
    
   private String assetName;
   private String quadKey;
   private String assetOwner;
}
