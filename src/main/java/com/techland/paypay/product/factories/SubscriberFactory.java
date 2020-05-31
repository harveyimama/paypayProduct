package com.techland.paypay.product.factories;import com.techland.paypay.contracts.PayPayEvent; import com.techland.paypay.contracts.EventSubscriber;import com.techland.paypay.contracts.StateSubscriber;import com.techland.paypay.contracts.Subscriber; import java.util.ArrayList;import java.util.List;import java.util.concurrent.ConcurrentHashMap;import org.springframework.stereotype.Component;

import com.techland.paypay.product.event.MerchantAddedEvent;
import com.techland.paypay.product.event.UserMerchantAddedEvent;
import com.techland.paypay.product.subscribers.MerchantPersistance;
import com.techland.paypay.product.subscribers.MerchantUserPersistance; @Component("paypaySub")  public class SubscriberFactory { private MerchantUserPersistance merchantuserpersistance; private MerchantPersistance merchantpersistance; private  ConcurrentHashMap<String,List<? extends Subscriber>> finalList = new ConcurrentHashMap<>(); private  List<EventSubscriber> eventlist = new ArrayList<EventSubscriber>(); private  List<StateSubscriber> statelist = new ArrayList<StateSubscriber>();public SubscriberFactory( MerchantUserPersistance merchantuserpersistance ,MerchantPersistance merchantpersistance ){ this.merchantuserpersistance=merchantuserpersistance; this.merchantpersistance=merchantpersistance;} public  <R extends PayPayEvent>  ConcurrentHashMap<String,List<? extends Subscriber>> getInstance(R event) {  List<EventSubscriber> eventlist = new ArrayList<EventSubscriber>();		 List<StateSubscriber> statelist = new ArrayList<StateSubscriber>(); if (event instanceof UserMerchantAddedEvent) { statelist.add(merchantuserpersistance);}if (event instanceof MerchantAddedEvent) { statelist.add(merchantpersistance);} return	getFinalList(eventlist,statelist); }  public  List<EventSubscriber> getAllEventSubscribers() {  if (eventlist.isEmpty()) {} return eventlist; }  public  List<StateSubscriber> getAllStateSubscribers() {  if (statelist.isEmpty()) {  statelist.add(merchantuserpersistance);   statelist.add(merchantpersistance); } return statelist; }  private ConcurrentHashMap<String,List<? extends Subscriber>> getFinalList (List<EventSubscriber> eventlist,List<StateSubscriber> statelist) { ConcurrentHashMap<String, List<? extends Subscriber>> finalList = new ConcurrentHashMap<>();  if (eventlist != null) finalList.put("events",eventlist);  if (statelist != null) finalList.put("states",statelist);  return finalList;}} 