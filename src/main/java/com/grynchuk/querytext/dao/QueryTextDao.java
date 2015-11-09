package com.grynchuk.querytext.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.grynchuk.querytext.models.MetaData;


public class QueryTextDao {
	private File file;
	
	private int limit = 10000;
	private String q = null;
	private Integer length = null;
	
	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public QueryTextDao(String fileName) {
		file = new File(fileName);
	}
	
	public Object getText() throws IOException {
		if(q == null) 
			//All text
			return getString();
		else
			return getStringsByQuery();	
	}
	
	public Set<String> getStringsByQuery()
			throws IOException {
		Set<String> result = new LinkedHashSet<>();
		
		// no need to read from file if limit == 0
		if (limit > 0) {

			try (Reader fr = new FileReader(file); Reader br = new BufferedReader(fr)) {
				
				StreamTokenizer tokenizer = new StreamTokenizer(br);
				tokenizer.resetSyntax();
				
				//setting chars
				tokenizer.wordChars('0', '9');
				tokenizer.wordChars('A', 'Z');
				tokenizer.wordChars('a', 'z');
				tokenizer.wordChars('_', '_');
				tokenizer.wordChars('$', '$');
				tokenizer.wordChars('-', '-');
				tokenizer.wordChars('+', '+');
				tokenizer.wordChars('#', '#');
				tokenizer.ordinaryChar('=');
				
				//setting delimiters
				tokenizer.whitespaceChars(' ', ' ');
				tokenizer.whitespaceChars('\t', '\t');
				tokenizer.whitespaceChars('\n', '\n');
				tokenizer.whitespaceChars('\r', '\r');

				int totalChars = 0;
				
				while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {

					//it's not a word
					if (tokenizer.ttype != StreamTokenizer.TT_WORD)
						continue;
					
					//word is too big
					if (length != null && tokenizer.sval.length() > length)
						continue;

					//not contains substring
					if (q != null && !tokenizer.sval.contains(q))
						continue;

					//too much symbols
					if (totalChars + tokenizer.sval.length() > limit)
						break;

					if(result.contains(tokenizer.sval)) 
						continue;
					
					result.add(tokenizer.sval);
					totalChars += tokenizer.sval.length();
				}

			} catch (IOException e) {
				e.printStackTrace();
				
				//will send error 500 to client-side
				throw e;
			}

		}

		return result;
	}
	
	//Get all text
	public String getString() throws IOException {
		StringBuilder result = new StringBuilder("");
		
		// no need to read from file if limit == 0
		if (limit > 0) {
			
			char[] buff = new char[limit];
			
			try (Reader fr = new FileReader(file); Reader br = new BufferedReader(fr, limit)) {
				
				int totalChars = 0;
				int count;
				while((count = br.read(buff)) != -1) {
					
					//too much symbols
					if (totalChars + count > limit)
						break;
					
					result.append(String.valueOf(buff, 0, count));
					
					totalChars += count;
				}
				
			}
			
		}
		
		return result.toString();
	}

	public MetaData getMetaData() throws IOException  {
		MetaData result = new MetaData();
		
	    Path path = Paths.get(file.getAbsolutePath());
	    BasicFileAttributes attr;
		try {
			attr = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
			
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
		result.setFileCreationDate(new Date(attr.creationTime().toMillis()));
		result.setFileSize(FileUtils.byteCountToDisplaySize(attr.size()));
		result.setFileName(file.getName());
		
		return result;
	}
}
