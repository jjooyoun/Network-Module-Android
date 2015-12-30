/*
 * Copyright (C) 2007-2008 Esmertec AG.
 * Copyright (C) 2007-2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package network.module.transaction;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

import network.module.data.TestAData;
import network.module.data.TestBData;
import network.module.data.TestData;
import network.module.data.TestDataHeader;

public class HttpUtils {
	private final static String TAG = "HttpUtils";

	public static final String HTTP_POST_METHOD = "POST";
	public static final String HTTP_GET_METHOD = "GET";
	private static final int TIME_OUT = 5 * 1000;

	private HttpUtils() {
	}

	public static JSONObject httpConnectionForJSONObject(String address, TestData testData, String httpMethod) throws IOException {
		if (address == null) {
			throw new IllegalArgumentException("address must not be null.");
		}
		try {
			URL url = new URL(address);
			Log.i(TAG, "address = " + url.toString());
			Log.i(TAG, "httpMethod = " + httpMethod);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIME_OUT);
			conn.setReadTimeout(TIME_OUT);
			conn.setRequestMethod(httpMethod);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);

			OutputStream outputStream = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write(getEncodedQuery(testData));
			writer.flush();
			writer.close();
			outputStream.flush();
			outputStream.close();

			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = conn.getInputStream();
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				byte[] byteBuffer = new byte[1024];
				byte[] byteData = null;
				int nLength = 0;
				while ((nLength = inputStream.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					byteArrayOutputStream.write(byteBuffer, 0, nLength);
				}
				byteData = byteArrayOutputStream.toByteArray();
				byteArrayOutputStream.close();

				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(new String(byteData));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return jsonObject;
			} else {
				Log.e(TAG, "responseCode = " + responseCode);
			}
		} catch (IllegalStateException e) {
			handleHttpConnectionException(e, address);
		} catch (IllegalArgumentException e) {
			handleHttpConnectionException(e, address);
		} catch (SocketException e) {
			handleHttpConnectionException(e, address);
		} catch (Exception e) {
			handleHttpConnectionException(e, address);
		}
		return null;
	}

	public static String httpConnectionForString(String address, TestData testData, String httpMethod) throws IOException {
		if (address == null) {
			throw new IllegalArgumentException("address must not be null.");
		}
		try {
			URL url = new URL(address);
			Log.i(TAG, "address = " + url.toString());
			Log.i(TAG, "httpMethod = " + httpMethod);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIME_OUT);
			conn.setReadTimeout(TIME_OUT);
			conn.setRequestMethod(httpMethod);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);

			OutputStream outputStream = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write(getEncodedQuery(testData));
			writer.flush();
			writer.close();
			outputStream.close();

			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = conn.getInputStream();
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				byte[] byteBuffer = new byte[1024];
				byte[] byteData = null;
				int nLength = 0;
				while ((nLength = inputStream.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					byteArrayOutputStream.write(byteBuffer, 0, nLength);
				}
				byteData = byteArrayOutputStream.toByteArray();
				byteArrayOutputStream.close();

				return new String(byteData);
			} else {
				Log.e(TAG, "responseCode = " + responseCode);
			}
		} catch (IllegalStateException e) {
			handleHttpConnectionException(e, address);
		} catch (IllegalArgumentException e) {
			handleHttpConnectionException(e, address);
		} catch (SocketException e) {
			handleHttpConnectionException(e, address);
		} catch (Exception e) {
			handleHttpConnectionException(e, address);
		}
		return null;
	}

	private static String getEncodedQuery(TestData testData) {
		Uri.Builder builder = null;
		if (testData instanceof TestAData) {
			builder = new Uri.Builder().appendQueryParameter(TestDataHeader.HEADER_TEST_A_EMAIL, ((TestAData) testData).getEmail())
					.appendQueryParameter(TestDataHeader.HEADER_TEST_A_PASSWORD, ((TestAData) testData).getPassword());
		} else if (testData instanceof TestBData) {
		}
		return builder.build().getEncodedQuery();
	}

	private static void handleHttpConnectionException(Exception exception, String url) throws IOException {
		Log.e(TAG, "Url: " + url + "\n" + exception.getMessage());
		IOException e = new IOException(exception.getMessage());
		e.initCause(exception);
		throw e;
	}
}
