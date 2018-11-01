package org.smartregister.anc.task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.LoginContract;
import org.smartregister.anc.helper.SyncSettingsServiceHelper;
import org.smartregister.anc.util.Constants;
import org.smartregister.domain.LoginResponse;
import org.smartregister.event.Listener;

/**
 * Created by ndegwamartin on 22/06/2018.
 */
public class RemoteLoginTask extends AsyncTask<Void, Integer, LoginResponse> {

    private final String TAG = RemoteLoginTask.class.getCanonicalName();

    private LoginContract.View mLoginView;
    private final String mUsername;
    private final String mPassword;

    private final Listener<LoginResponse> afterLoginCheck;

    public RemoteLoginTask(LoginContract.View loginView, String username, String password, Listener<LoginResponse> afterLoginCheck) {
        mLoginView = loginView;
        mUsername = username;
        mPassword = password;
        this.afterLoginCheck = afterLoginCheck;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mLoginView.showProgress(true);
    }

    @Override
    protected LoginResponse doInBackground(Void... params) {
        LoginResponse loginResponse = getOpenSRPContext().userService().isValidRemoteLogin(mUsername, mPassword);

        if (loginResponse != null && loginResponse.equals(LoginResponse.SUCCESS) && AncApplication.getInstance().getPassword() == null) {


            publishProgress(R.string.loading_client_settings);

            SyncSettingsServiceHelper syncSettingsServiceHelper = new SyncSettingsServiceHelper(getOpenSRPContext().applicationContext(), getOpenSRPContext().configuration().dristhiBaseURL(), getOpenSRPContext().getHttpAgent());
            syncSettingsServiceHelper.setUsername(mUsername);
            syncSettingsServiceHelper.setPassword(mPassword);

           String teamId =  mLoginView.getUserTeamId(loginResponse);
            syncSettingsServiceHelper.setTeamId(teamId);

            try {
                JSONArray settings = syncSettingsServiceHelper.pullSettingsFromServer();

                JSONObject data = new JSONObject();
                data.put(Constants.PREF_KEY.SITE_CHARACTERISTICS, settings);
                loginResponse.setRawData(data);

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }

        }

        return loginResponse;
    }

    @Override
    protected void onProgressUpdate(Integer... messageIdentifier) {

        mLoginView.updateProgressMessage(getOpenSRPContext().applicationContext().getString(messageIdentifier[0]));

    }

    @Override
    protected void onPostExecute(final LoginResponse loginResponse) {
        super.onPostExecute(loginResponse);

        mLoginView.showProgress(false);
        afterLoginCheck.onEvent(loginResponse);
    }

    @Override
    protected void onCancelled() {
        mLoginView.showProgress(false);
    }


    public static Context getOpenSRPContext() {
        return AncApplication.getInstance().getContext();
    }

}

