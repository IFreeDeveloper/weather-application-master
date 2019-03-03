package mg.studio.weatherappdesign;

public class GetWeatherURL {
    /*(required)
    1. CityID
    2. Latitude and longitude format: longitude in front of latitude after latitude, English, separated, decimal format, north latitude east longitude is positive, south latitude west longitude is negative
    3. City name, support Chinese and English and Chinese pinyin
    4. City name, superior city or province or country, English, separated, this way can only get the weather data of the desired region in the case of the same name, such as xi 'an, shaanxi
    5. IP Address
    6. Automatically judge according to the request, obtain IP according to the user's request, locate and obtain the city data through IP
    */
    private String Location="";
    /*(optional)language*/
    private String Lang="";
    /*(optional)Measurement unit*/
    private String Unit="";
    /*(required)Key for user authentication*/
    private String Key = "978cb51f72654effaf8e4d542183412e";
    private String URLdaily = "https://free-api.heweather.net/s6/weather/forecast?";

    public String getURLHour() {
        return URLHour;
    }

    public void setURLHour(String URLHour) {
        this.URLHour = URLHour;
    }

    private String URLHour = "https://free-api.heweather.net/s6/weather/hourly?parameters";


    public GetWeatherURL(String location, String lang, String unit) {
        Location = location;
        Lang = lang;
        Unit = unit;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getLang() {
        return Lang;
    }

    public void setLang(String lang) {
        Lang = lang;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getDailyURL(){
        String finalURL = URLdaily;
        if(Location!=""){
            finalURL = finalURL+"location="+Location+"&";
        }
        if(Lang!=""){
            finalURL = finalURL+"lang="+Lang+"&";
        }
        if(Unit!=""){
            finalURL = finalURL+"unit="+Unit+"&";
        }
        finalURL = finalURL+"key="+Key;
        return finalURL;
    }
}
