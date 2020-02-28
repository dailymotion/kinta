package com.dailymotion.kinta.integration.xcode

import com.dailymotion.kinta.KintaEnv
import org.w3c.dom.Node.ELEMENT_NODE
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object Xcode {
    fun getMarketingVersion(plistFile: File, pbxprojFile: File? = null): String {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(plistFile)

        val plist = document.documentElement;
        val dict = plist.getElementsByTagName("dict").item(0)
        val nodes = dict.childNodes

        val nodeArray = Array(nodes.length) { nodes.item(it) }

        nodeArray.first().textContent

        val keys = nodeArray.filter { it.nodeType == ELEMENT_NODE && it.nodeName == "key" }
        val values = nodeArray.filter { it.nodeType == ELEMENT_NODE && it.nodeName == "string" }

        val plistVersion = values[keys.indexOfFirst { it.textContent == "CFBundleShortVersionString" }].textContent

        if (plistVersion == "\$(MARKETING_VERSION)") {
            check(pbxprojFile != null) {
                "plist version is MARKETING_VERSION. You must provide pbxprojFile"
            }
            val regex = Regex("\\s*MARKETING_VERSION *= *(.*);")
            val matchResult = pbxprojFile.readLines().mapNotNull {
                regex.matchEntire(it)
            }.firstOrNull()

            if (matchResult == null) {
                throw Exception("cannot find MARKETING_VERSION")
            }

            return matchResult.groupValues[1]
        } else {
            return plistVersion
        }
    }
}