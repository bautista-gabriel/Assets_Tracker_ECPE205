package model;

public class SubAccount {
    private String  parentAccount;
    private String  accountName;
    private String  accountNumber;
    private String  accountType;
    private double  balance;
    private String  currency;
    private boolean active;

    public SubAccount(String parentAccount, String accountName,String accountNumber, String accountType,
                      double balance, String currency, boolean active) {
        this.parentAccount = parentAccount;
        this.accountName   = accountName;
        this.accountNumber = accountNumber;
        this.accountType   = accountType;
        this.balance       = balance;
        this.currency      = currency;
        this.active        = active;
    }

    public String  getParentAccount(){
        return parentAccount;
    }
    public String  getAccountName(){
        return accountName;   }
    public String  getAccountNumber(){
        return accountNumber;
    }
    public String  getAccountType(){
        return accountType;
    }
    public double  getBalance(){
        return balance;
    }
    public String  getCurrency(){
        return currency;
    }
    public boolean isActive(){
        return active;
    }

    public void setParentAccount(String v){
        parentAccount = v;
    }
    public void setAccountName(String v){
        accountName = v; }
    public void setAccountNumber(String v){
        accountNumber = v;
    }
    public void setAccountType(String v){
        accountType = v;
    }
    public void setBalance(double v){
        balance = v;
    }
    public void setCurrency(String v){
        currency = v;
    }
    public void setActive(boolean v){
        active = v;
    }
}