package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Christoforos Apostolopoulos
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    

	    // create a new SimplePointMarker for each PointFeature in 
	    // earthquakes.  Then add each new SimplePointMarker to the 
	    // List markers (so that it will be added to the map in the line below)
	    
	    for(int i = 0; i < earthquakes.size(); i++)
	    {
	    	PointFeature f = earthquakes.get(i);
	    	markers.add(createMarker(f));
	    }
	  // Add the markers to the map so that they are displayed
	    map.addMarkers(markers);
	}
		

	private SimplePointMarker createMarker(PointFeature feature)
	{  
		// To print all of the features in a PointFeature (so you can see what they are)
		// uncomment the line below.  Note this will only print if you call createMarker 
		// from setup
		//System.out.println(feature.getProperties());
		
		// Create a new SimplePointMarker at the location given by the PointFeature
		SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
		
		Object magObj = feature.getProperty("magnitude");
		float mag = Float.parseFloat(magObj.toString());
		
		// Here is an example of how to use Processing's color method to generate 
	    // an int that represents the color yellow.  
	    int red = color(255,0,0);
	    int yellow = color(255,255,0);
	    int blue = color(0,0,255);
		

	    float magnitude = (float) feature.getProperty("magnitude");
    	
    	// Minor earthquakes
    	if(magnitude < 4.0)
    	{
    		marker.setColor(blue);
    		((SimplePointMarker) marker).setRadius(5);
    	}
    	else
    	{
    		// Light earthquakes
    		if(magnitude < 5)
    		{
    			marker.setColor(yellow);
    			((SimplePointMarker) marker).setRadius(10);
    		}
    		// Moderate and higher earthquakes
    		else
    		{
    			marker.setColor(red);
    			((SimplePointMarker) marker).setRadius(15);
    		}
    	}
	    
	    // Finally return the marker
	    return marker;
	}
	
	public void draw() 
	{
	    background(10);
	    map.draw();
	    addKey();
	}

	private void addKey() 
	{	

		fill(153);
		rect(10, 250, 150, 150);
		textSize(18);
		// 
		fill(color(255,255,255));
		text("Properties", 15, 270);
		
		// Red 
		fill(color(255,0,0));
		ellipse(30, 290, 15, 15);
		textSize(12);
		text("Greater than 5.0 mag", 50, 290);
		
		// Yellow
		fill(color(255, 255, 0));
		ellipse(30, 330, 10, 10);
		textSize(12);
		text("Greater than 4.0 mag", 50, 330);
		
		// Blue
		fill(color(0,0,255));
		ellipse(30, 360, 5, 5);
		textSize(12);
		text("Less than 4.0 mag", 35, 360);
	}
}
