package cc.funkemunky.api.utils;

import lombok.Getter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Pastebin {

    static String api_user_key = "15479eb626106167cade921b7d2b7e3c"; //Insert your own api_user_key if you have one.
    static String pasteURL = "http://www.pastebin.com/api/api_post.php";

    public Pastebin()
    {
    }

    static String checkResponse(String response)
    {
        if (response.substring(0, 15).equals("Bad API request")) {
            return response.substring(17);
        }
        return "";
    }

    static public String makePaste(String body, String name, Privacy privacy)
            throws UnsupportedEncodingException
    {
        String content = URLEncoder.encode(body, "UTF-8");
        String title = URLEncoder.encode(name + " report", "UTF-8");
        String data = "api_option=paste&api_user_key=" + Pastebin.api_user_key
                + "&api_paste_private=" + privacy.getPrivacy() + "&api_paste_name=" + title
                + "&api_paste_expire_date=N&api_paste_format=" + "text"
                + "&api_dev_key=" + api_user_key + "&api_paste_code=" + content;
        return getString(data);
    }

    private static String getString(String data) {
        String response = Pastebin.page(Pastebin.pasteURL, data);
        String check = Pastebin.checkResponse(response);
        if (!check.equals("")) {
            return check;
        }
        return response;
    }

    static public String makePaste(String body, String name, Privacy privacy, String expire)
            throws UnsupportedEncodingException
    {
        String content = URLEncoder.encode(body, "UTF-8");
        String title = URLEncoder.encode(name + " report", "UTF-8");
        String data = "api_option=paste&api_user_key=" + Pastebin.api_user_key
                + "&api_paste_private=" + privacy.getPrivacy() + "&api_paste_name=" + title
                + "&api_paste_expire_date=" + expire + "&api_paste_format=" + "text"
                + "&api_dev_key=" + api_user_key + "&api_paste_code=" + content;
        return getString(data);
    }

    static String page(String uri, String urlParameters)
    {
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

    @Getter
    public static enum Privacy {
        PUBLIC(0), UNLISTED(1), PRIVATE(2);

        private int privacy;
        Privacy(int privacy) {
            this.privacy = privacy;
        }
    }
}