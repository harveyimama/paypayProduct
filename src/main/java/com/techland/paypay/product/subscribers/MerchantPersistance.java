package com.techland.paypay.product.subscribers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techland.paypay.contracts.PayPayState;
import com.techland.paypay.contracts.StateSubscriber;
import com.techland.paypay.contracts.TechLandSubscriber;
import com.techland.paypay.persistence.StateFailure;
import com.techland.paypay.persistence.StateFailureRepository;
import com.techland.paypay.product.impl.ProductState;
import com.techland.paypay.product.persistence.Merchant;
import com.techland.paypay.product.persistence.MerchantRepository;
import com.techland.paypay.product.persistence.User;
import com.techland.paypay.product.persistence.UserRepository;

@Component
@TechLandSubscriber(events = { "MerchantAddedEvent" }, isstate = true)
public class MerchantPersistance implements StateSubscriber {
	@Autowired
	private Merchant merchant;
	@Autowired
	private StateFailure failure;
	@Autowired
	private StateFailureRepository failureRepo;
	@Autowired
	private MerchantRepository merchantRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private User user;


	@Override
	public boolean isState() {
		return true;
	}

	@Override
	public void process(final PayPayState merchantState) {

		try {
			
			Merchant merchant = setMerchantEntity((ProductState) merchantState);
			Merchant merch = merchantRepo.save(merchant);

			if (merch == null)
				handleError(merchantState, failure, failureRepo);
			else 
			{
				handleSuccess(merchantState, failure, failureRepo);
				//saveUsers(((MerchantState) merchantState).getUsers(),merchant);
			}

		} catch (org.springframework.dao.DataIntegrityViolationException e) {

		} catch (Exception r) {
			r.printStackTrace();
			handleError(merchantState, failure, failureRepo);
		}

	}

	private Merchant setMerchantEntity(final ProductState merchantState) {
		merchant.setAddress(merchantState.getAddress());
		merchant.setBusinessDescription(merchantState.getBusinessDescription());
		merchant.setCategory(merchantState.getCategory());
		merchant.setEmail(merchantState.getEmail());
		merchant.setId(merchantState.getId());
		merchant.setName(merchantState.getName());
		merchant.setPhone(merchantState.getPhone());
		merchant.setRCCNumber(merchantState.getRCCNumber());

		return merchant;
	}


	@Override
	public void handleError(PayPayState merchantState, StateFailure failure, StateFailureRepository failureRepo) {
		failure.setStateUserId(((ProductState) merchantState).getId());
		failure.setStateSubscriber(this.getClass().getSimpleName());

		failureRepo.save(failure);

	}

	@Override
	public void handleSuccess(PayPayState merchantState, StateFailure failure, StateFailureRepository failureRepo) {
		failure.setStateUserId(((ProductState) merchantState).getId());
		failure.setStateSubscriber(this.getClass().getSimpleName());

		failureRepo.delete(failure);

	}
	
	}
