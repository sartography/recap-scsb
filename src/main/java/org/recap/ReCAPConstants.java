package org.recap;

import java.util.Arrays;
import java.util.List;

/**
 * Created by premkb on 19/8/16.
 */
public final class ReCAPConstants {

	public static final String FAILURE = "Failure";
	public static final String SUCCESS = "Success";

	public static final String ITEM_BARCDE_DOESNOT_EXIST = "Item barcode doesn't exist in SCSB database.";
	public static final String COLUMBIA = "CUL";
	public static final String PRINCETON = "PUL";
	public static final String NYPL = "NYPL";

	public static final String RESPONSE_DATE = "Date";

	public static final String INVALID_REQUEST_INSTITUTION = "Enter valid RequestingInstitution: PUL, CUL or NYPL.";
	public static final String START_PAGE_AND_END_PAGE_REQUIRED = "Start page and End page information is required for EDD request.";
	public static final String VALID_REQUEST = "All request parameters are valid.Patron is eligible to raise a request";

	// Retrieval,EDD, Hold, Recall, Borrow Direct
	public static final String REQUEST_TYPE_RETRIEVAL = "RETRIEVAL";
	public static final String REQUEST_TYPE_EDD = "EDD";
	public static final String REQUEST_TYPE_RECALL = "RECALL";
	public static final String REQUEST_TYPE_BORROW_DIRECT = "BORROW DIRECT";

	// MQ URI
	public static final String REQUEST_ITEM_QUEUE = "scsbactivemq:queue:RequestItemQ";
	public static final String REQUEST_TYPE_QUEUE_HEADER = "RequestType";
	public static final String BULK_REQUEST_ITEM_QUEUE = "scsbactivemq:queue:BulkRequestItemQ";

	public static final String URL_REQUEST_ITEM_INFORMATION = "requestItem/itemInformation";
	public static final String URL_REQUEST_ITEM_HOLD = "requestItem/holdItem";
	public static final String URL_REQUEST_ITEM_RECALL = "requestItem/recallItem";
	public static final String URL_REQUEST_ITEM_CREATEBIB = "requestItem/createBib";
	public static final String URL_REQUEST_PATRON_INFORMATION = "requestItem/patronInformation";
	public static final String URL_REQUEST_RE_FILE = "requestItem/refile";
	public static final String URL_REQUEST_ITEM_VALIDATE_ITEM_REQUEST = "requestItem/validateItemRequest";
	public static final String URL_REQUEST_CANCEL = "cancelRequest/cancel";
	public static final String URL_REQUEST_REPLACE = "requestItem/replaceRequest";

	public static final String REST_URL_REQUEST_ITEM = "/requestItem";
	public static final String REST_URL_VALIDATE_REQUEST_ITEM = "/validateItemRequestInformations";

	public static final String REST_URL_PURGE_EMAIL_ADDRESS = "purge/purgeEmailAddress";
	public static final String REST_URL_PURGE_EXCEPTION_REQUESTS = "purge/purgeExceptionRequests";

	public static final String DATADUMP_NO_RECORD = "There is no data to export.";
	public static final String DATADUMP_PROCESS_STARTED = "Export process has started and we will send an email notification upon completion";

	public static final String URL_SEARCH_BY_PARAM = "searchService/searchByParam";
	public static final String URL_SEARCH_BY_JSON = "searchService/search";
	public static final String URL_UPDATE_CGD = "updateCgdService/updateCgd";
	public static final String URL_REPORTS_ACCESSION_DEACCESSION_COUNTS = "reportsService/accessionDeaccessionCounts";
	public static final String URL_REPORTS_CGD_ITEM_COUNTS = "reportsService/cgdItemCounts";
	public static final String URL_REPORTS_DEACCESSION_RESULTS = "reportsService/deaccessionResults";
	public static final String URL_REPORTS_INCOMPLETE_RESULTS = "reportsService/incompleteRecords";
	public static final String URL_SCHEDULE_JOBS = "scheduleService/scheduleJob";

	public static final String INPUT_RECORDS = "inputRecords";
	public static final String INSTITUTION = "institution";
	public static final String IS_CGD_PROTECTED = "isCGDProtected";
	public static final String INVALID_SCSB_XML_FORMAT_MESSAGE = "Please provide valid SCSB xml format";
	public static final String INVALID_MARC_XML_FORMAT_MESSAGE = "Please provide valid Marc xml format";
	public static final String SUBMIT_COLLECTION_INTERNAL_ERROR = "Internal error occurred during submit collection";
	public static final String TRANSFER_INTERNAL_ERROR = "Internal error occurred during transfer";
	public static final String ACCESSION_INTERNAL_ERROR = "Internal error occurred during accession";

	public static final String API_KEY = "api_key";
	public static final String RECAP = "recap";

	public static final String CGD_UPDATE_ITEM_BARCODE = "itemBarcode";
	public static final String OWNING_INSTITUTION = "owningInstitution";
	public static final String OLD_CGD = "oldCollectionGroupDesignation";
	public static final String NEW_CGD = "newCollectionGroupDesignation";
	public static final String CGD_CHANGE_NOTES = "cgdChangeNotes";
	public static final String USER_NAME = "userName";

	public static final String REQUEST_EXCEPTION_REST = "RestClient : ";
	public static final String REQUEST_EXCEPTION = "Exception : ";
	public static final String ONGOING_ACCESSION_LIMIT_EXCEED_MESSAGE = "Input limit exceeded. Maximum allowed input limit is";

	public static final String SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE = "Scsb Solr Client Service is Unavailable.";
	public static final String SCSB_CIRC_SERVICE_UNAVAILABLE = "Scsb Circ Service is Unavailable.";
	public static final String XML = "xml";

	// Logger Error
	public static final String LOG_ERROR = "error-->";
	public static final String LOG_ERROR_REST_CLIENT = "RestClient : ";

	public static final String COMPLETE_STATUS = "Complete";
	public static final String REQUEST_MESSAGE_RECEVIED = "Message received, your request will be processed";

	public static final String PURGE_EXCEPTION_REQUESTS = "PurgeExceptionRequests";
	public static final String SCHEDULE = "Schedule";
	public static final String STATUS = "Status";

	public static final String BULK_REQUEST_MESSAGE_RECEIVED = "Bulk request process initiated.";

	public static final List<String> INSTITUTIONS = Arrays.asList(COLUMBIA, PRINCETON, NYPL);
	public static final List<String> COLLECTION_GROUP_DESIGNATIONS = Arrays.asList("Private", "Open", "Shared");
	public static final List<String> AVAILABILITY = Arrays.asList("Available", "NotAvailable");
	public static final List<String> MATERIAL_TYPES = Arrays.asList("Serial", "Monograph", "Other");
	public static final List<String> USE_RESTRICTIONS = Arrays.asList("NoRestrictions", "InLibraryUse",
			"SupervisedUse");

	private ReCAPConstants() {
	}
}
