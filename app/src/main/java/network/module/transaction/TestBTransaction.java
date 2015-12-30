package network.module.transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import network.module.data.TestBData;

public class TestBTransaction extends Transaction implements Runnable {
	private static final String TAG = "TestBTransaction";

	private Thread mThread;
	private TestBData mTestBData;

	public TestBTransaction(Context context, int transId, TransactionBundle bundle) {
		super(context, transId);
		mTestBData = new TestBData();
		mId = mTestBData.getUri();
	}

	@Override
	public void process() {
		mThread = new Thread(this, "TestBTransaction");
		mThread.start();
	}

	@Override
	public void run() {
		try {
			JSONObject jObject = TestB(mTestBData);
			Log.i(TAG, "json = " + jObject.toString());
			mTransactionState.setState(TransactionState.SUCCESS);
			mTransactionState.setContentUri(mTestBData.getUri());
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (mTransactionState.getState() != TransactionState.SUCCESS) {
				mTransactionState.setState(TransactionState.FAILED);
			}
			notifyObservers();
		}
	}

	@Override
	public int getType() {
		return TEST_B_TRANSACTION;
	}
}
