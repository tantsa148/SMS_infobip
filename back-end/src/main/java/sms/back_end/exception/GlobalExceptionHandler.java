package sms.back_end.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.CONFLICT.value());
        
        // Essayer différentes sources de message
        String errorMessage = null;
        String tableName = null;
        
        // 1. Essayer getMostSpecificCause
        if (ex.getMostSpecificCause() != null) {
            errorMessage = ex.getMostSpecificCause().getMessage();
            System.out.println("=== getMostSpecificCause ===");
            System.out.println(errorMessage);
            tableName = extractTableName(errorMessage);
        }
        
        // 2. Si pas trouvé, essayer getRootCause
        if (tableName == null && ex.getRootCause() != null) {
            errorMessage = ex.getRootCause().getMessage();
            System.out.println("=== getRootCause ===");
            System.out.println(errorMessage);
            tableName = extractTableName(errorMessage);
        }
        
        // 3. Si pas trouvé, essayer getCause
        if (tableName == null && ex.getCause() != null) {
            errorMessage = ex.getCause().getMessage();
            System.out.println("=== getCause ===");
            System.out.println(errorMessage);
            tableName = extractTableName(errorMessage);
        }
        
        // 4. Si toujours pas trouvé, essayer le message principal
        if (tableName == null) {
            errorMessage = ex.getMessage();
            System.out.println("=== getMessage ===");
            System.out.println(errorMessage);
            tableName = extractTableName(errorMessage);
        }
        
        if (tableName != null) {
            body.put("error", "Impossible de supprimer cet enregistrement car il est utilisé dans la table : " + tableName);
            body.put("table_liee", tableName);
        } else {
            body.put("error", "Impossible de supprimer cet enregistrement car il est lié à d'autres données");
        }

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    /**
     * Extrait le nom de la table depuis le message d'erreur SQL
     */
    /**
 * Extrait le nom de la table depuis le message d'erreur SQL
 */
private String extractTableName(String errorMessage) {
    if (errorMessage == null) {
        return null;
    }
    
    // Pour PostgreSQL - Chercher "is still referenced from table" en PRIORITÉ
    // car c'est la table qui fait la référence
    if (errorMessage.contains("is still referenced from table")) {
        int fromTableIndex = errorMessage.indexOf("is still referenced from table");
        if (fromTableIndex != -1) {
            // Trouver le prochain guillemet après "from table"
            int firstQuoteIndex = errorMessage.indexOf("\"", fromTableIndex);
            if (firstQuoteIndex != -1) {
                int secondQuoteIndex = errorMessage.indexOf("\"", firstQuoteIndex + 1);
                if (secondQuoteIndex != -1) {
                    String tableName = errorMessage.substring(firstQuoteIndex + 1, secondQuoteIndex);
                    System.out.println("Table trouvée via 'referenced from table': " + tableName);
                    return tableName;
                }
            }
        }
    }
    
    // Méthode alternative: chercher le DERNIER "on table" (pas le premier)
    if (errorMessage.contains("on table")) {
        int onTableIndex = errorMessage.lastIndexOf("on table"); // lastIndexOf au lieu de indexOf
        if (onTableIndex != -1) {
            // Trouver le prochain guillemet après "on table"
            int firstQuoteIndex = errorMessage.indexOf("\"", onTableIndex);
            if (firstQuoteIndex != -1) {
                int secondQuoteIndex = errorMessage.indexOf("\"", firstQuoteIndex + 1);
                if (secondQuoteIndex != -1) {
                    String tableName = errorMessage.substring(firstQuoteIndex + 1, secondQuoteIndex);
                    System.out.println("Table trouvée via 'on table': " + tableName);
                    return tableName;
                }
            }
        }
    }
    
    return null;
}
}