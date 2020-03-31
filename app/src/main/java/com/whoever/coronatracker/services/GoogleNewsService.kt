package com.whoever.coronatracker.services

import android.os.Build
import com.whoever.coronatracker.models.News
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList

class GoogleNewsService {
    private var pullParserFactory: XmlPullParserFactory? = null

    fun fetch(keyword: String): ArrayList<News?>? {
        pullParserFactory = XmlPullParserFactory.newInstance()
        val parser = pullParserFactory!!.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        val searchkeyword = URLEncoder.encode(keyword, "utf-8")
        val locale = Locale.getDefault()
        val url = "https://news.google.com/rss/search?q=${ searchkeyword }&hl=${ toBcp47Language(locale) }&gl=${ locale.country }"
        val ins: InputStream = URL(url).openStream()
        parser.setInput(ins, null)
        return parseXML(parser)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseXML(parser: XmlPullParser): ArrayList<News?>? {
        var newsList: ArrayList<News?>? = null
        var eventType = parser.eventType
        var news: News? = null
        while (eventType != XmlPullParser.END_DOCUMENT) {
            var name: String
            when (eventType) {
                XmlPullParser.START_DOCUMENT -> newsList = ArrayList()
                XmlPullParser.START_TAG -> {
                    name = parser.name
                    if (name == "item") {
                        news = News()
                    } else if (news != null) {
                        if (name == "title") {
                            news.title = parser.nextText()
                        } else if (name == "link") {
                            news.link = parser.nextText()
                        } else if (name == "description") {
                            news.description = parser.nextText()
                        } else if (name == "pubDate") {
                            news.pubDate = parser.nextText()
                        } else if (name == "source") {
                            news.source = parser.nextText()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    name = parser.name
                    if (name.equals("item", ignoreCase = true) && news != null) {
                        newsList!!.add(news)
                    }
                }
            }
            eventType = parser.next()
        }
        return newsList
    }

    fun toBcp47Language(loc: Locale): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return loc.toLanguageTag()
        }

        val SEP = '-'
        var language = loc.language
        var region = loc.country
        var variant = loc.variant
        if (language == "no" && region == "NO" && variant == "NY") {
            language = "nn"
            region = "NO"
            variant = ""
        }
        if (language.isEmpty() || !language.matches(Regex("\\p{Alpha}{2,8}"))) {
            language = "und"
        } else if (language == "iw") {
            language = "he"
        } else if (language == "in") {
            language = "id"
        } else if (language == "ji") {
            language = "yi"
        }

        if (!region.matches(Regex("\\p{Alpha}{2}|\\p{Digit}{3}"))) {
            region = ""
        }

        if (!variant.matches(Regex("\\p{Alnum}{5,8}|\\p{Digit}\\p{Alnum}{3}"))) {
            variant = ""
        }
        val bcp47Tag = StringBuilder(language)
        if (!region.isEmpty()) {
            bcp47Tag.append(SEP).append(region)
        }
        if (!variant.isEmpty()) {
            bcp47Tag.append(SEP).append(variant)
        }
        return bcp47Tag.toString()
    }
}