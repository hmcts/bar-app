package uk.gov.hmcts.bar.api.data.exceptions;

public class PaymentNotFoundException extends ResourceNotFoundException {
    public PaymentNotFoundException(String id) {
        super("payment", "id", id);
    }
}
