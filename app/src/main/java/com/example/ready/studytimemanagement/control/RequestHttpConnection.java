package com.example.ready.studytimemanagement.control;

import android.util.JsonReader;
import android.util.Log;

import com.example.ready.studytimemanagement.model.Data;
import com.example.ready.studytimemanagement.model.Time;
import com.example.ready.studytimemanagement.model.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * @brief Class to communicate android with server
 */
public class RequestHttpConnection {
    private String server_address = "http://192.168.0.9:5000";
    private InputStream is = null;

    /**
     * @brief method to connect to the server
     * @param url the url you want to communicate with
     * @return Connected HttpURLConnection Object
     */
    private HttpURLConnection connectHTTP(String url) {
        HttpURLConnection httpCon;
        try {
            URL urlCon = new URL(this.server_address + url);
            httpCon = (HttpURLConnection) urlCon.openConnection();
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Accept", "application/json");
            httpCon.setRequestProperty("Content-type", "application/json");
            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);
            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);
            return httpCon;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @brief method to get user data from server with specific id
     * @param url
     * @param id
     * @return User Object received from server
     */
    public User getUser(String url, String id){
        InputStream is = null;
        String result = "";
        User user = new User();
        try {
            HttpURLConnection httpCon = connectHTTP(url);
            String json = "";

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("id", id);

            // convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.e("post_data", json);

            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.flush();
            os.close();

            is = httpCon.getInputStream();
            ReadJSON readJson = new ReadJSON();
            try {
                // convert inputstream to string

                if(is != null)
                    user = readJson.readJsonUser(is);
                else
                    user = null;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                is.close();
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.e("InputStream", e.getLocalizedMessage());
        }
        return user;
    }

    /**
     * @brief method to register user data to server
     * @param url
     * @param user
     * @return 'complete' if successful, 'fail' if not
     */
    public String registerUser(String url, User user){
        InputStream is = null;
        String result = "";
        try {
            HttpURLConnection httpCon = connectHTTP(url);

            String json = "";

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("id", user.getId());
            jsonObject.accumulate("nickname", user.getNickname());
            jsonObject.accumulate("age", Integer.toString(user.getAge()));
            jsonObject.accumulate("job", user.getJob());

            // convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.e("post_data", json);

            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.flush();
            os.close();

            is = httpCon.getInputStream();
            InputStreamReader responseReader = new InputStreamReader(is, "UTF-8");
            JsonReader jsonReader = new JsonReader(responseReader);
            try {
                // convert inputstream to string

                if(is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "fail";

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                is.close();
                responseReader.close();
                jsonReader.close();
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.e("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * @brief method to get user data from server with specific id
     * @param url
     * @param id
     * @return Time Object received from server
     */
    public Time getAllTime(String url, String id){
        InputStream is = null;
        String result = "";
        Time time_list = new Time();
        try {
            HttpURLConnection httpCon = connectHTTP(url);
            String json = "";

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("id", id);

            // convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.e("post_data", json);

            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.flush();
            os.close();

            is = httpCon.getInputStream();
            ReadJSON readJson = new ReadJSON();
            try {
                // convert inputstream to string
                if(is != null) {
                    Log.d("get_time", "read start");
                    time_list.setData_list(readJson.readJsonTime(is));
                    Log.d("get_time", "read end");
                    Log.d("get_time", time_list.getData_list().get(0).getCategory());
                } else
                    time_list = null;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                is.close();
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.e("InputStream", e.getLocalizedMessage());
        }
        return time_list;
    }

    /**
     * @brief method to register time data to server
     * @param url
     * @param id
     * @param time
     * @return 'complete' if successful, 'fail' if not
     */
    public String registerTime(String url, String id, Data time){
        String result = "";
        try {
            HttpURLConnection httpCon = connectHTTP(url);
            String json = "";

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("id", id);
            jsonObject.accumulate("category", time.getCategory());
            jsonObject.accumulate("amount", time.getAmount());

            // convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.e("post_time", json);

            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.flush();
            os.close();

            is = httpCon.getInputStream();
            InputStreamReader responseReader = new InputStreamReader(is, "UTF-8");
            JsonReader jsonReader = new JsonReader(responseReader);
            try {
                if(is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "fail";
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                is.close();
                responseReader.close();
                jsonReader.close();
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.e("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * @brief method to convert data of Inputstream type to data of String type
     * @param inputStream
     * @return converted String data
     * @throws IOException
     */
    private String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}