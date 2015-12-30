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

public class TransactionState {
	public static final int INITIALIZED = 0;
	public static final int SUCCESS = 1;
	public static final int FAILED = 2;

	private String mContentUri;
	private int mState;
	private int mUpdateCount;

	public TransactionState() {
		mState = INITIALIZED;
		mContentUri = null;
		mUpdateCount = 0;
	}

	public synchronized int getState() {
		return mState;
	}

	public synchronized void setState(int state) {
		if ((state < INITIALIZED) && (state > FAILED)) {
			throw new IllegalArgumentException("Bad state: " + state);
		}
		mState = state;
	}

	public synchronized String getContentUri() {
		return mContentUri;
	}

	public synchronized void setContentUri(String uri) {
		mContentUri = uri;
	}

	public synchronized int getUpdateCount() {
		return mUpdateCount;
	}

	public synchronized void setUpdateCount(int updateCount) {
		mUpdateCount = updateCount;
	}
}
