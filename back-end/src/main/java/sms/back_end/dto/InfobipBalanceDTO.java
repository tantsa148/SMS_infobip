package sms.back_end.dto;

public class InfobipBalanceDTO {
    private Double balance;
    private String currency;

    public InfobipBalanceDTO() {}

    public InfobipBalanceDTO(Double balance, String currency) {
        this.balance = balance;
        this.currency = currency;
    }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
