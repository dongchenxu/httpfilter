package com.googlecode.httpfilter.manager.impl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.constant.DateHelper;
import com.googlecode.httpfilter.constant.ItemConstants;
import com.googlecode.httpfilter.domain.ItemDO;
import com.googlecode.httpfilter.manager.SpecialItemManager;

@Service
public class SpecialItemManagerImpl implements SpecialItemManager {

	private String csvFilepath = "data/";
	
	@Override
	public List<ItemDO> listForSpecialsWithEachStyle(int num) {
		List<ItemDO>items = null;
		String path = csvFilepath + "detail_allspecail_item" + ".csv";
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(new File(path)));
			BufferedReader reader = new BufferedReader( new InputStreamReader(in,"GBK") );
			List<Integer> indexs = new ArrayList<Integer>();
			String header = reader.readLine();
			indexs.add( getDataTitleIndex(header, ItemConstants.AUCTION_ID));// 第一行信息，为标题信息，不用,如果需要，注释掉
			indexs.add( getDataTitleIndex(header, ItemConstants.AUCTION_TITLE));
			indexs.add( getDataTitleIndex(header, ItemConstants.AUCTION_STYLE));
			items = getNeedDataStringWitEachStyle( indexs, reader, num );
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	@Override
	public List<ItemDO> listForSpecialsWithStyle(int style) {
		List<ItemDO>items = null;
//		String ysyStr = DateHelper.$STR( new Date( new Date().getTime() - 1000*60*60*24*2 ) );
//		String path = csvFilepath + "detail_allspecail_item_" + ysyStr + ".csv";
		String path = csvFilepath + "detail_allspecail_item" + ".csv";
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(new File(path)));
			BufferedReader reader = new BufferedReader( new InputStreamReader(in,"GBK") );
			List<Integer> indexs = new ArrayList<Integer>();
			String header = reader.readLine();
			indexs.add( getDataTitleIndex(header, ItemConstants.AUCTION_ID));// 第一行信息，为标题信息，不用,如果需要，注释掉
			indexs.add( getDataTitleIndex(header, ItemConstants.AUCTION_TITLE));
			indexs.add( getDataTitleIndex(header, ItemConstants.AUCTION_STYLE));
			items = getNeedDaString( indexs, reader, style );
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

	@Override
	public String queryDataUrl() {
		String url = null;
		String ysyStr = DateHelper.$STR( new Date( new Date().getTime() - 1000*60*60*24*2 ) );
		
		File file = new File( csvFilepath + "detail_allspecail_item.csv" );
		if(file.exists()){
			file.renameTo( new File( csvFilepath + "detail_allspecail_item_20130628" + ".csv" ) );
			return url;
		}
		File fileWithTime = new File( csvFilepath + "detail_allspecail_item_20130628" + ".csv" );
		if( fileWithTime.exists() ){
			return url;
		}else{
			url = "http://ide.cheetah.alibaba-inc.com:8000/main/IDERequest.do?actionName=download&partName=pt=" + ysyStr + "000000" + "&tableName=detail_allspecail_item&tableId=643146&env=dev&open=true";
			return url;
		}
	}
	
	private int getDataTitleIndex(String titleString, String expectTitle) {
		int dataIndex = ItemConstants.WRONG_INDEX;
		if ( ReadLineIsNotEmpty( titleString ) ) {
			String titles[] = titleString.split( "," );
			for (int index = 0; index < titles.length; index++) {
				if( expectTitle.equalsIgnoreCase(titles[index]) ){
					dataIndex = index;
					break;
				}
			}
		}
		
		return dataIndex;
	}
	
	private boolean ReadLineIsNotEmpty( String readLine ){
		if( null != readLine && !readLine.equalsIgnoreCase(",,,") ){
			return true;
		}else {
			return false;
		}
	}

	private List<ItemDO> getNeedDaString(List<Integer> needDataIndex,
			BufferedReader reader, int style) throws IOException {
		String line = reader.readLine();
		List<ItemDO> items = new ArrayList<ItemDO>();
		
		int index = 0;
		while (ReadLineIsNotEmpty(line)&&index<200) {
			String row[] = line.split(",");// CSV格式文件为逗号分隔符文件，这里根据逗号切分
			if( row[needDataIndex.get( needDataIndex.size()-1 )].equals( "\"" + style +"\"" ) ){
				ItemDO item = new ItemDO();
				item.setId( Long.parseLong( row[needDataIndex.get(0)].replaceAll( "\"", "") ) + "");
				item.setTitle( row[needDataIndex.get(1)] );
				items.add(item);
				index ++;
			}
			line = reader.readLine();
		}
		return items;
	}
	
	
	private List<ItemDO> getNeedDataStringWitEachStyle(List<Integer> needDataIndex,
			BufferedReader reader, int Num) throws IOException {
		String line = reader.readLine();
		List<ItemDO> items = new ArrayList<ItemDO>();
		final int sytleNum = 27;
		int index = 0;
		for( int i = 0; i < sytleNum; i ++ ){
			while (ReadLineIsNotEmpty(line)&&index< Num) {
				String row[] = line.split(",");// CSV格式文件为逗号分隔符文件，这里根据逗号切分
				if( row[needDataIndex.get( needDataIndex.size()-1 )].equals( "\"" + i +"\"" ) ){
					ItemDO item = new ItemDO();
					item.setId( Long.parseLong( row[needDataIndex.get(0)].replaceAll( "\"", "") ) + "");
					item.setTitle( row[needDataIndex.get(1)] );
					items.add(item);
					index ++;
				}
				line = reader.readLine();
			}
			index = 0;
		}
		return items;
	}
}
