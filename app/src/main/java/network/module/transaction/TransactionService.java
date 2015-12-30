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
import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class TransactionService extends Service implements Observer {
	private final static String TAG = "TransactionService";

	public static final String TRANSACTION_COMPLETED_ACTION = "android.intent.action.TRANSACTION_COMPLETED_ACTION";

	public static final String STATE = "state";
	public static final String STATE_URI = "uri";
	public static final String UPDATE_COUNT = "update_count";

	private static final int EVENT_TRANSACTION_REQUEST = 1;
	private static final int EVENT_HANDLE_NEXT_PENDING_TRANSACTION = 2;
	private static final int EVENT_NEW_INTENT = 3;
	private static final int EVENT_QUIT = 100;

	private ServiceHandler mServiceHandler;
	private Looper mServiceLooper;
	private final ArrayList<Transaction> mProcessing = new ArrayList<Transaction>();
	private final ArrayList<Transaction> mPending = new ArrayList<Transaction>();
	private ConnectivityManager mConnMgr;
	private ConnectivityBroadcastReceiver mReceiver;

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			Transaction transaction = null;

			switch (msg.what) {
			case EVENT_NEW_INTENT:
				onNewIntent((Intent) msg.obj, msg.arg1);
				break;

			case EVENT_QUIT:
				getLooper().quit();
				return;

			case EVENT_TRANSACTION_REQUEST:
				int serviceId = msg.arg1;
				try {
					TransactionBundle args = (TransactionBundle) msg.obj;

					int transactionType = args.getTransactionType();

					switch (transactionType) {
					case Transaction.TEST_A_TRANSACTION:
						transaction = new TestATransaction(TransactionService.this, serviceId, args);
						break;
					case Transaction.TEST_B_TRANSACTION:
						transaction = new TestBTransaction(TransactionService.this, serviceId, args);
						break;
					default:
						transaction = null;
						return;
					}

					if (!processTransaction(transaction)) {
						transaction = null;
						return;
					}
				} catch (Exception ex) {
					if (transaction != null) {
						try {
							transaction.detach(TransactionService.this);
							if (mProcessing.contains(transaction)) {
								synchronized (mProcessing) {
									mProcessing.remove(transaction);
								}
							}
						} catch (Throwable t) {
						} finally {
							transaction = null;
						}
					}
				} finally {
					if (transaction == null) {
						stopSelf(serviceId);
					}
				}
				return;
			case EVENT_HANDLE_NEXT_PENDING_TRANSACTION:
				processPendingTransaction(transaction);
				return;
			default:
				return;
			}
		}

		public void processPendingTransaction(Transaction transaction) {
			int numProcessTransaction = 0;
			synchronized (mProcessing) {
				// TODO
				if (mPending.size() != 0) {
					transaction = mPending.remove(0);
				}
				numProcessTransaction = mProcessing.size();
			}

			if (transaction != null) {
				try {
					int serviceId = transaction.getServiceId();

					if (processTransaction(transaction)) {
					} else {
						transaction = null;
						stopSelf(serviceId);
					}
				} catch (IOException e) {
				}
			} else {
				if (numProcessTransaction == 0) {
				}
			}
		}

		private boolean processTransaction(Transaction transaction) throws IOException {
			synchronized (mProcessing) {
				// TODO
				for (Transaction t : mPending) {
					if (t.isEquivalent(transaction)) {
						return true;
					}
				}
				for (Transaction t : mProcessing) {
					if (t.isEquivalent(transaction)) {
						return true;
					}
				}

				if (mProcessing.size() > 0) {
					// TOODO
					mPending.add(transaction);
					return true;
				} else {
					mProcessing.add(transaction);
				}
			}
			transaction.attach(TransactionService.this);
			transaction.process();
			return true;
		}
	}

	private class ConnectivityBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				return;
			}

			boolean network = isNetworkAvailable();

			if (network) {
				mServiceHandler.processPendingTransaction(null);
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		HandlerThread thread = new HandlerThread("TransactionService");
		thread.start();

		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

		mReceiver = new ConnectivityBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, intentFilter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			Message msg = mServiceHandler.obtainMessage(EVENT_NEW_INTENT);
			msg.arg1 = startId;
			msg.obj = intent;
			mServiceHandler.sendMessage(msg);
		}
		return Service.START_NOT_STICKY;
	}

	@Override
	public void update(Observable observable) {
		Transaction transaction = (Transaction) observable;
		int serviceId = transaction.getServiceId();

		try {
			synchronized (mProcessing) {
				mProcessing.remove(transaction);
				if (mPending.size() > 0) {
					// TODO
					Message msg = mServiceHandler.obtainMessage(EVENT_HANDLE_NEXT_PENDING_TRANSACTION);
					mServiceHandler.sendMessage(msg);
				} else if (mProcessing.isEmpty()) {
				} else {
				}
			}

			Intent intent = new Intent(TRANSACTION_COMPLETED_ACTION);
			TransactionState state = transaction.getState();
			int result = state.getState();
			intent.putExtra(STATE, result);
			intent.putExtra(STATE_URI, state.getContentUri());

			switch (result) {
			case TransactionState.SUCCESS:
				switch (transaction.getType()) {
				case Transaction.TEST_A_TRANSACTION:
					break;
				case Transaction.TEST_B_TRANSACTION:
					break;
				}
				break;
			case TransactionState.FAILED:
				switch (transaction.getType()) {
				case Transaction.TEST_A_TRANSACTION:
					break;
				case Transaction.TEST_B_TRANSACTION:
					break;
				}
				break;
			default:
				break;
			}
			sendBroadcast(intent);
		} finally {
			transaction.detach(this);
			stopSelfIfIdle(serviceId);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mServiceHandler.sendEmptyMessage(EVENT_QUIT);
	}

	private void onNewIntent(Intent intent, int serviceId) {
		mConnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean noNetwork = !isNetworkAvailable();

		TransactionBundle args = new TransactionBundle(intent.getExtras());
		launchTransaction(serviceId, args, noNetwork);
	}

	private boolean isNetworkAvailable() {
		boolean bConnect = false;
		if (mConnMgr == null) {
			return bConnect;
		} else {
			NetworkInfo info = mConnMgr.getActiveNetworkInfo();
			if (info == null) {
				return bConnect;
			}

			if (info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() == ConnectivityManager.TYPE_MOBILE
					|| info.getType() == ConnectivityManager.TYPE_WIMAX) {
				bConnect = (info.isAvailable() && info.isConnected());
			}
			return bConnect;
		}
	}

	private void launchTransaction(int serviceId, TransactionBundle txnBundle, boolean noNetwork) {
		if (noNetwork) {
			Log.e(TAG, "launchTransaction: no network error!");
			onNetworkUnavailable(serviceId, txnBundle.getTransactionType());
			return;
		}
		Message msg = mServiceHandler.obtainMessage(EVENT_TRANSACTION_REQUEST);
		msg.arg1 = serviceId;
		msg.obj = txnBundle;

		mServiceHandler.sendMessage(msg);
	}

	private void onNetworkUnavailable(int serviceId, int transactionType) {
		stopSelf(serviceId);
	}

	private void stopSelfIfIdle(int startId) {
		synchronized (mProcessing) {
			// TODO
			if (mProcessing.isEmpty() && mPending.isEmpty()) {
				stopSelf(startId);
			}
		}
	}
}
