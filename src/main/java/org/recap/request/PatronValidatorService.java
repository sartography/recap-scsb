package org.recap.request;

import com.ceridwen.circulation.SIP.messages.PatronInformationResponse;
import org.recap.ReCAPConstants;
import org.recap.ils.ColumbiaESIPConnector;
import org.recap.ils.NewYorkESIPConnector;
import org.recap.ils.PrincetonESIPConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * Created by hemalathas on 3/11/16.
 */
@Component
public class PatronValidatorService {

    PrincetonESIPConnector princetonESIPConnector;

    ColumbiaESIPConnector columbiaESIPConnector;

    NewYorkESIPConnector newYorkESIPConnector;

    public ResponseEntity patronValidation(String requestingInstitution,String patronBarcode) {
        if(!StringUtils.isEmpty(requestingInstitution) && !StringUtils.isEmpty(patronBarcode)){
            PatronInformationResponse patronInformationResponse = new PatronInformationResponse();
            if(requestingInstitution.equals(ReCAPConstants.PRINCETON)){
                patronInformationResponse = princetonESIPConnector.lookupUser(patronBarcode);
            }else if(requestingInstitution.equals(ReCAPConstants.COLUMBIA)){
                patronInformationResponse = columbiaESIPConnector.lookupUser(patronBarcode);
            }else{
                patronInformationResponse = newYorkESIPConnector.lookupUser(patronBarcode);
            }
            return new ResponseEntity(patronInformationResponse.getScreenMessage(), getHttpHeaders(), HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity(ReCAPConstants.EMPTY_PATRON_BARCODE, getHttpHeaders(), HttpStatus.BAD_REQUEST);
        }

    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ReCAPConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }
}
