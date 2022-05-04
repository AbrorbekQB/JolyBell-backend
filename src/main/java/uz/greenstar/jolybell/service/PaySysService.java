package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uz.greenstar.jolybell.api.payment.PaySysErrorMessage;
import uz.greenstar.jolybell.api.payment.PaySysResponse;
import uz.greenstar.jolybell.config.PaySysConfProperties;
import uz.greenstar.jolybell.dto.payment.PaySysPayDTO;
import uz.greenstar.jolybell.entity.OrderEntity;
import uz.greenstar.jolybell.entity.PaySysEntity;
import uz.greenstar.jolybell.enums.OrderState;
import uz.greenstar.jolybell.enums.OrderStatus;
import uz.greenstar.jolybell.enums.PaySysStatus;
import uz.greenstar.jolybell.repository.OrderRepository;
import uz.greenstar.jolybell.repository.PaySysRepository;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaySysService {
    private final OrderRepository orderRepository;
    private final PaySysConfProperties paySysConfProperties;
    private final PaySysRepository paySysRepository;

    @Autowired
    @Qualifier("sslRestTemplate")
    private RestTemplate paySysRestTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaySysResponse pay(PaySysPayDTO dto) {
        PaySysResponse paySysResponse = new PaySysResponse();
        Optional<OrderEntity> optionalOrder = orderRepository.findByIdAndStatus(dto.getOrderId(), OrderStatus.CHOICE_PAYMENT);
        if (optionalOrder.isEmpty()) {
            PaySysErrorMessage paySysErrorMessage = new PaySysErrorMessage();
            paySysErrorMessage.setCode(0);
            paySysErrorMessage.setMessage("Произошла ошибка. Пожалуйста, попробуйте еще раз или выберите другой способ оплаты!");
            paySysResponse.setError(paySysErrorMessage);
            return paySysResponse;
        }

        PaySysEntity paySysEntity = new PaySysEntity();

        Map<String, Object> paySysPreparePaymentRequest = new HashMap<>();
        paySysPreparePaymentRequest.put("method", "paysys.prepare_payment");

        String partnerTransactionId = UUID.randomUUID().toString();
        paySysEntity.setStatus(PaySysStatus.PREPARE_PAYMENT);
        paySysEntity.setTotalAmount(optionalOrder.get().getTotalAmount());
        paySysEntity.setOrderId(optionalOrder.get().getId());
        paySysEntity.setTransactionId(partnerTransactionId);
        paySysRepository.save(paySysEntity);

        Map<String, Object> paySysPreparePaymentParams = new HashMap<>();
        paySysPreparePaymentParams.put("card_holder_name", dto.getCardHolderName());
        paySysPreparePaymentParams.put("card_number", dto.getCardNumber());
        paySysPreparePaymentParams.put("card_expire", dto.getCardExpire());
        paySysPreparePaymentParams.put("card_cvc", dto.getCardCvc());
        paySysPreparePaymentParams.put("currency", "usd");
        paySysPreparePaymentParams.put("vendor_id", paySysConfProperties.getVendorId());
        paySysPreparePaymentParams.put("partner_trans_id", partnerTransactionId);
        paySysPreparePaymentParams.put("redirect_url", "");
        paySysPreparePaymentParams.put("amount", optionalOrder.get().getTotalAmount());

        paySysPreparePaymentRequest.put("params", paySysPreparePaymentParams);
        paySysPreparePaymentRequest.put("id", paySysEntity.getId());

        paySysResponse = preparePayment(paySysPreparePaymentRequest);
        if (Objects.isNull(paySysResponse.getResult())) {
            updateData(paySysEntity, PaySysStatus.PREPARE_FAIL);
            return paySysResponse;
        }
        updateData(paySysEntity, PaySysStatus.PREPARE_SUCCESS);


        Map<String, Object> checkPaymentRequest = new HashMap<>();
        checkPaymentRequest.put("method", "paysys.check_payment");

        Map<String, Object> checkPaymentRequestParam = new HashMap<>();
        checkPaymentRequestParam.put("partner_trans_id", partnerTransactionId);

        checkPaymentRequest.put("params", checkPaymentRequestParam);
        checkPaymentRequest.put("id", paySysEntity.getId());

        boolean checking = true;
        while (checking) {
            try {
                paySysResponse = checkPayment(checkPaymentRequest);
                if (Objects.isNull(paySysResponse.getResult())
                        || !paySysResponse.getResult().get("status").toString().equals("1")
                        || optionalOrder.get().getBookedDateTime().atZone(ZoneId.systemDefault()).toEpochSecond() - LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() < 0)
                    checking = false;
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (Objects.nonNull(paySysResponse.getResult()) && paySysResponse.getResult().get("status").toString().equals("2")) {
            updateData(paySysEntity, PaySysStatus.CHECK_SUCCESS);
            paySysResponse.setSuccessfullyFinished(true);
            optionalOrder.get().setLastUpdateTime(LocalDateTime.now());
            optionalOrder.get().setStatus(OrderStatus.PAY_SUCCESS);
            optionalOrder.get().setState(OrderState.USER_FINISH);
            orderRepository.save(optionalOrder.get());
        } else {
            updateData(paySysEntity, PaySysStatus.CHECK_FAIL);
            optionalOrder.get().setLastUpdateTime(LocalDateTime.now());
            optionalOrder.get().setStatus(OrderStatus.PAY_FAIL);
            orderRepository.save(optionalOrder.get());
        }

        return paySysResponse;
    }

    private PaySysResponse preparePayment(Map request) {
        Instant nowUtc = Instant.now();
        Long time = nowUtc.getEpochSecond();

        PaySysResponse response = new PaySysResponse();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Auth", generateAuth(time));
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));

            HttpEntity<Map> requestHttpEntity = new HttpEntity<>(request, headers);
            log.info("########################################### Request prepare_payment " + request + " ############ time:" + nowUtc);

//            Map<String, Object> responseEntity = paySysRestTemplate.postForObject(paySysConfProperties.getUrl(), requestHttpEntity, Map.class);
            Map<String, Object> responseEntity = new HashMap<>();
            responseEntity.put("id", request.get("id"));
            responseEntity.put("mx_id", UUID.randomUUID().toString());

            Map<String, Object> result = new HashMap<>();
            result.put("transaction_id", UUID.randomUUID().toString());
            result.put("confirm_url", "https://ecomm.kapital24.uz:6443/ecomm2/ClientHandler?trans_id=tYkiAVsnP3SnjBMSiXrWGrIRbeo%3D");
            responseEntity.put("result", result);

            log.info("######################################## Response prepare_payment " + responseEntity + "######################################## time " + Instant.now());

            response.setId((String) responseEntity.get("id"));
            response.setMxId((String) responseEntity.get("mx_id"));
            if (Objects.nonNull(responseEntity.get("result"))) {
                response.setResult((Map<String, Object>) responseEntity.get("result"));
                return response;
            }
            if (Objects.nonNull(responseEntity.get("error"))) {
                Map<String, Object> errorResponse = (Map<String, Object>) responseEntity.get("error");
                PaySysErrorMessage errorMessage = new PaySysErrorMessage();
                errorMessage.setCode((Integer) errorResponse.get("code"));
                errorMessage.setMessage((String) errorResponse.get("message"));
                response.setError(errorMessage);
            }
        } catch (Exception ex) {
            log.error("PaySys server error! Message: " + ex.getMessage());
            PaySysErrorMessage paySysErrorMessage = new PaySysErrorMessage();
            paySysErrorMessage.setCode(0);
            paySysErrorMessage.setMessage("Произошла ошибка. Пожалуйста, попробуйте еще раз или выберите другой способ оплаты!");
        }

        return response;
    }

    private PaySysResponse checkPayment(Map request) {
        Instant nowUtc = Instant.now();
        Long time = nowUtc.getEpochSecond();
        PaySysResponse response = new PaySysResponse();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Auth", generateAuth(time));
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));

            HttpEntity<Map> requestHttpEntity = new HttpEntity<>(request, headers);
            log.info("########################################### Request check_payment " + request + " ############ time:" + nowUtc);

//            Map<String, Object> responseEntity = paySysRestTemplate.postForObject(paySysConfProperties.getUrl(), requestHttpEntity, Map.class);
            Map<String, Object> responseEntity = new HashMap<>();
            responseEntity.put("id", request.get("id"));
            responseEntity.put("mx_id", UUID.randomUUID().toString());

            Map<String, Object> result = new HashMap<>();
            result.put("transaction_id", UUID.randomUUID().toString());
            result.put("partner_trans_id", request.get("partner_trans_id"));
            result.put("status", "2");
            responseEntity.put("result", result);

            log.info("######################################## Response check_payment " + responseEntity + "######################################## time " + Instant.now());

            response.setId((String) responseEntity.get("id"));
            response.setMxId((String) responseEntity.get("mx_id"));
            if (Objects.nonNull(responseEntity.get("result"))) {
                response.setResult((Map<String, Object>) responseEntity.get("result"));
                return response;
            }
            if (Objects.nonNull(responseEntity.get("error"))) {
                Map<String, Object> errorResponse = (Map<String, Object>) responseEntity.get("error");
                PaySysErrorMessage errorMessage = new PaySysErrorMessage();
                errorMessage.setCode((Integer) errorResponse.get("code"));
                errorMessage.setMessage((String) errorResponse.get("message"));
                response.setError(errorMessage);
            }
        } catch (Exception ex) {
            log.error("PaySys server error! Message: " + ex.getMessage());
            PaySysErrorMessage paySysErrorMessage = new PaySysErrorMessage();
            paySysErrorMessage.setCode(0);
            paySysErrorMessage.setMessage("Произошла ошибка. Пожалуйста, попробуйте еще раз или выберите другой способ оплаты!");
        }
        return response;
    }

    private String generateAuth(Long time) {
        return paySysConfProperties.getServiceId() + "-" + DigestUtils.sha1Hex(paySysConfProperties.getSecretKey() + time) + "-" + time;
    }

    private void updateData(PaySysEntity paySysEntity, PaySysStatus status) {
        paySysEntity.setStatus(status);
        paySysEntity.setLastUpdateTime(LocalDateTime.now());
        paySysRepository.save(paySysEntity);
    }
}
