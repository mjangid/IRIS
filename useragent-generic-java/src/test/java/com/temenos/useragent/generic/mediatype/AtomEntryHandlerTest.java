package com.temenos.useragent.generic.mediatype;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.temenos.useragent.generic.Link;
import com.temenos.useragent.generic.mediatype.AtomEntryHandler;

public class AtomEntryHandlerTest {

	private AtomEntryHandler handler = new AtomEntryHandler();

	@Test
	public void testGetLinks() {
		handler.setContent(AtomEntryHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt"));
		List<Link> links = handler.getLinks();
		assertEquals(4, links.size());

		// first 'self' link
		Link firstLink = links.get(0);
		assertEquals("self", firstLink.rel());
		assertEquals("Customers('100974')", firstLink.href());
		assertEquals("Customer", firstLink.title());
		assertEquals("", firstLink.description());
		assertFalse(firstLink.hasEmbeddedPayload());

		// second 'see' link
		Link secondLink = links.get(1);
		assertEquals("http://mybank/rels/see", secondLink.rel());
		assertEquals("Customers('100974')/see", secondLink.href());
		assertEquals("see record", secondLink.title());
		assertEquals(
				"http://schemas.microsoft.com/ado/2007/08/dataservices/related/Customer",
				secondLink.description());
		assertFalse(secondLink.hasEmbeddedPayload());

		// third 'see' link
		Link thirdLink = links.get(2);
		assertEquals("http://mybank/rels/input", thirdLink.rel());
		assertEquals("Customers('100974')/input", thirdLink.href());
		assertEquals("input deal", thirdLink.title());
		assertEquals("", thirdLink.description());
		assertFalse(thirdLink.hasEmbeddedPayload());

		// fourth 'see' link
		Link fourthLink = links.get(3);
		assertEquals("http://mybank/rels/review", fourthLink.rel());
		assertEquals("Customers('100974')/review", fourthLink.href());
		assertEquals("audit deal", fourthLink.title());
		assertEquals("", fourthLink.description());
		assertFalse(fourthLink.hasEmbeddedPayload());
	}

	@Test
	public void testGetId() {
		handler.setContent(AtomEntryHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt"));
		assertEquals("100974", handler.getId());
	}

	@Test
	public void testGetCountForValidProperties() {
		handler.setContent(AtomEntryHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt"));
		assertEquals(1, handler.getCount("GivenNames"));
		assertEquals(2, handler.getCount("Customer_LegalIdGroup"));
		assertEquals(1,
				handler.getCount("Customer_LegalIdGroup(0)/LegalDocName"));
		assertEquals(1,
				handler.getCount("Customer_LegalIdGroup(1)/LegalDocName"));
		assertEquals(2, handler.getCount("Customer_AddressGroup"));
		assertEquals(
				2,
				handler.getCount("Customer_AddressGroup(0)/Customer_AddressSubGroup"));
		assertEquals(
				1,
				handler.getCount("Customer_AddressGroup(0)/Customer_AddressSubGroup(0)/Address"));
		assertEquals(
				1,
				handler.getCount("Customer_AddressGroup(0)/Customer_AddressSubGroup(1)/Address"));
		assertEquals(
				2,
				handler.getCount("Customer_AddressGroup(1)/Customer_AddressSubGroup"));
		assertEquals(
				1,
				handler.getCount("Customer_AddressGroup(1)/Customer_AddressSubGroup(0)/Address"));
		assertEquals(
				1,
				handler.getCount("Customer_AddressGroup(1)/Customer_AddressSubGroup(1)/Address"));
	}

	@Test
	public void testGetCountForInvalidProperties() {
		handler.setContent(AtomEntryHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt"));
		assertEquals(0, handler.getCount("foo"));
		assertEquals(0,
				handler.getCount("Customer_LegalIdGroup(2)/LegalDocName"));
		assertEquals(
				0,
				handler.getCount("Customer_AddressGroup(0)/Customer_AddressSubGroup(10)/Address"));
		checkForCountWithException(null);
		checkForCountWithException("");
		checkForCountWithException("Customer_LegalIdGroup(-1)/LegalId");
		checkForCountWithException("Customer_LegalIdGroup[1]/LegalDocName");
		checkForCountWithException("Customer_AddressGroup(foo)");
		checkForCountWithException("Customer_AddressGroup(-1)");
		checkForCountWithException("Customer_AddressGroup/Customer_AddressSubGroup(-1)");
		checkForCountWithException("Customer_AddressGroup(0)/Customer_AddressSubGroup(-1)/Address");
		checkForCountWithException("Customer_AddressGroup(1)/Customer_AddressSubGroup()");
		checkForCountWithException("Customer_AddressGroup(1)/Customer_AddressSubGroup{1}/Address");
		checkForCountWithException("Customer_AddressGroup(1)/Customer_AddressSubGroup{0}/Address");
	}

	private void checkForCountWithException(String fqPropertyName) {
		try {
			handler.getCount(fqPropertyName);
			fail("IllegalArgumentException should have been thrown for property name '"
					+ fqPropertyName + "'");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testGetValueForValidProperties() {
		handler.setContent(AtomEntryHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt"));
		assertEquals("Peter", handler.getValue("GivenNames"));
		assertEquals("PASSPORT",
				handler.getValue("Customer_LegalIdGroup(0)/LegalDocName"));
		assertEquals("DRIVING LICENSE",
				handler.getValue("Customer_LegalIdGroup(1)/LegalDocName"));
		assertEquals(
				"18",
				handler.getValue("Customer_AddressGroup(0)/Customer_AddressSubGroup(0)/Address"));
		assertEquals(
				"Redds",
				handler.getValue("Customer_AddressGroup(0)/Customer_AddressSubGroup(1)/Address"));
		assertEquals(
				"18",
				handler.getValue("Customer_AddressGroup(1)/Customer_AddressSubGroup(0)/Address"));
		assertEquals(
				"Redes",
				handler.getValue("Customer_AddressGroup(1)/Customer_AddressSubGroup(1)/Address"));
	}

	@Test
	public void testGetValueForInvalidProperties() {
		handler.setContent(AtomEntryHandler.class
				.getResourceAsStream("/atom_entry_with_xml_content.txt"));
		assertEquals("", handler.getValue("foo"));
		assertEquals("",
				handler.getValue("Customer_LegalIdGroup(2)/LegalDocName"));
		assertEquals(
				"",
				handler.getValue("Customer_AddressGroup(0)/Customer_AddressSubGroup(10)/Address"));
		checkForValueWithException(null);
		checkForValueWithException("");
		checkForValueWithException("Customer_LegalIdGroup(-1)/LegalId");
		checkForValueWithException("Customer_LegalIdGroup[1]/LegalDocName");
		checkForValueWithException("Customer_AddressGroup(foo)");
		checkForValueWithException("Customer_AddressGroup(-1)");
		checkForValueWithException("Customer_AddressGroup/Customer_AddressSubGroup(-1)");
		checkForValueWithException("Customer_AddressGroup(0)/Customer_AddressSubGroup(-1)/Address");
		checkForValueWithException("Customer_AddressGroup(1)/Customer_AddressSubGroup()");
		checkForValueWithException("Customer_AddressGroup(1)/Customer_AddressSubGroup{1}/Address");
		checkForValueWithException("Customer_AddressGroup(1)/Customer_AddressSubGroup{0}/Address");
	}

	private void checkForValueWithException(String fqPropertyName) {
		try {
			handler.getValue(fqPropertyName);
			fail("IllegalArgumentException should have been thrown for property name '"
					+ fqPropertyName + "'");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
}
