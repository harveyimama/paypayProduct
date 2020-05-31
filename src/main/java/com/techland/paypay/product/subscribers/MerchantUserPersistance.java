package com.techland.paypay.product.subscribers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techland.paypay.contracts.PayPayState;
import com.techland.paypay.contracts.StateSubscriber;
import com.techland.paypay.contracts.TechLandSubscriber;
import com.techland.paypay.persistence.StateFailure;
import com.techland.paypay.persistence.StateFailureRepository;
import com.techland.paypay.product.data.MerchantUser;
import com.techland.paypay.product.helper.Settings;
import com.techland.paypay.product.impl.ProductState;
import com.techland.paypay.product.persistence.Merchant;
import com.techland.paypay.product.persistence.User;
import com.techland.paypay.product.persistence.UserRepository;

@Component
@TechLandSubscriber(events = { "UserMerchantAddedEvent" }, isstate = true)
public class MerchantUserPersistance implements StateSubscriber {
	@Autowired
	private Merchant merchant;
	@Autowired
	private StateFailure failure;
	@Autowired
	private StateFailureRepository failureRepo;
	@Autowired
	private UserRepository userRepo;


	@Override
	public boolean isState() {
		return true;
	}

	@Override
	public void process(final PayPayState merchantState) {

		try {
			setMerchantEntity((ProductState) merchantState);
			setUserEntity((ProductState) merchantState);

		} catch (org.springframework.dao.DataIntegrityViolationException e) {

		} catch (Exception r) {
			r.printStackTrace();
			handleError(merchantState, failure, failureRepo);
		}

	}

	private void setUserEntity(final ProductState merchantState) {
		
		List<MerchantUser> users = merchantState.getUsers();
		for (MerchantUser u : users) {
			User user = new User();
			user.setEmail(u.getEmail());
			user.setFullname(u.getFullname());
			user.setId(u.getUserId());
			user.setRole(u.getRole());
			user.setStatus(Settings.USERADDED);
			user.setMerchant(merchant);
			User ret = userRepo.save(user);
			
			if (ret == null)
				handleError(merchantState, failure, failureRepo);
			else 
				handleSuccess(merchantState, failure, failureRepo);

		}

	
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
	
	private void setMerchantEntity(final ProductState merchantState) {
		
		merchant.setAddress(merchantState.getAddress());
		merchant.setBusinessDescription(merchantState.getBusinessDescription());
		merchant.setCategory(merchantState.getCategory());
		merchant.setEmail(merchantState.getEmail());
		merchant.setId(merchantState.getId());
		merchant.setName(merchantState.getName());
		merchant.setPhone(merchantState.getPhone());
		merchant.setRCCNumber(merchantState.getRCCNumber());

	}
	
	
}
