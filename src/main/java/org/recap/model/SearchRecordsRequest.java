package org.recap.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.recap.ReCAPConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
@ApiModel(value = "SearchRecordsRequest", description = "Model for showing user details")
public class SearchRecordsRequest implements Serializable {

	private static final long serialVersionUID = 2589164457324829225L;

	@ApiModelProperty(name = "fieldValue", value = "Search Value", position = 0)
	@Builder.Default
	private String fieldValue = "";

	@ApiModelProperty(name = "fieldName", value = "Select a field name", position = 1)
	@Builder.Default
	private String fieldName = "";

	@ApiModelProperty(name = "owningInstitutions", value = "Publications Owning Instutions", position = 2, allowableValues = "PUL, NYPL, CUL")
	@Builder.Default
	private List<String> owningInstitutions = new ArrayList<>(ReCAPConstants.INSTITUTIONS);

	@ApiModelProperty(name = "collectionGroupDesignations", value = "Collection Group Designations", position = 3, allowableValues = "Shared, Open, Private")
	@Builder.Default
	private List<String> collectionGroupDesignations = new ArrayList<>(ReCAPConstants.COLLECTION_GROUP_DESIGNATIONS);

	@ApiModelProperty(name = "availability", value = "Availability of books in ReCAP", position = 4, allowableValues = "Available, NotAvailable")
	@Builder.Default
	private List<String> availability = new ArrayList<>(ReCAPConstants.AVAILABILITY);

	@ApiModelProperty(name = "materialTypes", value = "Material Types", position = 5, allowableValues = "Serial, Monograph, Other")
	@Builder.Default
	private List<String> materialTypes = new ArrayList<>(ReCAPConstants.MATERIAL_TYPES);

	@ApiModelProperty(name = "useRestrictions", value = "Book Use Restrictions", position = 6, allowableValues = "NoRestrictions, InLibraryUse, SupervisedUse")
	@Builder.Default
	private List<String> useRestrictions = new ArrayList<>(ReCAPConstants.USE_RESTRICTIONS);

	@ApiModelProperty(name = "isDeleted", value = "Is Deleted", position = 7)
	@Builder.Default
	private boolean isDeleted = false;

	@ApiModelProperty(name = "catalogingStatus", value = "Cataloging Status", position = 8, allowableValues = "Complete, Incomplete")
	@Builder.Default
	private String catalogingStatus = ReCAPConstants.COMPLETE_STATUS;

	@ApiModelProperty(name = "pageNumber", value = "Current Page Number", position = 9)
	@Builder.Default
	private Integer pageNumber = 0;

	@ApiModelProperty(name = "pageSize", value = "Total records to show is page", position = 10)
	@Builder.Default
	private Integer pageSize = 10;
}
