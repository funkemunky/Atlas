package cc.funkemunky.api.utils;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Pastebin {
    static String pasteURL = "https://funkemunky.cc/pastebin/make";

    public Pastebin() {

    }

    static String checkResponse(String response) {
        if (response.substring(0, 15).equals("Bad API request")) {
            return response.substring(17);
        }
        return "";
    }

    static public String makePaste(String body, String name, Privacy privacy)
            throws UnsupportedEncodingException {
        String content = URLEncoder.encode(body, "UTF-8");
        String title = URLEncoder.encode(name + " report", "UTF-8");
        String data = "body=" + content + "&name=" + title + "&privacy=" + privacy.name();
        String response = Pastebin.page(Pastebin.pasteURL, data);

        if(response == null) return "";
        String check = Pastebin.checkResponse(response);
        if (!check.equals("")) {
            return check;
        }
        return response;
    }

    static public String makePaste(String body, String name, Privacy privacy, String expire)
            throws UnsupportedEncodingException {
        String content = URLEncoder.encode(body, "UTF-8");
        String title = URLEncoder.encode(name + " report", "UTF-8");
        String data = "body=" + content + "&name=" + title + "&privacy=" + privacy.name() + "&expire=" + expire;
        String response = Pastebin.page(Pastebin.pasteURL, data);
        String check = Pastebin.checkResponse(response);
        if (!check.equals("")) {
            return check;
        }
        return response;
    }

    public static String page(String uri, String urlParameters) {
        URL url;
        HttpURLConnection connection = null;
        try {
            // Create connection
            url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static enum Privacy {
        PUBLIC(0), UNLISTED(1), PRIVATE(2);

        private int privacy;

        Privacy(int privacy) {
            this.privacy = privacy;
        }

        public int getPrivacy() {
            return privacy;
        }
    }
}