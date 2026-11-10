package com.example.dekutstudentregistration;

import android.util.Base64;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MpesaManager {

    private static final String CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
    private static final String CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET;
    private static final String BUSINESS_SHORTCODE = "174379";
    private static final String PASSKEY = BuildConfig.PASSKEY;
    private final OkHttpClient httpClient = new OkHttpClient();
    private final MpesaListener listener;

    public MpesaManager(MpesaListener listener) {
        this.listener = listener;
    }

    public void initiatePayment(String phoneNumber, String amount) {
        // Format phone number to start with 254
        String formattedNumber = phoneNumber;
        if (formattedNumber.startsWith("0")) {
            formattedNumber = "254" + formattedNumber.substring(1);
        }

        generateAccessToken(formattedNumber, amount);
    }

    private void generateAccessToken(String phoneNumber, String amount) {
        String auth = Credentials.basic(CONSUMER_KEY, CONSUMER_SECRET);
        Request request = new Request.Builder()
                .url("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials")
                .get()
                .addHeader("Authorization", auth)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onMpesaError("Authentication Failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        String accessToken = jsonObject.getString("access_token");
                        triggerSTKPush(accessToken, phoneNumber, amount);
                    } catch (Exception e) {
                        listener.onMpesaError("JSON Parsing Error");
                    }
                } else {
                    listener.onMpesaError("Authentication rejected by Safaricom");
                }
            }
        });
    }

    private void triggerSTKPush(String accessToken, String phoneNumber, String amount) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String passwordStr = BUSINESS_SHORTCODE + PASSKEY + timestamp;
        String password = Base64.encodeToString(passwordStr.getBytes(), Base64.NO_WRAP);

        try {
            JSONObject payload = new JSONObject();
            payload.put("BusinessShortCode", BUSINESS_SHORTCODE);
            payload.put("Password", password);
            payload.put("Timestamp", timestamp);
            payload.put("TransactionType", "CustomerPayBillOnline");
            payload.put("Amount", amount);
            payload.put("PartyA", phoneNumber);
            payload.put("PartyB", BUSINESS_SHORTCODE);
            payload.put("PhoneNumber", phoneNumber);
            payload.put("CallBackURL", "https://mydomain.com/path");
            payload.put("AccountReference", "DeKUT Fees");
            payload.put("TransactionDesc", "Registration Fee");

            RequestBody body = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url("https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onMpesaError("STK Push Failed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        listener.onMpesaSuccess();
                    } else {
                        listener.onMpesaError("STK Push rejected by Safaricom");
                    }
                }
            });
        } catch (Exception e) {
            listener.onMpesaError("Payload Error: " + e.getMessage());
        }
    }
}