package com.learnmymove.learnmymove.GMap;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Project Name => LearnMyMove
 * Created by   => Pankaj Koirala
 * Created on   => 2:23 PM 06 Jan 2018
 * Email Id     => koiralapankaj007@gmail.com
 */

// This class is going to retrieve data from url using HTTP Url connection and file handling method's.
class DownloadUrl {

    String readUrl(String myUrl) throws IOException {

        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(myUrl);   // Create Url
            urlConnection = (HttpURLConnection) url.openConnection();   // Open Url
            urlConnection.connect();    // Connect Url

            // Read data from url
            inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            data = stringBuffer.toString();
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (inputStream != null) {
                inputStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        Log.d("DownloadURL","Returning data= "+data);
        return data;
    }

}
