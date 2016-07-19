/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein is
 * confidential and proprietary to MediaTek Inc. and/or its licensors. Without
 * the prior written permission of MediaTek inc. and/or its licensors, any
 * reproduction, modification, use or disclosure of MediaTek Software, and
 * information contained herein, in whole or in part, shall be strictly
 * prohibited.
 *
 * MediaTek Inc. (C) 2015. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 * ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
 * RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 * INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES
 * TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 * RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 * OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN MEDIATEK
 * SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE
 * RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S
 * ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE
 * RELEASED HEREUNDER WILL BE, AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE
 * MEDIATEK SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 * CHARGE PAID BY RECEIVER TO MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek
 * Software") have been modified by MediaTek Inc. All revisions are subject to
 * any receiver's applicable license agreements with MediaTek Inc.
 */
package com.mediatek.xmp;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * ExtendXmpInterface, used to read or write xml.
 */
public class ExtendXmpInterface {
    private static final String TAG = "mtkGallery2/Xmp/ExtendXmpInterface";
    private static final String TAG_DESCRIPTION = "Description";

    private static final String NS_XMP = "adobe:ns:meta/";
    private static final String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    private static final String TAG_XMP_META = "xmpmeta";
    private static final String TAG_RDF = "RDF";

    private static final String ATTRIBUTE_XMP_TK = "xmptk";
    private static final String ATTRIBUTE_ABOUT = "about";
    private static final String ATTRIBUTE_XMLNS = "xmlns";
    private static final String ATTRIBUTE_XMP_TK_VALUE = "Adobe XMP Core 5.1.0-jc003";
    private static final String ATTRIBUTE_ABOUT_VALUE = "";

    private static final String PRIFIX_X = "x";
    private static final String PRIFIX_RDF = "rdf";
    private static final String COLON = ":";
    private static final String UTF_8 = "UTF-8";
    private static final String TAG_DESCRIPTION_WITH_PRIFIX = PRIFIX_RDF + COLON + TAG_DESCRIPTION;
    private static final String XML_SERIALIZATION_HEADER =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private Document mDocument;
    private Element mRootNode;
    private Element mDescriptionNode;
    private XmlPullParser mParser;

    /**
     * Do Serialization preparation.
     */
    public void prepareSerialization() {
        initDocument();
        setRootNode();
    }

    /**
     * Do parsing preparation.
     *
     * @param srcBuffer
     *            extend xmp source buffer
     */
    public void prepareParsing(byte[] srcBuffer) {
        ByteArrayInputStream inputStream = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            mParser = factory.newPullParser();
            inputStream = new ByteArrayInputStream(srcBuffer);
            mParser.setInput(inputStream, UTF_8);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "<prepareParsing> XmlPullParserException", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "<prepareParsing> IOException", e);
            }
        }
    }

    /**
     * Prepare updating xml data.
     *
     * @param is
     *            input stream
     */
    public void prepareUpdating(InputStream is) {
        reloadDocument(is);
    }

    /**
     * Get Property with name space and property name.
     *
     * @param nameSpacePrefix
     *            nameSpace Prefix
     * @param propertyName
     *            propertyName
     * @return property value,fail->null
     */
    public String getProperty(String nameSpacePrefix, String propertyName) {
        if (mParser == null || nameSpacePrefix == null || propertyName == null) {
            Log.d(TAG, "<getProperty> params error!!");
            return null;
        }

        try {
            String prefix = null;
            String attrName = null;
            int eventType = mParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (TAG_DESCRIPTION.equals(mParser.getName())) {
                        int count = mParser.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            prefix = mParser.getAttributePrefix(i);
                            attrName = mParser.getAttributeName(i);
                            if (nameSpacePrefix.equals(prefix) && propertyName.equals(attrName)) {
                                String value = mParser.getAttributeValue(i);
                                return value;
                            }
                        }
                    }
                }
                eventType = mParser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, "<getProperty> XmlPullParserException", e);
        } catch (IOException e) {
            Log.e(TAG, "<getProperty> IOException", e);
        }
        return null;
    }

    /**
     * Register NameSpace.
     *
     * @param nameSpace
     *            namespace
     * @param nameSpacePrefix
     *            nameSpacePrefix
     */
    public void registerNameSpace(String nameSpace, String nameSpacePrefix) {
        if (mDescriptionNode == null) {
            Log.d(TAG, "<registerNameSpace> mDescriptionNode is null, error!!");
            return;
        }
        setAttribute(mDescriptionNode, ATTRIBUTE_XMLNS + COLON + nameSpacePrefix, nameSpace);
    }

    /**
     * Set Property with namespace.
     *
     * @param nameSpacePrefix
     *            nameSpacePrefix
     * @param propertyName
     *            propertyName
     * @param value
     *            value to set
     */
    public void setProperty(String nameSpacePrefix, String propertyName, String value) {
        if (mDescriptionNode == null) {
            Log.d(TAG, "<setProperty> mDescriptionNode is null, error!!");
            return;
        }
        setAttribute(mDescriptionNode, nameSpacePrefix + COLON + propertyName, value);
    }

    /**
     * Serialize Xml to byte array.
     *
     * @return serialized xml data
     */
    public byte[] serializeXml() {
        Log.d(TAG, "<serializeXml> begin");
        float begin = System.currentTimeMillis();
        TransformerFactory tf = TransformerFactory.newInstance();
        byte[] serializedData = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(mDocument);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            byteArrayOutputStream = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(byteArrayOutputStream);
            transformer.transform(source, result);
            byte[] data = byteArrayOutputStream.toByteArray();
            // remove xml header: <?xml version="1.0" encoding="UTF-8"?>
            serializedData = new byte[data.length - XML_SERIALIZATION_HEADER.length()];
            System.arraycopy(data, XML_SERIALIZATION_HEADER.length(), serializedData, 0,
                    data.length - XML_SERIALIZATION_HEADER.length());
            Log.d(TAG, "<serializeXml> success");
        } catch (TransformerConfigurationException e) {
            Log.e(TAG, "<serializeXml> TransformerConfigurationException ", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "<serializeXml> IllegalArgumentException ", e);
        } catch (TransformerException e) {
            Log.e(TAG, "<serializeXml> TransformerException ", e);
        } finally {
            Log.d(TAG, "<serializeXml> costs " + (System.currentTimeMillis() - begin));
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "<serializeXml> IOException", e);
            }
            return serializedData;
        }
    }

    private void setAttributeNS(Element element, String nameSpace, String attributeName,
            String value) {
        if (element == null) {
            Log.d(TAG, "<setAttributeNS> element is null, error!!");
            return;
        }
        element.setAttributeNS(nameSpace, attributeName, value);
    }

    private void setAttribute(Element element, String attributeName, String value) {
        if (element == null) {
            Log.d(TAG, "<setAttribute> element is null, error!!");
            return;
        }
        element.setAttribute(attributeName, value);
    }

    private Element createElementNS(String nameSpace, String name) {
        if (mDocument == null) {
            Log.d(TAG, "<createElementNS> mDocument is null, error!!");
            return null;
        }
        return mDocument.createElementNS(nameSpace, name);
    }

    private Element createElement(String name) {
        if (mDocument == null) {
            Log.d(TAG, "<createElementNS> mDocument is null, error!!");
            return null;
        }
        return mDocument.createElement(name);
    }

    private Node appendChild(Node node) {
        if (mDocument == null) {
            Log.d(TAG, "<appendChild> mDocument is null, error!!");
            return null;
        }
        return mDocument.appendChild(node);
    }

    private Text createTextNode(String text) {
        if (mDocument == null) {
            Log.d(TAG, "<createTextNode> mDocument is null, error!!");
            return null;
        }
        return mDocument.createTextNode(text);
    }

    private void initDocument() {
        Log.d(TAG, "<initDocument>");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            mDocument = builder.newDocument();
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "<initDocument> ParserConfigurationException ", e);
        }
    }

    private void reloadDocument(InputStream is) {
        Log.d(TAG, "<reloadDocument>");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            mDocument = builder.parse(is);
            if (mDocument == null) {
                Log.d(TAG, "<reloadDocument> mDocument is null, error!!");
                return;
            }
            mRootNode = mDocument.getDocumentElement();
            if (mRootNode == null) {
                Log.d(TAG, "<reloadDocument> mRootNode is null, error!!");
                return;
            }
            NodeList list = mRootNode.getElementsByTagName(TAG_DESCRIPTION_WITH_PRIFIX);
            int count = list.getLength();
            for (int i = 0; i < count; i++) {
                Node node = list.item(i);
                if (TAG_DESCRIPTION_WITH_PRIFIX.equals(node.getNodeName())) {
                    mDescriptionNode = (Element) node;
                    break;
                }
            }
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "<reloadDocument> ParserConfigurationException ", e);
        } catch (SAXException e) {
            Log.e(TAG, "<reloadDocument> SAXException ", e);
        } catch (IOException e) {
            Log.e(TAG, "<reloadDocument> IOException ", e);
        }
    }

    /*
     * Create RootNode for extend xmp partition.
     * RootNode: <x:xmpmeta> ...... </x:xmpmeta>
     */
    private void setRootNode() {
        if (mDocument == null) {
            Log.d(TAG, "<setRootNode> mDocument is null, error!!");
            return;
        }
        // 1. create rootNode
        mRootNode = createElementNS(NS_XMP, PRIFIX_X + COLON + TAG_XMP_META);
        // 2. set attribute "x:xmptk"
        setAttributeNS(mRootNode, NS_XMP, PRIFIX_X + COLON + ATTRIBUTE_XMP_TK,
                ATTRIBUTE_XMP_TK_VALUE);
        // 3. create rdfNode
        Element rdfNode = createElementNS(NS_RDF, PRIFIX_RDF + COLON + TAG_RDF);
        // 4. create descriptionNode and set attributes
        mDescriptionNode = createElement(PRIFIX_RDF + COLON + TAG_DESCRIPTION);
        setAttribute(mDescriptionNode, PRIFIX_RDF + COLON + ATTRIBUTE_ABOUT, ATTRIBUTE_ABOUT_VALUE);
        rdfNode.appendChild(mDescriptionNode);
        mRootNode.appendChild(rdfNode);
        mDocument.appendChild(mRootNode);
    }
}