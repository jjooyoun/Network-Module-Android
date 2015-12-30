package network.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import network.module.data.TestDataHeader;
import network.module.transaction.Transaction;
import network.module.transaction.TransactionBundle;
import network.module.transaction.TransactionService;
import network.module.transaction.TransactionState;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

            String action = intent.getAction();

            if (action != null && action.equals(TransactionService.TRANSACTION_COMPLETED_ACTION)) {
                Log.i(TAG, "android.intent.action.TRANSACTION_COMPLETED_ACTION");
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    int state = bundle.getInt(TransactionService.STATE, TransactionState.INITIALIZED);
                    if (state == TransactionState.SUCCESS) {
                        Log.i(TAG, "TransactionState.SUCCESS");
                        String contentUri = bundle.getString(TransactionService.STATE_URI, null);
                        if (contentUri != null) {
                        }
                    } else if (state == TransactionState.FAILED) {
                        Log.i(TAG, "TransactionState.FAILED");
                        String contentUri = bundle.getString(TransactionService.STATE_URI, null);
                        if (contentUri != null) {
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startTestATransactionService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReciver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    private void startTestATransactionService() {
        // TODO
        Intent intent = new Intent(MainActivity.this, TransactionService.class);
        intent.putExtra(TransactionBundle.TRANSACTION_TYPE, Transaction.TEST_A_TRANSACTION);
        intent.putExtra(TestDataHeader.HEADER_TEST_A_EMAIL, "");
        intent.putExtra(TestDataHeader.HEADER_TEST_A_PASSWORD, "");
        startService(intent);
    }

    private void registerReciver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TransactionService.TRANSACTION_COMPLETED_ACTION);
        registerReceiver(mReceiver, filter);
    }
}
