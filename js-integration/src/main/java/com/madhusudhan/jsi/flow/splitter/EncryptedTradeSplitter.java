package com.madhusudhan.jsi.flow.splitter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.integration.Message;
import org.springframework.integration.splitter.AbstractMessageSplitter;

import com.madhusudhan.jsi.domain.EncryptedTrade;
import com.madhusudhan.jsi.domain.ITrade;
import com.madhusudhan.jsi.domain.TradeImpl;

public class EncryptedTradeSplitter extends AbstractMessageSplitter{

	@Override
	protected Object splitMessage(Message<?> message) {
		
		List<ITrade> trades = new ArrayList<ITrade>();
		
		TradeImpl t = (TradeImpl)message.getPayload();
		
		EncryptedTrade et = new EncryptedTrade(t.getEncryptedMsg());
		
		trades.add(t);
		
		trades.add(et);
		
		return trades;
	}

}
