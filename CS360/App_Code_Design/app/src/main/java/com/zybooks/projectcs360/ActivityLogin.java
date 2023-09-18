package com.zybooks.projectcs360;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityLogin extends AppCompatActivity
{
    //------------- Class Fields ----------------//
    private DatabaseHelper m_Database;

    //------------- Override Functions ----------------//
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        m_Database = new DatabaseHelper(this);
        setContentView(R.layout.activity_login);
    }

    //------------- Event Handling Functions ----------------//

    // This function handles authentication of users when logging in.
    // The login button in activity_login.xml triggers this function.
    public void onLoginClick(View v)
    {
        EditText usernameView = findViewById(R.id.login_username_input);
        String username = usernameView.getText().toString();

        EditText passwordView = findViewById(R.id.login_password_input);
        String password = passwordView.getText().toString();

        if (username.isEmpty() || password.isEmpty())
        {
            Toast.makeText(ActivityLogin.this,
                    getResources().getString(R.string.empty_input),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (m_Database.authenticateUser(username, password))
        {
            Toast.makeText(ActivityLogin.this,
                    getResources().getString(R.string.login_successful_toast),
                    Toast.LENGTH_SHORT).show();

            long userID = m_Database.getUserId(username);
            Intent loginIntent = new Intent(ActivityLogin.this, MainActivity.class);
            loginIntent.putExtra("userID", userID);
            startActivity(loginIntent);
        }
        else
        {
            Toast.makeText(ActivityLogin.this,
                    getResources().getString(R.string.login_invalid_input_toast),
                    Toast.LENGTH_SHORT).show();
        }

    }

    // This function handles authentication of users when attempting
    // to register a new account. The register button in activity_login.xml
    // triggers this function.
    public void onRegisterClick(View v)
    {
        EditText usernameView = findViewById(R.id.login_username_input);
        String username = usernameView.getText().toString();

        EditText passwordView = findViewById(R.id.login_password_input);
        String password = passwordView.getText().toString();

        if (username.isEmpty() || password.isEmpty())
        {
            Toast.makeText(ActivityLogin.this,
                    getResources().getString(R.string.empty_input),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!m_Database.doesUsernameExist(username))
        {
            Toast.makeText(ActivityLogin.this,
                    getResources().getString(R.string.register_successful_toast),
                    Toast.LENGTH_SHORT).show();
            m_Database.addUser(username, password);

            long userID = m_Database.getUserId(username);
            Intent loginIntent = new Intent(ActivityLogin.this, MainActivity.class);
            loginIntent.putExtra("userID", userID);
            startActivity(loginIntent);
        }
        else
        {
            Toast.makeText(ActivityLogin.this,
                    getResources().getString(R.string.register_username_collision_toast),
                    Toast.LENGTH_SHORT).show();
        }
    }
}