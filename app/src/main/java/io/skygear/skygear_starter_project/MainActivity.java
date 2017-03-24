package io.skygear.skygear_starter_project;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import io.skygear.skygear.AuthResponseHandler;
import io.skygear.skygear.Configuration;
import io.skygear.skygear.Container;
import io.skygear.skygear.Database;
import io.skygear.skygear.LogoutResponseHandler;
import io.skygear.skygear.Pubsub;
import io.skygear.skygear.Record;
import io.skygear.skygear.RecordSaveResponseHandler;
import io.skygear.skygear.User;

public class MainActivity extends AppCompatActivity {

    private TextView endpointTextView;
    private TextView apiKeyTextView;
    private TextView accessTokenTextView;
    private TextView userIdTextView;

    private EditText emailEditText;
    private EditText passwordEditText;

    private Button signupButton;
    private Button loginButton;
    private Button logoutButton;
    private Button sendDataButton;
    private EditText dataContentInput;

    private Container skygear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.endpointTextView = (TextView) findViewById(R.id.endpoint_text_view);
        this.apiKeyTextView = (TextView) findViewById(R.id.api_key_text_view);
        this.accessTokenTextView = (TextView) findViewById(R.id.access_token_text_view);
        this.userIdTextView = (TextView) findViewById(R.id.user_id_text_view);

        this.emailEditText = (EditText) findViewById(R.id.email_edit_text);
        this.passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        this.signupButton = (Button) findViewById(R.id.signup_button);
        this.loginButton = (Button) findViewById(R.id.login_button);
        this.logoutButton = (Button) findViewById(R.id.logout_button);
        this.sendDataButton = (Button) findViewById(R.id.send_data);
        this.dataContentInput = (EditText) findViewById(R.id.data_content_input);

        this.skygear = Container.defaultContainer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.updateViewState();
    }

    private void updateViewState() {
        Configuration config = this.skygear.getConfig();
        User currentUser = this.skygear.getCurrentUser();

        this.endpointTextView.setText(String.format("Endpoint: %s", config.getEndpoint()));
        this.apiKeyTextView.setText(String.format("API Key: %s", config.getApiKey()));

        String accessToken = "Undefined";
        String userId = "Undefined";
        if (currentUser != null) {
            accessToken = currentUser.getAccessToken();
            userId = currentUser.getId();
            subscribeToPing();
        } else {

        }

        this.accessTokenTextView.setText(String.format("Access Token: %s", accessToken));
        this.userIdTextView.setText(String.format("User ID: %s", userId));

        this.signupButton.setEnabled(currentUser == null);
        this.loginButton.setEnabled(currentUser == null);
        this.logoutButton.setEnabled(currentUser != null);
    }

    public void sendDataOnClickHandler(View view) {
        String content = this.dataContentInput.getText().toString();
        sendDataToServer(content);
    }

    public void signupOnClickHandler(View view) {
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Signing up...");

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Sign up success")
                .setMessage("")
                .setNegativeButton("Dismiss", null)
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Sign up failed")
                .setMessage("")
                .setNegativeButton("Dismiss", null)
                .create();

        loading.show();
        this.skygear.signupWithEmail(
                this.emailEditText.getText().toString(),
                this.passwordEditText.getText().toString(),
                new AuthResponseHandler() {
                    @Override
                    public void onAuthSuccess(User user) {
                        successDialog.setMessage(String.format(
                                "Sign up with user ID:\n%s",
                                user.getId()
                        ));

                        loading.dismiss();
                        successDialog.show();

                        MainActivity.this.updateViewState();
                    }

                    @Override
                    public void onAuthFail(String reason) {
                        failDialog.setMessage(String.format("Reason:\n%s", reason));

                        loading.dismiss();
                        failDialog.show();
                    }
                }
        );
    }

    public void loginOnClickHandler(View view) {
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Logging In...");

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Log in success")
                .setMessage("")
                .setNegativeButton("Dismiss", null)
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Log in failed")
                .setMessage("")
                .setNegativeButton("Dismiss", null)
                .create();

        loading.show();
        this.skygear.loginWithEmail(
                this.emailEditText.getText().toString(),
                this.passwordEditText.getText().toString(),
                new AuthResponseHandler() {
                    @Override
                    public void onAuthSuccess(User user) {
                        successDialog.setMessage(String.format(
                                "Log in with user ID:\n%s",
                                user.getId()
                        ));

                        loading.dismiss();
                        successDialog.show();

                        MainActivity.this.updateViewState();
                    }

                    @Override
                    public void onAuthFail(String reason) {
                        failDialog.setMessage(String.format("Reason:\n%s", reason));

                        loading.dismiss();
                        failDialog.show();
                    }
                }
        );
    }

    public void logoutOnClickHandler(View view) {
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Logging out...");

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Logout success")
                .setMessage("You have logged out.")
                .setNegativeButton("Dismiss", null)
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Logout failed")
                .setMessage("")
                .setNegativeButton("Dismiss", null)
                .create();

        loading.show();
        this.skygear.logout(new LogoutResponseHandler() {
            @Override
            public void onLogoutSuccess() {
                loading.dismiss();
                successDialog.show();

                MainActivity.this.updateViewState();
            }

            @Override
            public void onLogoutFail(String reason) {
                failDialog.setMessage(String.format("Reason:\n%s", reason));

                loading.dismiss();
                failDialog.show();
            }
        });
    }

    public void registerReport() {
        Pubsub pubsub = Container.defaultContainer(this).getPubsub();
        pubsub.subscribe("update-channel", new Pubsub.Handler(){
            @Override
            public void handle(JSONObject data) {
                String msg = null;
                try {
                    msg = data.getString("msg");
                    Log.i("Pubsub", "Receive Update message:"+ msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void subscribeToPing() {
        Pubsub pubsub = Container.defaultContainer(this).getPubsub();
        pubsub.subscribe("ping", new Pubsub.Handler(){
            @Override
            public void handle(JSONObject data) {
                String msg = null;
                try {
                    msg = data.getString("msg");
                    Log.i("Pubsub", "Receive ping:"+ msg);
                    replyToPing();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void replyToPing() throws JSONException {
        Pubsub pubsub = Container.defaultContainer(this).getPubsub();
        JSONObject data = new JSONObject();
        data.put("device", skygear.getCurrentUser().getId());
        data.put("platform", "android");
        data.put("lastReply", DateFormat.getDateTimeInstance().format(new Date()));
        pubsub.publish("reply", data);
    }

    public void sendDataToServer(String content) {
        // Send record, we will have an afterSave cloud function to send event to the channel.
        // Alternatively, we can call publishEvent on client side.
        // To pick which of these options depends on the usecase.


        Database publicDatabase = skygear.getPublicDatabase();

        Record aReport = new Record("Report");
        aReport.set("content", content);

        RecordSaveResponseHandler handler = new RecordSaveResponseHandler(){
            @Override
            public void onSaveSuccess(Record[] records) {
                Log.i(
                        "Skygear Record Save",
                        "Successfully saved " + records.length + " records"
                );
            }

            @Override
            public void onPartiallySaveSuccess(
                    Map<String, Record> successRecords,
                    Map<String, String> reasons
            ) {
                Log.i(
                        "Skygear Record Save",
                        "Successfully saved " + successRecords.size() + " records"
                );
                Log.i(
                        "Skygear Record Save",
                        reasons.size() + " records are fail to save"
                );
            }

            @Override
            public void onSaveFail(String reason) {
                Log.i(
                        "Skygear Record Save",
                        "Fail to save: " + reason
                );
            }
        };

        publicDatabase.save(aReport, handler);
    }

    public void publishEvent(){
        try {
            Pubsub pubsub = Container.defaultContainer(this).getPubsub();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "Hello World");
            pubsub.publish("update-channel", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
