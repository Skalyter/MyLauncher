package com.tiberiugaspar.mylauncher.xml_parser;
import com.tiberiugaspar.mylauncher.model.NewsInfo;
import com.tiberiugaspar.mylauncher.util.CalendarUtil;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static com.tiberiugaspar.mylauncher.xml_parser.BaseFeedParser.DESCRIPTION;
import static com.tiberiugaspar.mylauncher.xml_parser.BaseFeedParser.ITEM;
import static com.tiberiugaspar.mylauncher.xml_parser.BaseFeedParser.LINK;
import static com.tiberiugaspar.mylauncher.xml_parser.BaseFeedParser.PUB_DATE;
import static com.tiberiugaspar.mylauncher.xml_parser.BaseFeedParser.TITLE;

public class RssHandler extends DefaultHandler{
	private List<NewsInfo> newsList;
	private NewsInfo currentNews;
	private StringBuilder builder;
	
	public List<NewsInfo> getNewsList(){
		return this.newsList;
	}
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);
		if (this.currentNews != null){
			if (localName.equalsIgnoreCase(TITLE)){
				currentNews.setTitle(builder.toString());
			} else if (localName.equalsIgnoreCase(LINK)){
				currentNews.setLink(builder.toString());
			} else if (localName.equalsIgnoreCase(DESCRIPTION)){
				currentNews.setDescription(builder.toString());
			} else if (localName.equalsIgnoreCase(PUB_DATE)){
				currentNews.setPubDate(CalendarUtil.getCalendarFromString(builder.toString()));
			} else if (localName.equalsIgnoreCase(ITEM)){
				newsList.add(currentNews);
			}
			builder.setLength(0);	
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		newsList = new ArrayList<NewsInfo>();
		builder = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (localName.equalsIgnoreCase(ITEM)){
			this.currentNews = new NewsInfo();
		}
	}
}