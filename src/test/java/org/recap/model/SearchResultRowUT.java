package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/3/17.
 */
public class SearchResultRowUT extends BaseTestCase {

	@Test
	public void testSearchResultRow() {
		SearchItemResultRow searchItemResultRow = SearchItemResultRow.builder().callNumber("1321")
				.chronologyAndEnum("test").customerCode("PB").barcode("321564564647684")
				.useRestriction("Supervised Use").collectionGroupDesignation("Open").availability("Available")
				.selectedItem(true).itemId(1).build();
		
		SearchResultRow searchResultRow = SearchResultRow.builder().bibId(1).title("test").author("John")
				.publisher("Test").publisherDate(new Date().toString()).owningInstitution("PUL").customerCode("PB")
				.collectionGroupDesignation("Open").useRestriction("Supervised Use").barcode("3326564464656465465")
				.summaryHoldings("test").availability("Available").leaderMaterialType("Test").selected(true)
				.showItems(true).selectAllItems(true).requestPosition(1).itemId(1).patronBarcode(456985227)
				.requestingInstitution("CUL").deliveryLocation("PB").requestType("Recall").requestNotes("test")
				.searchItemResultRow(searchItemResultRow).build();

		assertNotNull(searchResultRow);
		assertNotNull(searchResultRow.getSearchItemResultRows());
	}
}