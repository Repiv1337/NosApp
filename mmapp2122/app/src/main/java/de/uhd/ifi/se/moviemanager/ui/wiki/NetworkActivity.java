package de.uhd.ifi.se.moviemanager.ui.wiki;

import static android.net.NetworkCapabilities.NET_CAPABILITY_NOT_METERED;
import static de.uhd.ifi.se.moviemanager.ui.wiki.WikiQueryService.RESULT_ID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages network availability and sending requests
 *
 * @param <R> type of the request data, e.g. String.
 */
public abstract class NetworkActivity<R> extends AppCompatActivity {

    protected ConnectivityManager connectivityManager;
    protected AtomicInteger requestId = new AtomicInteger(0);
    protected BroadcastReceiver serviceCallback = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int id = bundle.getInt(RESULT_ID);
                if (id == requestId.get()) {
                    latestServiceResponse(bundle);
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
    }

    protected void sendRequestIfInternet(R requestData) {
        if (isNetworkAvailable(connectivityManager)) {
            sendRequestIfWifi(requestData);
        } else {
            showFragment(NetworkInfoFragment.noInternetFragment(this));
        }
    }

    private boolean isNetworkAvailable(
            ConnectivityManager connectivityManager) {
        NetworkCapabilities capabilities = connectivityManager
                .getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return capabilities != null && (capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities
                .hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI) || capabilities
                .hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    protected void showFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(getFragmentContainerId(), fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    protected abstract int getFragmentContainerId();

    private void sendRequestIfWifi(R requestData) {
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities capabilities = connectivityManager
                .getNetworkCapabilities(network);
        if (capabilities.hasCapability(NET_CAPABILITY_NOT_METERED)) {
            showFragment(NetworkInfoFragment.loadingFragment(this));
            sendRequestToService(requestId.incrementAndGet(), requestData);
        } else {
            showFragment(NetworkInfoFragment.noWifiFragment(this));
        }
    }

    protected abstract void sendRequestToService(int requestId, R requestData);

    protected abstract void latestServiceResponse(Bundle extras);

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(serviceCallback,
                new IntentFilter(callbackIdentifier()));
    }

    protected abstract String callbackIdentifier();

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(serviceCallback);
    }
}
