package org.access.googledata.multithreat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.access.googledata.multithreat.GoogleAutoSearch.Search_Type;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.util.HSSFColor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class Keyword {
	private final String name;

	// private static final String raw_url =
	// "https://maps.googleapis.com/maps/api/place/nearbysearch/json?name=%s&location=%s&radius=%s&key=AIzaSyAAFXsoqBB01OnruUYmWZ6HhsE16UpLChI";
	// private static final String raw_url =
	// "https://maps.googleapis.com/maps/api/place/nearbysearch/json?name=%s&location=%s&radius=%s&key=AIzaSyCpA6UguHRWNxHDMKQDc7Ff-iR0XCA2VTI";
	private static final String raw_url_keyword = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?name=%s&location=%s&radius=%s&key=%s";
	private static final String raw_url_type = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?types=%s&location=%s&radius=%s&key=%s";
	/*
	 * Keys : AIzaSyAAFXsoqBB01OnruUYmWZ6HhsE16UpLChI
	 * AIzaSyCpA6UguHRWNxHDMKQDc7Ff-iR0XCA2VTI
	 * AIzaSyCjaTxBRbNqya8EkGbbLZ04KlBhCIGV6cw
	 */
	private final int max_count;
	private JProgressBar progreass;
	private final GoogleAutoSearch.Search_Type type;

	enum STATUS {
		OVER_QUERY_LIMIT, OK, ZERO_RESULTS,REQUEST_DENIED,INVALID_REQUEST, UNKNOWN;
		public static STATUS get(String s) {
			STATUS ret = UNKNOWN;
			switch (s) {
			case "ZERO_RESULT":
				ret = STATUS.ZERO_RESULTS;
				break;
			case "OVER_QUERY_LIMIT":
				ret = STATUS.OVER_QUERY_LIMIT;
				break;
			case "REQUEST_DENIED":
				ret = STATUS.REQUEST_DENIED;
				break;
			case "INVALID_REQUEST":
				ret = STATUS.INVALID_REQUEST;
				break;
			default:
				ret = STATUS.OK;
			}
			return ret;
		}
	}

	public Keyword(String name, int max_count, JProgressBar progress,
			GoogleAutoSearch.Search_Type type) {
		this.name = name;
		GoogleAutoSearch.results.put(name, new ArrayList<Result>());
		this.max_count = max_count;
		this.progreass = progress;
		this.type = type;
	}

	public STATUS search(Location location) throws IOException {
		STATUS ret;
		do {
			String key = GoogleAutoSearch.getKey();
			String url_str = String.format(
					(type == Search_Type.By_Keywords ? raw_url_keyword
							: raw_url_type), name,
					"" + location.getLat() + "," + location.getLng(),
					location.getRaduis(), key).replace(
					" ", "+");
			URL url = new URL(url_str);
			System.out.println(url_str);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			String res_str = response.toString();
			System.out.println(res_str);
			
			JsonObject json = new JsonParser().parse(res_str).getAsJsonObject();

			JsonPrimitive stats = json.getAsJsonPrimitive("status");

			ret = STATUS.get(stats.getAsString());
			if (ret == STATUS.OVER_QUERY_LIMIT) {
				GoogleAutoSearch.OverQueryKey(key);
				continue;
			} else if(ret==STATUS.REQUEST_DENIED){
				GoogleAutoSearch.InvalidKey(key);
				continue;
			} else if(ret==STATUS.ZERO_RESULTS){
				System.out.println("ZERO_RETULTS");
				return STATUS.ZERO_RESULTS;
			} else if(ret==STATUS.INVALID_REQUEST){
				System.out.println("URL was Invalid.Imidiately Exiting Program...");
				JOptionPane.showConfirmDialog(null,"URL was Invalid.Imidiately Exiting Program...");
				GoogleAutoSearch.exit();
			}

			JsonArray array = json.getAsJsonArray("results");

			// data.add(data_head);
			for (int i = 0; i < array.size(); i++) {
				// String data_row[] = new String[8];
				JsonObject result = array.get(i).getAsJsonObject();

				JsonArray type_obj = result.getAsJsonArray("types");
				String type = "";
				for (int j = 0; j < type_obj.size(); j++) {
					type += type_obj.get(j)
							+ (j == type_obj.size() - 1 ? "" : ",");
				}

				Result res = new Result(result.get("name").getAsString(),
						result.get("vicinity").getAsString(),
						((result.getAsJsonObject("geometry"))
								.getAsJsonObject("location")).get("lat")
								.getAsString(),
						((result.getAsJsonObject("geometry"))
								.getAsJsonObject("location")).get("lng")
								.getAsString(), type, result.get("icon")
								.getAsString(), result.get("id").getAsString(),
						result.get("place_id").getAsString());

				GoogleAutoSearch.results.get(this.name).add(res);
			}
		} while (ret!=STATUS.OK);
		progreass.setValue(progreass.getValue() + 1);
		GoogleAutoSearch.progress
				.setValue(GoogleAutoSearch.progress.getValue() + 1);
		return ret;
	}
}
