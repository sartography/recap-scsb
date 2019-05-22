package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 23/3/17.
 */
public class ReportsResponseUT extends BaseTestCase {

	@Test
	public void testReportResponse() {
		DeaccessionItemResultsRow deaccessionItemResultsRow = DeaccessionItemResultsRow.builder()
				.itemBarcode("32145686568567").deaccessionNotes("test").deaccessionOwnInst("PUL").itemId(1)
				.title("test").cgd("Open").build();

		IncompleteReportResultsRow incompleteReportResultsRow = IncompleteReportResultsRow.builder().title("test")
				.author("john").barcode("32145564654564").createdDate(new Date().toString()).customerCode("PB")
				.owningInstitution("PUL").build();

		ReportsResponse reportsResponse = ReportsResponse.builder().accessionPrivatePulCount(1)
				.accessionPrivateCulCount(1).accessionPrivateNyplCount(1).accessionPrivatePulCount(1)
				.accessionPrivateCulCount(1).accessionPrivateNyplCount(1).accessionPrivatePulCount(1)
				.accessionPrivateCulCount(1).accessionPrivateNyplCount(1).deaccessionPrivatePulCount(1)
				.deaccessionPrivateCulCount(1).deaccessionPrivateNyplCount(1).deaccessionPrivatePulCount(1)
				.deaccessionPrivateCulCount(1).deaccessionPrivateNyplCount(1).deaccessionPrivatePulCount(1)
				.deaccessionPrivateCulCount(1).deaccessionPrivateNyplCount(1).openPulCgdCount(1).openCulCgdCount(1)
				.openNyplCgdCount(1).sharedPulCgdCount(1).sharedCulCgdCount(1).sharedNyplCgdCount(1)
				.privatePulCgdCount(1).privateCulCgdCount(1).privateNyplCgdCount(1).totalRecordsCount("1")
				.totalPageCount(1).message("Success").incompletePageNumber(1).incompletePageSize(10)
				.incompleteTotalPageCount(1).incompleteTotalRecordsCount("1")
				.deaccessionItemResultsRow(deaccessionItemResultsRow)
				.incompleteReportResultsRow(incompleteReportResultsRow).build();

		assertNotNull(reportsResponse);
	}

}