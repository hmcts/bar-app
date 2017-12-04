package uk.gov.hmcts.bar.api.data.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
public class PaymentInstructionServiceTest {

    @InjectMocks
    private PaymentInstructionService paymentInstructionService;

    @Mock
    private PaymentInstructionRepository paymentInstructionRepository;

    @Mock
    private PaymentReferenceService paymentReferenceService;

    @Mock
    private List<PaymentInstruction> paymentInstructionList;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
        paymentInstructionService = new PaymentInstructionService(paymentReferenceService,paymentInstructionRepository);

    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenChequeInstructionIsCalled() throws Exception {

        PaymentInstruction savedChequePaymentInstruction = ChequePaymentInstruction.chequePaymentInstructionWith()
            .amount(200).currency("GBP").chequeNumber("000000").payerName("Mr Payer Payer")
            .build();

        PaymentReference paymentReference = new PaymentReference(new PaymentReferenceKey("BR01", LocalDate.now()),1);

        savedChequePaymentInstruction.setStatus(PaymentInstruction.DRAFT);
        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(paymentReference.getPaymentReferenceKey().getSiteId())).thenReturn(paymentReference);
        when(paymentInstructionRepository.save(savedChequePaymentInstruction)).thenReturn(savedChequePaymentInstruction);
        PaymentInstruction createdPaymentInstruction = paymentInstructionService.createPaymentInstruction(savedChequePaymentInstruction);

        assertEquals(savedChequePaymentInstruction,createdPaymentInstruction);


    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenCashInstructionIsCalled() throws Exception {

        PaymentInstruction savedCashPaymentInstruction = CashPaymentInstruction.cashPaymentInstructionWith()
            .amount(200).currency("GBP").payerName("Mr Payer Payer").build();

        PaymentReference paymentReference = new PaymentReference(new PaymentReferenceKey("BR01", LocalDate.now()),1);

        savedCashPaymentInstruction.setStatus(PaymentInstruction.DRAFT);
        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(paymentReference.getPaymentReferenceKey().getSiteId())).thenReturn(paymentReference);
        when(paymentInstructionRepository.save(savedCashPaymentInstruction)).thenReturn(savedCashPaymentInstruction);

        PaymentInstruction createdPaymentInstruction = paymentInstructionService.createPaymentInstruction(savedCashPaymentInstruction);

        assertEquals(savedCashPaymentInstruction,createdPaymentInstruction);


    }



    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenPostalOrderInstructionIsCalled() throws Exception {

        PaymentInstruction savedPostalOrderPaymentInstruction = PostalOrderPaymentInstruction.postalOrderPaymentInstructionWith()
            .amount(200).currency("GBP").payerName("Mr Payer Payer").postalOrderNumber("000000").build();

        PaymentReference paymentReference = new PaymentReference(new PaymentReferenceKey("BR01", LocalDate.now()),1);
        savedPostalOrderPaymentInstruction.setStatus(PaymentInstruction.DRAFT);

        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(paymentReference.getPaymentReferenceKey().getSiteId())).thenReturn(paymentReference);
        when(paymentInstructionRepository.save(savedPostalOrderPaymentInstruction)).thenReturn(savedPostalOrderPaymentInstruction);

        PaymentInstruction createdPaymentInstruction = paymentInstructionService.createPaymentInstruction(savedPostalOrderPaymentInstruction);

        assertEquals(savedPostalOrderPaymentInstruction,createdPaymentInstruction);


    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenAllPayInstructionIsCalled() throws Exception {

        PaymentInstruction savedAllPayPaymentInstruction = AllPayPaymentInstruction.allPayPaymentInstructionWith()
            .amount(200).currency("GBP").payerName("Mr Payer Payer").allPayTransactionId("allpayid").build();

        PaymentReference paymentReference = new PaymentReference(new PaymentReferenceKey("BR01", LocalDate.now()),1);
        savedAllPayPaymentInstruction.setStatus(PaymentInstruction.DRAFT);

        when(paymentReferenceService.getNextPaymentReferenceSequenceBySite(paymentReference.getPaymentReferenceKey().getSiteId())).thenReturn(paymentReference);
        when(paymentInstructionRepository.save(savedAllPayPaymentInstruction)).thenReturn(savedAllPayPaymentInstruction);

        PaymentInstruction createdPaymentInstruction = paymentInstructionService.createPaymentInstruction(savedAllPayPaymentInstruction);

        assertEquals(savedAllPayPaymentInstruction,createdPaymentInstruction);


    }


    @Test
    public void shouldReturnPaymentInstructionList_whenGetAllPaymentInstructionsIsCalled() throws Exception {

        when(paymentInstructionRepository.findBySiteIdAndPaymentDateIsAfter("BR01", LocalDateTime.now().truncatedTo(ChronoUnit.DAYS))).thenReturn(paymentInstructionList);
        List<PaymentInstruction> retrievedPaymentInstructionList = paymentInstructionService.getAllPaymentInstructions();
        assertEquals(paymentInstructionList,retrievedPaymentInstructionList);
    }



    @Test
    public void shouldDeletePaymentInstruction_whenDeletePaymentInstructionIsCalled() throws Exception {

       paymentInstructionService.deleteCurrentPaymentInstructionWithDraftStatus(1);

        verify(paymentInstructionRepository, times(1)).deletePaymentInstructionByIdAndStatusAndPaymentDateAfter(1,PaymentInstruction.DRAFT,LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
    }


    @Test(expected = PaymentInstructionNotFoundException.class)
    public void shouldThrowPaymentInstructionNotFoundException_whenDeletePaymentInstructionIsCalledAndNotFound() throws Exception {
        PaymentInstructionService service = mock(PaymentInstructionService.class);
        doThrow(PaymentInstructionNotFoundException.class).when(service).deleteCurrentPaymentInstructionWithDraftStatus(1);
        service.deleteCurrentPaymentInstructionWithDraftStatus(1);

    }

    @Test
    public void shouldDeleteDraftPaymentInstruction_whenDeletePaymentInstructionIsCalled() {

        ArgumentCaptor<Integer> idCapture = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> statusCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> dateTimeCapture = ArgumentCaptor.forClass(LocalDateTime.class);

        paymentInstructionService.deleteCurrentPaymentInstructionWithDraftStatus(1);
        verify(paymentInstructionRepository, times(1)).deletePaymentInstructionByIdAndStatusAndPaymentDateAfter(idCapture.capture(),statusCapture.capture(),dateTimeCapture.capture());

        assertEquals("draft", statusCapture.getValue());
    }


}

