import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    static int attempt = 0;

    public static void main (String[] agrs) {
        final String apikey = "d646ec6a-5c62-454d-b23d-70d516c7acc2";

        System.out.println("Введите ваши координаты через пробел и вам будет предложены прямые рейсы до Екатеринбурга в ближайшие 3 дня:");
        System.out.println("Пример: 55.75 37.62; 55.75 37.62; 43.6 39.7");
        Scanner scanner = new Scanner(System.in);

        try {
            double lat = Double.valueOf(scanner.next()/*.replace(".", ",")*/);
            double lng = Double.valueOf(scanner.next()/*.replace(".", ",")*/);

            System.out.println("lat=" + lat + " lng=" + lng);

            try {
                City city = getCity(apikey, lat, lng);
                city.print();

                Date date = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DATE, 1);
                date = c.getTime();
                Schedule sh = goToSaransk(city, apikey, date);

                sh.print();
            } catch (YaEx e) {
                System.out.println(e.getMessage());
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }





    public static City getCity(String apikey, double lat, double lng) throws YaEx {
        URL url = null;
        try{
            url = new URL("https://api.rasp.yandex.net/v3.0/nearest_settlement/?apikey=" + apikey +
                    "&format=json&lat=" + lat + "&lng=" + lng + "&distance=50&lang=ru_RU");
//            url = new URL("https://api.rasp.yandex.net/v3.0/nearest_settlement/?apikey=" + apikey + "&format=json&lat=54.1838&lng=45.1749&distance=50&lang=ru_RU");
//            url = new URL("https://api.artsy.net/api/tokens/xapp_token?client_id=d59284fc59044f9f0df3&client_secret=daec759b78aa7d1e7d9b44a5c33a650c");


            HttpURLConnection connection = (HttpURLConnection) url.openConnection();


            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                StringBuffer response = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                //System.out.println(response);

                Gson gson = new Gson();
                City city = new City();
                city = gson.fromJson(String.valueOf(response), city.getClass());

                return city;
            } catch (IOException e){
                //e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        throw new YaEx("Нет городов по введенному запросу");
    }

    public static Schedule goToSaransk(City city, String apikey, Date date) throws YaEx {
        URL url = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //System.out.println(sdf.format(date));
        final String CODE = "c54";

        try {
            url = new URL ("https://api.rasp.yandex.net/v3.0/search/?apikey=" + apikey + "&format=json&from=" + city.code + "&to=" + CODE + "&lang=ru_RU&page=1&date=" + sdf.format(date));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                StringBuffer response = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                //System.out.println(response);

                Gson gson = new Gson();
                Schedule sh = gson.fromJson(response.toString(), Schedule.class);
//                if (sh.pagination.total == 0)
                {
                    while (/*sh.pagination.total == 0 && */attempt ++ < 3) {
                        sh.print();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, 1);
                        date = c.getTime();
                        sh = goToSaransk(city, apikey, date);
                    }
                }
                return sh;
//                System.out.println(sh);

            } catch (IOException e){
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new YaEx("Нет маршрутов по указанным параметрам");
    }


}
