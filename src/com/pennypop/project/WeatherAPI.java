package com.pennypop.project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class WeatherAPI {
	private String api_url;
	
	private String name;
	private String description;
	private double windspeed;
	private double temp;
	
	
	/**
	 * Constructs instance of weatherAPI to read weather conditions from openweathermap.org
	 * @param url URL of the API
	 */
	public WeatherAPI(String url){
		this.api_url = url;
		pull();
	}
	
	/**
	 * Calls the API and updates the name, description, windspeed, 
	 * and temperature fields of the weatherAPI instance
	 */
	public void pull(){
		String response = callURL(api_url);
		
		JSONParser parser = new JSONParser();
        try {
        	//----------- Parse JSON from URL -----------------------------//
			JSONObject obj = (JSONObject) parser.parse(response);

			//Get city
			this.name = ((String) obj.get("name"));
			
			//Get weather description
			JSONObject weather = (JSONObject) ((JSONArray) obj.get("weather")).get(0);
			this.description = ((String) weather.get("description"));
			
			//Get windspeed
			this.windspeed = ((Double)((JSONObject) obj.get("wind")).get("speed"));
			
			//Get temperature
			JSONObject main = (JSONObject) obj.get("main");
			double tempK = (Double) main.get("temp");
			this.temp = (((tempK*9)/5) - 459.67);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Fetches JSON data from API at the given URL
	 * @param myURL the URL to the API
	 * @return a JSON string
	 */
	public static String callURL(String myURL) {
		StringBuilder builder = new StringBuilder();
		URLConnection conn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(myURL);
			conn = url.openConnection();
			if (conn != null) {
				conn.setReadTimeout(50000);
			}
			if (conn != null && conn.getInputStream() != null) {
				in = new InputStreamReader(conn.getInputStream(), Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						builder.append((char) cp);
					}
					bufferedReader.close();
				}
			}
		in.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception while calling URL:"+ myURL, e);
		} 
		return builder.toString();
	}

	public double getTemp() {
		return temp;
	}

	public double getWindspeed() {
		return windspeed;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

}