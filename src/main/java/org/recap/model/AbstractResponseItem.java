package org.recap.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class AbstractResponseItem {
	private String itemBarcode;
	@Builder.Default private String itemOwningInstitution = ""; // PUL, CUL, NYPL
	private String screenMessage;
	private boolean success;
	private String esipDataIn;
	private String esipDataOut;
}
