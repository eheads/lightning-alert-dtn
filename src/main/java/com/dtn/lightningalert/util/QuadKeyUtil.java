package com.dtn.lightningalert.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//https://social.msdn.microsoft.com/Forums/en-US/9ed38f44-e796-4a01-995d-8736fa67f305/get-quadkey-for-tile-at-specific-xy-latlong?forum=vemapcontroldev
//LatLongToPixelXY -> PixelXYToTileXY -> TileXYToQuadKey
public class QuadKeyUtil {
    private static final Logger log = LoggerFactory.getLogger(QuadKeyUtil.class);

    private QuadKeyUtil(){}
    
    private static double EarthRadius = 6378137;  
    private static double MinLatitude = -85.05112878;  
    private static double MaxLatitude = 85.05112878;  
    private static double MinLongitude = -180;  
    private static double MaxLongitude = 180;
    
    public static String convertLocationToQuadKey(double latitude, double longtitude, int zoomLevel) {
        int[] pixelXY = latLongToPixelXY(latitude, longtitude, zoomLevel);
        int[] tilelXY = pixelXYToTileXY(pixelXY[0], pixelXY[1]);
        return tileXYToQuadKey(tilelXY[0], tilelXY[1], zoomLevel);
    }
    
    public static double[] convertQuadKeyToLocation(String quadKey, int zoomLevel) {
        int[] tileXY = quadKeyToTileXY(quadKey, zoomLevel);
        int[] pixelXY = tileXYToPixelXY(tileXY[0], tileXY[1]);
        return pixelXYToLatLong(pixelXY[0], pixelXY[1], zoomLevel);
    }

    /// <summary>  
    /// Clips a number to the specified minimum and maximum values.  
    /// </summary>  
    /// <param name="n">The number to clip.</param>  
    /// <param name="minValue">Minimum allowable value.</param>  
    /// <param name="maxValue">Maximum allowable value.</param>  
    /// <returns>The clipped value.</returns>  
    private static double Clip(double n, double minValue, double maxValue){  
        return Math.min(Math.max(n, minValue), maxValue);  
    }  

    /// <summary>  
    /// Determines the map width and height (in pixels) at a specified level  
    /// of detail.  
    /// </summary>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <returns>The map width and height in pixels.</returns>  
    public static long MapSize(int levelOfDetail){  
        return (long) 256 << levelOfDetail;  
    }  

    /// <summary>  
    /// Converts a point from latitude/longitude WGS-84 coordinates (in degrees)  
    /// into pixel XY coordinates at a specified level of detail.  
    /// </summary>  
    /// <param name="latitude">Latitude of the point, in degrees.</param>  
    /// <param name="longitude">Longitude of the point, in degrees.</param>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <param name="pixelX">Output parameter receiving the X coordinate in pixels.</param>  
    /// <param name="pixelY">Output parameter receiving the Y coordinate in pixels.</param>  
    public static int[] latLongToPixelXY(double latitude, double longitude, int levelOfDetail){ 
        int[] result = new int[2];
        latitude = Clip(latitude, MinLatitude, MaxLatitude);  
        longitude = Clip(longitude, MinLongitude, MaxLongitude);  

        double x = (longitude + 180) / 360;   
        double sinLatitude = Math.sin(latitude * Math.PI / 180);  
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);  

        long mapSize = MapSize(levelOfDetail);  
        result[0] = (int) Clip(x * mapSize + 0.5, 0, mapSize - 1);  
        result[1] = (int) Clip(y * mapSize + 0.5, 0, mapSize - 1);
        log.debug("pixelX: {}, pixelY: {}", result[0], result[1]);
        return result;
    }  

    /// <summary>  
    /// Converts a pixel from pixel XY coordinates at a specified level of detail  
    /// into latitude/longitude WGS-84 coordinates (in degrees).  
    /// </summary>  
    /// <param name="pixelX">X coordinate of the point, in pixels.</param>  
    /// <param name="pixelY">Y coordinates of the point, in pixels.</param>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <param name="latitude">Output parameter receiving the latitude in degrees.</param>  
    /// <param name="longitude">Output parameter receiving the longitude in degrees.</param>  
    public static double[] pixelXYToLatLong(int pixelX, int pixelY, int levelOfDetail){
        double[] result = new double[2];
        double mapSize = MapSize(levelOfDetail);  
        double x = (Clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;  
        double y = 0.5 - (Clip(pixelY, 0, mapSize - 1) / mapSize);  

        result[0] = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;  
        result[1] = 360 * x;
        
        log.debug("lat: {}, long: {}", result[0], result[1]);
        return result;
    }  

    /// <summary>  
    /// Converts pixel XY coordinates into tile XY coordinates of the tile containing  
    /// the specified pixel.  
    /// </summary>  
    /// <param name="pixelX">Pixel X coordinate.</param>  
    /// <param name="pixelY">Pixel Y coordinate.</param>  
    /// <param name="tileX">Output parameter receiving the tile X coordinate.</param>  
    /// <param name="tileY">Output parameter receiving the tile Y coordinate.</param>  
    public static int[] pixelXYToTileXY(int pixelX, int pixelY){  
        int[] result = new int[2];
        result[0] = pixelX / 256;  
        result[1] = pixelY / 256;
        
        log.debug("tileX: {}, tileY: {}", result[0], result[1]);
        return result;
    }  

    /// <summary>  
    /// Converts tile XY coordinates into pixel XY coordinates of the upper-left pixel  
    /// of the specified tile.  
    /// </summary>  
    /// <param name="tileX">Tile X coordinate.</param>  
    /// <param name="tileY">Tile Y coordinate.</param>  
    /// <param name="pixelX">Output parameter receiving the pixel X coordinate.</param>  
    /// <param name="pixelY">Output parameter receiving the pixel Y coordinate.</param>  
    public static int[] tileXYToPixelXY(int tileX, int tileY){  
        int[] result = new int[2];
        result[0] = tileX * 256;  
        result[1] = tileY * 256;
        return result;
    } 

    /**
     * Calculate the QuadKey based on latitude and Longtitude
     * 
     * 
     * @param latitude
     * @param longtitude
     * @return quadKey
     */
    public static String tileXYToQuadKey(int tileX, int tileY, int levelOfDetail) {
        log.debug("getting quadkey for tileX {}, tileY: {}, zoom: {}",tileX, tileY, levelOfDetail);

        StringBuilder quadKey = new StringBuilder();  
        for (int i = levelOfDetail; i > 0; i--)  
        {  
            char digit = '0';  
            long mask = 1 << (i - 1);  

            if ((tileX & mask) != 0){  
                digit++;  
            }  
            if ((tileY & mask) != 0){  
                digit++;  
                digit++;  
            }
            quadKey.append(digit);  
        }  
        return quadKey.toString();  
    } 

    /// <summary>  
    /// Converts a QuadKey into tile XY coordinates.  
    /// </summary>  
    /// <param name="quadKey">QuadKey of the tile.</param>  
    /// <param name="tileX">Output parameter receiving the tile X coordinate.</param>  
    /// <param name="tileY">Output parameter receiving the tile Y coordinate.</param>  
    /// <param name="levelOfDetail">Output parameter receiving the level of detail.</param>  
    public static int[] quadKeyToTileXY(String quadKey, int zoomLevel){
        int[] result = new int[2]; 
        int tileX = 0; 
        int tileY = 0;  
        for (int i = zoomLevel; i > 0; i--){  
            long mask = 1 << (i - 1);
            char[] quadKeys = quadKey.toCharArray();
            char qkey = quadKeys[zoomLevel - i];
            switch (qkey){  
            case '0':  
                break;  

            case '1':  
                tileX |= mask;  
                break;  

            case '2':  
                tileY |= mask;
                break;  

            case '3':  
                tileX |= mask; 
                tileY |= mask;  
                break;  
            default:  
                ;  
            }  
        }
        log.debug("tileX: {}, tileY: {}", tileX, tileY);
        result[0] = tileX;
        result[1] = tileY;
        return result;
    }  
}  