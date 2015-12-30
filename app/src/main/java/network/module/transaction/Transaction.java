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

import java.io.IOException;

import org.json.JSONObject;

import android.content.Context;

import network.module.data.TestAData;
import network.module.data.TestBData;

public abstract class Transaction extends Observable {
	private final int mServiceId;

	protected Context mContext;
	protected String mId;
	protected TransactionState mTransactionState;
	
	public static final int TEST_A_TRANSACTION = 0;
	public static final int TEST_B_TRANSACTION = 1;

	public Transaction(Context context, int serviceId) {
		mContext = context;
		mTransactionState = new TransactionState();
		mServiceId = serviceId;
	}

	@Override
	public TransactionState getState() {
		return mTransactionState;
	}

	public abstract void process();

	public boolean isEquivalent(Transaction transaction) {
		return mId.equals(transaction.mId);
	}

	public int getServiceId() {
		return mServiceId;
	}

	protected JSONObject TestA(TestAData testAData) throws IOException {
		return HttpUtils.httpConnectionForJSONObject(TransactionSettings.TEST_A_URL, testAData, HttpUtils.HTTP_POST_METHOD);
	}

	protected JSONObject TestB(TestBData testBData) throws IOException {
		return HttpUtils.httpConnectionForJSONObject(TransactionSettings.TEST_B_URL, testBData, HttpUtils.HTTP_POST_METHOD);
	}

	abstract public int getType();
}
