package com.example.runit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeoutException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class Activitylogon extends AppCompatActivity {


    public void Activitylogon()  {
    }


    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon);

        Button confirmlogonbut = (Button) findViewById(R.id.logonaccbutton);
        EditText accountinput = (EditText) findViewById(R.id.runitaccountidText);
        EditText accountpword = (EditText) findViewById(R.id.EditTextlogonTextPassword);

        spinner=(ProgressBar)findViewById(R.id.progressBarlogon);
        spinner.setVisibility(View.GONE);






        confirmlogonbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Account inputted ok, not null

                if (accountinput.getText().equals(null) || (accountinput.getText().equals(" ")))
                {
                    Toast.makeText(getApplicationContext(), "Run.it logon ID is in format x.x.xxxxxxx", Toast.LENGTH_LONG).show();
                    return;
                }

                if (accountpword.getText().equals(null) || (accountpword.getText().equals(" ")))
                {
                    Toast.makeText(getApplicationContext(), "No password entered", Toast.LENGTH_LONG).show();
                    return;
                }



                HederaServices.createoperatorClient();
                // user pays for the password retrieval and balance check of their or ANY account
                // but platform has to pay for Hedera File read.

                ByteString encrypted;

                try {

                    encrypted = HederaServices.gethederafile(accountinput.getText().toString());

                } catch (TimeoutException | PrecheckStatusException hederaStatusException) {
                    System.out.println("Hedera msg : " + hederaStatusException);
                    Toast.makeText(getApplicationContext(), "Sorry this is not a valid Run.it logon ID", Toast.LENGTH_LONG).show();
                    return;
                }


                DecodeFileid decodedfile = null;

                try {
                    decodedfile = new DecodeFileid(encrypted, accountpword.getText().toString());

                    // check to see if account in the hedera file id matches input AND the password matches

                    if (!decodedfile.matchedok) {
                        Toast.makeText(getApplicationContext(), "Your Password on your Hedera File on the Ledger, does NOT match the Password you entered, Please re-enter the correct Password.", Toast.LENGTH_LONG).show();
                        return;
                    }

                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(getApplicationContext(), "Incorrect Password, please re-enter", Toast.LENGTH_LONG).show();
                    accountpword.getText().clear();
                    return;
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(getApplicationContext(), "Incorrect Password, please re-enter", Toast.LENGTH_LONG).show();
                    accountpword.getText().clear();
                    return;
                } catch (NoSuchPaddingException e) {
                    Toast.makeText(getApplicationContext(), "Incorrect Password, please re-enter", Toast.LENGTH_LONG).show();
                    accountpword.getText().clear();
                    return;
                } catch (InvalidKeyException e) {
                    Toast.makeText(getApplicationContext(), "Incorrect Password, please re-enter", Toast.LENGTH_LONG).show();
                    accountpword.getText().clear();
                    return;
                } catch (BadPaddingException e) {
                    Toast.makeText(getApplicationContext(), "Incorrect Password, please re-enter", Toast.LENGTH_LONG).show();
                    accountpword.getText().clear();
                    return;
                } catch (IllegalBlockSizeException e) {
                    Toast.makeText(getApplicationContext(), "Incorrect Password, please re-enter", Toast.LENGTH_LONG).show();
                    accountpword.getText().clear();
                    return;
                } catch (InvalidKeySpecException e) {
                    Toast.makeText(getApplicationContext(), "Incorrect Password, please re-enter", Toast.LENGTH_LONG).show();
                    accountpword.getText().clear();
                    return;
                }

                spinner.setVisibility(View.VISIBLE);


                // do Hedera processing - create the user Client now we have the account retrieved

                HederaServices.createuserClient(AccountId.fromString(decodedfile.usraccnt), PrivateKey.fromString(decodedfile.usrpkey));

                spinner.setVisibility(View.GONE);

                //.. call method to open dashboard

                openActivitydashboard();
            }

        });







    }


    public void openActivitydashboard () {
      //  Intent intent = new Intent(this, com.example.runit.Activitydashboard.class);
      //  startActivity(intent);
    }

}