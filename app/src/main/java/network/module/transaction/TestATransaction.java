package network.module.transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import network.module.data.TestAData;
import network.module.data.TestDataHeader;

public class TestATransaction extends Transaction implements Runnable {
	private static final String TAG = "TestATransaction";

	private Thread mThread;
	private TestAData mTestAData;

	public TestATransaction(Context context, int transId, TransactionBundle bundle) {
		super(context, transId);
		mTestAData = new TestAData();
		mTestAData.setEmail(bundle.getBundle().getString(TestDataHeader.HEADER_TEST_A_EMAIL));
		mTestAData.setPassword(bundle.getBundle().getString(TestDataHeader.HEADER_TEST_A_PASSWORD));
		mId = mTestAData.getUri();
	}

	@Override
	public void process() {
		mThread = new Thread(this, "TestATransaction");
		mThread.start();
	}

	@Override
	public void run() {
		try {
			JSONObject jObject = TestA(mTestAData);
			Log.i(TAG, "json = " + jObject.toString());
			mTransactionState.setState(TransactionState.SUCCESS);
			mTransactionState.setContentUri(mTestAData.getUri());
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
		return TEST_A_TRANSACTION;
	}
}
