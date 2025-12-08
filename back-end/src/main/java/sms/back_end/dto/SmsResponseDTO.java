package sms.back_end.dto;

public class SmsResponseDTO {

    private Long idNumero;
    private Long idDestinataire;
    private Long idMessage;   // ✅ AJOUT
    private String from;
    private String to;
    private String message;
    private String baseUrl;
    private String apiKey;
    private InfobipResponseDTO infobipResponse;

    public SmsResponseDTO(Long idNumero,
                          Long idDestinataire,
                          Long idMessage,      // ✅ AJOUT
                          String from,
                          String to,
                          String message,
                          String baseUrl,
                          String apiKey,
                          InfobipResponseDTO infobipResponse) {

        this.idNumero = idNumero;
        this.idDestinataire = idDestinataire;
        this.idMessage = idMessage;
        this.from = from;
        this.to = to;
        this.message = message;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.infobipResponse = infobipResponse;
    }

    public Long getIdNumero() { return idNumero; }
    public Long getIdDestinataire() { return idDestinataire; }
    public Long getIdMessage() { return idMessage; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getMessage() { return message; }
    public String getBaseUrl() { return baseUrl; }
    public String getApiKey() { return apiKey; }
    public InfobipResponseDTO getInfobipResponse() { return infobipResponse; }
}
