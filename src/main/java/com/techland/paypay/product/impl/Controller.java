package com.techland.paypay.product.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.techland.paypay.product.command.AddMerchantCommand;
import com.techland.paypay.product.helper.Constants;
import com.techland.paypay.product.helper.Settings;
import com.techland.paypay.product.persistence.Processor;
import com.techland.paypay.product.responses.ServiceResponse;
import com.techland.paypay.util.LogFeed;

@RestController
public class Controller {

	@Autowired
	private ServiceResponse resp;
	@Autowired
	private ProductEntity merchantEntity;
	@Autowired
	private Processor processor;

	@PostMapping(path = "/api/merchant", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ServiceResponse CreateMerchant(@RequestBody final AddMerchantCommand merchant) {
		try {

			merchantEntity.handleCommand(merchant);

			resp.setMessage(Constants.SUCESS_MESSAGE);
			resp.setResponseCode(Constants.SUCESS_CODE);
			resp.setSuccess(true);
			LogFeed logfeed = new LogFeed.LogProcessor().setInfo(Constants.SUCESS_MESSAGE).setClazz(Controller.class)
					.setDomain(Settings.DOMAIN).setOtherInfo(merchant.toString()).build();
			logfeed.process();

		} catch (Exception e) {
			e.printStackTrace();
			resp.setMessage(Constants.SERVER_ERROR);
			resp.setResponseCode(Constants.SERVER_ERROR_CODE);
			resp.setSuccess(false);
			LogFeed logfeed = new LogFeed.LogProcessor().setInfo(Constants.SERVER_ERROR).setClazz(Controller.class)
					.setDomain(Settings.DOMAIN).setOtherInfo(merchant.toString()).build();
			logfeed.process();

		}
		return resp;
	}

	@GetMapping(path = "/api/merchant/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ProductState getMerchantById(@PathVariable(value = "id") final String id) {

		return processor.getAccountdetails(id);

	}

}
