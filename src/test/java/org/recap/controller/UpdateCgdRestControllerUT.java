package org.recap.controller;

import org.junit.Test;
import org.recap.ReCAPConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rajeshbabuk on 3/1/17.
 */
public class UpdateCgdRestControllerUT extends BaseControllerUT {

    @Test
    public void updateCgdForItem() throws Exception {
        String itemBarcode = "1";
        String owningInstitution = "PUL";
        String oldCollectionGroupDesignation = "Shared";
        String newCollectionGroupDesignation = "Private";
        String cgdChangeNotes = "Notes";
        MvcResult savedResult = this.mockMvc.perform(get("/updateCgdService/updateCgd")
                .headers(getHttpHeaders())
                .param(ReCAPConstants.CGD_UPDATE_ITEM_BARCODE, String.valueOf(itemBarcode))
                .param(ReCAPConstants.OWNING_INSTITUTION, owningInstitution)
                .param(ReCAPConstants.OLD_CGD, oldCollectionGroupDesignation)
                .param(ReCAPConstants.NEW_CGD, newCollectionGroupDesignation)
                .param(ReCAPConstants.CGD_CHANGE_NOTES, cgdChangeNotes))
                .andExpect(status().isOk())
                .andReturn();

        String statusResponse = savedResult.getResponse().getContentAsString();
        assertNotNull(statusResponse);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(ReCAPConstants.API_KEY, ReCAPConstants.RECAP);
        return headers;
    }
}
