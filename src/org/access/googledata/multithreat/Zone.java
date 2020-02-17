package org.access.googledata.multithreat;

public class Zone {
	private final Point start_point;
	private final Point end_point;
	private final int raduis;
	private final double distance;
	
	public static final double meter = 0.000001/Zone.distance(0, 0, 0, 0.000001);
//	public static final double meter = 0.000001/0.11119492664455875;
	
	private Location location;

	public Zone(double start_lat, double start_lng, double end_lat,
			double end_lng, int raduis) {
		this.start_point = new Point(start_lat,start_lng);
		this.end_point = new Point(end_lat,end_lng);
		this.raduis = raduis;
		this.distance = Math.round(Math.pow(10, 6)*2*raduis*meter)/Math.pow(10, 6);
		
		System.out.println(raduis);
		System.out.println(distance);
		
		this.location = new Location(this.start_point, this.raduis);
	}

	public double getStartLat() {
		return start_point.getLat();
	}

	public double getStartLng() {
		return start_point.getLng();
	}

	public double getEndLat() {
		return end_point.getLat();
	}

	public double getEndLng() {
		return end_point.getLng();
	}

	public int getRaduis() {
		return raduis;
	}
	
	
	public Point getStartPoint(){
		return start_point;
	}
	
	public Point getEndPoint(){
		return end_point;
	}
	
	public int calculatePoints(){
		double latdis = distance(getStartLat(), getEndLat(), 0.0, 0.0);
		double lngdis = distance(0.0, 0.0,getStartLng(),getEndLng());
		
		int r = raduis;
		
		System.out.println(latdis);
		System.out.println(lngdis);
		
		int w = (int) ((lngdis-r)%(2*r));
		
		int n = 0;
		if(w==0){
			n = (int) ((lngdis-r)/(2*r)+1);
		}else if(w > r){
			n = (int) (lngdis-lngdis%2)/(2*r) +1;
		}else if (w <= r){
			n = (int) (lngdis-lngdis%2+r)/(2*r) +1;
		}
		
		int t = (int) ((latdis-r)%(2*r));
		
		int m = 0;
		if(t==0){
			m = (int) ((latdis-r)/(2*r)+1);
		}else if(t > r){
			m = (int) (latdis-latdis%2)/(2*r) +1;
		}else if (t <= r){
			m = (int) (latdis-latdis%2+r)/(2*r) +1;
		}
		
		return m*n;

	}
	
	public boolean hasNext(){
		double new_lat = location.getLat();
		double new_lng = location.getLng();
//		while (new_lat > end_lat - distance)
		if (new_lng+distance/2 < end_point.getLng() || new_lat-distance/2 > end_point.getLat()){
			return true;
		} else {
			return false;
		}
	}
	
	public Location getFirst(){
		return location;
	}
	
	public Location next(){
//		System.out.print(location.toString());
		double new_lat = location.getLat();
		double new_lng = location.getLng();
//		while (new_lat > end_lat - distance)
		if (new_lng+distance/2 < end_point.getLng() ){
			new_lng += distance;
		} else if(new_lat-distance/2 > end_point.getLat() ){
			new_lat -= distance;
			new_lng = start_point.getLng();
		} else {
			return null;
		}
		location = new Location(new_lat, new_lng, raduis);
		return location;
	}
	
	public static double distance(double lat1, double lat2, double lon1,
			double lon2) {

		final int R = 6371; // Radius of the earth

		Double latDistance = Math.toRadians(lat2 - lat1);
		Double lonDistance = Math.toRadians(lon2 - lon1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2)
				* Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		return distance;
	} 
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("%.6f,%.6f - %.6f,%.6f",start_point.getLat(),start_point.getLng(),end_point.getLat(),end_point.getLng());
	}

	public void resume(Point p) {
		location = new Location(p, raduis);
		
	}
}
