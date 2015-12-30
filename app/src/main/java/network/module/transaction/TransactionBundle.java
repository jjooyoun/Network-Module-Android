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

import android.os.Bundle;

public class TransactionBundle {
	public static final String CONTENT_URI = "uri";
	public static final String TRANSACTION_TYPE = "type";

	private final Bundle mBundle;

	public TransactionBundle(Bundle bundle) {
		mBundle = bundle;
	}

	public Bundle getBundle() {
		return mBundle;
	}

	public int getTransactionType() {
		return mBundle.getInt(TRANSACTION_TYPE);
	}

	public String getContentUri() {
		return mBundle.getString(CONTENT_URI);
	}
}
