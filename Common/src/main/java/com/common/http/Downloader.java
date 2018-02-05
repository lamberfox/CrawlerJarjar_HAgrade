package com.common.http;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

//import static com.sun.xml.internal.ws.api.message.Packet.Status.Request;

/**
 * Created by tonyjarjar on 2017/9/1.
 */
public class Downloader {

    public static OkHttpClient client = new OkHttpClient();

    public static String downLoad(String url) throws IOException {
        String feed = "";
        Request request = new Request.Builder().url(url).build();
        okhttp3.Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            feed = response.body().string();
            response.close();
            response.body().close();
            return feed;
        } else {
            throw new IOException("Unexpected code " + response);
        }


    }

    public static String downLoad(HttpEntity httpEntity)
    {
        String feed = "访问失败";
        try {
            FormBody.Builder builder = new FormBody.Builder();
            if (httpEntity.paramter!=null&&!httpEntity.paramter.isEmpty()){
                for(Map.Entry<String,String> entry:httpEntity.paramter.entrySet()){
                    builder.add(entry.getKey(),entry.getValue());
                }
            }
            RequestBody requestBody = builder.build();
            Request request = new Request.Builder().url(httpEntity.uri.toString()).post(requestBody).build();
            okhttp3.Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                feed = response.body().string();
                response.body().close();
                response.close();
                return feed ;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }catch (Exception ex)
        {}
        return feed;

    }
    public static String downLoad(String url,String content)
    {
        String line         = "";
        String message        = "";
        String returnData   = "";
        boolean postState     = false;
        BufferedReader bufferedReader = null;

        try {
            URL urlObject = new URL(url);
            HttpURLConnection urlConn = (HttpURLConnection) urlObject.openConnection();
            urlConn.setDoOutput(true);
            /*设定禁用缓存*/
            //urlConn.setRequestProperty("Pragma:", "no-cache");
            urlConn.setRequestProperty("Cache-Control", "no-cache");
            /*维持长连接*/
            urlConn.setRequestProperty("Connection", "Keep-Alive");
            /*设置字符集*/
            urlConn.setRequestProperty("Charset", "UTF-8");
            /*设定输出格式为json*/
            urlConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            /*设置使用POST的方式发送*/
            urlConn.setRequestMethod("POST");
            /*设置不使用缓存*/
            urlConn.setUseCaches(false);
            /*设置容许输出*/
            urlConn.setDoOutput(true);
            /*设置容许输入*/
            urlConn.setDoInput(true);
            urlConn.connect();

            OutputStreamWriter outStreamWriter = new OutputStreamWriter(urlConn.getOutputStream(),"UTF-8");
            outStreamWriter.write(content);
            outStreamWriter.flush();
            outStreamWriter.close();

            /*若post失败*/
            if((urlConn.getResponseCode() != 200)){
                returnData = "{\"jsonStrStatus\":0,\"processResults\":[]}";
                message = "发送POST失败！"+ "code="+urlConn.getResponseCode() + "," + "失败消息："+ urlConn.getResponseMessage();
                // 定义BufferedReader输入流来读取URL的响应
                InputStream errorStream = urlConn.getErrorStream();

                if(errorStream != null)
                {
                    InputStreamReader inputStreamReader = new InputStreamReader(errorStream,"utf-8");
                    bufferedReader = new BufferedReader(inputStreamReader);

                    while ((line = bufferedReader.readLine()) != null) {
                        message += line;
                    }
                    inputStreamReader.close();
                }
                errorStream.close();
                System.out.println("发送失败！错误信息为："+message);
            }else{
                /*发送成功返回发送成功状态*/
                postState = true;

                // 定义BufferedReader输入流来读取URL的响应
                InputStream inputStream = urlConn.getInputStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"utf-8");
                bufferedReader = new BufferedReader(inputStreamReader);

                while ((line = bufferedReader.readLine()) != null) {
                    message += line;
                }
                returnData = message;
                inputStream.close();
                inputStreamReader.close();

                returnData = "成功！";
                System.out.println("发送POST成功！返回内容为：" + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return returnData;
        }

    }
}
