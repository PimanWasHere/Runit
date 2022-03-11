package com.runnerup.runit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.FileId;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.ReceiptStatusException;

import java.math.BigInteger;
import java.util.concurrent.TimeoutException;


public class Activityupdateprofile extends AppCompatActivity {

    // Soul can be ..  so rolecode permitted values P/F/S/C/B/R/D   R=sponsor - for duplicate and ease of install.


    BigInteger multiplier108 = new BigInteger("100000000");

    BigInteger multiplier1018 = new BigInteger("1000000000000000000");

    String rolecode, nicknameglobal, fnameglobal, lnameglobal;
    ProgressBar spinupdate;

    EditText nicknameinputprof, fnameinputprof, lnameinputprof;

    TextView pkey, accountid;

    Switch indidivual, team, organisation, showkeyswitch;

    private GennedAccount newDetails;
    private AccountId newAccount;
    private FileId newhederaFileid;

    private String rolearray[] = null;


    public Activityupdateprofile() {
    }

    Runitprofile runitprofilesource;

    Runitprofile runitprofilecurrent;

    Runitprofile runitprofileupdated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);

        spinupdate = (ProgressBar) findViewById(R.id.progressBarupdate);

        Intent intent = getIntent();
        runitprofilesource = (Runitprofile) intent.getSerializableExtra("profileobjtupdateprof");

        System.out.println("runit profile3 obj " + runitprofilesource.runitprofilescid);
        System.out.println("nickname " + runitprofilesource.nickname);


        Button updateprofilebut = (Button) findViewById(R.id.updateprofbutton);
        Button sendataprefbut = (Button) findViewById(R.id.updatedataprefbutt);


        nicknameinputprof = (EditText) findViewById(R.id.editTextnicknameupdate);
        fnameinputprof = (EditText) findViewById(R.id.editTextfnameedit);
        lnameinputprof = (EditText) findViewById(R.id.editTextlnameedit);

        //  EditText nationality = (EditText) findViewById(R.id.nationality);

        indidivual = (Switch) findViewById(R.id.switch1edit);
        team = (Switch) findViewById(R.id.switch2edit);
        organisation = (Switch) findViewById(R.id.switch3edit);

        showkeyswitch = (Switch) findViewById(R.id.switchshow);

        pkey = (TextView) findViewById(R.id.TexViewkey);
        String pkeyout = HederaServices.getkey().toString();
        accountid = (TextView) findViewById(R.id.textViewAccount);
        String accountout = HederaServices.getAccount().toString();

        showkeyswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position

                if (isChecked) {
                    pkey.setText(pkeyout);
                    accountid.setText(accountout);
                } else {
                    pkey.setText("");
                    accountid.setText("");
                }
            }
        });


        sendataprefbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openActivitydatapreferencesupdate();

            }
        });


        updateprofilebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (nicknameinputprof.getText().equals(null)) {
                    Toast.makeText(getApplicationContext(), "Nickname cannot be blank", Toast.LENGTH_LONG).show();
                    return;
                }

                if (fnameinputprof.getText().equals(null)) {
                    Toast.makeText(getApplicationContext(), "First name cannot be blank", Toast.LENGTH_LONG).show();
                    return;
                }

                if (lnameinputprof.getText().equals(null)) {
                    Toast.makeText(getApplicationContext(), "Last name cannot be blank", Toast.LENGTH_LONG).show();
                    return;
                }


                rolecode = "";


                // System.out.println("particpant " +  participant.isChecked());

                if (!indidivual.isChecked() && !team.isChecked() && !organisation.isChecked()) {
                    Toast.makeText(getApplicationContext(), "You must have at least one role or many roles at any time", Toast.LENGTH_LONG).show();
                    return;
                }


                // build role string


                if (indidivual.isChecked()) {

                    rolecode = rolecode + "I/";
                }

                if (team.isChecked()) {

                    rolecode = rolecode + "T/";
                }

                if (organisation.isChecked()) {

                    rolecode = rolecode + "O/";
                }


                System.out.println("new role code string" + rolecode);

                // set global to local

                nicknameglobal = nicknameinputprof.getText().toString();
                fnameglobal = fnameinputprof.getText().toString();
                lnameglobal = lnameinputprof.getText().toString();

                // bump to background thread and update the profile SC

                Toast.makeText(getApplicationContext(), "Thankyou for your patience.. updating your profile  now ..", Toast.LENGTH_LONG).show();

                spinupdate.setVisibility(View.VISIBLE);

                // need to lock the UI as we bump to bkgrnd

                // bump the below to new thread

                Activityupdateprofile.UpdateaccThread threadupdate = new Activityupdateprofile.UpdateaccThread();
                threadupdate.start();


                //  we  stop the spinner from the backgrnd thread spincreate.setVisibility(View.GONE);
            }


        });


        spinupdate.setVisibility(View.VISIBLE);

        // need to lock the UI as we bump to bkgrnd

        // bump the below to new thread

        Activityupdateprofile.GetLatestaccThread threadget = new Activityupdateprofile.GetLatestaccThread();
        threadget.start();



    }



    class GetLatestaccThread extends Thread {

        GetLatestaccThread() {
        }

        @Override
        public void run() {
            // we have to re-read the profile from ledger as consensus is consensus - we MUST
            // get latest profile form ledger.. cannot use old object that dashboard passes!


            try {
                runitprofilecurrent = HederaServices.getacontract(runitprofilesource.runitprofilescid);
                System.out.println("sc id from current " + runitprofilesource.runitprofilescid) ;

            } catch (TimeoutException e) {
                showToast( "Failed to get profile ! ");
                return;
            } catch (PrecheckStatusException e) {
                showToast( "Failed to get profile ! ");
                return;
            } catch (ReceiptStatusException e) {
                showToast( "Failed to get profile ! ");
                return;
            }

            fnameglobal = runitprofilecurrent.fname;
            lnameglobal = runitprofilecurrent.lname;
            nicknameglobal = runitprofilecurrent.nickname;

            // parse the rolecode and se the switches


            rolearray = (runitprofilecurrent.rolecode).split("/");



            // stop spinner
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    spinupdate.setVisibility(View.GONE);

                    nicknameinputprof.setText(nicknameglobal);
                    fnameinputprof.setText(fnameglobal);
                    lnameinputprof.setText(lnameglobal);

                    // has to have at min 1 role


                    for (int i = 0; i < rolearray.length; ++i) {
                        if (rolearray[i].equals("I")) indidivual.setChecked(true);

                        if (rolearray[i].equals("T")) team.setChecked(true);

                        if (rolearray[i].equals("O")) organisation.setChecked(true);

                    }


                }
            });
        }

    }


    class UpdateaccThread extends Thread {

        UpdateaccThread() {
        }

        @Override
        public void run() {


            // call hedera update profile basics

            // create new profile object for updating the SC

            runitprofileupdated= new Runitprofile();

            runitprofileupdated.nickname = nicknameglobal;

            System.out.println("new role code string 1" + rolecode);

            runitprofileupdated.fname = fnameglobal;
            runitprofileupdated.lname = lnameglobal;
            runitprofileupdated.rolecode = rolecode;
            // not needed as it is a subset update of profile in the SC
            /*
            runitprofileupdated.runitrunaccountid = runitprofilecurrent.runitrunaccountid;

            runitprofileupdated.runitprofilescid = runitprofilecurrent.runitprofilescid;
            runitprofileupdated.runitlogonaccountid = runitprofilecurrent.runitlogonaccountid;
            runitprofileupdated.interest1 = runitprofilecurrent.interest1;
            runitprofileupdated.interest2 = runitprofilecurrent.interest2;
            runitprofileupdated.interest3 = runitprofilecurrent.interest3;
            runitprofileupdated.grpsponsorslevel = runitprofilecurrent.grpsponsorslevel;
            runitprofileupdated.sponsorslevel = runitprofilecurrent.sponsorslevel;
            runitprofileupdated.interests = runitprofilecurrent.interests;
            runitprofileupdated.demographic = runitprofilecurrent.demographic;
            runitprofileupdated.behavioral = runitprofilecurrent.behavioral;
            */

            // now update just the name and role selections

             String phonenum = "417 300 4812"; // to be added later.
             String nationality = "Australian"; // ditto

       //    public static void updateprofile(String usersprofilescID , String _fname, String _lname, String _nickname, String _phone, String _nationality, String _rolecode) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {

            try {

                System.out.println(" fname" + fnameglobal);

                HederaServices.updateprofile(runitprofilecurrent.runitprofilescid,fnameglobal,lnameglobal,nicknameglobal,phonenum,nationality,rolecode);
                showToast("Your profile has been successfully updated");

            } catch (TimeoutException e) {
                showToast("Exception updating Profile SC" + e);
            } catch (PrecheckStatusException e) {
                showToast("Exception updating Profile SC" + e);
            } catch (ReceiptStatusException e) {
                showToast("Exception updating Profile SC" + e);

            }


            // stop spinner
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    spinupdate.setVisibility(View.GONE);


                }
            });


        }




    }


    public void showToast(final String toast) {

        // stop spinner
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinupdate.setVisibility(View.GONE);
            }
        });


        runOnUiThread(() -> Toast.makeText(Activityupdateprofile.this, toast, Toast.LENGTH_LONG).show());


    }



    public void openActivitydatapreferencesupdate () {

        Intent intent = new Intent(this, Activitydatapreferenceaccupdate.class);
        intent.putExtra("profileobjtodataprefupdate", runitprofilecurrent);
        //intent.putExtra("profile obj", decodedfile);
        startActivity(intent);
    }


}
