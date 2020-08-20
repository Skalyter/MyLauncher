package com.tiberiugaspar.mylauncher.xml_parser;
import com.tiberiugaspar.mylauncher.model.NewsInfo;

import java.util.List;

public interface FeedParser {
	List<NewsInfo> parse();
}
