/*
 * SnmpV1UdpChangeParams.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.snmp2xml4j.snmptoolkit.snmptoolkit;

import net.itransformers.snmp2xml4j.snmptoolkit.SnmpManager;
import net.itransformers.snmp2xml4j.snmptoolkit.SnmpUdpV1Manager;
import net.itransformers.snmp2xml4j.snmptoolkit.SnmpXmlPrinter;
import net.percederberg.mibble.MibLoaderException;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by niau on 4/7/16.
 */
public class SnmpV1UdpChangeParams {
    /**
     * <p>prepareSettings.</p>
     *
     *
     */



    private static SnmpManager snmpManager = null;

    @BeforeClass
    public static void prepareSettings() throws IOException, MibLoaderException {

        snmpManager = new SnmpUdpV1Manager(TestResources.getMibLoaderHolder().getLoader(), "193.19.175.150", "netTransformer-r", 3, 2000, 65535,10, 161);
        snmpManager.init();

    }
    /**
     * <p>snmpGetNext.</p>
     *
     * @throws IOException if any.
     */
    @Test
    public void AssertGetChangeIpAddress() throws IOException {

        Map<String,String> conParams = new HashMap<String,String>();
        conParams.put("ipAddress", "192.168.1.1");
        conParams.put("snmpCommunity", "netTransformer-r");

        OID oid = new OID("1.3.6.1.2.1.1.1.0");
        OID oids[] = new OID[]{oid};

        SnmpManager snmpManager1 = new SnmpUdpV1Manager(TestResources.getMibLoaderHolder().getLoader());
        snmpManager1.init();
        snmpManager1.setParameters(conParams);
        ResponseEvent responseEvent =  snmpManager1.snmpGet(oids);

        org.junit.Assert.assertEquals(responseEvent, null);
        conParams.put("ipAddress", "193.19.175.150");

        snmpManager1.setParameters(conParams);

        ResponseEvent responseEvent2 =  snmpManager1.snmpGet(oids);


        PDU response = responseEvent2.getResponse();

        VariableBinding vb1 = response.get(0);
        Assert.assertEquals("SunOS zeus.snmplabs.com 4.1.3_U1 1 sun4m", vb1.toValueString());


    }


    @Test
    public void AssertGetChangeSnmpCommunity() throws IOException {

        Map<String,String> conParams = new HashMap<String,String>();
        conParams.put("ipAddress", "193.19.175.150");
        conParams.put("snmpCommunity", "test123");

        OID oid = new OID("1.3.6.1.2.1.1.1.0");
        OID oids[] = new OID[]{oid};

        SnmpManager snmpManager1 = new SnmpUdpV1Manager(TestResources.getMibLoaderHolder().getLoader());
        snmpManager1.init();
        snmpManager1.setParameters(conParams);
        ResponseEvent responseEvent =  snmpManager1.snmpGet(oids);

        org.junit.Assert.assertEquals(responseEvent, null);
        conParams.put("snmpCommunity", "netTransformer-r");

        snmpManager1.setParameters(conParams);

        ResponseEvent responseEvent2 =   snmpManager1.snmpGet(oids);


        PDU response = responseEvent2.getResponse();

        VariableBinding vb1 = response.get(0);
        Assert.assertEquals(vb1.toValueString(), "SunOS zeus.snmplabs.com 4.1.3_U1 1 sun4m");
    }

    @Test
    public void snmpWalk() throws MibLoaderException, ParserConfigurationException, SAXException, XPathExpressionException, IOException, XpathException {
        String oids = "system,host,ifEntry";


        SnmpXmlPrinter xmlPrinter = new SnmpXmlPrinter(TestResources.getMibLoaderHolder().getLoader(), snmpManager.snmpWalk(oids.split(",")));


        String xml = xmlPrinter.printTreeAsXML(true);

        String xpath = "/root/iso/org/dod/internet/mgmt/mib-2/system/sysName/value";
        Document doc = XMLUnit.buildControlDocument(xml);
        XpathEngine engine = XMLUnit.newXpathEngine();
        String value = engine.evaluate(xpath, doc);
        Assert.assertEquals(value, "zeus.snmplabs.com");

    }
}
