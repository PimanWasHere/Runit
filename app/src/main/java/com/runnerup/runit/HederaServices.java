package com.runnerup.runit;


import android.view.View;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.*;
import org.threeten.bp.Duration;
import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeoutException;


public final class HederaServices implements  Serializable{


    private static final AccountId OPERATOR_ID = AccountId.fromString("0.0.33980215");
    private static final PrivateKey OPERATOR_KEY = PrivateKey.fromString("302e020100300506032b6570042204205153a28ace7c099ae36325105960b59fde660fecd1d123f35068f7c08ff88bbd");
/*  "accountId": "0.0.33980215",
  "publicKey": "302a300506032b6570032100a4005d9964605f52260b77dce399689e477127c871f22e415e4ecf1f75d2bf09",
  "privateKey": "302e020100300506032b6570042204205153a28ace7c099ae36325105960b59fde660fecd1d123f35068f7c08ff88bbd"
}*/

    private static Client OPERATING_ACCOUNT = null;
    private static Client USER_ACCOUNT = null;
    private static GennedAccount GENNED_ACCOUNT = null;
    private static PrivateKey USERSPK = null;


    private static final ContractId runtokensc = ContractId.fromString("0.0.34390662");

   // private static final FileId runitprofilefile = FileId.fromString("0.0.2165167");
   // 0.0.2276665
    private static final FileId runitprofilefile = FileId.fromString("0.0.34390688");

    private static final ContractId simpletermssc = ContractId.fromString("0.0.34390679");

    private static final BigInteger multiplier1018 = new BigInteger("1000000000000000000");

    private static final BigInteger multiplier108 = new BigInteger("100000000");



    public static void createoperatorClient() {

        System.out.println(".. connecting to Hedera nodes from Operating Account..");

        /*


        Client hederaClient = Client.fromJson("{\n" +
                " \"network\": {\n" +
                " \"0.0.4\" : \"1.testnet.hedera.com:50211\",\n" +
                " \"0.0.5\" : \"2.testnet.hedera.com:50211\",\n" +
                " \"0.0.6\" : \"3.testnet.hedera.com:50211\"\n" +
                "      }\n" +
                "}");

        Client hederaClient = Client.fromJson("{\n" +
                " \"network\": {\n" +
                " \"0.0.3\" : \"0.previewnet.hedera.com:50211\",\n" +
                " \"0.0.4\" : \"1.previewnet.hedera.com:50211\",\n" +
                " \"0.0.5\" : \"2.previewnet.hedera.com:50211\",\n" +
                " \"0.0.6\" : \"3.previewnet.hedera.com:50211\"\n" +
                "      }\n" +
                "}");
*/
        OPERATING_ACCOUNT = Client.forTestnet();
        //  OPERATING_ACCOUNT = Client.forMainnet();

        OPERATING_ACCOUNT.setOperator(OPERATOR_ID, OPERATOR_KEY);
        OPERATING_ACCOUNT.setMaxQueryPayment(new Hbar(10));
        OPERATING_ACCOUNT.setMaxTransactionFee(new Hbar(100));

        System.out.println("using operating Account.. " + OPERATOR_ID.toString());

    }

    public static void createuserClient(AccountId useraccount, PrivateKey userskey)  {

        System.out.println(".. to pay for SC deploy.. and subsequent calls.., SC display list, Contract create, Payout etc, user account in ! =  " + useraccount);
 /*


        Client hederaClient = Client.fromJson("{\n" +
                " \"network\": {\n" +
                " \"0.0.4\" : \"1.testnet.hedera.com:50211\",\n" +
                " \"0.0.5\" : \"2.testnet.hedera.com:50211\",\n" +
                " \"0.0.6\" : \"3.testnet.hedera.com:50211\"\n" +
                "      }\n" +
                "}");

        Client hederaClient = Client.fromJson("{\n" +
                " \"network\": {\n" +
                " \"0.0.3\" : \"0.previewnet.hedera.com:50211\",\n" +
                " \"0.0.4\" : \"1.previewnet.hedera.com:50211\",\n" +
                " \"0.0.5\" : \"2.previewnet.hedera.com:50211\",\n" +
                " \"0.0.6\" : \"3.previewnet.hedera.com:50211\"\n" +
                "      }\n" +
                "}");
*/
        USER_ACCOUNT = Client.forTestnet();
        // USER_ACCOUNT = Client.forMainnet();

        USER_ACCOUNT.setOperator(useraccount, userskey);
        USER_ACCOUNT.setMaxQueryPayment(new Hbar(5));
        USER_ACCOUNT.setMaxTransactionFee(new Hbar(50));
        USERSPK = userskey;
        System.out.println("Connected to User's Account.. " + useraccount.toString());

    }


    public static GennedAccount createnewkeypair() throws BadMnemonicException {

        GENNED_ACCOUNT = new GennedAccount();

        return GENNED_ACCOUNT;

    }

    public static PrivateKey getkey() { return USERSPK;
    }

    public static AccountId getAccount() { return USER_ACCOUNT.getOperatorAccountId();
    }


    public static AccountId createnewaccount() throws TimeoutException, PrecheckStatusException, ReceiptStatusException {

        TransactionResponse newAccounttx = new AccountCreateTransaction()
                .setKey(GENNED_ACCOUNT.newPublicKey)
                .setInitialBalance(new Hbar(50))
                //.setInitialBalance(100_000_000) // not mandatory for create?
                .execute(OPERATING_ACCOUNT);

        TransactionReceipt receipt = newAccounttx.getReceipt(OPERATING_ACCOUNT);

        AccountId newAccountIdchecksum = receipt.accountId;

        return newAccountIdchecksum;
    }

    /* no longer used as string is passed.
    private static byte[] stringtohash32(String input) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String hashurl = generateStorngPasswordHash(input);
        byte[] byte32output = hashurl.getBytes(StandardCharsets.UTF_8);
        // return 0 if > 32
        if (byte32output.length > 32) byte32output = "0".getBytes(StandardCharsets.UTF_8);
        return byte32output;
    }
*/

    public static void setricardian(String urllink) throws ReceiptStatusException, PrecheckStatusException, TimeoutException, NoSuchAlgorithmException, InvalidKeySpecException {
/* as above no longer used.. going to pass string to contract of the url(i)

        boolean bytes32ok = true;

        byte[] output32 = stringtohash32(urllink);

        if (output32.equals("0".getBytes(StandardCharsets.UTF_8))){ bytes32ok = false; return bytes32ok;}
*/
        String account_urlink =  USER_ACCOUNT.getOperatorAccountId()+urllink;

        TransactionResponse contractExecTransactionResponse = new ContractExecuteTransaction()
                .setContractId(simpletermssc)
                .setGas(4000000)
                .setFunction("accept", new ContractFunctionParameters()
                        .addString(account_urlink))
                .execute(USER_ACCOUNT);

        // if this doesn't throw then we know the contract executed successfully

        contractExecTransactionResponse.getReceipt(USER_ACCOUNT);

       // return bytes32ok;
    };

    public static FileId createuserstore(AccountId newaccountId, String pword, ContractId profileid) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, TimeoutException, PrecheckStatusException, ReceiptStatusException {

        // salt and then hash, then composite then encrypt with operating key then write to Hedera file
        // PBKDF2 in Java for hash - about as secure as it gets.. THEN it is AES/ECB/PKCS5Padding encrypted

        String generatedSecuredPasswordHash = generateStorngPasswordHash(pword);
        System.out.println(generatedSecuredPasswordHash);

        // random salt is pre-pended

        // for testing   String kycin = "0.0.12345/thebprivate999key/" + generatedSecuredPasswordHash;

        String accntandkeyhash = newaccountId.toString() + "/" + GENNED_ACCOUNT.newPrivKey.toString() + "/" + generatedSecuredPasswordHash + "/" + profileid.toString();


        // less than 6K so no append needed

        SecretKeySpec secretKey;
        byte[] key;

        MessageDigest sha = null;

        key = pword.getBytes("UTF-8");
        sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        String encrytpedstring = Base64.getEncoder().encodeToString(cipher.doFinal(accntandkeyhash.getBytes("UTF-8")));

        // now writing encrypted to Hedera file Service

        // User's OR platform pay for FileID create ??? the question reimburse the
        // platform when funds deposited/ present upon contract create

        byte[] encrypt = encrytpedstring.getBytes();

        // for testing to be removed

        // System.out.println("encrypted as bytes to string on hedera : " + encrytpedstring);
        //System.out.println("encrypted as bytes on hedera : " + encrypt);

        TransactionResponse fileTxId2 = new FileCreateTransaction()
                .setKeys(OPERATOR_KEY.getPublicKey())
                .setContents(encrypt)
                .setMaxTransactionFee(new Hbar(3))
                .execute(OPERATING_ACCOUNT);

        TransactionReceipt fileReceipt2 = fileTxId2.getReceipt(OPERATING_ACCOUNT);

        FileId newFileIdchecksum = fileReceipt2.fileId;

        return newFileIdchecksum;
    }


    private static String generateStorngPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }


    public static ByteString gethederafile(String hederafileid) throws TimeoutException, PrecheckStatusException {
        // Defaults the operator account ID and key such that all generated transactions will be paid for
        // by this account and be signed by this key

        FileId existingfileid = FileId.fromString(hederafileid);

        ByteString hederafilecontents = new FileContentsQuery()
                .setFileId(existingfileid)
                .execute(OPERATING_ACCOUNT);

        return hederafilecontents;
    }


    public static Hbar getbalance(String accountEntry) throws TimeoutException, PrecheckStatusException {

        Hbar balance;

        AccountId accounttoquery = AccountId.fromString(accountEntry);

        balance = new AccountBalanceQuery()
                .setAccountId(accounttoquery)
                .execute(OPERATING_ACCOUNT)
                .hbars;

        return balance;
    }


    public static ContractId createdeployedprofile(String _fname, String _lname, String _nickname, String _phone, String _nationality, String _rolecode, String _accountid, BigInteger _runbal, String _hederafileid, String _dataipfshash) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {
        String newcontractid = null;

        // constructor(string _fname, string _lname, string _nickname, string _phone, string _nationality, string _rolecodes, string _profilehederafileid, string _profiledataipfshash, address _platformaddress) public {
        System.out.println("run it profile - accountid sent to hedera services for create ." + _accountid);

        System.out.println("run it profile - fileid sent to hedera services for create ." + _hederafileid);

        System.out.println("role code in " + _rolecode);
        // set admin key to zero address - this is done by Hedera as default.

        TransactionResponse contractcreatetran = new ContractCreateTransaction()
                .setAutoRenewPeriod(Duration.ofDays(90)) //   90 days in seconds, is the autorenew when the creator account will have to pay modest renewfee
                .setGas(4000000) // set by user
                .setBytecodeFileId(runitprofilefile)
                .setConstructorParameters(
                        new ContractFunctionParameters()
                                .addAddress(simpletermssc.toSolidityAddress())
                                .addString(_fname)
                                .addString(_lname)
                                .addString(_nickname)
                                .addString(_phone)
                                .addString(_nationality)
                                .addString(_rolecode)
                                .addAddress(_accountid) // already solidity
                                .addUint256(_runbal)
                                .addString(_hederafileid)
                               // .addString(_dataipfshash)
                                .addAddress(OPERATOR_ID.toSolidityAddress()))
                .setContractMemo("Run.it profile Smart Contract")
                .execute(USER_ACCOUNT);

        TransactionReceipt createreceipt = contractcreatetran.getReceipt(USER_ACCOUNT);

        ContractId provisionalcontractid = Objects.requireNonNull(createreceipt.contractId);

        return provisionalcontractid;

    }



    public static String getutcstringtimestamp(BigInteger timeinmilliseconds) {

        // to show time in UTC Zulu

        long seconds = 1320105600;
        long millis = timeinmilliseconds.longValue() * 1000;

        Date hhutcdate = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(hhutcdate);

        return formattedDate;
    }

    public static String getlocalstringtimestamp(BigInteger timeinmilliseconds) {

        long seconds = 1320105600;
        long millis = timeinmilliseconds.longValue() * 1000;

        Date localdate = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(localdate);

        return formattedDate;
    }




    public static Runitprofile getacontract(String existingcontractid)  throws TimeoutException, PrecheckStatusException, ReceiptStatusException{


        // creating contract POJO

        Runitprofile runitdetails = new Runitprofile();

        // .. get all the profile details - all held private in the SC - only accessible via modifier 'only owner' - see the solidity for details


        ContractFunctionResult result_1 = new ContractCallQuery()
                .setGas(4000000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("getfname")
                .setMaxQueryPayment(Hbar.fromString("1"))
                .execute(USER_ACCOUNT);

        if (result_1.errorMessage != null) {
            System.out.println("Error calling Contract " + result_1.errorMessage);
            return runitdetails;
        }

        System.out.println("got first name " + result_1.getString(0));

        runitdetails.fname = result_1.getString(0);



        ContractFunctionResult result_2 = new ContractCallQuery()
                .setGas(4000000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("getlname")
                .setMaxQueryPayment(Hbar.fromString("1"))
                .execute(USER_ACCOUNT);

        if (result_2.errorMessage != null) {
            System.out.println("Error calling Contract " + result_2.errorMessage);
            return runitdetails;
        }

        runitdetails.lname = result_2.getString(0);

        ContractFunctionResult result_3 = new ContractCallQuery()
                .setGas(4000000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("getnickname")
                .setMaxQueryPayment(Hbar.fromString("1"))
                .execute(USER_ACCOUNT);

        if (result_3.errorMessage != null) {
            System.out.println("Error calling Contract " + result_3.errorMessage);
            return runitdetails;
        }

        runitdetails.nickname = result_3.getString(0);

        ContractFunctionResult result_4 = new ContractCallQuery()
                .setGas(4000000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("getphone")
                .setMaxQueryPayment(Hbar.fromString("1"))
                .execute(USER_ACCOUNT);


        if (result_4.errorMessage != null) {
            System.out.println("Error calling Contract " + result_4.errorMessage);
            return runitdetails;
        }

        runitdetails.phonenum = result_4.getString(0);


        ContractFunctionResult result_5 = new ContractCallQuery()
                .setGas(4000000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("getnationality")
                .setMaxQueryPayment(Hbar.fromString("1"))
                .execute(USER_ACCOUNT);

        if (result_5.errorMessage != null) {
            System.out.println("Error calling Contract " + result_5.errorMessage);
            return runitdetails;
        }

        runitdetails.nationality = result_5.getString(0);



        ContractFunctionResult result_6 = new ContractCallQuery()
                .setGas(4000000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("getrolecode")
                .setMaxQueryPayment(Hbar.fromString("1"))
                .execute(USER_ACCOUNT);

        if (result_6.errorMessage != null) {
            System.out.println("Error calling Contract " + result_6.errorMessage);
            return runitdetails;
        }

        runitdetails.rolecode = result_6.getString(0);
        System.out.println("got role code " + result_6.getString(0));


        ContractFunctionResult result_7 = new ContractCallQuery()
                .setGas(4000000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("getrunaccountid")
                .setMaxQueryPayment(Hbar.fromString("1"))
                .execute(USER_ACCOUNT);

        if (result_7.errorMessage != null) {
            System.out.println("Error calling Contract " + result_7.errorMessage);
            return runitdetails;
        }

        runitdetails.runitrunaccountid = AccountId.fromSolidityAddress(result_7.getAddress(0)).toString();

        System.out.println("got run.it HBAR account id as string" + runitdetails.runitrunaccountid);

        // 8 cut

        ContractFunctionResult result_9 = new ContractCallQuery()
                .setGas(4000000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("gethederafileid")
                .setMaxQueryPayment(Hbar.fromString("1"))
                .execute(USER_ACCOUNT);

        if (result_9.errorMessage != null) {
            System.out.println("Error calling Contract " + result_9.errorMessage);
            return runitdetails;
        }

        System.out.println("got run.it account id (fileid) as string" + result_9.getString(0));


        runitdetails.runitlogonaccountid = result_9.getString(0);

        // also get the hedera info
        // for transparency and pas back in the object

        ContractInfo info = new ContractInfoQuery()
                .setContractId(ContractId.fromString(existingcontractid))
                .execute(USER_ACCOUNT);

        runitdetails.runitprofilescid= info.accountId.toString();
        runitdetails.adminkey = info.adminKey.toString();
        runitdetails.memo = info.contractMemo;
        runitdetails.autorenew = Long.toString(info.autoRenewPeriod.toDays());
        runitdetails.sizeinkbytes = Long.toString(info.storage);
        runitdetails.expiration = Long.toString(info.expirationTime.toEpochMilli());


        return runitdetails;
    }






    public static void updateprofile(String usersprofilescID , String _fname, String _lname, String _nickname, String _phone, String _nationality, String _rolecode) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {

        /* // update profile by Soul ONLY ie OnlyOwner.

  function updateprofile (string _fname, string _lname, string _nickname, string _phone, string _nationality, string _rolecode)  public  onlyOwner{

    fname = _fname;
    lname = _lname;
    nickname = _nickname;
    phone = _phone;
    nationality = _nationality;
    rolecode = _rolecode;

  }
*/

        TransactionResponse contractExecTransactionResponse = new ContractExecuteTransaction()
                .setContractId(ContractId.fromString(usersprofilescID))
                .setGas(4000000)
                .setFunction("updateprofile", new ContractFunctionParameters()
                        .addString(_fname)
                        .addString(_lname)
                        .addString(_nickname)
                        .addString(_phone)
                        .addString(_nationality)
                        .addString(_rolecode))
                .execute(USER_ACCOUNT);


        // if this doesn't throw then we know the contract executed successfully

        contractExecTransactionResponse.getReceipt(USER_ACCOUNT);


    }






    public static TransactionRecord transferhbarfromrunit(Long hbartosendlong, String destaccnt, String memo) throws ReceiptStatusException, PrecheckStatusException, TimeoutException {

        Boolean resultstate = false;

        AccountId recipientaccount = AccountId.fromString(destaccnt);

        if (memo.isEmpty()) memo = " ";

        Hbar amount = new Hbar(hbartosendlong);

        TransactionResponse transactionResponse = new TransferTransaction()
                // .addSender and .addRecipient can be called as many times as you want as long as the total sum from
                // both sides is equivalent
                .addHbarTransfer(OPERATING_ACCOUNT.getOperatorAccountId(), amount.negated())
                .addHbarTransfer(recipientaccount, amount)
                .setTransactionMemo(memo)
                .execute(OPERATING_ACCOUNT);

        TransactionRecord record = transactionResponse.getRecord(OPERATING_ACCOUNT);

        return record;

    }


    public static void runtokensfromplatform(BigInteger runtosendlong, String destaccnt) throws ReceiptStatusException, PrecheckStatusException, TimeoutException {

        // platform pays the gas to send Welcome tokens - IF USER is KYCd

        TransactionResponse contractExecTransactionResponse = new ContractExecuteTransaction()
                .setContractId(runtokensc)
                .setGas(4000000)
                .setFunction("transfer", new ContractFunctionParameters()
                        .addAddress(destaccnt)
                        .addUint256(runtosendlong))
                .execute(OPERATING_ACCOUNT);


        // if this doesn't throw then we know the contract executed successfully

        contractExecTransactionResponse.getReceipt(OPERATING_ACCOUNT);

    }

public static void sendhbar(BigInteger hbartosend, String destaccnt) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {

    Hbar amount = Hbar.fromTinybars(multiplier108.multiply(hbartosend).longValue());

    System.out.println(" hbar " + amount + " to " + destaccnt);
     new TransferTransaction()
            // .addSender and .addRecipient can be called as many times as you want as long as the total sum from
            // both sides is equivalent
            .addHbarTransfer(USER_ACCOUNT.getOperatorAccountId(), amount.negated())
            .addHbarTransfer(AccountId.fromString(destaccnt), amount)
            .setTransactionMemo("HBAR sent via RUN mobile")
            .execute(USER_ACCOUNT)
            .getReceipt(USER_ACCOUNT);

}




    public static void runtokensfromuser(BigInteger runtosendlong, String destaccnt) throws ReceiptStatusException, PrecheckStatusException, TimeoutException {

        // User pays the gas to send tokens

        TransactionResponse contractExecTransactionResponse = new ContractExecuteTransaction()
                .setContractId(runtokensc)
                .setGas(400000)
                .setFunction("transfer", new ContractFunctionParameters()
                        .addAddress(destaccnt)
                        .addUint256(runtosendlong))
                .execute(USER_ACCOUNT);


        // if this doesn't throw then we know the contract executed successfully

        contractExecTransactionResponse.getReceipt(USER_ACCOUNT);

    }


        // uses runitaccount ie the hedera account of HBAR bal and RUN token balance

    public static BigInteger getruntokenbal() throws ReceiptStatusException, PrecheckStatusException, TimeoutException {

        BigInteger tokenbal = new BigInteger("0");

        ContractFunctionResult contractCallResult01 = new ContractCallQuery()
                .setGas(4000000)
                .setContractId(runtokensc)
                .setFunction("balanceOf", new ContractFunctionParameters()
                        .addAddress(USER_ACCOUNT.getOperatorAccountId().toSolidityAddress()))
                .execute(OPERATING_ACCOUNT);


        if (contractCallResult01.errorMessage != null) {
            System.out.println("error calling contract: " + contractCallResult01.errorMessage);
            return tokenbal;
        }

        BigInteger coinsto18 = contractCallResult01.getUint256(0);

        tokenbal = coinsto18.divide(multiplier1018);

        return tokenbal;
    }


    // used to update the profile because the profile SC contractID is stored in the Run.it fileID(ieaccount) But
    // the profile also is to hold the run.it account (hedera fileid).. chick & egg. So this method below is called after the File create in the DApp


    public static void updaterunitaccountid_fileid_inprofile(ContractId usersprofilescID, String runitaccountidfileid) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {

        TransactionResponse contractExecTransactionResponse = new ContractExecuteTransaction()
                .setContractId(usersprofilescID)
                .setGas(400000)
                .setFunction("updaterunitaccountid", new ContractFunctionParameters()
                        .addString(runitaccountidfileid))
                .execute(USER_ACCOUNT);


        // if this doesn't throw then we know the contract executed successfully

        contractExecTransactionResponse.getReceipt(USER_ACCOUNT);
    }



    public static void updatedataprefsettings(ContractId usersprofilescID, String _interest1, String _interest2, String _interest3, Boolean _demo, Boolean _behav, Boolean _inter, BigInteger _sponsorslevel, BigInteger _grpsponsorslevel) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {

        //SC methods
            /*
            function updateinterests(string _interest1, string _interest2, string _interest3, bool _demo, bool _behav, bool _inter, uint256 _sponsorslevel, uint256 _grpsponsorslevel) public onlyOwner{

            }*/
        System.out.println("_interest1 in " +  _interest1);

        TransactionResponse contractExecTransactionResponse = new ContractExecuteTransaction()
                .setContractId(usersprofilescID)
                .setGas(4000000)
                .setFunction("updateinterests", new ContractFunctionParameters()
                        .addString(_interest1)
                        .addString(_interest2)
                        .addString(_interest3)
                        .addBool(_demo)
                        .addBool(_behav)
                        .addBool(_inter)
                        .addUint256(_sponsorslevel)
                        .addUint256(_grpsponsorslevel))
                .execute(USER_ACCOUNT);


        // if this doesn't throw then we know the contract executed successfully

        contractExecTransactionResponse.getReceipt(USER_ACCOUNT);
    }



    public static Runitprofile getdataprefsettings(String existingcontractid)  throws TimeoutException, PrecheckStatusException, ReceiptStatusException{


        // creating contract POJO with just the settings and then merge so original POJO has these value

        // hit the SCs public getters. Query.

        // get interests, booleans and 2 sliders ie sponsor exposure


        Runitprofile runitprefsettings = new Runitprofile();

        // interests..

        ContractFunctionResult result_1 = new ContractCallQuery()
                .setGas(300000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("getinterest1")
                .execute(USER_ACCOUNT);

        if (result_1.errorMessage != null) {
            System.out.println("Error calling Contract " + result_1.errorMessage);
            return runitprefsettings;
        }

        runitprefsettings.interest1 = result_1.getString(0);

        System.out.println("interest 1 in Hedera services " + runitprefsettings.interest1);

        ContractFunctionResult result_2 = new ContractCallQuery()
                .setGas(300000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("getinterest2")
                .execute(USER_ACCOUNT);

        if (result_2.errorMessage != null) {
            System.out.println("Error calling Contract " + result_2.errorMessage);
            return runitprefsettings;
        }

        runitprefsettings.interest2 = result_2.getString(0);

        ContractFunctionResult result_3 = new ContractCallQuery()
                .setGas(300000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("getinterest3")
                .execute(USER_ACCOUNT);

        if (result_3.errorMessage != null) {
            System.out.println("Error calling Contract " + result_3.errorMessage);
            return runitprefsettings;
        }

        runitprefsettings.interest3 = result_3.getString(0);


        // get bools

        ContractFunctionResult result4 = new ContractCallQuery()
                .setGas(300000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("demographic")
                .execute(USER_ACCOUNT);

        if (result4.errorMessage != null) {
            System.out.println("Error calling Contract " + result4.errorMessage);
            return runitprefsettings;
        }

       runitprefsettings.demographic = result4.getBool(0);


        ContractFunctionResult result5 = new ContractCallQuery()
                .setGas(300000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("behavioral")
                .execute(USER_ACCOUNT);

        if (result5.errorMessage != null) {
            System.out.println("Error calling Contract " + result5.errorMessage);
            return runitprefsettings;
        }

        runitprefsettings.behavioral = result5.getBool(0);


        ContractFunctionResult result6 = new ContractCallQuery()
                .setGas(300000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("interests")
                .execute(USER_ACCOUNT);

        if (result6.errorMessage != null) {
            System.out.println("Error calling Contract " + result6.errorMessage);
            return runitprefsettings;
        }

        runitprefsettings.interests = result6.getBool(0);

       ContractFunctionResult result7 = new ContractCallQuery()
                .setGas(300000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("sponsorslevel")
                .execute(USER_ACCOUNT);

        if (result7.errorMessage != null) {
            System.out.println("Error calling Contract " + result7.errorMessage);
            return runitprefsettings;
        }

        runitprefsettings.sponsorslevel = result7.getUint256(0);

        ContractFunctionResult result8= new ContractCallQuery()
                .setGas(300000)
                .setContractId(ContractId.fromString(existingcontractid))
                .setFunction("grpsponsorslevel")
                .execute(USER_ACCOUNT);

        if (result8.errorMessage != null) {
            System.out.println("Error calling Contract " + result8.errorMessage);
            return runitprefsettings;
        }

        runitprefsettings.grpsponsorslevel = result8.getUint256(0);

        return runitprefsettings;
    }



}
